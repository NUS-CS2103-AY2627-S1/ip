package zsiggy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Launches the Zsiggy JavaFX application.
 */
public class Launcher {

    /**
     * Entry point of the GUI application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        prepareMacLibraries();
        Main.launch(Main.class, args);
    }

    /**
     * Makes matching Mac native libraries available before JavaFX initializes.
     * JavaFX's NativeLibLoader reads java.library.path when loading each library.
     */
    private static void prepareMacLibraries() throws IOException, URISyntaxException {
        if (!System.getProperty("os.name").startsWith("Mac")) {
            return;
        }
        Path source = Path.of(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isDirectory(source)) {
            return; // Gradle and IDE runs use their normal dependency classpath.
        }
        String architecture = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        String prefix = architecture.equals("aarch64") || architecture.equals("arm64")
                ? "natives/mac-aarch64/" : "natives/mac/";
        Path directory = Files.createTempDirectory("zsiggy-javafx-");
        directory.toFile().deleteOnExit();
        try (JarFile jar = new JarFile(source.toFile())) {
            for (JarEntry entry : jar.stream().filter(item -> item.getName().startsWith(prefix)
                    && item.getName().endsWith(".dylib")).toList()) {
                Path library = directory.resolve(Path.of(entry.getName()).getFileName());
                try (InputStream stream = jar.getInputStream(entry)) {
                    Files.copy(stream, library);
                }
                library.toFile().deleteOnExit();
            }
        }
        System.setProperty("java.library.path", directory + java.io.File.pathSeparator
                + System.getProperty("java.library.path", ""));
    }
}
