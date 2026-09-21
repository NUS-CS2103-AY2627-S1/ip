package yqr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

/**
 * Selects the Mac JavaFX native libraries that match the running JVM's architecture.
 * Windows and Linux libraries have distinct filenames and can remain at the JAR root.
 */
final class JavaFxNativeLibraries {
    private static final List<String> MAC_LIBRARIES = List.of(
            "libglass.dylib", "libjavafx_font.dylib", "libjavafx_iio.dylib",
            "libprism_common.dylib", "libprism_es2.dylib", "libprism_sw.dylib", "libdecora_sse.dylib");

    /** Prevents instantiation of this startup utility. */
    private JavaFxNativeLibraries() {
    }

    /**
     * Makes the matching Mac libraries available before JavaFX starts its toolkit.
     */
    static void configure() {
        String platform = macPlatformFor(System.getProperty("os.name"), System.getProperty("os.arch"));
        if (platform.isEmpty()) {
            return;
        }

        try {
            // Each launch gets its own directory, avoiding stale or conflicting cached architectures.
            Path directory = Files.createTempDirectory("yqr-javafx-" + platform + "-");
            directory.toFile().deleteOnExit();
            extractLibraries(platform, directory);
            String existingPath = System.getProperty("java.library.path", "");
            String libraryPath = directory.toAbsolutePath().toString();
            if (!existingPath.isEmpty()) {
                libraryPath += File.pathSeparator + existingPath;
            }
            // JavaFX's native loader reads this property when looking for each library.
            System.setProperty("java.library.path", libraryPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to prepare JavaFX libraries for " + platform, e);
        }
    }

    /**
     * Returns the Mac resource directory, or an empty string for other operating systems.
     * Rejects unknown Mac architectures instead of silently choosing incompatible libraries.
     */
    static String macPlatformFor(String osName, String osArch) {
        if (!osName.toLowerCase(Locale.ROOT).startsWith("mac")) {
            return "";
        }
        return switch (osArch.toLowerCase(Locale.ROOT)) {
            case "aarch64", "arm64" -> "mac-aarch64";
            case "x86_64", "amd64" -> "mac";
            default -> throw new IllegalStateException("Unsupported Mac Java architecture: " + osArch);
        };
    }

    /**
     * Extracts the selected libraries into an existing directory for JavaFX to load.
     */
    static void extractLibraries(String platform, Path directory) throws IOException {
        for (String library : MAC_LIBRARIES) {
            String resource = "/javafx-natives/" + platform + "/" + library;
            try (InputStream input = JavaFxNativeLibraries.class.getResourceAsStream(resource)) {
                if (input == null) {
                    throw new IOException("Missing bundled JavaFX library: " + resource);
                }
                Path target = directory.resolve(library);
                Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
                target.toFile().deleteOnExit();
            }
        }
    }
}
