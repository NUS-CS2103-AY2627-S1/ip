package gary.gui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Selects the bundled JavaFX native libraries matching the Mac JVM architecture.
 */
public final class MacNativeLibraries {
    private MacNativeLibraries() {
    }

    /**
     * Prepares Mac libraries before JavaFX initializes its native toolkit.
     *
     * @throws IOException If bundled libraries cannot be extracted.
     */
    public static void prepare() throws IOException {
        if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("mac")) {
            return;
        }
        String architecture = System.getProperty("os.arch");
        if (requiresSoftwareRenderer(architecture) && System.getProperty("prism.order") == null) {
            System.setProperty("prism.order", "sw");
        }
        String resourceDirectory = resourceDirectory(architecture);
        try (InputStream index = MacNativeLibraries.class.getResourceAsStream(resourceDirectory + "libraries.txt")) {
            if (index == null) {
                throw new IOException("Missing bundled Mac JavaFX libraries: " + resourceDirectory);
            }
            Path directory = Files.createTempDirectory("gary-javafx-");
            directory.toFile().deleteOnExit();
            String names = new String(index.readAllBytes(), StandardCharsets.UTF_8);
            for (String name : names.lines().toList()) {
                extractLibrary(resourceDirectory, name, directory);
            }
            String existingPath = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path", directory + System.getProperty("path.separator") + existingPath);
        }
    }

    static String resourceDirectory(String architecture) {
        return switch (architecture.toLowerCase(Locale.ROOT)) {
            case "aarch64", "arm64" -> "/natives/mac-aarch64/";
            case "x86_64", "amd64" -> "/natives/mac-x86_64/";
            default -> throw new IllegalArgumentException("Unsupported Mac JVM architecture: " + architecture);
        };
    }

    static boolean requiresSoftwareRenderer(String architecture) {
        return switch (architecture.toLowerCase(Locale.ROOT)) {
            case "x86_64", "amd64" -> true;
            default -> false;
        };
    }

    private static void extractLibrary(String resourceDirectory, String name, Path directory) throws IOException {
        if (!name.matches("[A-Za-z0-9_]+\\.dylib")) {
            throw new IOException("Invalid bundled native library name: " + name);
        }
        try (InputStream library = MacNativeLibraries.class.getResourceAsStream(resourceDirectory + name)) {
            if (library == null) {
                throw new IOException("Missing bundled native library: " + name);
            }
            Path target = directory.resolve(name);
            Files.copy(library, target);
            target.toFile().deleteOnExit();
        }
    }
}
