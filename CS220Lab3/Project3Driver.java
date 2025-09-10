import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;

public class Project3Driver {
	public static void main(String[] args) throws FileNotFoundException {
		Project3Driver driver = new Project3Driver();
		File fileName=null;
		Scanner scan = new Scanner(System.in);
		System.out.println("Please Input the .txt File Name");
		String fileDir = scan.nextLine();
		if(driver.FileLocator(fileDir)) {
			fileName = new File(fileDir);
		}
		else {
			System.out.println("Unfortunatly that file does not exist please restart and try again");
			System.exit(0);
		}
		
		System.out.println("File Found!");
		System.out.println("Would you like to Read or Write?");
		driver.Choice(fileName);
	}
	
	private Boolean FileLocator(String dir) {
		File testFile = new File(dir);
		if(testFile.exists()) {
			return true;
		}
		else
			return false;
	}
	
	private void Choice(File fileName) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);
		
		while(true) {
			String input = scan.nextLine();
			input = input.toLowerCase();
			
			if(input.equals("read")) {
				Read(fileName);
				//System.out.println("r");	*Debug*
			}
			else if(input.equals("write")){
				Write(fileName);
				//System.out.println("w");	*Debug*
			}
			else if(input.equals("q")) {
				System.out.println("Exiting Program");
				System.exit(0);
			}
			else
				System.out.println("Sorry I didnt quite catch that please put 'Read' or 'Write' or if you would like to quit 'q'");
			
		}
	}
	
	private void Read(File name) throws FileNotFoundException {
		Scanner scan = new Scanner(name);
		while(scan.hasNextLine()) {
			String text = scan.nextLine();
			System.out.println(text);
		}
	}
	
	private void Write(File fileName) throws FileNotFoundException {
		
	}
}
