package cs220.overarching;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    /**
     * Runs Project
     *
     * @throws IOException when having issues grabbing data from website
     * @throws SQLException when having issues inputting or outputting data from the database
     */
    public static void main(String[] args) throws IOException, SQLException {
    Gui gui = new Gui();
    gui.show();
    }
}
