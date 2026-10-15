package kdb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import javafx.application.Application;

/**
 * Starts KDB after preparing the JavaFX native libraries for the current OS.
 */
public class Launcher {

    private static final List<String> WINDOWS_LIBRARIES = List.of(
            "api-ms-win-core-console-l1-1-0.dll", "api-ms-win-core-console-l1-2-0.dll",
            "api-ms-win-core-datetime-l1-1-0.dll", "api-ms-win-core-debug-l1-1-0.dll",
            "api-ms-win-core-errorhandling-l1-1-0.dll", "api-ms-win-core-file-l1-1-0.dll",
            "api-ms-win-core-file-l1-2-0.dll", "api-ms-win-core-file-l2-1-0.dll",
            "api-ms-win-core-handle-l1-1-0.dll", "api-ms-win-core-heap-l1-1-0.dll",
            "api-ms-win-core-interlocked-l1-1-0.dll", "api-ms-win-core-libraryloader-l1-1-0.dll",
            "api-ms-win-core-localization-l1-2-0.dll", "api-ms-win-core-memory-l1-1-0.dll",
            "api-ms-win-core-namedpipe-l1-1-0.dll", "api-ms-win-core-processenvironment-l1-1-0.dll",
            "api-ms-win-core-processthreads-l1-1-0.dll", "api-ms-win-core-processthreads-l1-1-1.dll",
            "api-ms-win-core-profile-l1-1-0.dll", "api-ms-win-core-rtlsupport-l1-1-0.dll",
            "api-ms-win-core-string-l1-1-0.dll", "api-ms-win-core-synch-l1-1-0.dll",
            "api-ms-win-core-synch-l1-2-0.dll", "api-ms-win-core-sysinfo-l1-1-0.dll",
            "api-ms-win-core-timezone-l1-1-0.dll", "api-ms-win-core-util-l1-1-0.dll",
            "api-ms-win-crt-conio-l1-1-0.dll", "api-ms-win-crt-convert-l1-1-0.dll",
            "api-ms-win-crt-environment-l1-1-0.dll", "api-ms-win-crt-filesystem-l1-1-0.dll",
            "api-ms-win-crt-heap-l1-1-0.dll", "api-ms-win-crt-locale-l1-1-0.dll",
            "api-ms-win-crt-math-l1-1-0.dll", "api-ms-win-crt-multibyte-l1-1-0.dll",
            "api-ms-win-crt-private-l1-1-0.dll", "api-ms-win-crt-process-l1-1-0.dll",
            "api-ms-win-crt-runtime-l1-1-0.dll", "api-ms-win-crt-stdio-l1-1-0.dll",
            "api-ms-win-crt-string-l1-1-0.dll", "api-ms-win-crt-utility-l1-1-0.dll",
            "decora_sse.dll", "glass.dll", "javafx_font.dll", "javafx_iio.dll",
            "msvcp140.dll", "msvcp140_1.dll", "msvcp140_2.dll", "prism_common.dll",
            "prism_d3d.dll", "prism_sw.dll", "ucrtbase.dll", "vcruntime140.dll",
            "vcruntime140_1.dll");

    private static final List<String> LINUX_LIBRARIES = List.of(
            "libjavafx_font.so", "libprism_sw.so", "libjavafx_font_freetype.so",
            "libglassgtk3.so", "libglass.so", "libjavafx_iio.so", "libprism_common.so",
            "libjavafx_font_pango.so", "libprism_es2.so", "libglassgtk2.so",
            "libdecora_sse.so");

    private static final List<String> MAC_LIBRARIES = List.of(
            "libjavafx_iio.dylib", "libglass.dylib", "libjavafx_font.dylib",
            "libprism_common.dylib", "libprism_es2.dylib", "libdecora_sse.dylib",
            "libprism_sw.dylib");

    public static void main(String[] args) {
        configureNativeLibraries();
        Application.launch(Main.class, args);
    }

    /** Extracts the native library set matching the current operating system. */
    private static void configureNativeLibraries() {
        String platform = getPlatform();
        List<String> libraries = switch (platform) {
            case "win" -> WINDOWS_LIBRARIES;
            case "linux" -> LINUX_LIBRARIES;
            case "mac", "mac-aarch64" -> MAC_LIBRARIES;
            default -> throw new IllegalStateException("Unsupported JavaFX platform: " + platform);
        };

        try {
            Path nativeDirectory = Files.createTempDirectory("kdb-javafx-natives");
            for (String library : libraries) {
                copyNativeLibrary(platform, library, nativeDirectory);
            }
            String libraryPath = System.getProperty("java.library.path", "");
            System.setProperty("java.library.path", nativeDirectory + System.getProperty("path.separator")
                    + libraryPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to prepare JavaFX native libraries.", e);
        }
    }

    /** Copies one bundled native library into the temporary native directory. */
    private static void copyNativeLibrary(String platform, String library, Path nativeDirectory)
            throws IOException {
        String resourcePath = "/kdb/natives/" + platform + "/" + library;
        try (InputStream input = Launcher.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Missing bundled JavaFX library: " + resourcePath);
            }
            Files.copy(input, nativeDirectory.resolve(library), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Returns the JavaFX platform identifier for the current machine. */
    private static String getPlatform() {
        String operatingSystem = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String architecture = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        if (operatingSystem.contains("mac")) {
            return architecture.contains("aarch64") || architecture.contains("arm64")
                    ? "mac-aarch64" : "mac";
        }
        if (operatingSystem.contains("win")) {
            return "win";
        }
        if (operatingSystem.contains("linux")) {
            return "linux";
        }
        return "unsupported";
    }
}
