package com.ipod.backuper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.ID3Tag;
import org.cmc.music.myid3.MyID3;

import com.ipod.backuper.exception.FileSystemCopyException;

public class FileSystemManager {
	final static private Logger LOGGER = Logger .getLogger(FileSystemManager.class);
	final static private String BADFILE_REP = "unsuccess";

	static private File DESTPATH = null;
	static private File DESTPATHBADFILE = null;

	public FileSystemManager(String destPath) {
		FileSystemManager.DESTPATH = new File(destPath);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.log(Level.DEBUG, "is using: " + FileSystemManager.DESTPATH.getAbsolutePath());
		}

		if (!FileSystemManager.DESTPATH.exists()) {
			LOGGER.log(Level.DEBUG, "is creating " + FileSystemManager.DESTPATH.getAbsolutePath());
			boolean res = FileSystemManager.DESTPATH.mkdirs();
			if (!res) {
				LOGGER.log(Level.ERROR, "unable to create: " + FileSystemManager.DESTPATH.getAbsolutePath());
				System.exit(-1);
			}
		}
		
		FileSystemManager.DESTPATHBADFILE = new File(destPath + File.separator + BADFILE_REP);
	}

	public File getDestPath() {
		return DESTPATH;
	}

	public static void write(File source) throws FileSystemCopyException {
		write(source, "");
	}
	
	public static void write(File source, String artistChoice) throws FileSystemCopyException {
		
		String artist = "";
		String album = "";
		String song = "";
		String trackNumber = "";

		try {
			MusicMetadataSet src_set = new MyID3().read(source); // read metadata
			ID3Tag id3Raw = src_set.id3v2Raw;

			if (id3Raw == null) {
				id3Raw = src_set.id3v1Raw;
			}
			IMusicMetadata metadata = id3Raw.values;	

			artist = metadata.getArtist().trim();
			album = metadata.getAlbum().trim();
			song = metadata.getSongTitle().trim(); 
			trackNumber = metadata.getTrackNumberNumeric().toString().trim();
			
			// si la valeur est vide ou null, on tente avec id3tag_v2
			id3Raw = src_set.id3v2Raw; 
			if (id3Raw != null) {
				if (artist.length() == 0) { 
					artist = id3Raw.values.getArtist().trim(); 
				}
				if (album.length() == 0) { 
					album = id3Raw.values.getAlbum().trim(); 
				}	
				if (song.length() == 0) { 
					song = id3Raw.values.getSongTitle().trim(); 
				}	
				if (trackNumber.length() == 0) { 
					trackNumber = id3Raw.values.getTrackNumberNumeric().toString().trim(); 
				}	
			}
			
		} catch (Exception e) {
			FileSystemManager.copyBadFile(source);
			throw new FileSystemCopyException("unable to get MP3File.getID3Tag: " + source.getAbsolutePath() + ": " + e.getMessage());
		}

		if (artistChoice.equalsIgnoreCase(artist) || "".equals(artistChoice)) {
			if ("".equals(artist) || "".equals(album)) {
				FileSystemManager.copyBadFile(source);
				throw new FileSystemCopyException("invalid artist or album into MP3File.getID3Tag: " + source.getAbsolutePath());
			}

			if (trackNumber.length() == 1) {
				trackNumber = "0" + trackNumber;
			}
			
			FileSystemManager.normalizeString(new String[] {artist});
			FileSystemManager.normalizeString(new String[] {album});
			FileSystemManager.normalizeString(new String[] {song});
			
			File childFile = new File(FileSystemManager.DESTPATH.getAbsolutePath() + File.separator + artist + " - " + album);
			if (!childFile.exists()) {
				if (!childFile.mkdirs()) {
					FileSystemManager.copyBadFile(source);
					throw new FileSystemCopyException("unable to create dir: " + childFile.getAbsolutePath());
				}
			}

			File mp3 = new File(childFile.getAbsolutePath() + File.separator + trackNumber + " - " + song + ".mp3");
			try {
				if (!mp3.createNewFile()) {
					FileSystemManager.copyBadFile(source);
					throw new FileSystemCopyException("unable to create file: " + mp3.getAbsolutePath());
				}
			} catch (IOException e) {
				FileSystemManager.copyBadFile(source);
				throw new FileSystemCopyException("unable to create file: " + mp3.getAbsolutePath());
			}
			FileSystemManager.copyFile(source, mp3);
		}
	}

	private static void copyBadFile(File source) throws FileSystemCopyException {
		if (!FileSystemManager.DESTPATHBADFILE.exists()) {
			FileSystemManager.DESTPATHBADFILE.mkdirs();
		}
		
		File badFile = new File(FileSystemManager.DESTPATHBADFILE + File.separator + source.getName());
		try {
			badFile.createNewFile();
		} catch (IOException e) {
			throw new FileSystemCopyException("unable to create bad file: " + badFile.getAbsolutePath());
		}
		
		copyFile(source, badFile);
	}
	
	private static void copyFile(File source, File mp3)	throws FileSystemCopyException {
		try {
			FileInputStream fis = new FileInputStream(source);
			FileOutputStream fos = new FileOutputStream(mp3);
			try {
				byte[] buf = new byte[1024];
				int i = 0;
				while ((i = fis.read(buf)) != -1) {
					fos.write(buf, 0, i);
				}
			} catch (Exception e) {
				throw new FileSystemCopyException("unable to copy file: " + source.getAbsolutePath() + " to: " + mp3.getAbsolutePath());
			} finally {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			}
		} catch (IOException e) {
			throw new FileSystemCopyException("unable to copy file: " + source.getAbsolutePath() + " to: " + mp3.getAbsolutePath());
		}
	}
	
	private static void normalizeString(String[] value) {
		if (value[0].indexOf("?") != -1) {
			value[0] = value[0].replaceAll("\\?", " ");
		}
		if (value[0].indexOf(".") != -1) {
			value[0] = value[0].replaceAll("\\.", " ");
		}
		if (value[0].indexOf("&") != -1) {
			value[0] = value[0].replaceAll("\\&", " ");
		}
		if (value[0].indexOf("'") != -1) {
			value[0] = value[0].replaceAll("\\'", " ");
		}
		if (value[0].indexOf("/") != -1) {
			value[0] = value[0].replaceAll("\\/", "-");
		}
		if (value[0].indexOf("\\") != -1) {
			value[0] = value[0].replaceAll("\\\\", "-");
		}
		if (value[0].indexOf("(") != -1) {
			value[0] = value[0].replaceAll("\\(", "");
		}
		if (value[0].indexOf(")") != -1) {
			value[0] = value[0].replaceAll("\\)", "");
		}
		if (value[0].indexOf(":") != -1) {
			value[0] = value[0].replaceAll("\\:", " ");
		}
	}
}
