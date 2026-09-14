package arthur.state;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import arthur.exceptions.StorageException;

/**
 * Loads and saves the command context on disk.
 */
public class Storage {
    // AI-assisted: Stores the injected persistence file path for isolated storage tests.
    private final Path filePath;

    /**
     * Creates storage backed by the supplied file.
     *
     * @param filePath the file used to persist the command context.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the command context from the file.
     * If the file does not exist, a new command context is returned.
     *
     * @return the command context.
     */
    public CommandContext load() {
        if (!Files.exists(filePath)) {
            return new CommandContext();
        }
        try (FileInputStream fileIn = new FileInputStream(filePath.toFile());
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return ((CommandContext) objectIn.readObject()).fixed();
        } catch (IOException | ClassNotFoundException e) {
            return new CommandContext();
        }
    }

    /**
     * Saves the command context to the file.
     *
     * @param context the command context to save.
     * @throws StorageException if an error occurs while saving the command context.
     */
    public void save(CommandContext context) {
        Path directoryPath = filePath.getParent();
        if (directoryPath != null && !Files.isDirectory(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new StorageException(e);
            }
        }
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(context);
        } catch (IOException e) {
            throw new StorageException(e);
        }
    }
}
