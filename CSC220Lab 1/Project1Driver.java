import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

public class Project1Driver{
    public static void main(String[] args) throws FileNotFoundException {
        FileReader reader = new FileReader();
        Scanner input = new Scanner(System.in);
        reader.createReader();
        // reader.printFileContent();
        char[][] lines = reader.readFileContent();
        System.out.println("Enter the name of the word you would like to search for: ");
        String wordToSearch = input.nextLine();
        //System.out.println(lines[1][2]);
        //reader.wordExists(wordToSearch, lines);
        // reader.vertSearch(wordToSearch, lines);
        // System.out.println(lines[0][0]);
        reader.diagSearchRight(wordToSearch, lines);
        input.close();
    }
}

class FileReader{

    private Scanner fileScan;
    private int rowNumber;
    private int colNumber;

    void createReader() throws FileNotFoundException {
        File fileDir = new File("input");
        this.fileScan = new Scanner(fileDir);
        // updates the class to know how many lines there are
        while (this.fileScan.hasNextLine()) {
            this.fileScan.nextLine();
            this.rowNumber++;
        }
        this.fileScan.close();
        this.fileScan = new Scanner(fileDir);
    }
    void printFileContent() {
        while (this.fileScan.hasNextLine()) {
            String line = this.fileScan.nextLine();
            System.out.println(line);
        }
    }
    char[][] readFileContent() {
        char[][] lines = new char[this.rowNumber][];
        int row = 0;
        while (this.fileScan.hasNextLine()) {
            String line = this.fileScan.nextLine();
            char[] lineArray = new char[line.length()];
            line = line.replaceAll("\\s+", "");
            for (int i = 0; i < line.length(); i++) {
                    lineArray[i] = line.charAt(i);
            }
            String rowString = new String(lineArray).trim();
            this.colNumber = rowString.length();
            lines[row]=lineArray;
            row++;
        }
        return lines;
    }
    // get a line in each direction (* pattern) and make an array out of it then run contains on it to see
    // pretty much how I solve word searches normally
    void horiSearch(String word, char[][] lines) {
        word = word.trim().toUpperCase();
        for (int i = 0; i < rowNumber; i++) {
            String rowString = new String(lines[i]).trim();
            String reversedRowString = new StringBuilder(rowString).reverse().toString();
            if (rowString.contains(word)) {System.out.println("Found, Row: " + (i+1));} // Horizontal search
            if (reversedRowString.contains(word)) {System.out.println("Found, Row: " + (i+1));} // Reversed horizontal search
        }
    }
    void vertSearch(String word, char[][] lines) {
        word = word.trim().toUpperCase();
        char[][] vertLines = new char[this.colNumber][];
        char[] colChars = new char[this.rowNumber];
        for (int j = 0; j < this.colNumber; j++) {
            for (int i = 0; i < this.rowNumber; i++) {
                colChars[i] = lines[i][j];
                // System.out.println(colChars[i]);
            }
            vertLines[j]=colChars;
            // System.out.println(vertLines[j]);
            String colString = new String(vertLines[j]).trim();
            System.out.println(colString);
            String reversedColString = new StringBuilder(colString).reverse().toString();
            if (colString.contains(word)) {System.out.println("Found, Column: " + (j+1));}
            if (reversedColString.contains(word)) {System.out.println("Found, Column: " + (j+1));}
        }


    }

    void diagSearchRight(String word, char[][] lines){
        word = word.trim().toUpperCase();
        int biggerNum;
        if(this.rowNumber > this.colNumber) {
        	biggerNum = this.rowNumber;
        }
        else {
        	biggerNum = this.colNumber;
        }
        char[][] diag = new char[biggerNum*2][];
        char [] diagLetters = new char[biggerNum*2];
        
        
        // goes from left to right bottem left to top right
        for (int y = this.colNumber; y >= 0; y--) {
        	int i=0;
            for (int x = this.rowNumber-1; x >= 0; x--) {
            	i++;			
            	char tempLetter = lines[x][y+i];
                diagLetters[x] = tempLetter;
                // System.out.println(colChars[i]);
                
            }
            diag[y]=diagLetters;
            
            String diagString = new String(diag[y]).trim();
            System.out.println(diagString);
            String reversedDiagString = new StringBuilder(diagString).reverse().toString();
            if (diagString.contains(word)) {
            	System.out.println("Found, Diagnaly Up Right, Starting Letter in Column: " + (y+2));}
            if (reversedDiagString.contains(word)) {
            	System.out.println("Reversed | Found, Diagnaly Up Right, Ending Letter in Column: " + (y+2));}
        }
            
        /*Code below goes along the top row, left to right going diagonaly down right*/
        for (int y = 0; y < this.colNumber; y++) {
                for (int x = 0; x < this.rowNumber; x++) {
                	char tempLetter = lines[x][x+y];
                    diagLetters[x] = tempLetter;
                    // System.out.println(colChaurs[i]);
                }
                
                diag[y]=diagLetters;
                
                String diagString = new String(diag[y]).trim();
                System.out.println(diagString);
                String reversedDiagString = new StringBuilder(diagString).reverse().toString();
                if (diagString.contains(word)) {
                	System.out.println("Found, Diagnaly Down Right, Starting Letter in Column: " + (y+1));}
                if (reversedDiagString.contains(word)) {
                	System.out.println("Reversed | Found, Diagnaly Down Right, Ending Letter in Column: " + (y+1));}
            //System.out.println(diagLetters);
            }
    }
}