import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Project1Driver {
    /**
     * @throws FileNotFoundException If input file is not found
     */
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
 * Our main class that we use to search through our file.
 *
 * @author Aiden & Micheal
 * @version 1.0
 */
class WordSearcher {

    /**
     * We use all of these in multiple functions so
     * we decided to make them class variables
     */
	private Scanner fileScan;
	private int rowNumber;
	private int colNumber;

    /**
     * Populates the file scan variable, defines the number of rows, then reopens the
     * scanner so we can search again.
     *
     * @throws FileNotFoundException if the input file is not found.
     */
	void createReader() throws FileNotFoundException {
		File fileDir = new File("input.txt");
		this.fileScan = new Scanner(fileDir);
		// updates the class to know how many lines there are
		while (this.fileScan.hasNextLine()) {
			this.fileScan.nextLine();
			this.rowNumber++;
		}
		this.fileScan.close();
		this.fileScan = new Scanner(fileDir);
	}

	/**
	 * Goes through each row of our grid, strips them of their whitespaces
     * then adds to the "lines" char[][] list to return
	 *
	 * @return Char[][] List containing the Horizontal rows of our grid
	 */
	char[][] readFileContent() {
		char[][] lines = new char[this.rowNumber][];
		int row = 0;
		while (this.fileScan.hasNextLine()) {
			String line = this.fileScan.nextLine();
			char[] lineArray = new char[line.length()];
			line = line.replaceAll("\\s+", "");
			for (int i = 0; i < line.length(); i++) lineArray[i] = line.charAt(i);
			String rowString = new String(lineArray).trim();
			this.colNumber = rowString.length();
			lines[row] = lineArray;
			row++;
		}
		return lines;
	}

	/**
	 * Uses each search function so we don't have to in the main function, reducing lines.
	 *
	 * @param wordToSearch String the user inputs that we use to search
	 * @param lines        The List Char[][] That holds the Chars of Characters for
	 *                     our grid
	 */
	void starSearch(String wordToSearch, char[][] lines) {
		horiSearch(wordToSearch, lines);
		vertSearch(wordToSearch, lines);
		diagSearchRight(wordToSearch, lines);
		diagSearchLeft(wordToSearch, lines);
	}

	/**
	 * Goes through each row (lines param), Standardizes it, then searches for the
	 * word given by the word param. If word is found, prints to the console that it
	 * found the word on X row
	 *
	 * @param word  String the user inputs that we use to search
	 * @param lines The List Char[][] That holds the Chars of Characters for our
	 *              grid
	 */
	void horiSearch(String word, char[][] lines) {
		word = word.trim().toUpperCase();
		for (int i = 0; i < rowNumber; i++) {
			String rowString = new String(lines[i]).trim().toUpperCase();
			String reversedRowString = new StringBuilder(rowString).reverse().toString();
			if (rowString.contains(word)) System.out.println("Found Horizontally, Row: " + (i + 1)); // Horizontal search
			if (reversedRowString.contains(word)) System.out.println("Found Reversed Horizontally, Row: " + (i + 1)); // Reversed horizontal search
		}
	}

	/**
	 * Goes through lines param to make an equivalent List for Columns Goes through
	 * columns, converts them to a string then compares the word param to the string
	 * If word is found, prints to the console that the word was found on X column
	 *
	 * @param word  String the user inputs that we use to search
	 * @param lines The List Char[][] That holds the Chars of Characters for our
	 *              grid
	 */
	void vertSearch(String word, char[][] lines) {
		word = word.trim().toUpperCase();
		char[][] vertLines = new char[this.colNumber][];
		char[] colChars = new char[this.rowNumber];
		for (int j = 0; j < this.colNumber; j++) {
			for (int i = 0; i < this.rowNumber; i++) colChars[i] = lines[i][j];
			vertLines[j] = colChars;
			String colString = new String(vertLines[j]).trim().toUpperCase();
			String reversedColString = new StringBuilder(colString).reverse().toString().toUpperCase();
			if (colString.contains(word)) System.out.println("Found Vertically, Column: " + (j + 1));
			if (reversedColString.contains(word)) System.out.println("Found Reversed Vertically, Column: " + (j + 1));
		}
	}

	/**
	 * This method searches all diagonals that go from the top left to bottom right
	 * It starts at the top row going threw each column then it goes though each row of the first column
	 * It also reverses the string to check if the word is present but backwards
	 *
	 * @param word  String the user inputs that we use to search
	 * @param lines The Array Char[][] That holds the Chars of Characters for our
	 *              grid
	 */
	void diagSearchRight(String word, char[][] lines) {
		word = word.trim().toUpperCase();

		// from top row
		for (int startCol = 0; startCol < colNumber; startCol++) {
			int y = 0;
			int x = startCol;
			int l = 0;
			char[] diagChars = new char[rowNumber];
			while (y < rowNumber && x < colNumber) {
				diagChars[l++] = lines[y][x];
				y++;
				x++;
			}
			String diag = new String(diagChars, 0, l).toUpperCase();

			String reversed = "";
			for (int i = diag.length() - 1; i >= 0; i--)
				reversed += diag.charAt(i);

			if (diag.contains(word)) {
				System.out.println("Found Diagonally Down-Right, in Row 1, Col " + (startCol + 1));
			}
			if (reversed.contains(word)) {
				System.out.println("Found Reversed Diagonally Down-Right, in Row 1, Col " + (startCol + 1));
			}
		}

		// from first column 
		for (int startRow = 1; startRow < rowNumber; startRow++) {
			int y = startRow, x = 0, l = 0;
			char[] diagChars = new char[colNumber];
			while (y < rowNumber && x < colNumber) {
				diagChars[l++] = lines[y][x];
				y++;
				x++;
			}
			String diagString = new String(diagChars, 0, l).toUpperCase();
			String reversedDiagString = new StringBuilder(diagString).reverse().toString().toUpperCase();
			if (diagString.contains(word)) {
				System.out.println("Found Diagonally Down-Right, in Row " + (startRow + 1) + ", Col 1");
			}
			if (reversedDiagString.contains(word)) {
				System.out
						.println("Found Reversed Diagonally Down-Right, in Row " + (startRow + 1) + ", Col 1");
			}
		}
	}

	/**
	 * This method searches all diagonals that go from the top right to bottom left
	 * It starts at the top row going threw each column then it goes though each row of the first column
	 * It also reverses the string to check if the word is present but backwards
	 *
	 * @param word  String the user inputs that we use to search
	 * @param lines The Array Char[][] That holds the Chars of Characters for our
	 *              grid
	 */
	void diagSearchLeft(String word, char[][] lines) {
		word = word.trim().toUpperCase();

		//from first row
		for (int startCol = 0; startCol < colNumber; startCol++) {
			int y = 0;
			int x = startCol;
			int l = 0;
			char[] diagChars = new char[rowNumber];
			while (y < rowNumber && x >= 0) {
				diagChars[l++] = lines[y][x];
				y++;
				x--;
			}
			String diag = new String(diagChars, 0, l).toUpperCase();

			String reversed = "";
			for (int i = diag.length() - 1; i >= 0; i--)
				reversed += diag.charAt(i);

			if (diag.contains(word)) {
				System.out.println("Found Diagonally Down-Left, in Row 1, Col " + (startCol + 1));
			}
			if (reversed.contains(word)) {
				System.out.println("Found Reversed Diagonally Down-Left, in Row 1, Col " + (startCol + 1));
			}
		}
		//from first column
		for (int startRow = 1; startRow < rowNumber; startRow++) {
			int y = startRow, x = colNumber - 1, l = 0;
			char[] diagChars = new char[colNumber];
			while (y < rowNumber && x >= 0) {
				diagChars[l++] = lines[y][x];
				y++;
				x--;
			}
			String diagString = new String(diagChars, 0, l).toUpperCase();
			String reversedDiagString = new StringBuilder(diagString).reverse().toString().toUpperCase();

			if (diagString.contains(word)) {
				System.out.println(
						"Found Diagonally Down-Left, in Row " + (startRow + 1) + ", Col " + colNumber);
			}
			if (reversedDiagString.contains(word)) {
				System.out.println("Found Reversed Diagonally Down-Left, in Row " + (startRow + 1) + ", Col "
						+ colNumber);
			}
		}
	}

}
