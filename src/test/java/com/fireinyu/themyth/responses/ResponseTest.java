package com.fireinyu.themyth.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.FatalException;
import com.fireinyu.themyth.exceptions.InvalidCommandException;
import com.fireinyu.themyth.exceptions.TweakingException;
import com.fireinyu.themyth.requests.Request;

/**
 * Unit tests for {@link Response} and its subclasses.
 */
public class ResponseTest {

    /**
     * Tests standard {@link Response} construction and properties.
     */
    @Test
    public void response_defaultExitIsFalse() {
        Response response = new Response("Operation completed");
        assertEquals("Operation completed", response.getBody());
        assertFalse(response.doExit());
        assertEquals(Response.Mood.NORMAL, response.getMood());
    }

    /**
     * Tests {@link Response} with custom mood.
     */
    @Test
    public void response_customMood() {
        Response happyResponse = new Response("Great job!", Response.Mood.HAPPY);
        assertEquals(Response.Mood.HAPPY, happyResponse.getMood());

        Response angryResponse = new Response("Stop that!", true, Response.Mood.ANGRY);
        assertEquals(Response.Mood.ANGRY, angryResponse.getMood());
        assertTrue(angryResponse.doExit());
    }

    /**
     * Tests {@link Response} with custom exit flag.
     */
    @Test
    public void response_customExitFlag() {
        Response response = new Response("Goodbye", true);
        assertEquals("Goodbye", response.getBody());
        assertTrue(response.doExit());
        assertEquals(Response.Mood.NORMAL, response.getMood());
    }

    /**
     * Tests {@link ExitResponse} always sets exit flag to true.
     */
    @Test
    public void exitResponse_setsExitTrue() {
        ExitResponse response = new ExitResponse("Bye!");
        assertEquals("Bye!", response.getBody());
        assertTrue(response.doExit());
    }

    /**
     * Tests {@link ExceptionResponse} wraps exception message, does not exit, and has ANGRY mood.
     */
    @Test
    public void exceptionResponse_containsMessageAndDoesNotExit() {
        TweakingException ex = new InvalidCommandException(Request.of("badcommand"));
        ExceptionResponse response = new ExceptionResponse(ex);
        assertEquals(ex.getMessage(), response.getBody());
        assertFalse(response.doExit());
        assertEquals(Response.Mood.ANGRY, response.getMood());
    }

    /**
     * Tests {@link FatalResponse} wraps fatal exception and flags for termination.
     */
    @Test
    public void fatalResponse_containsMessageAndExits() {
        FatalException fatal = new FatalException("critical error");
        FatalResponse response = new FatalResponse(fatal);
        assertEquals(fatal.getMessage(), response.getBody());
        assertTrue(response.doExit());
    }
}
