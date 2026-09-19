package yawned;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationPathsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void taskFileForCodeSource_packagedJar_usesSiblingDataDirectory() throws IOException {
        Path jarFile = Files.createFile(temporaryDirectory.resolve("yawned.jar"));

        assertEquals(temporaryDirectory.resolve("data").resolve("Yawned.txt"),
                ApplicationPaths.taskFileForCodeSource(jarFile));
    }

    @Test
    void taskFileForCodeSource_compiledClasses_usesCurrentDirectoryDataDirectory() {
        Path classesDirectory = temporaryDirectory.resolve("classes");

        assertEquals(Path.of("data", "Yawned.txt"), ApplicationPaths.taskFileForCodeSource(classesDirectory));
    }
}
