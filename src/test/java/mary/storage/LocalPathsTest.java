package mary.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies writes against actual child-process working directories, not user.dir overrides.
 */
class LocalPathsTest {
    @TempDir
    Path directory;

    /**
     * Allows local descendants while rejecting parent traversal and outside absolute paths.
     */
    @Test
    void save_outsidePaths_rejectedWithoutCreatingFiles() throws Exception {
        Path work = Files.createDirectory(directory.resolve("work"));
        Path outside = directory.resolve("outside");
        for (String path : List.of("../outside/tasks.txt", outside.resolve("tasks.txt").toString(),
                "nested/../../outside/tasks.txt")) {
            assertTrue(run(work, "save", path).startsWith("REJECTED:"), path);
            assertFalse(Files.exists(outside));
        }
        assertEquals("SAVED", run(work, "save", "data/tasks.txt").strip());
        assertEquals(List.of("T | 0 | local only"), Files.readAllLines(work.resolve("data/tasks.txt")));
        assertEquals("SAVED", run(work, "save", work.resolve("tasks.txt").toString()).strip());
    }

    /**
     * Rejects existing directory links before creating tasks or JavaFX caches through them.
     */
    @Test
    void save_linkedParent_rejectedWithoutTouchingTarget() throws Exception {
        Path work = Files.createDirectory(directory.resolve("work"));
        Path outside = Files.createDirectory(directory.resolve("outside"));
        Path link = work.resolve(".mary");
        createDirectoryLink(link, outside);
        try {
            assertTrue(run(work, "save", ".mary/tasks.txt").startsWith("REJECTED:"));
            assertTrue(run(work, "cache").startsWith("REJECTED:"));
            try (var files = Files.list(outside)) {
                assertEquals(0, files.count());
            }
        } finally {
            Files.delete(link);
        }
    }

    /**
     * Keeps normal and fallback JavaFX caches local despite external JVM property overrides.
     */
    @Test
    void configureCaches_externalOverrides_replacedWithLocalPaths() throws Exception {
        Path work = Files.createDirectory(directory.resolve("work"));
        String output = run(work, "cache");
        assertTrue(output.contains("CACHE: " + work.resolve(".mary/javafx-cache")));
        assertTrue(output.contains("TEMP: " + work.resolve(".mary/tmp")));
        assertFalse(Files.exists(directory.resolve("fake-home")));
        assertFalse(Files.exists(directory.resolve("external-cache")));
        assertFalse(Files.exists(directory.resolve("external-temp")));
    }

    /**
     * Fails cache preparation when a local cache path is a file rather than falling back elsewhere.
     */
    @Test
    void configureCaches_blockedLocalPath_rejectsWithoutFallback() throws Exception {
        Path work = Files.createDirectory(directory.resolve("work"));
        Files.writeString(work.resolve(".mary"), "keep");
        assertTrue(run(work, "cache").startsWith("REJECTED:"));
        assertEquals("keep", Files.readString(work.resolve(".mary")));
        assertFalse(Files.exists(directory.resolve("fake-home")));
        assertFalse(Files.exists(directory.resolve("external-temp")));
    }

    /**
     * Creates a Windows junction or Unix directory symlink inside the isolated fixture.
     */
    private void createDirectoryLink(Path link, Path target) throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            Process process = new ProcessBuilder("cmd", "/c", "mklink", "/J", link.toString(), target.toString())
                    .redirectErrorStream(true).start();
            assertTrue(process.waitFor(10, TimeUnit.SECONDS));
            assertEquals(0, process.exitValue(), new String(process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8));
        } else {
            Files.createSymbolicLink(link, target);
        }
    }

    /**
     * Runs the probe with outside cache/home overrides and a bounded process lifetime.
     */
    private String run(Path work, String... arguments) throws Exception {
        String classpath = Path.of(LocalPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                + File.pathSeparator
                + Path.of(LocalWriteProbe.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        // Launcher references JavaFX, so retain the JavaFX jars from the Gradle test runtime.
        classpath += File.pathSeparator + System.getProperty("mary.probeClasspath");
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Duser.home=" + directory.resolve("fake-home"),
                "-Djavafx.cachedir=" + directory.resolve("external-cache"),
                "-Djava.io.tmpdir=" + directory.resolve("external-temp"),
                "-cp", classpath, LocalWriteProbe.class.getName()));
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command).directory(work.toFile()).start();
        try {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Write-policy probe timed out");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String errors = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            assertTrue(errors.isEmpty() || errors.equals("WARNING: java.io.tmpdir directory does not exist"), errors);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}
