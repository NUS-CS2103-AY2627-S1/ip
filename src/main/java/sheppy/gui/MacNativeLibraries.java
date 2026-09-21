package sheppy.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.application.Application;

/** Selects the matching Mac native libraries when JavaFX is loaded from the fat JAR. */
final class MacNativeLibraries {
    private static final List<String> LIBRARIES = List.of(
            "decora_sse", "glass", "javafx_font", "javafx_iio", "prism_common", "prism_es2", "prism_sw");

    private MacNativeLibraries() {
    }

    /** Makes packaged native libraries available before JavaFX starts its toolkit. */
    static void prepare() throws IOException {
        if (!System.getProperty("os.name").startsWith("Mac") || Application.class.getModule().isNamed()) {
            return;
        }
        String architecture = System.getProperty("os.arch");
        String platform = switch (architecture) {
            case "aarch64", "arm64" -> "mac-aarch64";
            case "x86_64", "amd64" -> "mac-x86_64";
            default -> throw new IOException("Unsupported Mac architecture: " + architecture);
        };
        Path directory = Files.createTempDirectory("sheppy-javafx-");
        directory.toFile().deleteOnExit();
        for (String library : LIBRARIES) {
            extractLibrary(platform, library, directory);
        }
        // JavaFX searches this property before falling back to the JDK's native libraries.
        System.setProperty("java.library.path", directory + File.pathSeparator
                + System.getProperty("java.library.path", ""));
    }

    /** Extracts a native library to a private temporary directory for this process. */
    private static void extractLibrary(String platform, String library, Path directory) throws IOException {
        String filename = "lib" + library + ".dylib";
        String resource = "/natives/" + platform + "/" + filename;
        try (InputStream input = MacNativeLibraries.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("Missing " + resource + "; rebuild Sheppy with shadowJar.");
            }
            Path destination = directory.resolve(filename);
            Files.copy(input, destination);
            destination.toFile().deleteOnExit();
        }
    }
}
