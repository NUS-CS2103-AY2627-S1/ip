package gary.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MacNativeLibrariesTest {
    @Test
    void resourceDirectory_supportedArchitectures_selectsMatchingLibraries() {
        assertEquals("/natives/mac-aarch64/", MacNativeLibraries.resourceDirectory("aarch64"));
        assertEquals("/natives/mac-aarch64/", MacNativeLibraries.resourceDirectory("arm64"));
        assertEquals("/natives/mac-x86_64/", MacNativeLibraries.resourceDirectory("x86_64"));
        assertEquals("/natives/mac-x86_64/", MacNativeLibraries.resourceDirectory("AMD64"));
    }

    @Test
    void resourceDirectory_unsupportedArchitecture_rejectsInsteadOfLoadingWrongLibraries() {
        assertThrows(IllegalArgumentException.class, () -> MacNativeLibraries.resourceDirectory("riscv64"));
    }

    @Test
    void requiresSoftwareRenderer_intelMac_usesStableRendererFallback() {
        assertTrue(MacNativeLibraries.requiresSoftwareRenderer("x86_64"));
        assertTrue(MacNativeLibraries.requiresSoftwareRenderer("AMD64"));
        assertFalse(MacNativeLibraries.requiresSoftwareRenderer("aarch64"));
        assertFalse(MacNativeLibraries.requiresSoftwareRenderer("arm64"));
    }
}
