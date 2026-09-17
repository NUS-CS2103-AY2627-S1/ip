package penny;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Handles reading and writing of data to files at a relative path.
 */
public class FileManager {
    private static final Path SAVE_PATH = Paths.get("./");

    /**
     * Create and write files.
     *
     * @param fileName name of created file.
     * @param content content to write to the created file.
     * @throws PennyException if the complete replacement cannot be saved.
     */
    public static void writeData(String fileName, String content) throws PennyException {
        Path temporary = null;
        try {
            Path filePath = SAVE_PATH.resolve(fileName).toAbsolutePath();
            Files.createDirectories(filePath.getParent());
            if (Files.exists(filePath) && (!Files.isRegularFile(filePath) || !Files.isWritable(filePath))) {
                throw new IOException("The save path is not a writable regular file.");
            }
            temporary = Files.createTempFile(filePath.getParent(), "penny-", ".tmp");
            Files.writeString(temporary, content);
            replaceSave(temporary, filePath);
        } catch (IOException | InvalidPathException | SecurityException e) {
            throw new PennyException("Could not save tasks to " + fileName
                    + ". Check the path, permissions, and available disk space.", e);
        } finally {
            cleanUpTemporaryFile(temporary);
        }
    }

    /**
     * Replaces the save atomically where supported, after the complete new content has been written.
     */
    private static void replaceSave(Path temporary, Path filePath) throws IOException {
        try {
            Files.move(temporary, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes any temporary file without hiding the result of the save operation.
     */
    private static void cleanUpTemporaryFile(Path temporary) {
        if (temporary == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporary);
        } catch (IOException | SecurityException e) {
            // A leftover temporary file must not hide the original save error.
        }
    }

    /**
     * Returns contents of the fileName as a string,
     * if not found, returns "".
     *
     * @param fileName name of file to look for.
     * @return contents of the file if found as a string.
     * @throws PennyException if the file cannot be read or created.
     */
    public static String readData(String fileName) throws PennyException {
        try {
            Path filePath = SAVE_PATH.resolve(fileName);
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return ""; // Return empty string for newly created file
            }

            return Files.readString(filePath);
        } catch (IOException | InvalidPathException | SecurityException e) {
            throw new PennyException("Could not read tasks from " + fileName
                    + ". Check that it is a readable UTF-8 file.", e);
        }
    }
}
