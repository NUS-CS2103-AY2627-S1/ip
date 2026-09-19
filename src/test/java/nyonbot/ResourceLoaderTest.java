package nyonbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ResourceLoaderTest {
    @Test
    void readTextFile_existingResource_returnsContents() {
        String withoutSlash = ResourceLoader.readTextFile("static/ascii-banner.txt");
        String withSlash = ResourceLoader.readTextFile("/static/ascii-banner.txt");

        assertFalse(withoutSlash.isBlank());
        assertEquals(withoutSlash, withSlash);
    }

    @Test
    void readTextFile_missingResource_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> ResourceLoader.readTextFile("static/missing.txt"));
    }
}
