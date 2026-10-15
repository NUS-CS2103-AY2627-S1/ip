package anders;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks platform selection and extraction without attempting to load another operating system's libraries. */
public class NativeLibrariesTest {
    @TempDir
    private Path directory;

    @Test
    public void platformFor_supportedSystems_selectsMatchingCpu() {
        assertEquals("win", NativeLibraries.platformFor("Windows 11", "amd64"));
        assertEquals("linux", NativeLibraries.platformFor("Linux", "x86_64"));
        assertEquals("mac", NativeLibraries.platformFor("Mac OS X", "x86_64"));
        assertEquals("mac-aarch64", NativeLibraries.platformFor("Mac OS X", "aarch64"));
        assertEquals("mac-aarch64", NativeLibraries.platformFor("macOS", "arm64"));
    }

    @Test
    public void platformFor_unsupportedSystems_rejectsWrongBinaries() {
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("Windows 11", "x86"));
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("Linux", "aarch64"));
        assertThrows(IllegalArgumentException.class, () -> NativeLibraries.platformFor("FreeBSD", "amd64"));
    }

    @Test
    public void extractLibraries_sameMacFilenames_selectsOnlyRequestedArchitecture() throws IOException {
        Path archive = createArchive(Map.of("natives/mac/libglass.dylib", "intel",
                "natives/mac-aarch64/libglass.dylib", "apple"));
        Path cache = directory.resolve("cache");
        try (JarFile jar = new JarFile(archive.toFile())) {
            NativeLibraries.extractLibraries(jar, "mac-aarch64", cache);
        }
        assertEquals("apple", Files.readString(cache.resolve("libglass.dylib")));
        try (var files = Files.list(cache)) {
            assertEquals(1, files.count());
        }
    }

    @Test
    public void extractLibraries_existingCache_preservesIntactFilesAndRepairsCorruption() throws IOException {
        Path archive = createArchive(Map.of("natives/win/glass.dll", "valid"));
        Path cache = directory.resolve("cache");
        Path library = cache.resolve("glass.dll");
        try (JarFile jar = new JarFile(archive.toFile())) {
            NativeLibraries.extractLibraries(jar, "win", cache);
            FileTime earlier = FileTime.fromMillis(1_000_000);
            Files.setLastModifiedTime(library, earlier);
            NativeLibraries.extractLibraries(jar, "win", cache);
            assertEquals(earlier, Files.getLastModifiedTime(library));

            Files.writeString(library, "wrong");
            NativeLibraries.extractLibraries(jar, "win", cache);
            assertArrayEquals("valid".getBytes(StandardCharsets.UTF_8), Files.readAllBytes(library));
        }
    }

    @Test
    public void extractLibraries_missingPlatform_reportsIncompleteJar() throws IOException {
        Path archive = createArchive(Map.of("natives/win/glass.dll", "windows"));
        try (JarFile jar = new JarFile(archive.toFile())) {
            assertThrows(IOException.class, () ->
                    NativeLibraries.extractLibraries(jar, "linux", directory.resolve("cache")));
        }
    }

    @Test
    public void extractLibraries_pathOutsideCache_isRejected() throws IOException {
        Path archive = createArchive(Map.of("natives/win/../outside.dll", "invalid"));
        try (JarFile jar = new JarFile(archive.toFile())) {
            assertThrows(IOException.class, () ->
                    NativeLibraries.extractLibraries(jar, "win", directory.resolve("cache")));
        }
        assertFalse(Files.exists(directory.resolve("outside.dll")));
    }

    private Path createArchive(Map<String, String> libraries) throws IOException {
        Path archive = directory.resolve("application.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(archive))) {
            for (var library : libraries.entrySet()) {
                output.putNextEntry(new JarEntry(library.getKey()));
                output.write(library.getValue().getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
        return archive;
    }
}
