package yqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks Mac architecture selection and the actual native binaries bundled by Gradle.
 */
class JavaFxNativeLibrariesTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void macPlatformFor_supportedArchitectures_correctResourcesSelected() {
        assertEquals("mac-aarch64", JavaFxNativeLibraries.macPlatformFor("Mac OS X", "aarch64"));
        assertEquals("mac-aarch64", JavaFxNativeLibraries.macPlatformFor("Mac OS X", "arm64"));
        assertEquals("mac", JavaFxNativeLibraries.macPlatformFor("Mac OS X", "x86_64"));
        assertEquals("mac", JavaFxNativeLibraries.macPlatformFor("Mac OS X", "amd64"));
    }

    @Test
    void macPlatformFor_otherOperatingSystems_macSetupSkipped() {
        assertEquals("", JavaFxNativeLibraries.macPlatformFor("Windows 11", "amd64"));
        assertEquals("", JavaFxNativeLibraries.macPlatformFor("Linux", "amd64"));
    }

    @Test
    void macPlatformFor_unknownMacArchitecture_rejected() {
        assertThrows(IllegalStateException.class, () -> {
            JavaFxNativeLibraries.macPlatformFor("Mac OS X", "unknown");
        });
    }

    @Test
    void extractLibraries_appleSilicon_everyLibraryContainsArm64Code() throws IOException {
        assertNativeArchitecture("mac-aarch64", 0x0100000c);
    }

    @Test
    void extractLibraries_intelMac_everyLibraryContainsX86Code() throws IOException {
        assertNativeArchitecture("mac", 0x01000007);
    }

    @Test
    void extractLibraries_missingResources_clearErrorReported() {
        IOException exception = assertThrows(IOException.class, () -> {
            JavaFxNativeLibraries.extractLibraries("missing", temporaryDirectory);
        });
        assertEquals("Missing bundled JavaFX library: /javafx-natives/missing/libglass.dylib",
                exception.getMessage());
    }

    /** Verifies the Mach-O file header and CPU type of every extracted library. */
    private void assertNativeArchitecture(String platform, int expectedCpuType) throws IOException {
        JavaFxNativeLibraries.extractLibraries(platform, temporaryDirectory);
        try (var paths = Files.list(temporaryDirectory)) {
            var libraries = paths.toList();
            assertEquals(7, libraries.size());
            for (Path library : libraries) {
                ByteBuffer header = ByteBuffer.wrap(Files.readAllBytes(library)).order(ByteOrder.LITTLE_ENDIAN);
                assertEquals(0xfeedfacf, header.getInt(), library.toString());
                assertEquals(expectedCpuType, header.getInt(), library.toString());
            }
        }
    }
}
