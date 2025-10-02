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

    // Helpers
    public void show(){
        frame.setVisible(true);
    }
    private void switchPage(String name){
        layout.show(mainPanel, name);
    }


    // Constructor
    Gui() throws SQLException {
        ladder = new LadderSearcher();

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



        frame.add(mainPanel); // Show
    }

    // Panels

    private JPanel welcomePanel(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Pokemon Showdown Utility");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton button = new JButton("Enter!");
        button.setPreferredSize(new Dimension(100,100));
        button.addActionListener(e -> {switchPage("hub1");});
        panel.add(button, BorderLayout.SOUTH);


        return panel;
    }

    private JPanel hubPanel1(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Hub Page 1");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel optionGrid = new JPanel(new GridLayout(2,2, 20, 20));
        optionGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton top500Button = new JButton("View Top 500 in a format");
        top500Button.addActionListener(e -> {switchPage("top500ChooseFormat");});
        JButton reloadFormatsButton = new JButton("Reload Formats");
        reloadFormatsButton.addActionListener(e -> {switchPage("reloadFormat");});
        JButton searchPlayersButton = new JButton("Search Players");
        searchPlayersButton.addActionListener(e -> {switchPage("searchPlayers");});
        JButton playerHistoryButton = new JButton("Check Player History");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(200, 80);
        for (JButton btn : new JButton[]{top500Button, reloadFormatsButton, searchPlayersButton, playerHistoryButton}) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            optionGrid.add(btn);
        }
        panel.add(optionGrid, BorderLayout.CENTER);

        JButton nextPageButton = new JButton("Next Page");
        nextPageButton.setFont(buttonFont);
        nextPageButton.setPreferredSize(new Dimension(120, 40));
        nextPageButton.addActionListener(e -> {switchPage("hub2");});
        panel.add(nextPageButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel hubPanel2(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Hub Page 2");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> switchPage("hub1"));
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    // Button Pages
    private JPanel chooseTop500FormatPage(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Please Pick a Format");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel formatGrid = new JPanel(new GridLayout(10,10, 10, 10));
        formatGrid.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        for (String format : LadderSearcher.formats){
            JButton btn = new JButton(format);
            btn.addActionListener(e -> {switchPage("top500-" + format);});
            formatGrid.add(btn);
        }

        JButton backButton = new JButton("Back to Hub");
        backButton.addActionListener(e -> {switchPage("hub1");});
        panel.add(backButton, BorderLayout.SOUTH);
        panel.add(formatGrid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel top500Panel(String format) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("Back to Formats");
        backButton.addActionListener(e -> {switchPage("top500ChooseFormat");});
        backButton.setPreferredSize(new Dimension(120, 35));
        panel.add(backButton, BorderLayout.SOUTH);

        JLabel title = new JLabel("Top 500 - " + format);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

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

        return panel;
    }

    private JPanel reloadFormatPage(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Choose a Format to Reload");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

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
        panel.add(formatsGrid, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Hub");
        backButton.addActionListener(e -> {switchPage("hub1");});
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel searchPlayersPage(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Please enter the name of the player you want to search");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.add(title, BorderLayout.NORTH);

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
                for (Component comp : mainPanel.getComponents()) {
                    if (panelName.equals(comp.getName())){
                        mainPanel.remove(comp);
                        break;
                    }
                }
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
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel playerFoundPage(Map<String, Player> playerMap){
        JPanel panel = new JPanel(new BorderLayout());

        List<Map.Entry<String, Player>> playerEntries = new ArrayList<>(playerMap.entrySet());

        JLabel title = new JLabel(playerEntries.getFirst().getValue().getName());
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

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

        JButton backButton = new JButton("Back to Player Search");
        backButton.addActionListener(e -> {switchPage("searchPlayers");});
        panel.add(backButton, BorderLayout.SOUTH);

        return panel;
    }

}
