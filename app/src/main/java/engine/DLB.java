package engine;

import java.util.*;
import java.io.*;

public class DLB implements Dict {
	private DLBNode root;
	public String prefix = "";
	public char nextChar;

	// method to add a new word to the trie
	public void add(String key) {
		if (key == null) return;

		DLBNode cur = root;
		DLBNode temp;

		// if key is the first word in the table we insert it at root
		if (root == null) {
			root = new DLBNode(key.charAt(0));
			cur = root;

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
						// if this node has a right pointer, we get the
						// rightmost sibling
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
	}

	// method to check if the trie contains a valid word by key (but not valid prefix)
	public boolean contains(String key) {
		// base case 
		if (root == null || key == null) return false;
		DLBNode checkNode = root;
		
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
		
		// calling helper method that finds the last node of some key in the trie
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

	// method to check if the trie contains a valid prefix by key (but not valid word)
	public boolean containsPrefix(String pre) {
		// base case
		if (root == null || pre == "") return false;

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

	// method that returns a value indicating  whether the current prefix is
	// a valid word, a valid prefix, both, or neither
	public int searchByChar(char next) {
		// base case
		if (root == null) return -1;
		nextChar = next;
		prefix = prefix + next;

		// corner case to eliminate double chars
		if (prefix != null && prefix.length() >= 2 && prefix.charAt(prefix.length() - 1) == nextChar && prefix.charAt(prefix.length() - 2) == nextChar) {
         	prefix = prefix.substring(0, prefix.length() - 1);
    	}
		
		boolean isWord = contains(prefix);
		boolean isPrefix = containsPrefix(prefix);

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

	// method to reset searchByChar results
	public void resetByChar() {
		prefix = "";
	}

	// helper method to find a pointer to the last character of some key
	public DLBNode getLastNode(String key) {
		// base case
		if (root == null) {
			return null;
		}

		int index = 0;
		DLBNode cur = root;

		// shortcut for short keys
		if (key.length() < 2) {
			if (root.getLet() == key.charAt(index)) {
				return root;
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

	// method to suggest 5 words for given prefix
	public ArrayList<String> suggest() {
		if (root == null) return new ArrayList<String>();
		char toLookFor = nextChar;

		ArrayList<String> suggestions = new ArrayList<String>();
		int sbcResult = searchByChar(toLookFor);	
		
		// shortcuts depending on searchByChar results
		if (sbcResult == -1) return new ArrayList<String>();
		if (sbcResult == 1) {
			suggestions.add(prefix);
		}
		else if (sbcResult == 0 || sbcResult == 2) {
			// calling the recursive function
			suggest_rec(prefix, getLastNode(prefix), suggestions, false);
		}

		return suggestions;
	}

	// recursive helper function find all plausible words for given prefix
	private void suggest_rec(String pre, DLBNode cur, ArrayList<String> aList, boolean limitless) {
		// base case
		if (pre == null || getLastNode(prefix) == null) return;

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

	// method to print all words in the trie
	public ArrayList<String> traverse() {
		// base case
		if (root == null) return new ArrayList<String>();

		ArrayList<String> aList = new ArrayList<String>();
		DLBNode cur = root;
		String suggestedPre = "";

		// calling the recursive function on every node at the first level
		do {
			suggestedPre += cur.getLet();
			suggest_rec(suggestedPre, cur, aList, true);
			suggestedPre = "";
			cur = cur.getRight();
		} while (cur != null);

		return aList;
	} 

	// method to count valid words in the trie
	public int count() {
		// calling recursive function
		return count_rec(root, 0);
	}

	// recursive helper function that counts valid words
	public int count_rec(DLBNode node, int curResult) {
		// base case
		if (node == null) return curResult;

		// if we find a terminal value, increment counter
		if (node.getLet() == '.') {
			curResult += 1;
			if (node.getRight() == null) {
				return curResult;
			}
		}

		// checking sibling nodes
		if (node.getRight() != null) {
		 	curResult = count_rec(node.getRight(), curResult);
		}

		// checking children nodes
		if (node.getDown() != null) {
			curResult = count_rec(node.getDown(), curResult);
		}			

		return curResult;
	}
}