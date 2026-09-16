package arin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Selects bundled JavaFX native binaries without mixing incompatible Mac architectures.
 * OpenJFX's NativeLibLoader checks java.library.path when root resources are absent.
 */
public final class NativeLibraries {
    private NativeLibraries() {
    }

    /**
     * Extracts only the current platform's binaries before JavaFX initializes.
     *
     * @throws IOException If native resources are missing or the platform is unsupported.
     */
    public static void prepare() throws IOException {
        String platform = getPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
        String resourceDirectory = "/natives/" + platform + "/";
        String index;
        try (InputStream input = openResource(resourceDirectory + "index.txt")) {
            index = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        Path directory = Files.createTempDirectory("arin-javafx-");
        directory.toFile().deleteOnExit();
        for (String library : index.lines().toList()) {
            Path target = directory.resolve(library);
            try (InputStream input = openResource(resourceDirectory + library)) {
                Files.copy(input, target);
            }
            target.toFile().deleteOnExit();
        }
        System.setProperty("java.library.path", directory + File.pathSeparator
                + System.getProperty("java.library.path", ""));
    }

    /**
     * Maps Java platform names to the classifiers shipped in the release JAR.
     */
    static String getPlatform(String osName, String architecture) throws IOException {
        String normalizedOs = osName.toLowerCase(Locale.ROOT);
        boolean isArm = architecture.equals("aarch64") || architecture.equals("arm64");
        boolean isIntel = architecture.equals("amd64") || architecture.equals("x86_64");
        if (normalizedOs.contains("mac") && (isArm || isIntel)) {
            return isArm ? "mac-aarch64" : "mac";
        }
        if (isIntel && normalizedOs.startsWith("windows")) {
            return "win";
        }
        if (isIntel && normalizedOs.equals("linux")) {
            return "linux";
        }
        throw new IOException("Unsupported platform: " + osName + " " + architecture
                + ". Use Java 25 on Windows/Linux x64, or macOS Intel/Apple silicon.");
    }

    private static InputStream openResource(String name) throws IOException {
        InputStream input = NativeLibraries.class.getResourceAsStream(name);
        if (input == null) {
            throw new IOException("Missing JavaFX resource " + name + ". Download the complete arin-all.jar.");
        }
        return input;
    }
}
