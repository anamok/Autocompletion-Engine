package engine;

import java.util.*;
import java.io.*;

public class UserHistory extends DLB {
	private DLBNode uRoot;
	public String uPrefix = "";
	public char uNextChar;
	private boolean selectedBefore;
	private int freqHolder;
	public TreeMap<Integer, ArrayList<String>> freqTable = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());

	// method to add a new word to the trie	
	public void add(String key) {
		if (key == null) return;

		DLBNode cur = uRoot;
		DLBNode temp;

		// if key is the first word in the table we insert it at root
		if (uRoot == null) {
			uRoot = new DLBNode(key.charAt(0));
			cur = uRoot;

			for (int i = 1; i < key.length(); i++) {
				cur.setDown(new DLBNode(key.charAt(i)));
				cur = cur.getDown();
			}

			cur.setDown(new DLBNode('.'));
		}
		else {
			int count = 0;
			while (count < key.length()) {
				if (cur.getLet() != key.charAt(count)) {
					if (cur.getRight() != null) {
						// if this node has a right pointer, we get the rightmost sibling
						while (cur.getRight() != null && cur.getLet() != key.charAt(count)) {
							cur = cur.getRight();
						}
						if (cur.getRight() == null && cur.getLet() != key.charAt(count)) {
							// if there's no sibling that matches current key,
							// we create a new sibling and move the current to it
							temp = new DLBNode(key.charAt(count));
							cur.setRight(temp);
							cur = cur.getRight();

							// if the word is complete, we set the terminal value pointer
							if (count == key.length() - 1) {
								cur.setDown(new DLBNode('.'));
							} 
							else {
								// if not, we give the new node a child
								temp = new DLBNode(key.charAt(count + 1));
								cur.setDown(temp);
							}
							cur = cur.getDown();
						} 
						// if there IS a sibling that matches the current key
						else if (cur.getLet() == key.charAt(count)) {
							// if the word is complete, we set the terminal value pointer
							if (count == key.length() - 1) {
								cur.setDown(new DLBNode('.'));
							} else if (cur.getDown() == null) {
								// if not, we give the new node a child
								temp = new DLBNode(key.charAt(count + 1));
								cur.setDown(temp);
							} 
							cur = cur.getDown();
						}
					}
					// if this node doesn't have a valid right pointer
					else if (cur.getRight() == null) { 
						// we create a new sibling and move the current to it
						temp = new DLBNode(key.charAt(count));
						cur.setRight(temp);
						cur = cur.getRight();

						// if the word is complete, we set the terminal value pointer
						if (count == key.length() - 1) {
							cur.setDown(new DLBNode('.'));
						} 
						// if not, we give the new node a child
						else {
							temp = new DLBNode(key.charAt(count + 1));
							cur.setDown(temp);
						}
						cur = cur.getDown();
					}
				} 
				// if the character is already in the trie
				else if (cur.getLet() == key.charAt(count)) {
					// if the word is complete, we set the terminal value pointer
					if (count == key.length() - 1) {
						cur.setDown(new DLBNode('.'));
					} else if (cur.getDown() == null) {
						// set new children for the remaining characters
						temp = new DLBNode(key.charAt(count + 1));
						cur.setDown(temp);
					} 
					cur = cur.getDown();
				}
				count++;
			}
		}
		// calling helper method to record frequency
		populateHistory(key);
	}

	public void populateHistory(String word) {
		ArrayList<String> aList = new ArrayList<String>();
		ArrayList<String> bList = new ArrayList<String>();
		checkSelectedBefore(word);

		// if word is already in the table, we find its key
		// and get the corresponding ArrayList
		if (selectedBefore) {
			aList = freqTable.get(freqHolder);
			// if the Arraylist has more than one word
			// we remove the selected word and
			// we keep the key-value pair in the table
			if (aList.size() > 1) {
				aList.remove(word);
				freqTable.put(freqHolder, aList);
			}
			else {
				freqTable.remove(freqHolder);
			}
			// if our TreeMap contains freq+1, we get the
			// corresponding ArrayList and insert the new
			// word in it and record the new key-value pair
			if (freqTable.containsKey(freqHolder + 1)) {
				bList = freqTable.get(freqHolder + 1);
				bList.add(word);
				Collections.sort(bList);
				freqTable.put(freqHolder + 1, bList);
			} 
			// otherwise, we insert the word into an
			// empty ArrayList and record the new key-value
			// pair in the TreeMap
			else {
				bList.add(word);
				Collections.sort(bList);
				freqTable.put(freqHolder + 1, bList);
			}
		}
		// if the word is not yet in the table, we add it to
		// the ArrayList that corresponds to frequency 1
		else {
			if (freqTable.containsKey(1)){
				aList = freqTable.get(1);
			}
			aList.add(word);
			Collections.sort(aList);
			freqTable.put(1, aList);
		}
	}

	// helper method to check if the trie is already in the 
	// frequency table and if so, record it's frequency
	private void checkSelectedBefore (String cur) {
		selectedBefore = false;
		freqHolder = -1;
		for (Map.Entry<Integer, ArrayList<String>> entry : freqTable.entrySet()) {
			if (entry.getValue().contains(cur)) {
				selectedBefore = true;
				freqHolder = entry.getKey();
			}
		}
	}

	// ALTERED DLB METHOD to find a pointer to the last character of some key
	public DLBNode getLastNode(String key) {
		if (uRoot == null || key == "") {
			return null;
		}

		int index = 0;
		DLBNode cur = uRoot;
		
		// shortcut for short keys
		if (key.length() < 2) {
			if (uRoot.getLet() == key.charAt(index)) {
				return uRoot;
			}
			while (cur.getRight() != null) {
				if (cur.getLet() == key.charAt(index)) {
					return cur;
				}
				cur = cur.getRight();
			}
			return null;
		}
		else {
			// traverse the tree to find last node
			while (index < key.length()) {
				if (cur.getLet() != key.charAt(index)) {
					while (cur.getLet() != key.charAt(index)) {
						if (cur.getRight() == null) {
							return null;
						}
						cur = cur.getRight();
					}
					if (index == key.length() - 1) {
						return cur;
					}
					cur = cur.getDown();
				} 
				else if (cur.getLet() == key.charAt(index)) {
					if (index == key.length() - 1) { //
						return cur;
					} 
					else if (cur.getDown() == null) {
						return null;
					}
					cur = cur.getDown();
				}
				index++;
			}
		}

		return cur;
	}

	// ALTERED DLB METHOD to check if the trie contains a valid word (but not valid prefix)
	public boolean contains(String key) {
		// base case 
		if (uRoot == null || key == null) return false;
		DLBNode checkNode = uRoot;
		
		// shortcut for short keys that are not in the trie
		if (key.length() < 2) {
			boolean flag = false;
			while (checkNode.getRight() != null) {
				if (checkNode.getLet() == key.charAt(0) && checkNode.getDown().getLet() == '.') {
					flag = true;
				}
				checkNode = checkNode.getRight();
			}
			if (!flag) {
				return false;
			}
		} 
		
		// calling helper method that finds the last node of some key
		checkNode = getLastNode(key);

		if (checkNode != null && checkNode.getDown() != null ) {
			// if the current's child is a terminal character, return true
			if (checkNode.getDown().getLet() == '.') {
				return true;
			} 
			else {
				// otherwise, check all current's child siblings
				// if a terminal character is found, return true
				checkNode = checkNode.getDown();
				if (checkNode.getRight() != null) {
					while (checkNode.getRight() != null) {
						if (checkNode.getLet() == '.') {
							return true;
						}
						checkNode = checkNode.getRight();
					}
				}
				
			}
		}
		return false;
	}

	// ALTERED DLB METHOD to check if the trie contains a valid prefix by key (but not valid word)
	public boolean containsPrefix(String pre) {
		// base case
		if (uRoot == null || pre == "") return false;

		// calling helper method that finds the last node of some key in the trie
		DLBNode checkNode = getLastNode(pre);
		if (checkNode != null) {
			// if the current's child is not a terminal character or
			// has any right pointers, return true
			if (checkNode.getDown().getLet() != '.' || checkNode.getDown().getRight() != null)
				return true;
		}

		return false;
	}

	// ALTERED DLB METHOD that returns a value indicating  whether the current prefix is
	// a valid word, a valid prefix, both, or neither
	public int searchByChar(char next) {
		// base case
		if (uRoot == null){
			return -1;
		}
		uNextChar = next;
		uPrefix = uPrefix + next;

		// corner case to eliminate double chars
		if (uPrefix != null && uPrefix.length() >= 2 && uPrefix.charAt(uPrefix.length() - 1) == next && uPrefix.charAt(uPrefix.length() - 2) == next) {
         	uPrefix = uPrefix.substring(0, uPrefix.length() - 1);
    	}

		boolean isWord = contains(uPrefix);
		boolean isPrefix = containsPrefix(uPrefix);
		
		if (isWord && isPrefix) {
			return 2;
		}
		else if (isWord) {
			return 1;
		}
		else if (isPrefix) {
			return 0;
		}

		return -1;
	}

	// ALTERED DLB METHOD to reset searchByChar results
	public void resetByChar() {
		uPrefix = "";
		uNextChar = '\0';
	}

	// ALTERED DLB METHOD to suggest 5 words for given prefix
	public ArrayList<String> suggest() {
		// base case
		if (uRoot == null) return new ArrayList<String>(); 

		char toLookFor = uNextChar;
		ArrayList<String> suggestions = new ArrayList<String>();
   		int sbcResult = searchByChar(toLookFor);

		// shortcuts depending on searchByChar results
		if (sbcResult == -1) return new ArrayList<String>();
		if (sbcResult == 1) {
			suggestions.add(uPrefix);
		}
		else if (sbcResult == 0 || sbcResult == 2) {
			// calling the recursive function
			suggest_rec(uPrefix, getLastNode(uPrefix), suggestions, true);
		}

		// adjusting the suggested arraylist for frequency
		ArrayList<String> result = new ArrayList<String>();
		int count = 0;
		for (Map.Entry<Integer, ArrayList<String>> entry : freqTable.entrySet()) {
			for (String word : suggestions) {
				if (entry.getValue().contains(word)) {
					if (count < 5) {
						result.add(word);
						count++;
					}
				}
			}
		}
		//Collections.sort(result);
		
		return result;
	}

	// ALTERED DLB recursive helper function find all plausible words for given prefix
	private void suggest_rec(String pre, DLBNode cur, ArrayList<String> aList, boolean limitless) {
		// base case
		if (pre == null || getLastNode(uPrefix) == null) return;

		// condition to allow code reuse
		if (!limitless) {
			if (aList.size() >= 5) return;
		}

		// if we get to a terminal node, ignore it
		if (cur != null) {
			if (cur.getLet() == '.') {
				return;
			}
		}

		// valid word condition. if satisfied, we add the word to list
		if (cur.getDown().getLet() == '.') {
			aList.add(pre);			
		}

		// checking children nodes
		if (cur.getDown() != null) {
			cur = cur.getDown();
			suggest_rec(pre += cur.getLet(), cur, aList, limitless);
		} 
		else {
			return;
		}

		// checking sibling nodes
		if (cur.getRight() != null) {
			while (cur.getRight() != null) {
				cur = cur.getRight();
				pre = pre.substring(0, pre.length() - 1);
				suggest_rec(pre += cur.getLet(), cur, aList, limitless);
			}
		}
		else {
			return;
		}
	}

	// ALTERED DLB METHOD to count valid words in the trie
	public int count() {
		// calling recursive function
		return count_rec(uRoot, 0);
	}
}