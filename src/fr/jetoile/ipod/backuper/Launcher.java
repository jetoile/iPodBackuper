package fr.jetoile.ipod.backuper;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Launcher {
    final static private Logger LOGGER = Logger.getLogger(Launcher.class);


    private static boolean checker(IPodManager iPodManager, FileSystemManager fileSystemManager, String artistChoice) {
        System.out.printf("You are going to copy files in: " + fileSystemManager.getDestPath());
        System.out.printf("You are going to copy files from: " + iPodManager.getIPodRoot());
        if (!"N".equalsIgnoreCase(artistChoice)) {
            System.out.printf("You are filtering with artist: " + artistChoice);
        }

        System.out.printf("Sure you want to continue ? (Y/N)" );
        Scanner in = new Scanner(System.in);
        String response = in.nextLine();
        return "Y".equalsIgnoreCase(response);

    }

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

        FileSystemManager fileSystemManager = FileSystemManager.getInstance(fileSystemPath);

        if (!checker(iPodManager, fileSystemManager, artistChoice)) {
            System.exit(0);
        }

        List<File> files = IPodManager.getMp3Leaf(iPodManager.getIPodRoot());
        if ("N".equalsIgnoreCase(artistChoice) || artistChoice.length() == 0) {
            artistChoice = "";
        }

        for (File file : files) {
            try {
//                if ("N".equalsIgnoreCase(artistChoice) || artistChoice.length() == 0) {
//                    fileSystemManager.write(file);
//                } else {
                    fileSystemManager.write(file, artistChoice);
//                }
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
