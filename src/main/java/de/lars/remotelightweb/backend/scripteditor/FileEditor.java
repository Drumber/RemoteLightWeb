package de.lars.remotelightweb.backend.scripteditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileEditor {

	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
	}
	
	public static void writeStringToFile(String fileName, String text) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(fileName)) {
			out.print(text);
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return True if the file was created, False if the file already exists
	 * @throws IOException
	 */
	public static boolean createFile(String fileName) throws IOException {
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		return file.createNewFile();
	}
	
}
