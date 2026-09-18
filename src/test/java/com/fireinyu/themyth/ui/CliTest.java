package com.fireinyu.themyth.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.TheMyth;
import com.fireinyu.themyth.chatmodes.ChatMode;
import com.fireinyu.themyth.exceptions.FatalException;
import com.fireinyu.themyth.requests.ExitRequest;
import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.responses.ExitResponse;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for {@link Cli}.
 */
public class CliTest {

    /**
     * Tests the command-line interface execution loop with normal, exception, fatal, and exit responses.
     */
    @Test
    public void cli_runLoop_handlesAllResponseTypesAndExits() {
        ChatMode mockMode = new ChatMode() {
            @Override
            public Response respondTo(Request request) {
                String cmd = request.getArg(0, String.class);
                if ("die".equals(cmd)) {
                    throw new FatalException("fatal cli test");
                }
                if (request instanceof ExitRequest) {
                    return new ExitResponse("Bye!");
                }
                if ("ok".equals(cmd)) {
                    return new Response("Everything is fine");
                }
                return super.respondTo(request);
            }
        };

        String simulatedInput = "ok\nunknown\ndie\nbye\n";
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        try {
            System.setIn(testIn);
            System.setOut(new PrintStream(testOut));

            TheMyth model = new TheMyth(mockMode);
            Cli cli = new Cli(model);
            cli.run();

            String output = testOut.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Haaaay superstar! The Myth has entered the room! ✨"));
            assertTrue(output.contains("Everything is fine"));
            assertTrue(output.contains("The Myth is clutching pearls"));
            assertFalse(output.contains("Bye!"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
