package fr.jetoile.ipod.backuper;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class IPodManager {
	final static private Logger LOGGER = Logger.getLogger(IPodManager.class); 
	final static private String IPOD_CONTROL = "iPod_Control";
	final static private String MUSIC = "Music";
	 
	private File iPodRoot = null;
	
	public IPodManager(String iPodRootPath) {
		this.iPodRoot = new File(iPodRootPath + File.separator + IPOD_CONTROL + File.separator + MUSIC);
//		this.iPodRoot = new File(iPodRootPath);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.log(Level.DEBUG, "is loading: " + this.iPodRoot.getAbsolutePath());
		}
		
		if (!this.iPodRoot.exists()) {
			LOGGER.log(Level.ERROR, "unable to open: " + this.iPodRoot.getAbsolutePath());
			System.exit(-1);
		}
		
	}
	
	public File getIPodRoot() {
		return iPodRoot;
	}
	
	public static List<File> getMp3Leaf(File file) {
		List<File> result = new ArrayList<File>(); 
		File[] filesTab = file.listFiles(new Mp3FileFilter());
		List<File> files = Arrays.asList(filesTab);
		File file2 = null;
		for (int i = 0; i < files.size(); i++) {
			file2 = files.get(i);
			if (file2.isDirectory()) {
				List<File> childFiles = getMp3Leaf(file2);
				result.addAll(childFiles);
			} else {
				result.add(file2);
			}
		}
		return result;
	}
}

class Mp3FileFilter implements FileFilter {

	public boolean accept(File f) {
		return f.getName().endsWith(".mp3") || f.getName().endsWith(".MP3") || f.isDirectory();
	}
}