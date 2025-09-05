import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Project1Driver{
    public static void main(String[] args) throws FileNotFoundException {
        WordSearcher reader = new WordSearcher();
        Scanner input = new Scanner(System.in);
        reader.createReader();
        char[][] lines = reader.readFileContent();
        System.out.println("Enter the name of the word you would like to search for: ");
        String wordToSearch = input.nextLine();
        reader.starSearch(wordToSearch, lines);
    }
}


/**
 *
 *
 *
 */
class WordSearcher {

    // We use all of these in multiple functions so we decided to make them class variables
    private Scanner fileScan;
    private int rowNumber;
    private int colNumber;

    // Creates the file scan variable, defines the number of rows, then reopens the scanner.
    void createReader() throws FileNotFoundException {
        File fileDir = new File("src/input.txt");
        this.fileScan = new Scanner(fileDir);
        // updates the class to know how many lines there are
        while (this.fileScan.hasNextLine()) {
            this.fileScan.nextLine();
            this.rowNumber++;
        }
        this.fileScan.close();
        this.fileScan = new Scanner(fileDir);
    }

    // This was used for debugging, may remove later
    void printFileContent() {
        while (this.fileScan.hasNextLine()) {
            String line = this.fileScan.nextLine();
            System.out.println(line);
        }
    }

    /**
     * Goes through each row of our grid then
     *
     * @return Char[][] Array containing the Horizontal rows of our grid
     */
     char[][] readFileContent() {
        char[][] lines = new char[this.rowNumber][];
        int row = 0;
        while (this.fileScan.hasNextLine()) {
            String line = this.fileScan.nextLine();
            char[] lineArray = new char[line.length()];
            line = line.replaceAll("\\s+", "");
            for (int i = 0; i < line.length(); i++) {lineArray[i] = line.charAt(i);}
            String rowString = new String(lineArray).trim();
            this.colNumber = rowString.length();
            lines[row]=lineArray;
            row++;
        }
        return lines;
    }

    /**
     * Uses each search function so we don't have to in the main function.
     *
     * @param wordToSearch String the user inputs that we use to search
     * @param lines The Array Char[][] That holds the Chars of Characters for our grid
     */
    void starSearch(String wordToSearch, char[][] lines) {
        horiSearch(wordToSearch, lines);
        vertSearch(wordToSearch, lines);
        // diagSearchRight(wordToSearch, lines);
        // diagSearchLeft(wordToSearch, lines);
    }

    /**
     * Goes through each row (lines param), Standardizes it, then searches for the word given by the word param.
     * If word is found, prints to the console that it found the word on X row
     *
     * @param word String the user inputs that we use to search
     * @param lines The Array Char[][] That holds the Chars of Characters for our grid
     */
    void horiSearch(String word, char[][] lines) {
        word = word.trim().toUpperCase();
        for (int i = 0; i < rowNumber; i++) {
            String rowString = new String(lines[i]).trim().toUpperCase();
            String reversedRowString = new StringBuilder(rowString).reverse().toString();
            if (rowString.contains(word)) {System.out.println("Found Horizontally, Row: " + (i+1));} // Horizontal search
            if (reversedRowString.contains(word)) {System.out.println("Found Reversed Horizontally, Row: " + (i+1));} // Reversed horizontal search
        }
    }

    /**
     * Goes through lines param to make an equivalent array for Columns
     * Goes through columns, converts them to a string then compares the word param to the string
     * If word is found, prints to the console that the word was found on X column
     *
     * @param word String the user inputs that we use to search
     * @param lines The Array Char[][] That holds the Chars of Characters for our grid
     */
    void vertSearch(String word, char[][] lines) {
        word = word.trim().toUpperCase();
        char[][] vertLines = new char[this.colNumber][];
        char[] colChars = new char[this.rowNumber];
        for (int j = 0; j < this.colNumber; j++) {
            for (int i = 0; i < this.rowNumber; i++) {colChars[i] = lines[i][j];}
            vertLines[j]=colChars;
            String colString = new String(vertLines[j]).trim().toUpperCase();
            String reversedColString = new StringBuilder(colString).reverse().toString().toUpperCase();
            if (colString.contains(word)) {System.out.println("Found Vertically, Column: " + (j+1));}
            if (reversedColString.contains(word)) {System.out.println("Found Reversed Vertically, Column: " + (j+1));}
        }
    }


    void diagSearchRight(String word, char[][] lines){
        word = word.trim().toUpperCase();
        for(int i = 0; i < this.rowNumber; i++){
            char[] diag = new char[this.rowNumber];
            char letter = lines[i][i];
            System.out.println(letter);
        }
    }

}