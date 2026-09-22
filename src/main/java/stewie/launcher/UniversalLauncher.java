package stewie.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Provides a single-JAR entry point for the supported desktop operating systems and CPU architectures.
 * Shared files are bundled once; platform overlays keep differing classes and native libraries separate.
 */
public final class UniversalLauncher {
    private UniversalLauncher() {
    }

    /**
     * Launches the matching bundled application using the same Java installation and working directory.
     *
     * @param args Arguments to pass unchanged to the desktop application.
     */
    public static void main(String[] args) {
        int exitCode;
        try {
            String platform = selectPlatform(System.getProperty("os.name"), System.getProperty("os.arch"));
            exitCode = launchBundled(platform, args);
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Unable to launch Stewie: " + exception.getMessage());
            exitCode = 1;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.err.println("Stewie startup was interrupted.");
            exitCode = 1;
        }
        System.exit(exitCode);
    }

    /**
     * Returns the package matching the JVM architecture, which may differ from the physical CPU under emulation.
     */
    static String selectPlatform(String operatingSystem, String architecture) {
        String os = operatingSystem.toLowerCase(Locale.ROOT);
        String cpu = switch (architecture.toLowerCase(Locale.ROOT)) {
            case "amd64", "x86_64", "x64" -> "x64";
            case "aarch64", "arm64" -> "aarch64";
            default -> throw unsupportedPlatform(operatingSystem, architecture);
        };
        if (os.startsWith("windows") && cpu.equals("x64")) {
            return "windows-x64";
        } else if (os.equals("mac os x") || os.equals("macos") || os.equals("darwin")) {
            return "mac-" + cpu;
        } else if (os.equals("linux")) {
            return "linux-" + cpu;
        }
        throw unsupportedPlatform(operatingSystem, architecture);
    }

    private static IllegalArgumentException unsupportedPlatform(String operatingSystem, String architecture) {
        return new IllegalArgumentException("Unsupported platform: " + operatingSystem + " / " + architecture
                + ". Use Java 25 on Windows x64, macOS x64/ARM64, or desktop Linux x64/ARM64.");
    }

    /**
     * Assembles the selected runtime inside the working directory and removes it after the app exits.
     */
    static int launchBundled(String platform, String[] args) throws IOException, InterruptedException {
        String resource = "/platforms/stewie-" + platform + ".jar";
        try (InputStream input = UniversalLauncher.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("Missing bundled runtime " + resource + ". Download the complete stewie.jar.");
            }
            Path directory = Files.createTempDirectory(Path.of("."), ".stewie-runtime-");
            Path archive = directory.resolve("stewie.jar");
            // Also attempt cleanup if the terminal interrupts the launcher during a running session.
            directory.toFile().deleteOnExit();
            archive.toFile().deleteOnExit();
            try {
                try (InputStream common = UniversalLauncher.class.getResourceAsStream("/platforms/common.jar")) {
                    if (common == null) {
                        throw new IOException("Missing bundled runtime /platforms/common.jar.");
                    }
                    try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(archive))) {
                        appendEntries(common, output);
                        appendEntries(input, output);
                    }
                }
                return launchArchive(archive, args);
            } finally {
                Files.deleteIfExists(archive);
                Files.deleteIfExists(directory);
            }
        }
    }

    /**
     * Copies shared or platform entries into the runtime JAR without extracting arbitrary paths to disk.
     */
    private static void appendEntries(InputStream input, ZipOutputStream output) throws IOException {
        try (ZipInputStream archive = new ZipInputStream(input)) {
            ZipEntry entry;
            while ((entry = archive.getNextEntry()) != null) {
                output.putNextEntry(new ZipEntry(entry.getName()));
                archive.transferTo(output);
                output.closeEntry();
            }
        }
    }

    /**
     * Runs a platform JAR in a child JVM so JavaFX uses the normal application class loader and native loading.
     * The inherited working directory preserves the user's existing relative task-storage location.
     */
    static int launchArchive(Path archive, String[] args) throws IOException, InterruptedException {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        List<String> command = new ArrayList<>(List.of(java.toString(), "-jar", archive.toString()));
        command.addAll(Arrays.asList(args));
        Process child = new ProcessBuilder(command).inheritIO().start();
        Thread shutdownHook = new Thread(child::destroyForcibly, "stewie-runtime-shutdown");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        try {
            return child.waitFor();
        } finally {
            child.destroyForcibly();
            child.waitFor();
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }
}
