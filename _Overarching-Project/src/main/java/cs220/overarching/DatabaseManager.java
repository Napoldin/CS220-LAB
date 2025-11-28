package cs220.overarching;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    /**
     * Create a connection with our database.
     *
     * @return A connection to use for any processes
     * @throws SQLException When we cant connect to the DB
     */
    // Helpers
    protected Connection connectDB() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/showdowndb";
        String user = "cs220";
        String pass = "showdown42";
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Deletes the data within a table
     *
     * @param table The name of the table we wish to truncate
     * @throws SQLException If we cant connect to the DB
     */
    public void truncateTable(String table) throws SQLException {
        try (Connection conn = connectDB()){
            Statement ps = conn.createStatement();
            String cmd = "TRUNCATE TABLE " + table;
            ps.executeUpdate(cmd);
            System.out.println("Table '" + table + "' truncated successfully.");
        } catch (SQLException e) {
            System.err.println("Error while truncating table '" + table + "'");
            throw e;
        }
    }

    /**
     * Commits Player Data into the DB
     *
     * @param format The format we are inputting player Data for
     * @param player The player object we are putting into the database
     * @param conn Connection with the database
     * @param insert The SQL Query we are using to insert the player
     * @param history A Boolean telling us if were inputting player History or not
     * @throws SQLException If we cant connect to the DB
     */
    private void commitPlayer(String format, Player player, Connection conn, String insert, Boolean history) throws SQLException {
        if (history) {
            int playerId;
            PreparedStatement ps1 = conn.prepareStatement("SELECT id FROM players WHERE format = ? AND name = ?");
            ps1.setString(1, format);
            ps1.setString(2, player.getName());
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {playerId = rs.getInt("id");}
            else {throw new SQLException("Player not found after insert/update");}
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setInt(1, playerId);
            ps.setString(2, format);
            ps.setString(3, player.getName());
            ps.setInt(4, player.getElo());
            ps.setDouble(5, player.getGxe());
            ps.setInt(6, player.getGlickoRating());
            ps.setInt(7, player.getGlickoDeviation());
            ps.addBatch();
            ps.executeBatch();
        } else {
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, format);
            ps.setString(2, player.getName());
            ps.setInt(3, player.getElo());
            ps.setDouble(4, player.getGxe());
            ps.setInt(5, player.getGlickoRating());
            ps.setInt(6, player.getGlickoDeviation());
            ps.addBatch();
            ps.executeBatch();
        }
    }

    /**
     * Inserts player data to be commited to the DB
     *
     * @param format What format we are inserting for
     * @param player The player data we are inserting
     */
    // Insertions
    public void insertPlayer(String format, Player player){
        try (Connection conn = connectDB()){
            String insert = "INSERT INTO players(format, name, elo, gxe, glickoRating, glickoDev) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "elo = VALUES(elo), " +
                "gxe = VALUES(gxe), " +
                "glickoRating = VALUES(glickoRating), " +
                "glickoDev = VALUES(glickoDev)";
            commitPlayer(format, player, conn, insert, false);
            insertHistory(format, player);
        } catch (SQLException e) {
            System.out.println("Failed to insert player");
            e.printStackTrace();
        }
    }

    /**
     * Inserts Player History to be commited
     *
     * @param format What format we are inserting for
     * @param player The player data we are inserting
     */
    public void insertHistory(String format, Player player){
        try (Connection conn = connectDB()){
            String insert = "INSERT INTO playerHistory(playerId, format, name, elo, gxe, glickoRating, glickoDev) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "elo = VALUES(elo), " +
                    "gxe = VALUES(gxe), " +
                    "glickoRating = VALUES(glickoRating), " +
                    "glickoDev = VALUES(glickoDev)";
            commitPlayer(format, player, conn, insert, true);
        } catch (SQLException e) {
            System.out.println("Failed to insert player history");
            e.printStackTrace();
        }
    }

    /**
     * Inserts all the top 500 players for a format tnto the DB
     *
     * @param format The format we are inserting into
     * @param players All the players within the top 500 for that format
     * @param conn Connection to the Database
     * @throws SQLException If we cant connect to the Database
     */
    public void insertAllTop500(String format, List<Player> players, Connection conn) throws SQLException {
        String deleteCmd = "DELETE FROM top500 WHERE format = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteCmd)) {
            psDelete.setString(1, format);
            psDelete.executeUpdate();
        }

        String insert = "INSERT INTO top500(format, rank, name, elo, gxe, glickoRating, glickoDev, coil) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "elo = VALUES(elo), " +
                "gxe = VALUES(gxe), " +
                "glickoRating = VALUES(glickoRating), " +
                "glickoDev = VALUES(glickoDev), " +
                "coil = VALUES(coil)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            for (Player player : players) {
                ps.setString(1, format);
                ps.setInt(2, player.getRank());
                ps.setString(3, player.getName());
                ps.setInt(4, player.getElo());
                ps.setDouble(5, player.getGxe());
                ps.setInt(6, player.getGlickoRating());
                ps.setInt(7, player.getGlickoDeviation());
                ps.setString(8, player.getCoil());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Inserts A single player into the top 500 for a format
     *
     * @param format The format we are inserting into
     * @param player All the player within the top 500 for that format
     */
    public void insertTop500(String format, Player player){
        try (Connection conn = connectDB()){
            conn.setAutoCommit(false);
            String insert = "INSERT INTO top500(format, rank, name, elo, gxe, glickoRating, glickoDev, coil) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "elo = VALUES(elo), " +
                "gxe = VALUES(gxe), " +
                "glickoRating = VALUES(glickoRating), " +
                "glickoDev = VALUES(glickoDev), " +
                "coil = VALUES(coil)";
            PreparedStatement ps = conn.prepareStatement(insert);
            ps.setString(1, format);
            ps.setInt(2, player.getRank());
            ps.setString(3, player.getName());
            ps.setInt(4, player.getElo());
            ps.setDouble(5, player.getGxe());
            ps.setInt(6, player.getGlickoRating());
            ps.setInt(7, player.getGlickoDeviation());
            ps.setString(8, player.getCoil());
            ps.addBatch();
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Failed to insert top500, Failed at format: " + format);
            e.printStackTrace();
        }
    }

    // Query's

    /**
     * Grabs the top 500 players in each format we support and returns it in a map of format > Player
     *
     * @return A Map of formats to Lists of player objects created from the DB Data for that specific format
     * @throws SQLException If we cannot grab the data from the DB
     */
    protected Map<String, List<Player>> getAllTop500() throws SQLException {
        String[] formats = LadderSearcher.formats;
        Map<String, List<Player>> players = new HashMap<>();
        boolean foundFormat = false;
        for (String format : formats) {
            String query = "SELECT * FROM top500 WHERE format = ? ORDER BY rank LIMIT 500";
            try (Connection conn = connectDB()){
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, format);
                ResultSet rs = ps.executeQuery();
                List<Player> playerArr = new ArrayList<>();
                while (rs.next()) {
                    Player player = new Player(
                            rs.getInt("rank"),
                            rs.getString("name"),
                            rs.getInt("elo"),
                            rs.getDouble("gxe"),
                            rs.getInt("glickoRating"),
                            rs.getInt("glickoDev"),
                            rs.getString("coil")
                    );
                    playerArr.add(player);
                }
                if (!playerArr.isEmpty()) foundFormat = true;
                players.put(format, playerArr);
            }
        }
        if (!foundFormat) return null;
        return players;
    }

    /**
     * Grabs data from the DB to put into a list of Player Objects
     *
     * @param format The format we are grabbing data from
     * @return A list of player objects regarding the top 500 in the format we inputted
     * @throws SQLException If we cannot grab data from the DB
     */
    protected List<Player> getFormatTop500(String format) throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT * FROM top500 WHERE format = ? ORDER BY rank LIMIT 500";
        try (Connection conn = connectDB()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, format);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player player = new Player(
                        rs.getInt("rank"),
                        rs.getString("name"),
                        rs.getInt("elo"),
                        rs.getDouble("gxe"),
                        rs.getInt("glickoRating"),
                        rs.getInt("glickoDev"),
                        rs.getString("coil")
                );
                players.add(player);
            }
        }
        if (players.isEmpty()) return null;
        return players;
    }

    /**
     * Grabs data about One player and returns it in a format > player Data Map
     *
     * @param name The Players Name we are searching
     * @return A map of player Data to formats (format > Player Data
     */
    protected Map<String, Player> getAllPlayerData(String name){
        Map<String, Player> player = new HashMap<>();
        String query = "SELECT * FROM players WHERE name = ?";
        try (Connection conn = connectDB()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String format = rs.getString("format");
                Player newPlayer = new Player(
                        rs.getString("name"),
                        rs.getInt("elo"),
                        rs.getDouble("gxe"),
                        rs.getInt("glickoRating"),
                        rs.getInt("glickoDev")
                );
                player.put(format, newPlayer);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get player data");
            e.printStackTrace();
            return null;
        }
        if (player.isEmpty()) return null;
        return player;
    }

    /**
     * Grab the data for a Player for a specific format
     *
     * @param format Format we are grabbing data from
     * @param name The player we are searching
     * @return A Plyer object for the format we inputted
     */
    protected Player getFormatPlayerData(String format, String name){
        Player player = null;
        String query = "SELECT * FROM players WHERE format = ? AND name = ?";
        try (Connection conn = connectDB()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, format);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                player = new Player(
                        rs.getString("name"),
                        rs.getInt("elo"),
                        rs.getDouble("gxe"),
                        rs.getInt("glickoRating"),
                        rs.getInt("glickoDev")
                );
            }
        } catch (SQLException e) {
            System.out.println("Failed to get player data");
            e.printStackTrace();
            return null;
        }
        return player;
    }

    /**
     * Grabs Player Data for a format over all time
     *
     * @param format The format we want to get the history for
     * @param name The player we are searching for
     * @return A list of past Player data in a format for a player
     */
    protected List<Player> getPlayerFormatHistory(String format, String name){ // Change this
        List<Player> history = new ArrayList<>();
        String query = "SELECT name, elo, gxe, glickoRating, glickoDev, timestamp  FROM playerHistory WHERE format = ? AND name = ? ORDER BY timestamp DESC";
        try (Connection conn = connectDB()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, format);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player player = new Player(
                        rs.getString("name"),
                        rs.getInt("elo"),
                        rs.getDouble("gxe"),
                        rs.getInt("glickoRating"),
                        rs.getInt("glickoDev"),
                        rs.getString("Timestamp")
                );
                history.add(player);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get player data");
            e.printStackTrace();
            return null;
        }
        if (history.isEmpty()) return null;
        return history;
    }

}

