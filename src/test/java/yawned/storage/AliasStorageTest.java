package yawned.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yawned.parser.CommandType;

/** Tests persistent storage of custom command aliases. */
class AliasStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoadAliases_preservesOrderAndCanonicalTargets() {
        AliasStorage storage = new AliasStorage(temporaryDirectory.resolve("aliases.txt"));
        Map<String, CommandType> aliases = new LinkedHashMap<>();
        aliases.put("hw", CommandType.TODO);
        aliases.put("report", CommandType.DEADLINE);

        assertEquals(true, storage.saveAliases(aliases));
        assertEquals(aliases, storage.loadAliases());
    }

    @Test
    void loadAliases_invalidLinesAreSkippedAndLastDuplicateWins() throws IOException {
        Path aliasFile = temporaryDirectory.resolve("aliases.txt");
        Files.writeString(aliasFile, "hw=todo\ninvalid-line\nhw=deadline\nt=todo\n");
        AliasStorage storage = new AliasStorage(aliasFile);

        Map<String, CommandType> aliases = storage.loadAliases();

        assertEquals(Map.of("hw", CommandType.DEADLINE), aliases);
    }
}
