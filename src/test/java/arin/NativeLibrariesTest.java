package arin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Checks architecture selection and native-resource completeness for the single release JAR.
 */
class NativeLibrariesTest {
    @ParameterizedTest
    @CsvSource({"Mac OS X, aarch64, mac-aarch64", "Mac OS X, arm64, mac-aarch64", "Mac OS X, x86_64, mac",
        "Windows 11, amd64, win", "Windows 10, x86_64, win", "Linux, amd64, linux", "Linux, x86_64, linux"})
    void getPlatform_supportedPlatform_selectsCorrectNatives(String osName, String architecture, String expected)
            throws Exception {
        assertEquals(expected, NativeLibraries.getPlatform(osName, architecture));
    }

    @ParameterizedTest
    @CsvSource({"Linux, aarch64", "Windows 11, x86", "FreeBSD, amd64", "Mac OS X, ppc"})
    void getPlatform_unsupportedPlatform_returnsActionableError(String osName, String architecture) {
        assertThrows(IOException.class, () -> NativeLibraries.getPlatform(osName, architecture));
    }

    @Test
    void resources_everyPlatform_hasIndexAndAllLibraries() throws Exception {
        for (String platformClass : List.of("win/WinApplication", "gtk/GtkApplication", "mac/MacApplication")) {
            assertNotNull(NativeLibraries.class.getResource("/com/sun/glass/ui/" + platformClass + ".class"));
        }
        for (String platform : List.of("win", "linux", "mac", "mac-aarch64")) {
            String directory = "/natives/" + platform + "/";
            try (InputStream input = NativeLibraries.class.getResourceAsStream(directory + "index.txt")) {
                assertNotNull(input, platform);
                List<String> libraries = new String(input.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
                assertTrue(libraries.size() >= 5, platform);
                for (String library : libraries) {
                    try (InputStream binary = NativeLibraries.class.getResourceAsStream(directory + library)) {
                        assertNotNull(binary, platform + "/" + library);
                        assertTrue(binary.readNBytes(16).length == 16, library);
                    }
                }
            }
        }
    }
}
