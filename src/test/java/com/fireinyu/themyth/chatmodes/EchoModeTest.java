package com.fireinyu.themyth.chatmodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for {@link EchoMode}.
 */
public class EchoModeTest {

    /**
     * Tests that EchoMode responds by asking for more details about the given command.
     */
    @Test
    public void respondToRemaining_echoesQuery() {
        EchoMode echoMode = new EchoMode();
        Response response = echoMode.respondTo(Request.of("dragons"));
        assertEquals(
                "Oh honey, did you really just say \"dragons\"? Spill every last drop of tea right now! ☕✨",
                response.getBody());
    }
}
