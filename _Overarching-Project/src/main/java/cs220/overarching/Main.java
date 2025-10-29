package cs220.overarching;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
    Gui gui = new Gui();
    gui.show();
    }
}

// Next to add:
// Maybe a graph of elo over time?
// add format object for nice names
// Use dropdown instead of a bunch of buttons
