package cs220.overarching;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// For reference https://happycoding.io/tutorials/java/swing/higher-lower-computer-guil

public class Gui {

    private final JFrame frame;
    private final CardLayout layout;
    private final JPanel mainPanel;
    private final LadderSearcher ladder;

    /**
     * Show the Visuals
     */
    // Helpers
    public void show(){
        frame.setVisible(true);
    }

    /**
     * Switches the displayed Page
     *
     * @param name name of the panel we're switching to
     */
    private void switchPage(String name){
        layout.show(mainPanel, name);
    }

    /**
     * Removes dupe pages as to not slow down the app or create issues in displaying data
     *
     * @param panelName The name of the panel we want to remove dupes from
     */
    private void removeDupes(String panelName) {
        for (Component comp : mainPanel.getComponents()) {
            if (panelName.equals(comp.getName())){
                mainPanel.remove(comp);
                break;
            }
        }
    }

    /**
     * Helper function to make a title label, made as to reuse code
     *
     * @param title Title string to show
     * @param panel the panel we're adding it to
     * @param border Boolean to know if were adding a border or not
     */
    private void createTitle(String title, JPanel panel, boolean border) {
        JLabel newTitle = new JLabel(title);
        newTitle.setFont(new Font("Arial", Font.BOLD, 20));
        newTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(newTitle, BorderLayout.NORTH);
        if (border){
            newTitle.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        }
    }

    /**
     * Helper function to make a back panel as to reuse code
     *
     * @param text Text to show
     * @param panel Panel to add it to
     * @param backPanel Panel to redirect to
     */
    private void createBackButton(String text, JPanel panel, String backPanel) {
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        JButton nextPageButton = new JButton(text);
        nextPageButton.setFont(buttonFont);
        nextPageButton.setPreferredSize(new Dimension(120, 40));
        nextPageButton.addActionListener(e -> {switchPage(backPanel);});
        panel.add(nextPageButton, BorderLayout.SOUTH);
    }


    /**
     * Main function creating all the pages.
     *
     * @throws SQLException In case we cant grab or input data to the DB
     * @throws IOException In case we cant grab data from the Website
     */
    // On run Startup
    Gui() throws SQLException, IOException {
        ladder = new LadderSearcher();

        // only load new ladder when asked or when no ladder data is found on startup, otherwise use db data
        if (ladder.getAllTop500() == null) ladder.loadLadder();
        frame = new JFrame("Showdown Utils");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,700);
        frame.setLocationRelativeTo(null);

        layout = new CardLayout();
        mainPanel = new  JPanel(layout);

        // Pages
        mainPanel.add(welcomePanel(), "welcome"); // Starts here
        mainPanel.add(hubPanel1(), "hub1");
        mainPanel.add(hubPanel2(), "hub2");
        mainPanel.add(chooseTop500FormatPage(), "top500ChooseFormat");
        // all formats
        for (String format : LadderSearcher.formats) mainPanel.add(top500Panel(format), "top500-" + format);
        mainPanel.add(reloadFormatPage(), "reloadFormat");
        mainPanel.add(searchPlayersPage(), "searchPlayers");
        // Found players is made within search players
        mainPanel.add(playerHistorySearchPage(), "playerHistorySearch");


        frame.add(mainPanel); // Show
    }

    // Panels

    /**
     * The Opening panel to let us in the app
     *
     * @return The welcome Panel
     */
    private JPanel welcomePanel(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Pokemon Showdown Utility");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton enterButton = new JButton("Enter!");
        enterButton.setPreferredSize(new Dimension(100,100));
        enterButton.addActionListener(e -> {switchPage("hub1");});
        panel.add(enterButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * The Panel that leads to others, a connector
     *
     * @return The first Hub panel
     */
    private JPanel hubPanel1(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Hub Page 1", panel, false);

        JPanel optionGrid = new JPanel(new GridLayout(2,2, 20, 20));
        optionGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton top500Button = new JButton("View Top 500 in a format");
        top500Button.addActionListener(e -> {switchPage("top500ChooseFormat");});
        JButton reloadFormatsButton = new JButton("Reload Formats");
        reloadFormatsButton.addActionListener(e -> {switchPage("reloadFormat");});
        JButton searchPlayersButton = new JButton("Search Players");
        searchPlayersButton.addActionListener(e -> {switchPage("searchPlayers");});
        JButton playerHistoryButton = new JButton("Check Player History");
        playerHistoryButton.addActionListener(e -> {switchPage("playerHistorySearch");});

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(200, 80);
        for (JButton btn : new JButton[]{top500Button, reloadFormatsButton, searchPlayersButton, playerHistoryButton}) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            optionGrid.add(btn);
        }
        panel.add(optionGrid, BorderLayout.CENTER);

        createBackButton("Next Page", panel, "hub2");

        return panel;
    }

    /**
     * Second connector
     *
     * @return 2nd Hub panel
     */
    private JPanel hubPanel2(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Hub Page 2", panel, false);

        createBackButton("Back", panel, "hub1");

        return panel;
    }

    /**
     * The panel we use to choose which top 500 to display
     *
     * @return Choose top 500 page panel
     */
    // Button Pages
    private JPanel chooseTop500FormatPage(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Please Pick a Format", panel, false);

        JPanel formatGrid = new JPanel(new GridLayout(10,10, 10, 10));
        formatGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        for (String format : LadderSearcher.formats){
            JButton btn = new JButton(format);
            btn.addActionListener(e -> {switchPage("top500-" + format);});
            formatGrid.add(btn);
        }

        createBackButton("Back to Hub", panel, "hub1");

        panel.add(formatGrid, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Shows the Data for a format's top 500 players
     *
     * @param format Format we want to display
     * @return Panel showing the data for a format in the top 500
     * @throws SQLException if we cant connect to the DB
     */
    private JPanel top500Panel(String format) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Top 500 - " + format, panel, false);

        List<Player> playerData = ladder.getFormatTop500(format);
        String[] columns = {"Rank", "Name", "Elo", "Gxe","Glicko Rating", "Coil"};
        Object[][] tableData = new Object[playerData.size()][columns.length];
        for (int i = 0; i < playerData.size(); i++) {
            tableData[i][0] = playerData.get(i).getRank();
            tableData[i][1] = playerData.get(i).getName();
            tableData[i][2] = playerData.get(i).getElo();
            tableData[i][3] = playerData.get(i).getGxe() + "%";
            tableData[i][4] = playerData.get(i).getGlicko();
            tableData[i][5] = playerData.get(i).getCoil();
        }
        JTable playersTable = new JTable(tableData, columns);
        playersTable.setRowHeight(22);
        JScrollPane playersScroll = new JScrollPane(playersTable);
        playersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(playersScroll, BorderLayout.CENTER);

        createBackButton("Back to Formats", panel, "top500ChooseFormat");

        return panel;
    }

    /**
     * Page to reload our data in the DB
     *
     * @return Panel for the reload page
     */
    private JPanel reloadFormatPage(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Choose a Format to Reload", panel, false);

        JPanel formatsGrid = new JPanel(new GridLayout(10,10, 20, 20));
        formatsGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (String format : LadderSearcher.formats){
            JButton btn = new JButton(format);
            btn.addActionListener(e -> {
                try {
                    ladder.loadFormatData(format);
                    JPanel newPanel = top500Panel(format);
                    mainPanel.add(newPanel, "top500-" + format);
                } catch (IOException | SQLException er) {
                    throw new RuntimeException(er);
                }
            });
            formatsGrid.add(btn);
        }
        JButton reloadAllFormats = new JButton("Reload All Formats");
        reloadAllFormats.addActionListener(e -> {
            try {
                ladder.loadLadder();
            } catch (IOException er) {
                throw new RuntimeException(er);
            }
        });
        formatsGrid.add(reloadAllFormats);
        panel.add(formatsGrid, BorderLayout.CENTER);

        createBackButton("Back to Hub", panel, "hub1");

        return panel;
    }

    /**
     * Where we search for players in Pokemon Showdown to show their Data
     *
     * @return panel to search players
     */
    private JPanel searchPlayersPage(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Please enter the name of the player you want to search", panel, true);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameInput = new JTextField();
        nameInput.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(nameInput);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String name = nameInput.getText().toLowerCase().trim();
            if (name.isEmpty()) return;
            try {
                Map<String, Player> player = ladder.searchPlayer(name);
                if (player.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Player not found");
                    return;
                }
                JPanel playerPanel = playerFoundPage(player);
                String panelName = "player-" + name;
                removeDupes(panelName);
                playerPanel.setName(panelName);
                mainPanel.add(playerPanel, panelName);
                switchPage(panelName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        inputPanel.add(searchButton);
        panel.add(inputPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Hub");
        backButton.addActionListener(e -> {switchPage("hub1");});
        backButton.setPreferredSize(new Dimension(200, 50));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Displays player Data in a table
     *
     * @param playerMap A map of a player's data (format > Player Data)
     * @return Jpanel of player found page
     */
    private JPanel playerFoundPage(Map<String, Player> playerMap){
        JPanel panel = new JPanel(new BorderLayout());

        List<Map.Entry<String, Player>> playerEntries = new ArrayList<>(playerMap.entrySet());

        createTitle(playerEntries.get(playerEntries.size()-1).getValue().getName(), panel, false);

        String[] columns = {"Format", "Elo", "Gxe", "Glicko"};
        Object[][] tableData = new Object[playerEntries.size()][columns.length];
        for (int i = 0; i < playerEntries.size(); i++){
            String format = playerEntries.get(i).getKey();
            Player player = playerEntries.get(i).getValue();
            tableData[i][0] = format;
            tableData[i][1] = player.getElo();
            tableData[i][2] = player.getGxe() + "%";
            tableData[i][3] = player.getGlicko();

        }
        JTable playersTable = new JTable(tableData, columns);
        playersTable.setRowHeight(22);
        JScrollPane playersScroll = new JScrollPane(playersTable);
        playersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(playersScroll, BorderLayout.CENTER);

        createBackButton("Back to Player Search", panel, "searchPlayers");

        return panel;
    }

    /**
     * We use this page as to search for recorded history for players
     *
     * @return Player History search page
     */
    private JPanel playerHistorySearchPage(){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Please enter the name of the player you want to see the history of",  panel, true);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField nameInput = new JTextField();
        nameInput.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(nameInput);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String name = nameInput.getText().toLowerCase().trim();
            if (name.isEmpty()) return;
            Map<String, Player> player = ladder.getSavedPlayer(name);

            if (player == null || player.isEmpty()){
                JOptionPane.showMessageDialog(null, "Player not found! Creating History...");
                try {
                    ladder.searchPlayer(name);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            player = ladder.getSavedPlayer(name);
            if (player.isEmpty()){
                JOptionPane.showMessageDialog(null, "No player exists with that name");
                return;
            }
            JPanel playerPanel = choosePlayerFormatsPage(player);
            String panelName = "player-" + name + "-Formats";
            removeDupes(panelName);
            playerPanel.setName(panelName);
            mainPanel.add(playerPanel, panelName);
            switchPage(panelName);
        });
        inputPanel.add(searchButton);
        panel.add(inputPanel, BorderLayout.CENTER);

        createBackButton("Back to hub", panel, "hub1");

        return panel;
    }

    /**
     * Page to choose what format we want to show
     *
     * @param playerMap Player map of their history (Format > Player History)
     * @return Panel of the page
     */
    private JPanel choosePlayerFormatsPage(Map<String, Player> playerMap){
        JPanel panel = new JPanel(new BorderLayout());

        createTitle("Please Choose a format to view the history of!", panel, true);

        int formatAmount = playerMap.size();

        JPanel formatsGrid = new JPanel(new GridLayout(formatAmount/2,formatAmount/2, 20, 20));
        formatsGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (String format : playerMap.keySet()){
            JButton btn = new JButton(format);
            btn.addActionListener(e -> {
                Player player = playerMap.get(format);
                List<Player> playerHistory = ladder.getPlayerFormatHistory(format, player.getName());
                JPanel playerPanel = playerHistoryPage(playerHistory);
                String panelName = "player-" + player + " History";
                removeDupes(panelName);
                playerPanel.setName(panelName);
                mainPanel.add(playerPanel, panelName);
                switchPage(panelName);
            });
            formatsGrid.add(btn);
        }
        panel.add(formatsGrid,  BorderLayout.CENTER);

        createBackButton("Back to Player History Search",  panel, "playerHistorySearch");

        return panel;
    }

    /**
     * Page to display Player History for a specific format.
     *
     * @param playerHistory A List of player history for a format
     * @return Page panel
     */
    private JPanel playerHistoryPage(List<Player> playerHistory){
        JPanel panel = new JPanel(new BorderLayout());

        String playerName = playerHistory.get(playerHistory.size()-1).getName();

        createTitle("Player History of " + playerName, panel, true);

        String[] columns = {"Elo", "Gxe", "Glicko", "TimeStamp"};
        Object[][] tableData = new Object[playerHistory.size()][columns.length];
        for (int i = 0; i < playerHistory.size(); i++){
            Player player = playerHistory.get(i);
            tableData[i][0] = player.getElo();
            tableData[i][1] = player.getGxe()+"%";
            tableData[i][2] = player.getGlicko();
            tableData[i][3] = player.getTimestamp();
        }

        JTable playersTable = new JTable(tableData, columns);
        playersTable.setRowHeight(22);
        JScrollPane playersScroll = new JScrollPane(playersTable);
        playersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(playersScroll, BorderLayout.CENTER);

        createBackButton("Back to Format Select", panel, "player-" + playerName + "-Formats");

        return panel;
    }

}
