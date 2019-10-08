package com.csg.snake.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Storage {
	
	// Saves to be used later to implement high scores

	static String path = System.getProperty("user.home") + File.pathSeparator + "Snake" + File.pathSeparator;
	static String storageFile = path + "save.cfg";

	/**
	 * setString - Associates a key String with a value String
	 * @param score
	 * @throws IOException
	 */
	public static void set(String key, Object value) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile));
		if(getString("key") != null)
			removeLineByKey(key + ": ");

		writer.write(key + ": " + value);

		writer.close();
	}

	/**
	 * getString - Gets the value String associated with a given key String
	 * @param key
	 * @return the value String associated with the key
	 */
	public static String getString(String key) {
		Scanner scanner = new Scanner(storageFile);

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.startsWith(key + ":")) { 
				scanner.close();
				return line.replace(key + ":", "").trim();
			}
		}

		scanner.close();
		return null;
	}
	
	/**
	 * getString - Gets the value int associated with a given key String
	 * @param key
	 * @return the value int associated with the key
	 */
	public static int getInt(String key) {
		return Integer.parseInt(getString(key));
	}

	/**
	 * getLine - Finds which line a key is located on in a file
	 * @param key
	 * @return the line the key was found on, if no key was found returns 0
	 */
	public static int getLine(String key) {
		Scanner scanner = new Scanner(storageFile);

		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.startsWith(key + ": ")) { 
				scanner.close();
				return lineNumber;
			}
		}

		scanner.close();
		return 0;
	}

	/**
	 * removeLineByBeginning - Removes all lines from a file that begin with a specific key
	 * @param key
	 * @return whether or not a line was removed
	 * @throws IOException 
	 */
	public static boolean removeLineByKey(String key) throws IOException {
		boolean success = false;
		
		File temp = new File("temp.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
		Scanner scanner = new Scanner(storageFile);

		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();

			if(!line.startsWith(key + ": "))
				writer.write(line + System.getProperty("line.separator"));
			else
				success = true;
		}

		writer.close();
		scanner.close();
		return success;
	}

}