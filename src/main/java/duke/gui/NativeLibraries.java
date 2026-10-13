package duke.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * Selects the JavaFX native libraries bundled for the current computer.
 */
final class NativeLibraries {
    private NativeLibraries() {
    }

    /**
     * Extracts the release's libraries before JavaFX starts. Gradle and IDE runs use their own dependencies.
     *
     * @throws IOException If the release cannot provide the required libraries.
     */
    static void prepare() throws IOException {
        if (NativeLibraries.class.getResource("/javafx-natives/win/libraries.list") == null) {
            return;
        }

        String platform = selectPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
        String resourceFolder = "/javafx-natives/" + platform + "/";
        Path directory = Path.of(".wangsa", "javafx-natives", platform).toAbsolutePath();
        Files.createDirectories(directory);
        try (InputStream index = openResource(resourceFolder + "libraries.list");
                BufferedReader reader = new BufferedReader(new InputStreamReader(index, StandardCharsets.UTF_8))) {
            for (String name : reader.lines().toList()) {
                Path library = directory.resolve(name);
                try (InputStream content = openResource(resourceFolder + name)) {
                    cacheLibrary(library, content);
                }
            }
        }

        // JavaFX reads this property when loading each library, including after the JVM has started.
        String previousPath = System.getProperty("java.library.path", "");
        System.setProperty("java.library.path", directory + File.pathSeparator + previousPath);
    }

    /**
     * Reuses identical native libraries inside the launch folder and replaces stale or damaged copies.
     *
     * <p>Windows locks loaded DLLs, so unchanged files must not be rewritten on every launch.
     * A fixed cache location also avoids leaving a new temporary directory behind on every run.</p>
     */
    static void cacheLibrary(Path library, InputStream content) throws IOException {
        byte[] bundledBytes = content.readAllBytes();
        if (!Files.exists(library) || !Arrays.equals(Files.readAllBytes(library), bundledBytes)) {
            Files.write(library, bundledBytes);
        }
    }

    /**
     * Maps Java's operating system and processor names to a bundled JavaFX platform.
     *
     * @throws IOException If the combination is not included in the release.
     */
    static String selectPlatform(String osName, String architecture) throws IOException {
        String os = osName.toLowerCase(Locale.ROOT);
        String arch = architecture.toLowerCase(Locale.ROOT);
        boolean isArm = arch.equals("aarch64") || arch.equals("arm64");
        boolean isIntel = arch.equals("amd64") || arch.equals("x86_64");
        if (os.startsWith("mac") && (isArm || isIntel)) {
            return isArm ? "mac-aarch64" : "mac";
        }
        if (os.startsWith("windows") && isIntel) {
            return "win";
        }
        if (os.startsWith("linux") && isIntel) {
            return "linux";
        }
        throw new IOException("This release supports Windows/Linux x64 and macOS Intel/Apple Silicon."
                + " Detected: " + osName + " (" + architecture + ").");
    }

    private static InputStream openResource(String name) throws IOException {
        InputStream resource = NativeLibraries.class.getResourceAsStream(name);
        if (resource == null) {
            throw new IOException("A JavaFX library is missing. Please download Wangsa.jar again.");
        }
        return resource;
    }
}
