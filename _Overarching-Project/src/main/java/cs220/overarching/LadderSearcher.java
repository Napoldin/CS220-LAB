package cs220.overarching;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LadderSearcher {

    final private DatabaseManager db;

    // Now that we made the searching so much faster we may be able to add more formats
    static final public String[] formats = {
            "gen9randombattle", "gen9freeforallrandombattle", "gen9randombattleblitz",
            "gen9ou", "gen9ubers", "gen9uu", "gen9ru", "gen9pu", "gen9lc", "gen9monotype", "gen9cap",
            "gen9bssregj", "gen9randomdoublesbattle", "gen9doublesou", "gen9doublesubers",
            "gen9doublesuu", "gen9vgc2025regh", "gen9vgc2025reghbo3", "gen9vgc2025regj"
    };


    public LadderSearcher(){
        db = new DatabaseManager();
    }

    //Helpers

    /**
     * Helper function that takes a String array of data that can be used to make a player object then
     * makes one and returns it.
     *
     * @param playerParts String array of data used to construct the player object
     * @return Created player object
     */
    private Player createPlayer(String[] playerParts) {
        int len = playerParts.length;
        int rank = safeParseInt(playerParts[0]);
        int elo = safeParseInt(playerParts[len - 6]);
        double gxe = safeParseDouble(playerParts[len - 5].replace("%", ""));
        int glickoRating = safeParseInt(playerParts[len - 4]);
        int glickoDev = safeParseInt(playerParts[len - 2]);
        String coil = playerParts[len - 1];
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < len - 6; i++) {
            if (i > 1) nameBuilder.append(" ");
            nameBuilder.append(playerParts[i]);
        }
        String name = nameBuilder.toString();
        return new Player(rank, name, elo, gxe, glickoRating, glickoDev, coil);
    }

    /** Helper function that safely parses int's due to how showdown formating is.
     *
     * @param num int to parse
     * @return Parsed int or -1 if failed
     */
    private int safeParseInt(String num) {
        try {return Integer.parseInt(num);} catch (NumberFormatException e) {return -1;}
    }
    /** Helper function that safely parses doubles due to how showdown formating is.
     *
     * @param num double to parse
     * @return Parsed double or -1 if failed
     */
    private double safeParseDouble(String num) {
        try {return Double.parseDouble(num);} catch (NumberFormatException e) {return -1.0;}
    }

    // Loaders/Parsers

    /**
     * Loads every ladder that we have in our formats array into our DB
     *
     * @throws IOException In case parsing the Pkmn showdown pages goes wrong
     */
    public void loadLadder() throws IOException {
        for (int attempts = 1; attempts <= 10; attempts++){
            System.out.println("Loading Ladder...");
            try (Connection conn = db.connectDB()) {
                conn.setAutoCommit(false);
                try {
                    for (String format : formats) {
                        LoadFormatData(format, conn);
                    }
                    conn.commit();
                    System.out.println("Finished loading Ladder");
                    return;
                } catch (IOException e) {
                    System.out.println("Failed to load Ladder, Attempts remaining: " + (10-attempts));
                    if (attempts==10){
                        System.out.println("Failed to load Ladder");
                        throw e;
                    }
                }
            } catch (SQLException e){
                System.out.println("Failed to connect to db");
            }
        }
    }

    /**
     * Go to the format page in Pkmn showdown and grabs the format data from that page.
     * Uses the create player method for each user in the top 500 and then inserts it into our DB
     *
     * @param format Pkmn Showdown format that were getting data for.
     * @param conn Connection to our database (doesn't make its own as that's a lot slower)
     * @throws IOException In case of The parsing of the showdown website goes wrong
     * @throws SQLException In case we can't connect to our database or something goes wrong
     */
    private void LoadFormatData(String format, Connection conn) throws IOException, SQLException {
        String url = "https://play.pokemonshowdown.com/ladder.php?format=" + format + "&server=showdown&output=html&prefix=";
        Document doc = Jsoup.connect(url).get();
        Elements tableRows = doc.getElementsByTag("tr");
        List<Player> players = new ArrayList<>();
        for (int j = 1; j < tableRows.size(); j++) {
            Element user = tableRows.get(j);
            String userData = user.text();
            String[] userParts = userData.trim().split("\\s+");
            Player player = createPlayer(userParts);
            players.add(player);
        }
        db.insertAllTop500(format, players, conn);
    }

    /**
     * Takes in a name and searches for that player in Showdown.
     * Then puts it in the DB and saves the history for that user as well
     * Gives data even for formats not in the format array.
     *
     * @param name name of the player we want to search for.
     * @return a map with each key being a format type containing the player data for that type
     * @throws IOException in case the showdown scraping messes up
     */
    public Map<String, Player> searchPlayer(String name) throws IOException { // format > Player
        Map<String, Player> playerData = new HashMap<>();
        if (name.split("\\s+").length > 1) {name = name.split("\\s+")[0] + name.split("\\s+")[1];}
        for (int attempts = 1; attempts <= 10; attempts++){
            try{
                String url = "https://pokemonshowdown.com/users/" + name + "?output=html";
                Document doc = Jsoup.connect(url).get();
                Elements tableRows = doc.getElementsByTag("tr");
                for (int i = 1; i < tableRows.size(); i++) {
                    Element tableRow = tableRows.get(i);
                    Elements userData = tableRow.getElementsByTag("td");
                    if (!userData.isEmpty()) {
                        String format = userData.get(0).text();
                        int elo = safeParseInt(userData.get(1).text());
                        if (userData.size() > 3) {
                            double gxe = safeParseDouble(userData.get(2).text().replace("%", ""));
                            int glickoRating = safeParseInt(userData.get(3).text().split("\\s+")[0]);
                            int glickoDev = safeParseInt(userData.get(3).text().split("\\s+")[2]);
                            Player player = new Player(name, elo, gxe, glickoRating, glickoDev);
                            playerData.put(format, player);
                            db.insertPlayer(format, player);
                        } else {
                            double gxe = 0.0;
                            int glickoRating = 0;
                            int glickoDev = 0;
                            Player player = new Player(name, elo, gxe, glickoRating, glickoDev);
                            playerData.put(format, player);
                            db.insertPlayer(format, player);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error Connecting to Database, Retrying. " + (10 - attempts) + " attempts remaining");
                if (attempts == 10) {
                    System.out.println("Could not connect to database");
                    throw e;
                }
                continue;
            }
            return playerData;
        }
        return null;
    }

    // Database searchers

    /**
     * Grabs player Data from our DB for the requested format and returns it
     *
     * @param format Pkmn showdown Format name
     * @param name Name of the Player we want data for
     * @return Player object for the format we selected or null if it doesn't exist in our DB
     */
    public Player getSavedPlayerFormat(String format, String name){
        return db.getFormatPlayerData(format, name);
    }

    /**
     * Gets a player that is saved in our DB
     *
     * @param name Name of the player we want to search for
     * @return a map with each key being a format type containing the player data for that type or null if it doesn't exit in our DB
     */
    public Map<String, Player> getSavedPlayer(String name)  {return db.getAllPlayerData(name);}

    /**
     * Gets all Data for the top 500 in each format of our formats array
     *
     * @return a map with each key being a format type containing all player data for that format or null if it doesn't exit in our DB
     * @throws SQLException In case something messes up with our DB
     */
    public Map<String, List<Player>> getAllTop500() throws SQLException {return db.getAllTop500();}

    /**
     * Gets data from our DB for one specific format
     *
     * @param format Format name we want the data for
     * @return A list of the top 500 in the format or null if we don't have it in our DB
     * @throws SQLException In case we have issues getting from our DB
     */
    public List<Player> getFormatTop500(String format) throws SQLException {return db.getFormatTop500(format);}


    /**
     * Gets a list of previous Elo's from a player, may be used for graphs or more.
     *
     * @param format Format we want the history for
     * @param name name of the player we want to get the history of from our DB
     * @return A list of all the previous Elo's the player has had
     */
    public List<Integer> getPlayerFormatHistory(String format, String name){return db.getPlayerFormatHistory(format, name);}

    //Operations

    /**
     * Checks Elo Difference between the users current elo and whatever elo they would like to check against
     *
     * @param format Format we want to check the difference in history in
     * @param name name of the player we're checking
     * @param depth How far deep we want to check. (0 = current Elo, max = First elo recorded)
     * @return The difference between the current elo and the elo we're checking against
     * @throws IOException In case something goes wrong in parsing the user page
     */
    public int getEloHistoryDiff(String format, String name, int depth) throws IOException {
        int difference = 0;
        Player newPlayer = searchPlayer(name).get(format);
        List<Integer> oldPlayer = getPlayerFormatHistory(format, name);
        if (depth >= oldPlayer.size()){
            difference = newPlayer.getElo()-oldPlayer.getLast(); // reset to last if depth is too deep
            System.out.println("Entered Depth is too large, defaulting to last elo in history. Highest depth allowed: " + (oldPlayer.size()-1));
        }
        else difference = newPlayer.getElo()-oldPlayer.get(-1*oldPlayer.size()+(oldPlayer.size()+depth));
        return difference; // to reverse is just: (-1*diff+elo)
    }

    /**
     * Checks the difference in current elo between 2 players
     *
     * @param format Format we are checking the Elo for
     * @param user1 The first user we're checking for
     * @param user2 The Second user we're checking for
     * @return The difference between the two players elo
     * @throws IOException In case parsing the user pages messes up
     */
    public int checkEloAgainstOther(String format, String user1, String user2) throws IOException {
        int difference;
        Player player1 = searchPlayer(user1).get(format);
        Player player2 = searchPlayer(user2).get(format);
        difference = player1.getElo()-player2.getElo();
        return difference;
    }

}
