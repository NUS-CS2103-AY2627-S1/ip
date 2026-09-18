package com.fireinyu.themyth.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.requests.Request;

/**
 * Unit tests for custom exception classes in {@link com.fireinyu.themyth.exceptions}.
 */
public class ExceptionTest {

    private static class ConcreteTweakingException extends TweakingException {
        ConcreteTweakingException(String message) {
            super(message);
        }
    }

    /**
     * Tests {@link TweakingException} formatting.
     */
    @Test
    public void tweakingException_formatsMessage() {
        TweakingException ex = new ConcreteTweakingException("something broke");
        assertEquals("Honey, I am clutching my pearls because something broke", ex.getMessage());
    }

    /**
     * Tests {@link FatalException} formatting.
     */
    @Test
    public void fatalException_formatsMessage() {
        FatalException ex = new FatalException("out of memory");
        assertEquals("DRAMA! The Myth has perished in absolute agony! Cause of this catastrophic demise: out of memory",
                ex.getMessage());
    }

    /**
     * Tests {@link FileAccessException} formatting.
     */
    @Test
    public void fileAccessException_formatsMessage() {
        FileAccessException ex = new FileAccessException("data/test.csv");
        assertEquals("Honey, I am clutching my pearls because "
                + "Ugh, I can't even get into \"data/test.csv\" right now! "
                + "Going strictly into memory-only diva mode! 💅",
                ex.getMessage());
    }

    /**
     * Tests {@link CorruptedTaskFileException} formatting.
     */
    @Test
    public void corruptedTaskFileException_formatsMessage() {
        CorruptedTaskFileException ex = new CorruptedTaskFileException("tasks.csv");
        assertEquals("Honey, I am clutching my pearls because "
                + "Ew! File \"tasks.csv\" is a total disaster zone of corrupted nonsense! "
                + "Memory-only diva mode activated! 💋",
                ex.getMessage());
    }

    /**
     * Tests {@link ArgumentFormatException} formatting.
     */
    @Test
    public void argumentFormatException_formatsMessage() {
        ArgumentFormatException ex = new ArgumentFormatException("number", "abc", "integer");
        assertEquals("Honey, I am clutching my pearls because "
                + "Babe, your number input \"abc\" is giving total chaos! "
                + "Serve it to me in integer format or not at all! 💁‍♀️",
                ex.getMessage());
    }

    /**
     * Tests {@link DateFormatException} formatting.
     */
    @Test
    public void dateFormatException_formatsMessage() {
        DateFormatException ex = new DateFormatException("2025-99-99");
        assertEquals("Honey, I am clutching my pearls because "
                + "Babe, your date input \"2025-99-99\" is giving total chaos! "
                + "Serve it to me in " + Defaults.DATE_INPUTFORMAT + " format or not at all! 💁‍♀️",
                ex.getMessage());
    }

    /**
     * Tests {@link ArugmentMismatchException} constructor with positional argument counts.
     */
    @Test
    public void arugmentMismatchException_positionalCounts_formatsMessage() {
        ArugmentMismatchException ex = new ArugmentMismatchException(2, 1);
        assertEquals("Honey, I am clutching my pearls because "
                + "Excuse me? I asked for 2 juicy arguments and you dared hand me 1? The audacity! 🙄",
                ex.getMessage());
    }

    /**
     * Tests {@link ArugmentMismatchException} constructor with keyword argument sets.
     */
    @Test
    public void arugmentMismatchException_keywordCollections_formatsMessage() {
        ArugmentMismatchException ex = new ArugmentMismatchException(Set.of("by"), Set.of("from", "to"));
        assertTrue(ex.getMessage().contains("Sweetie, I ordered {by}, but you handed me {"));
        assertTrue(ex.getMessage().contains("That is NOT what was on the menu! 💅"));
    }

    /**
     * Tests {@link InvalidCommandException} formatting with a request.
     */
    @Test
    public void invalidCommandException_formatsMessage() {
        Request req = Request.of("dance");
        InvalidCommandException ex = new InvalidCommandException(req);
        assertEquals("Honey, I am clutching my pearls because "
                + "Sweetheart, \"dance\"? That is NOT in my fabulous repertoire! Try harder, babe! 💅",
                ex.getMessage());
    }

    /**
     * Tests {@link WrongTypeException} formatting.
     */
    @Test
    public void wrongTypeException_formatsMessage() {
        WrongTypeException ex = new WrongTypeException(List.class);
        assertEquals("Honey, I am clutching my pearls because "
                + "Oh darling, wrong vibe entirely! I specifically requested a " + List.class.toString() + "! 💅",
                ex.getMessage());
    }
}
