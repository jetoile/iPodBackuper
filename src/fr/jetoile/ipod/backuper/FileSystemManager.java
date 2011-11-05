package fr.jetoile.ipod.backuper;

import fr.jetoile.ipod.backuper.exception.FileSystemCopyException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSystemManager {
    static private FileSystemManager instance = null;

    final static private Logger LOGGER = Logger.getLogger(FileSystemManager.class);
    final static private String BADFILE_REP = "unsuccess";

    static private File DESTPATH = null;
    static private File DESTPATHBADFILE = null;

    public static FileSystemManager getInstance(String destPath) {
        if (instance == null) {
            instance = new FileSystemManager(destPath);
        }
        return instance;
    }

    private FileSystemManager(String destPath) {
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

    public void write(File source) throws FileSystemCopyException {
        write(source, "");
    }

    public void write(File source, String artistChoice) throws FileSystemCopyException {
        String artist = "";
        String album = "";
        String song = "";
        String trackNumber = "";

        try {
            MusicMetadataSet src_set = new MyID3().read(source); // read metadata

            artist = Mp3MetaDataUtils.getArtistMetaData(src_set);
            album = Mp3MetaDataUtils.getAlbumMetaData(src_set);
            song = Mp3MetaDataUtils.getSongMetaData(src_set);
            trackNumber = Mp3MetaDataUtils.getTrackNumberMetaData(src_set);

        } catch (Exception e) {
            copyBadFile(source);
            throw new FileSystemCopyException("unable to get MP3File.getID3Tag: " + source.getAbsolutePath() + ": " + e.getMessage());
        }

        if (filterOnArtistDoesNotExistOrArtistMatch(artistChoice, artist)) {
            checkLegalValueForArtistAndAlbum(source, artist, album);

            File childFile = createDestinationDirectoryIfNecessary(source, artist, album);

            createAndCopyFile(source, song, trackNumber, childFile);
        }
    }

    private File createDestinationDirectoryIfNecessary(File source, String artist, String album) throws FileSystemCopyException {
        File childFile = new File(FileSystemManager.DESTPATH.getAbsolutePath() + File.separator + artist + " - " + album);
        if (!childFile.exists()) {
            if (!childFile.mkdirs()) {
                copyBadFile(source);
                throw new FileSystemCopyException("unable to create dir: " + childFile.getAbsolutePath());
            }
        }
        return childFile;
    }

    private void createAndCopyFile(File source, String song, String trackNumber, File childFile) throws FileSystemCopyException {
        File mp3 = new File(childFile.getAbsolutePath() + File.separator + trackNumber + " - " + song + ".mp3");
        copyFile(source, mp3);
    }

    private boolean filterOnArtistDoesNotExistOrArtistMatch(String artistChoice, String artist) {
        return artistChoice.equalsIgnoreCase(artist) || "".equals(artistChoice);
    }


    private void copyFile(File source, File mp3) throws FileSystemCopyException {
        try {
            if (!mp3.createNewFile()) {
                copyBadFile(source);
                throw new FileSystemCopyException("unable to create file: " + mp3.getAbsolutePath());
            }
        } catch (IOException e) {
            copyBadFile(source);
            throw new FileSystemCopyException("unable to create file: " + mp3.getAbsolutePath());
        }
        copyFileContent(source, mp3);
    }

    private void checkLegalValueForArtistAndAlbum(File source, String artist, String album) throws FileSystemCopyException {
        if ("".equals(artist) || "".equals(album)) {
            copyBadFile(source);
            throw new FileSystemCopyException("invalid artist or album into MP3File.getID3Tag: " + source.getAbsolutePath());
        }
    }

    private void copyBadFile(File source) throws FileSystemCopyException {
        if (!FileSystemManager.DESTPATHBADFILE.exists()) {
            FileSystemManager.DESTPATHBADFILE.mkdirs();
        }

        File badFile = new File(FileSystemManager.DESTPATHBADFILE + File.separator + source.getName());
        try {
            badFile.createNewFile();
        } catch (IOException e) {
            throw new FileSystemCopyException("unable to create bad file: " + badFile.getAbsolutePath());
        }

        copyFileContent(source, badFile);
    }

    private void copyFileContent(File source, File mp3) throws FileSystemCopyException {
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
}
