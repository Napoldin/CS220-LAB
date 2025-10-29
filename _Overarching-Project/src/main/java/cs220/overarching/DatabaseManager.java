package cs220.overarching;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    // Helpers
    protected Connection connectDB() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/showdowndb";
        String user = "cs220";
        String pass = "showdown42";
        return DriverManager.getConnection(url, user, pass);
    }
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

