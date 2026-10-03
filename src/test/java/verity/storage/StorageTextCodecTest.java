package verity.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import verity.exception.VerityException;

/**
 * Tests encoding and decoding of multiline storage text.
 */
class StorageTextCodecTest {
    @Test
    void encodeAndDecode_newlineAndBackslash_roundTrips()
            throws VerityException {
        String original = "First line\nC:\\quotes";

        String encoded = StorageTextCodec.encode(original);

        assertEquals("First line\\nC:\\\\quotes", encoded);
        assertEquals(original, StorageTextCodec.decode(encoded));
    }

    @Test
    void decode_unknownEscape_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> StorageTextCodec.decode("invalid\\tvalue"));
    }

    @Test
    void decode_incompleteEscape_throwsVerityException() {
        assertThrows(
                VerityException.class,
                () -> StorageTextCodec.decode("invalid\\"));
    }
}
