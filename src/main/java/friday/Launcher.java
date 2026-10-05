package friday;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        configureMacLibraries();
        Application.launch(Main.class, args);
    }

    private static void configureMacLibraries() throws IOException {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            return;
        }
        String architecture = System.getProperty("os.arch");
        String resourceFolder = architecture.equals("aarch64") || architecture.equals("arm64")
                ? "/natives/mac-arm64/" : "/natives/mac-x64/";
        // Gradle run uses its normal dependency libraries rather than the fat JAR's resources.
        if (Launcher.class.getResource(resourceFolder + "libglass.dylib") == null) {
            return;
        }
        Path directory = Files.createTempDirectory("friday-javafx-");
        directory.toFile().deleteOnExit();
        String[] libraries = {"decora_sse", "glass", "javafx_font", "javafx_iio",
            "prism_common", "prism_es2", "prism_sw"};
        for (String library : libraries) {
            String filename = "lib" + library + ".dylib";
            try (InputStream input = Launcher.class.getResourceAsStream(resourceFolder + filename)) {
                if (input == null) {
                    throw new IOException("Missing bundled JavaFX library: " + filename);
                }
                Path target = directory.resolve(filename);
                Files.copy(input, target);
                target.toFile().deleteOnExit();
            }
        }
        // OpenJFX's NativeLibLoader reads this property when resolving each native library.
        System.setProperty("java.library.path", directory + File.pathSeparator
                + System.getProperty("java.library.path", ""));
    }
}
