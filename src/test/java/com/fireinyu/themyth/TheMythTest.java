package com.fireinyu.themyth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.chatmodes.ChatMode;
import com.fireinyu.themyth.chatmodes.EchoMode;
import com.fireinyu.themyth.exceptions.FatalException;
import com.fireinyu.themyth.exceptions.TweakingException;
import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.ExceptionResponse;
import com.fireinyu.themyth.responses.FatalResponse;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for {@link TheMyth} application lifecycle and request orchestration.
 */
public class TheMythTest {

    @Test
    public void handleInput_fatalFailure_returnsCloseResponse() {
        ChatMode mode = new ChatMode() {
            @Override
            protected Response respondToRemaining(Request request) {
                throw new FatalException("input failure");
            }

            @Override
            protected Response respondToCloseEvent(CloseRequest request) {
                return new Response("closed", true);
            }
        };

        Response response = new TheMyth(mode).handleInput("unknown");
        assertEquals("closed", response.getBody());
        assertTrue(response.doExit());
    }

    /**
     * Tests standard startup, input handling, and exit flow using {@link EchoMode}.
     */
    @Test
    public void handleInput_normalAndExitFlow() {
        TheMyth myth = new TheMyth(new EchoMode());
        myth.start();

        Response response = myth.handleInput("dragons");
        assertEquals(
                "Oh honey, did you really just say \"dragons\"? Spill every last drop of tea right now! ☕✨",
                response.getBody());

        Response exitResponse = myth.handleInput("bye");
        assertTrue(exitResponse.doExit());
    }

    /**
     * Tests handling of {@link TweakingException} which returns an {@link ExceptionResponse}.
     */
    @Test
    public void handleInput_tweakingException_returnsExceptionResponse() {
        TheMyth myth = new TheMyth(new ChatMode() {});
        Response response = myth.handleInput("unsupported_command");

        assertInstanceOf(ExceptionResponse.class, response);
        assertTrue(response.getBody().contains(
                "Honey, I am clutching my pearls because Sweetheart, \"unsupported_command\"? "
                + "That is NOT in my fabulous repertoire! Try harder, babe! 💅"));
    }

    /**
     * Tests handling of {@link FatalException} during user input.
     */
    @Test
    public void handleInput_fatalException_returnsFatalResponse() {
        ChatMode fatalMode = new ChatMode() {
            @Override
            public Response respondTo(Request request) {
                throw new FatalException("disk exploded");
            }
        };

        TheMyth myth = new TheMyth(fatalMode);
        Response response = myth.handleInput("boom");

        assertInstanceOf(FatalResponse.class, response);
        assertTrue(response.getBody().contains(
                "DRAMA! The Myth has perished in absolute agony! Cause of this catastrophic demise: disk exploded"));
    }

    /**
     * Tests interrupt cycle recovering from {@link TweakingException} during startup.
     */
    @Test
    public void interruptCycle_tweakingException_handlesGracefully() {
        ChatMode tweakingInitMode = new ChatMode() {
            @Override
            protected Response respondToInit(InitRequest request) {
                throw new TweakingException("minor warning") {};
            }
        };

        TheMyth myth = new TheMyth(tweakingInitMode);
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            myth.start();
        } finally {
            System.setOut(originalOut);
        }
    }

    /**
     * Tests interrupt cycle encountering {@link FatalException} during startup.
     */
    @Test
    public void interruptCycle_fatalException_stopsApp() {
        ChatMode fatalInitMode = new ChatMode() {
            @Override
            protected Response respondToInit(InitRequest request) {
                throw new FatalException("fatal bootstrap");
            }
        };

        TheMyth myth = new TheMyth(fatalInitMode);
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            myth.start();
        } finally {
            System.setOut(originalOut);
        }
    }
}
