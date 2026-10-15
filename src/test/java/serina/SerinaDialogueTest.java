package serina;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests shared dialogue personalization. */
public class SerinaDialogueTest {
    @Test
    public void address_message_prefixesCaptainName() {
        assertEquals("Captain Cutter, report ready.", SerinaDialogue.address("report ready."));
    }
}
