package com.ipod.backuper;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Launcher {
	final static private Logger LOGGER = Logger .getLogger(Launcher.class);
	
	
	public static void main(String[] args) throws IOException {
		System.out.println("give iPod path...");
		Scanner in = new Scanner(System.in);
		String iPodPath = in.nextLine(); 
		System.out.println("give the File System path to create...");
		String fileSystemPath = in.nextLine();
		System.out.println("need a prefered artist? If yes, enter his name... (N)");
		String artistChoice = in.nextLine();

		//create a log file
		File logFile = new File("./log.txt");
		logFile.createNewFile();
		PrintWriter fos = new PrintWriter(logFile); 
		
		IPodManager iPodManager = new IPodManager(iPodPath);
		FileSystemManager fileSystemManager = new FileSystemManager(fileSystemPath);
		
		List<File> files = IPodManager.getMp3Leaf(iPodManager.getIPodRoot());
		for (File file : files) {
			try {
				if ("N".equalsIgnoreCase(artistChoice) || artistChoice.length() == 0) {
					FileSystemManager.write(file);
				} else {
					FileSystemManager.write(file, artistChoice);
				}
			} catch (Exception e) {
				LOGGER.log(Level.ERROR, e);
				e.printStackTrace(fos);
				fos.flush();
				continue;
			}
		}	
		fos.close();
	}

	
}
