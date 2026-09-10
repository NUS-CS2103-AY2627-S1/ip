package surveyprogram.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the pause-free dialogue used by the graphical interface. */
class SecretTest {
    @Test
    void getOpeningDialogue_returnsCompleteIntroductionWithoutOuterWhitespace() {
        String dialogue = Secret.getOpeningDialogue();

        assertTrue(dialogue.startsWith("GREETINGS."));
        assertTrue(dialogue.contains("LET US POPULATE THE CONTENTS"));
        assertTrue(dialogue.endsWith("leave!)"));
        assertFalse(Character.isWhitespace(dialogue.charAt(0)));
        assertFalse(Character.isWhitespace(dialogue.charAt(dialogue.length() - 1)));
    }

    @Test
    void getClosingDialogue_returnsCompleteFarewellWithoutOuterWhitespace() {
        String dialogue = Secret.getClosingDialogue();

        assertTrue(dialogue.startsWith("THEN, UNTIL WE MEET ONCE MORE."));
        assertTrue(dialogue.endsWith("there was not a man here.)"));
        assertFalse(Character.isWhitespace(dialogue.charAt(0)));
        assertFalse(Character.isWhitespace(dialogue.charAt(dialogue.length() - 1)));
    }
}
