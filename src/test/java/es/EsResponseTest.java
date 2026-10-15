package es;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EsResponseTest {
    @Test
    void conversation_matchesCaseInsensitivelyWithOptionalQuestionMark() {
        Es es = new Es();
        assertEquals("Pleasure to make your acquaintance.", es.getResponse("HELLO"));
        assertEquals("I am the librarian of Somniareves. You can call me Es.", es.getResponse("Who are you?"));
    }

    @Test
    void unknownInput_isReportedAsError() {
        Es es = new Es();
        assertEquals("... I didn't expect you to say that.", es.getResponse("hi there"));
        assertEquals("Perhaps you added an extra space somewhere?", es.getResponse(" hi"));
    }
}
