package yawned.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

import yawned.alias.AliasManager;
import yawned.parser.CommandType;

/** Loads and saves user-defined command aliases. */
public class AliasStorage {
    private final Path saveFile;

    /**
     * Creates alias storage that uses the given file path.
     *
     * @param saveFile Path of the alias configuration file.
     */
    public AliasStorage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Loads valid alias mappings from storage.
     *
     * @return Loaded aliases in file order.
     */
    public Map<String, CommandType> loadAliases() {
        Map<String, CommandType> aliases = new LinkedHashMap<>();
        if (!Files.exists(saveFile)) {
            return aliases;
        }
        try {
            var storedAliases = Files.readAllLines(saveFile);
            for (int lineIndex = 0; lineIndex < storedAliases.size(); lineIndex++) {
                addStoredAlias(aliases, storedAliases.get(lineIndex), lineIndex + 1);
            }
        } catch (IOException exception) {
            System.out.println("*yawn* I couldn't read saved aliases, so I'm starting with none."
                    + " They seem to be napping.");
            aliases.clear();
        }
        return aliases;
    }

    /**
     * Saves aliases to storage.
     *
     * @param aliases Alias mappings to save.
     * @return Whether all mappings were saved successfully.
     */
    public boolean saveAliases(Map<String, CommandType> aliases) {
        Path temporaryFile = null;
        try {
            Files.createDirectories(saveFile.getParent());
            temporaryFile = saveFile.resolveSibling(saveFile.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (Map.Entry<String, CommandType> alias : aliases.entrySet()) {
                    writer.write(alias.getKey() + "=" + alias.getValue().getWord());
                    writer.newLine();
                }
            }
            moveTemporaryFile(temporaryFile);
            return true;
        } catch (IOException exception) {
            return false;
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Adds one valid stored alias to the map, or reports an invalid line. */
    private static void addStoredAlias(Map<String, CommandType> aliases, String storedAlias, int lineNumber) {
        if (storedAlias.isBlank()) {
            return;
        }
        String[] fields = storedAlias.split("=", -1);
        if (fields.length != 2 || !AliasManager.isValidAliasName(fields[0])
                || CommandType.isReservedAliasName(fields[0])) {
            reportInvalidStoredAlias(lineNumber);
            return;
        }
        CommandType targetCommandType = CommandType.fromAliasTarget(fields[1]);
        if (targetCommandType == CommandType.UNKNOWN) {
            reportInvalidStoredAlias(lineNumber);
            return;
        }
        aliases.put(AliasManager.normalize(fields[0]), targetCommandType);
    }

    /** Moves the completed temporary file into place. */
    private void moveTemporaryFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Deletes a temporary storage file if one was created. */
    private static void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // The temporary file does not affect saved aliases.
        }
    }

    /** Reports one invalid alias record. */
    private static void reportInvalidStoredAlias(int lineNumber) {
        System.out.println("*yawn* I skipped an unreadable saved alias on line " + lineNumber
                + ". It must have wandered off in its sleep.");
    }
}
