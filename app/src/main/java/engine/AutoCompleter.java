package engine;

import java.util.*;
import java.io.*;

public class AutoCompleter implements AutoComplete_Inter {
	DLB dict = new DLB();
	UserHistory uHist = new UserHistory();
	String trackPrefix = "";
	
	// constructor for two files
	public AutoCompleter(String file1, String file2) {
		try {
			File file = new File(file1);
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				dict.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("dictionary.txt not found.");
		}

		try {
			File histFile = new File(file2);
			Scanner sc = new Scanner(histFile);
			while (sc.hasNextLine()) {
				uHist.add(sc.nextLine());
			}
			sc.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("FILE NOT FOUND!");
		}
	}

	// constructor for one file
	public AutoCompleter(String onlyFile) {
		try {
			File file = new File(onlyFile);
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				dict.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("dictionary.txt not found.");
		}

		try {
			// saving the file in the same directory as the dictionary
			File histFile = new File("src/main/resources/uhist_state.p2");
			histFile.createNewFile();

		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	// method that produces 5 suggestions based on the current word the user has
	// entered. pulles suggestions from the user history first, then from the
	// dictionary
	public ArrayList<String> nextChar(char next) {
		uHist.uNextChar = next;
		dict.nextChar = next;
		int counter = 0;
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> userSugs = uHist.suggest();

		// if there are any UserHistory suggestions, put them
		// in the result list first
		if (userSugs.size() != 0) {
			while (counter < 5 && counter < userSugs.size()) {
				for (String uWord : userSugs) {
					result.add(uWord);
					counter++;
				}
			}
		}

		ArrayList<String> dictSugs = dict.suggest();
		
		// fill the rest of the result list with DLB suggestions
		if (dictSugs.size() != 0 && counter < 5) {
			for (String dWord : dictSugs) {
				if (counter < 5 && !result.contains(dWord)) {
					result.add(dWord);
					counter++;
				}
			}
		}

		return result;
	}

	// method that records the word the user selected. 
	public void finishWord(String cur) {
		// record the word in the frequency table
		uHist.add(cur);
		// reset both DLB and UserHistory objects
		dict.resetByChar();
		uHist.resetByChar();	
	}

	// saves state of the user history to a file
	public void saveUserHistory(String fname) {
		try {
			FileWriter fw = new FileWriter(fname, false);
			BufferedWriter bw = new BufferedWriter(fw);
			TreeMap<Integer, ArrayList<String>> table = uHist.freqTable;
			for (Map.Entry<Integer, ArrayList<String>> entry : table.entrySet()) {
				ArrayList<String> aList = entry.getValue();
				for (String word : aList) {
					for (int i = 0; i < entry.getKey(); i++) {
						bw.write(word);
						bw.newLine();
					}
				}
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}