import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Trie {

	private class Node {
		/* Leaves or subtrees of this node.
		 * Label of the leaf/subtree serves as index in the array. */
		private Node leafs[];
		/* Number of leaves/subtrees of this node. */
		private int nOfLeafs;
		/* Label/key of this node */
		private char label;
		/* Depth in the trie of this node. 
		 * The root of the trie has a depth of 0.*/
		private int depth;

		/**
		 * Constructor for an instance of Trie.Node.
		 * @param label label of the new node
		 * @param depth depth in the trie of the new node
		 */
		Node (char label, int depth) {
			leafs = new Node['z' - 'a' + 1];
			for (int i = 0; i < 'z' - 'a'; leafs[i++] = null);
			nOfLeafs = 0;
			this.label = label;
			this.depth = depth;
		}

		int getNOfLeafs() {
			return nOfLeafs;
		}

		/**
		 * Recursive method for inserting a String into the trie.
		 * Creates a new leaf if the first element of the remaining String is not a label of this node's leaves.
		 * Otherwise, it calls this method from the labeled leaf with the next substring.
		 * @param s the remaining String to insert
		 * @return true if there was at least one new leaf or subtree created from the String, false otherwise. 
		 */
		boolean insert(String s) { 
			/* Determine if this node has a leaf with the first element of s as its label. */
			char c = s.charAt(0);
			
			if (leafs[c - 'a'] != null) {
				/* Label was found in this node's leaves. */
				if (s.length() == 1) // Is s the last substring?
					return false;
				/* Traverse down the tree with the next substring. */
				return leafs[c - 'a'].insert(s.substring(1)); 
			} else {
				leafs[c - 'a'] = new Node(c, depth + 1);
				nOfLeafs++;

				/* Keep building the the tree until s is the last substring. */
				if (s.length() > 1)
					leafs[c - 'a'].insert(s.substring(1));

				return true;
			}
		}

		/**
		 * Recursive method for searching the trie for a String.
		 * Searches this subtree for the current String.
		 * @param s the current String to look for
		 * @return true if the String was found, false otherwise
		 */
		boolean find(String s) {
			char c = s.charAt(0);
			
			if (s.length() == 1) 
				return leafs[c - 'a'] != null && leafs[c - 'a'].getNOfLeafs() == 0;
			
			return leafs[c - 'a'].find(s.substring(1));
		}

		public String toString() {
			String s = depth > 0 ? "" + label : ""; //Is this node the root node?
			if (nOfLeafs > 0) {
				if (nOfLeafs == 1 && depth > 0) {
					for (Node n : leafs) {
						if (n != null) {
							s += n;
							break;
						}
					}
				} else 
					for (Node n : leafs) {
						if (n != null) {
							s += "(" + n + ")";
						}
					}
			}
			return s;
		}
	}

	/* Root node of the trie. */
	private Node root;

	private File inputFile, outputFile;
	private PrintWriter out;

	/**
	 * Constructor for an instance of Trie. 
	 * Makes sure that the input file can be read, and that the output file can be created or deleted.
	 * @param infname	filename of the input file
	 * @param outfname	filename of the output file
	 */
	public Trie(String infname, String outfname) {
		inputFile = new File(infname);
		if (!inputFile.canRead()) {
			System.out.println("File " + infname + " could be not found, or cannot be read!");
			System.exit(0);
		}
		outputFile = new File(outfname);
		if (outputFile.exists() && !outputFile.delete()) {
			System.out.println("File " + infname + " could not be deleted!");
			System.exit(0);
		}
		try {
			out = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		root = new Node('\0', 0);
	}

	/**
	 * Attempts to insert the given String into the trie.
	 * Prints the Trie in textual form if it was successful, or an error otherwise, to the output file.
	 * @param s the string to insert into the trie
	 */
	void insert(String s) {
		if (root.insert(s))
			out.println(s + " " + toString());
		else
			out.println(s + " PREFIX");
	}

	/**
	 * Attempts to find the given String in the trie.
	 * Outputs the result to the output file.
	 * @param s the String to look for
	 */
	void find(String s) {
		out.println(s + (root.find(s) ? " YES" : " NO"));
	}

	public String toString() {
		return root.toString();
	}

	/**
	 * Processes the input file.
	 */
	public void readInputFile() {
		Scanner s = null;
		try {
			s = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		/* Inserts the first i strings into the trie */
		for (int i = s.nextInt(); i > 0; i--)
			insert(s.next());
		/* Attempts to find the remaining input strings in the trie */
		while (s.hasNext())
			find(s.next());
		s.close();
		out.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			Trie t = new Trie(args[0], args[1]);
			t.readInputFile();
		} else {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			System.err.println("Invalid number of arguments:\n" + stack[stack.length - 1].getClassName() + " input_filename output_filename");
		}
	}

}
