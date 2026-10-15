package storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import tasklist.Tasklist;

/**
 * Handles reading and writing the task list to persistent storage on disk.
 */
public class FileManipulator {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final Path BASE_DIR = FileManipulator.resolveBaseDir();
    private static final Path MASTER_PATH = BASE_DIR.resolve("masterTasklist.txt");
    private static final Path TEMP_PATH = BASE_DIR.resolve("tempTasklist.txt");

    /**
     * Constructs a {@code FileManipulator}.
     */
    public FileManipulator() { }

    /**
     * Resolves the OS-appropriate base directory for storing task list data.
     *
     * @return the resolved base directory path.
     */
    private static Path resolveBaseDir() {
        String home = System.getProperty("user.home");
        if (OS.contains("win")) {
            String localAppData = System.getenv("LOCALAPPDATA");
            Path base = (localAppData != null) ? Paths.get(localAppData) : Paths.get(home, "AppData", "Local");
            return base.resolve("remy");
        } else {
            String xdgCache = System.getenv("XDG_CACHE_HOME");
            Path base = (xdgCache != null) ? Paths.get(xdgCache) : Paths.get(home, ".cache");
            return base.resolve("remy");
        }
    }

    /**
     * Loads the task list from the master save file, creating an empty file
     * if none exists. Returns an empty task list if loading fails.
     *
     * @return the loaded {@code Tasklist}, or an empty one on failure.
     */
    public static Tasklist loadFile() {
        try {
            if (!Files.exists(MASTER_PATH)) {
                Files.createDirectories(MASTER_PATH.getParent());
                Files.writeString(MASTER_PATH, "");
            }
            File f = MASTER_PATH.toFile();
            Scanner s = new Scanner(f);
            Tasklist curr = new Tasklist(s);
            s.close();
            return curr;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new Tasklist();
        }
    }

    /**
     * Saves the given task list to disk, writing to a temporary file first
     * before atomically replacing the master save file.
     *
     * @param tL the task list to save.
     */
    public static void saveFile(Tasklist tL) {
        try {
            if (!Files.exists(TEMP_PATH)) {
                Files.writeString(TEMP_PATH, "");
            }
            File tmp = TEMP_PATH.toFile();
            FileWriter fw = new FileWriter(tmp);
            fw.write(tL.fileFormat());
            fw.close();
            Files.move(TEMP_PATH, MASTER_PATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Something went wrong! Can't write to temp file!");
            System.out.println(e.getMessage());
        }
    }
}
