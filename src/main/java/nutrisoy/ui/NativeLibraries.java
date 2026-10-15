package nutrisoy.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Selects the bundled JavaFX native libraries for the current operating system.
 */
final class NativeLibraries {
    private NativeLibraries() {
    }

    /**
     * Extracts native libraries from the release JAR before JavaFX starts.
     * Gradle and IDE launches keep using their normal platform dependencies.
     *
     * @throws Exception if the bundle cannot be read or extracted
     */
    static void prepare() throws Exception {
        Path location = Path.of(NativeLibraries.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        if (Files.isDirectory(location)) {
            return;
        }
        String platform = platformFor(System.getProperty("os.name"), System.getProperty("os.arch"));
        try (JarFile jar = new JarFile(location.toFile())) {
            String prefix = "natives/" + platform + "/";
            var entries = jar.stream().filter(entry -> !entry.isDirectory()
                    && entry.getName().startsWith(prefix)).toList();
            if (entries.isEmpty()) {
                throw new IOException("Missing bundled JavaFX libraries. Please use nutrisoy.jar.");
            }
            Path directory = Files.createTempDirectory("nutrisoy-javafx-");
            directory.toFile().deleteOnExit();
            for (JarEntry entry : entries) {
                Path target = directory.resolve(Path.of(entry.getName()).getFileName());
                try (InputStream input = jar.getInputStream(entry)) {
                    Files.copy(input, target);
                }
                target.toFile().deleteOnExit();
            }
            // JavaFX's NativeLibLoader reads this property before loading each library.
            // Approach informed by the OpenJFX loader implementation:
            // https://github.com/openjdk/jfx/blob/jfx25/modules/javafx.graphics/src/main/java/
            // com/sun/glass/utils/NativeLibLoader.java
            System.setProperty("java.library.path", directory.toString());
        }
    }

    /**
     * Maps supported Java runtime platforms to the bundled Maven classifiers.
     *
     * @param osName operating system name
     * @param architecture Java runtime architecture
     * @return platform classifier
     */
    static String platformFor(String osName, String architecture) {
        String os = osName.toLowerCase(Locale.ROOT);
        String arch = architecture.toLowerCase(Locale.ROOT);
        boolean x64 = arch.equals("amd64") || arch.equals("x86_64");
        boolean arm64 = arch.equals("aarch64") || arch.equals("arm64");
        if (os.startsWith("mac") && (x64 || arm64)) {
            return arm64 ? "mac-aarch64" : "mac";
        }
        if (os.startsWith("windows") && x64) {
            return "win";
        }
        if (os.startsWith("linux") && x64) {
            return "linux";
        }
        throw new IllegalArgumentException("Unsupported platform: " + osName + " / " + architecture
                + ". Use Java 25 on Windows/Linux x64 or macOS Intel/Apple Silicon.");
    }
}
