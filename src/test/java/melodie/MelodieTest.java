package melodie;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MelodieTest {
    @Test
    public void personality_coreMessages_useMusicalConductorTheme() {
        Melodie melodie = new Melodie();

        assertTrue(melodie.getGreeting().contains(Melodie.NAME));
        assertTrue(melodie.getGreeting().contains("task conductor"));
        assertTrue(melodie.getResponse("bye").contains("Rhapsodically accomplished"));
        assertTrue(melodie.getResponse("unknown-command").contains("score"));
    }

    @Test
    public void responseStatus_errorThenSuccess_tracksLatestResponse() {
        Melodie melodie = new Melodie();

        melodie.getResponse("unknown-command");
        assertTrue(melodie.wasLastResponseError());

        melodie.getResponse("list");
        assertFalse(melodie.wasLastResponseError());
    }
}
