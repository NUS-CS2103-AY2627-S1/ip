package avon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StorageFieldCodecTest {
    @Test
    void escape_allReservedCharacters_usesSupportedEscapeSequences() {
        assertEquals("one\\\\two\\tthree\\nfour\\rfive",
                StorageFieldCodec.escape("one\\two\tthree\nfour\rfive"));
    }

    @Test
    void unescape_allSupportedSequences_restoresReservedCharacters() {
        assertEquals("one\\two\tthree\nfour\rfive",
                StorageFieldCodec.unescape("one\\\\two\\tthree\\nfour\\rfive"));
    }

    @Test
    void unescape_unsupportedOrIncompleteSequence_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> StorageFieldCodec.unescape("read\\q"));
        assertThrows(IllegalArgumentException.class, () -> StorageFieldCodec.unescape("read\\"));
    }

    @Test
    void escapeThenUnescape_arbitraryText_restoresOriginalText() {
        String originalText = "\tTo be,\\ or not\nto be\r";

        assertEquals(originalText,
                StorageFieldCodec.unescape(StorageFieldCodec.escape(originalText)));
    }
}
