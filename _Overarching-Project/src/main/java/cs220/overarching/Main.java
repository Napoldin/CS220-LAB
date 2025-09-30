package cs220.overarching;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
    LadderSearcher ladder = new LadderSearcher();
    // only load new ladder when asked or when no ladder data is found on startup, otherwise use db data
    if (ladder.getAllTop500() == null) ladder.loadLadder();


    }
}

// Next to add:
// Maybe a graph of elo over time?
// GUI next? How do we want that to work