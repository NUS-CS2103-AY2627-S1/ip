package judey.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DialogBoxTest {
    @Test
    void frameWithDividers_replacesExistingOuterDividers() {
        String response = "----------------------------------------\n"
                + "Oopsie! The find command is missing a search keyword.\n"
                + "\nTry: find <keyword>\n"
                + "----------------------------------------";

        assertEquals("----------------------------------------\n"
                        + "Oopsie! The find command is missing a search keyword.\n\nTry: find <keyword>\n"
                        + "----------------------------------------",
                DialogBox.frameWithDividers(response));
    }

    @Test
    void frameWithDividers_addsDividersToUnwrappedResponses() {
        assertEquals("----------------------------------------\nPurrfect!\n----------------------------------------",
                DialogBox.frameWithDividers("Purrfect!"));
    }

    @Test
    void isErrorResponse_ignoresLeadingDivider() {
        assertEquals(true, DialogBox.isErrorResponse(
                "----------------------------------------\nOopsie! The command is invalid.\n----------------------------------------"));
    }

    @Test
    void fallbackMessage_isUserFriendly() {
        assertEquals("Judey could not display this message. Please restart the application.",
                DialogBox.fallbackMessage());
    }

}
