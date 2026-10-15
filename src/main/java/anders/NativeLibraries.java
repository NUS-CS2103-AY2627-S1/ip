package anders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/** Selects the bundled JavaFX native libraries for the operating system and CPU running the JAR. */
final class NativeLibraries {
    private NativeLibraries() {
    }

    /** Extracts native libraries before JavaFX starts; Gradle and IDE runs use their host dependencies. */
    static void prepare() throws IOException, URISyntaxException {
        Path application = Path.of(NativeLibraries.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isDirectory(application) || ModuleLayer.boot().findModule("javafx.graphics").isPresent()) {
            return;
        }

        String platform = platformFor(System.getProperty("os.name"), System.getProperty("os.arch"));
        try (JarFile jar = new JarFile(application.toFile())) {
            String version = jar.getManifest().getMainAttributes().getValue("Anders-JavaFX-Version");
            if (version == null) {
                throw new IOException("The JAR is missing its JavaFX version. Rebuild it with shadowJar.");
            }
            Path cache = Path.of(System.getProperty("user.home"), ".anders", "javafx", version, platform);
            extractLibraries(jar, platform, cache);
            // JavaFX 17's native loader reads this property when searching for each library.
            String existingPath = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path", cache.toAbsolutePath() + File.pathSeparator + existingPath);
        }
    }

    /** Returns the bundled classifier, rejecting operating systems and CPUs that are not included. */
    static String platformFor(String osName, String architecture) {
        String os = osName.toLowerCase(Locale.ROOT);
        String arch = architecture.toLowerCase(Locale.ROOT);
        boolean isIntel = arch.equals("amd64") || arch.equals("x86_64");
        boolean isArm = arch.equals("aarch64") || arch.equals("arm64");
        if (os.startsWith("mac") && (isIntel || isArm)) {
            return isArm ? "mac-aarch64" : "mac";
        }
        if (os.startsWith("windows") && isIntel) {
            return "win";
        }
        if (os.startsWith("linux") && isIntel) {
            return "linux";
        }
        throw new IllegalArgumentException("Unsupported Java platform: " + osName + " / " + architecture
                + ". Use 64-bit Java 25 on Windows or Linux (x64), or macOS (Intel or Apple Silicon).");
    }

    /** Copies only the selected platform's libraries and repairs missing or damaged cached files. */
    static void extractLibraries(JarFile jar, String platform, Path cache) throws IOException {
        String prefix = "natives/" + platform + "/";
        boolean hasLibraries = false;
        var entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory() || !entry.getName().startsWith(prefix)) {
                continue;
            }
            String filename = entry.getName().substring(prefix.length());
            if (filename.contains("/") || filename.contains("\\") || filename.equals("..")) {
                throw new IOException("Invalid bundled library path: " + entry.getName());
            }
            Files.createDirectories(cache);
            copyLibrary(jar, entry, cache.resolve(filename));
            hasLibraries = true;
        }
        if (!hasLibraries) {
            throw new IOException("The JAR is missing JavaFX libraries for " + platform
                    + ". Rebuild it with shadowJar.");
        }
    }

    /** Reuses intact cached libraries and replaces damaged files only after a complete copy. */
    private static void copyLibrary(JarFile jar, JarEntry entry, Path target) throws IOException {
        if (isCurrentLibrary(target, entry)) {
            return;
        }
        Path temporary = Files.createTempFile(target.getParent(), "library-", ".tmp");
        try {
            try (InputStream input = jar.getInputStream(entry)) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Checks the archive's checksum so an interrupted extraction cannot leave a broken cached library. */
    private static boolean isCurrentLibrary(Path target, JarEntry entry) throws IOException {
        if (!Files.isRegularFile(target) || Files.size(target) != entry.getSize()) {
            return false;
        }
        CRC32 checksum = new CRC32();
        try (CheckedInputStream input = new CheckedInputStream(Files.newInputStream(target), checksum)) {
            input.transferTo(OutputStream.nullOutputStream());
        }
        return checksum.getValue() == entry.getCrc();
    }
}
