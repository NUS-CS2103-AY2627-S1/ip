package stewie.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies platform selection and the real child-JVM handoff without requiring a graphical desktop.
 */
class UniversalLauncherTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void selectPlatform_matchesSupportedSystemsAndAliases() {
        assertEquals("windows-x64", UniversalLauncher.selectPlatform("Windows 11", "amd64"));
        assertEquals("windows-x64", UniversalLauncher.selectPlatform("Windows 11 Pro N", "x86_64"));
        assertEquals("mac-x64", UniversalLauncher.selectPlatform("Mac OS X", "x86_64"));
        assertEquals("mac-aarch64", UniversalLauncher.selectPlatform("Mac OS X", "aarch64"));
        assertEquals("mac-aarch64", UniversalLauncher.selectPlatform("Darwin", "arm64"));
        assertEquals("linux-x64", UniversalLauncher.selectPlatform("Linux", "amd64"));
        assertEquals("linux-aarch64", UniversalLauncher.selectPlatform("Linux", "aarch64"));
        assertEquals("linux-aarch64", UniversalLauncher.selectPlatform("LINUX", "ARM64"));
    }

    @Test
    void selectPlatform_rejectsUnsupportedSystemsAndArchitectures() {
        for (String[] platform : new String[][] {
            {"Windows 11", "aarch64"}, {"Linux", "i386"}, {"Linux", "riscv64"}, {"FreeBSD", "amd64"}
        }) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> UniversalLauncher.selectPlatform(platform[0], platform[1]));
            assertTrue(exception.getMessage().contains("Unsupported platform"));
        }
    }

    @Test
    void launchBundled_reportsMissingPackage() {
        IOException exception = assertThrows(IOException.class,
                () -> UniversalLauncher.launchBundled("missing", new String[0]));
        assertTrue(exception.getMessage().contains("Missing bundled runtime"));
    }

    @Test
    void launchArchive_preservesArgumentsDirectoryJavaVersionAndExitCode() throws Exception {
        Path archive = temporaryDirectory.resolve("application with spaces.jar");
        Path record = temporaryDirectory.resolve("child output.txt");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, Probe.class.getName());
        String resource = Probe.class.getName().replace('.', '/') + ".class";
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(archive), manifest);
                InputStream input = Probe.class.getResourceAsStream("/" + resource)) {
            output.putNextEntry(new JarEntry(resource));
            input.transferTo(output);
            output.closeEntry();
        }
        assertEquals(7, UniversalLauncher.launchArchive(archive,
                new String[] {record.toString(), "argument with spaces", "မြန်မာ"}));
        assertEquals(Path.of("").toAbsolutePath() + "\n25\nargument with spaces\nမြန်မာ", Files.readString(record));
        Files.delete(archive);
    }

    /**
     * Provides a standalone child process that records its environment and exits with a distinct status.
     */
    public static final class Probe {
        /**
         * Writes the inherited directory, Java version, and arguments for the handoff assertion.
         *
         * @param args Output file followed by two arguments to record.
         * @throws IOException If the output file cannot be written.
         */
        public static void main(String[] args) throws IOException {
            Files.writeString(Path.of(args[0]), Path.of("").toAbsolutePath() + "\n"
                    + Runtime.version().feature() + "\n" + args[1] + "\n" + args[2]);
            System.exit(7);
        }
    }
}
