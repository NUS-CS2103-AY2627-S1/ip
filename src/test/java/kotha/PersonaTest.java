package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PersonaTest {
    @Test
    void choose_selectorReturnsTrue_returnsRoyalPersona() {
        assertEquals(Persona.ROYAL, Persona.choose(() -> true));
    }

    @Test
    void choose_selectorReturnsFalse_returnsRudePersona() {
        assertEquals(Persona.RUDE, Persona.choose(() -> false));
    }

    @Test
    void choosePhrase_personasReturnTheirOwnPhrase() {
        assertEquals("Your Majesty", Persona.ROYAL.choosePhrase("Your Majesty", "dummy"));
        assertEquals("dummy", Persona.RUDE.choosePhrase("Your Majesty", "dummy"));
    }
}
