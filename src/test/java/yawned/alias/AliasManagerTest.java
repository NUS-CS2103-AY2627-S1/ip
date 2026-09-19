package yawned.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yawned.parser.CommandType;
import yawned.storage.AliasStorage;

/** Tests custom alias management. */
class AliasManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getAliases_returnsReadOnlySnapshotInCreationOrder() {
        AliasManager aliasManager = new AliasManager(new AliasStorage(temporaryDirectory.resolve("aliases.txt")));

        assertEquals(AliasResult.SUCCESS, aliasManager.defineAlias("hw", "todo"));
        assertEquals(AliasResult.SUCCESS, aliasManager.defineAlias("proj", "deadline"));

        Map<String, CommandType> aliases = aliasManager.getAliases();

        assertEquals(List.of("hw", "proj"), new ArrayList<>(aliases.keySet()));
        assertEquals(Map.of("hw", CommandType.TODO, "proj", CommandType.DEADLINE), aliases);
        assertThrows(UnsupportedOperationException.class, () -> aliases.put("work", CommandType.EVENT));
    }
}
