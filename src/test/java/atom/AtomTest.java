package atom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AtomTest {
    @Test
    void greeting_isReturned() {
        assertEquals("Hi, I'm Atom! ('^')/ ✨", new Atom().getGreeting());
    }

    @Test
    void invalidInput_returnsUserFriendlyError() {
        assertEquals("Oh no! I don't know what that means.", new Atom().getResponse("unknown"));
    }
}
