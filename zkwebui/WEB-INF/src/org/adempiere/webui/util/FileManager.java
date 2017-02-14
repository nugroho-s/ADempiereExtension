package org.adempiere.webui.util;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;


public class FileManager {
	
	private String file_loc;
	
	public FileManager(String file_loc) {
	
		this.file_loc = file_loc;
	
	}
	
	int readLines() throws IOException {
	
		FileReader file_to_read = new FileReader(file_loc);
		BufferedReader bf = new BufferedReader(file_to_read);
		
		String aLine;
		int numberOfLines = 0;
		
		while ((aLine = bf.readLine()) != null) {
			numberOfLines++;
		}
		
		bf.close();
		
		return numberOfLines;
	
	}
	
	public String[] OpenFile() throws IOException {
		
		FileReader fr = new FileReader(file_loc);
		BufferedReader textReader = new BufferedReader(fr);
		
		int numberOfLines = readLines();
		String[] textData = new String[numberOfLines];
		
		for (int i = 0; i < numberOfLines; i++) {
			textData[i] = textReader.readLine();
		}
		
		textReader.close();
		
		return textData;
		
	}
	
}