package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KothaEngineTest {
    @Test
    void createGreeting_royalPersona_usesKothaNameAndShakespeareanTone() {
        KothaEngine engine = new KothaEngine(() -> true);

        String greeting = engine.createGreeting();

        assertTrue(greeting.contains("I am Kotha"));
        assertTrue(greeting.contains("thy devoted keeper"));
        assertEquals("royal", engine.getLastResponseStyle());
    }

    @Test
    void processCommand_rudePersona_usesGenZTone() {
        KothaEngine engine = new KothaEngine(() -> false);

        String response = engine.processCommand("not-a-command");

        assertTrue(response.contains("giving nonsense"));
        assertTrue(response.contains("dummy"));
        assertEquals("rude", engine.getLastResponseStyle());
    }
}
