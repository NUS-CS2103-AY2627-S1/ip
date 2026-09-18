package com.fireinyu.themyth.chatmodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.InvalidCommandException;
import com.fireinyu.themyth.requests.AnswerRequest;
import com.fireinyu.themyth.requests.AtRequest;
import com.fireinyu.themyth.requests.DeadlineRequest;
import com.fireinyu.themyth.requests.DeleteRequest;
import com.fireinyu.themyth.requests.DueRequest;
import com.fireinyu.themyth.requests.EventRequest;
import com.fireinyu.themyth.requests.ExitRequest;
import com.fireinyu.themyth.requests.FindRequest;
import com.fireinyu.themyth.requests.ListRequest;
import com.fireinyu.themyth.requests.MarkRequest;
import com.fireinyu.themyth.requests.QuizRequest;
import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.requests.TodoRequest;
import com.fireinyu.themyth.requests.UnmarkRequest;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for default behavior and dispatching in {@link ChatMode}.
 */
public class ChatModeTest {

    private static class ConcreteChatMode extends ChatMode {
    }

    private final ChatMode mode = new ConcreteChatMode();

    /**
     * Tests default respondToInit behavior.
     */
    @Test
    public void respondTo_initRequest_returnsNoHandlerResponse() {
        Response response = mode.respondTo(new InitRequest());
        assertEquals("Darling, I have arrived, but where is my dramatic entrance music?! ✨", response.getBody());
    }

    /**
     * Tests default respondToClose behavior.
     */
    @Test
    public void respondTo_closeRequest_returnsNoHandlerResponse() {
        Response response = mode.respondTo(new CloseRequest());
        assertEquals("Curtain down, beauties! But someone forgot my grand finale encore! 💅", response.getBody());
    }

    /**
     * Tests default respondToExit behavior.
     */
    @Test
    public void respondTo_exitRequest_returnsExitResponse() {
        Response response = mode.respondTo(new ExitRequest(List.of("bye"), Map.of()));
        assertTrue(response.doExit());
        assertEquals("Ciao, gorgeous! Try not to weep without my sparkle in your life~ 💋✨", response.getBody());
    }

    /**
     * Tests that unhandled request types throw {@link InvalidCommandException}.
     */
    @Test
    public void respondTo_unhandledRequests_throwInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(Request.of("unsupported")));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new AnswerRequest(List.of("answer", "1"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new AtRequest(List.of("at", "2025-01-01-00-00-00"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new DeadlineRequest(List.of("deadline", "task"),
                        Map.of("by", "2025-01-01-00-00-00"))));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new DeleteRequest(List.of("delete", "1"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new DueRequest(List.of("due", "2025-01-01-00-00-00"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new EventRequest(List.of("event", "task"),
                        Map.of("from", "2025-01-01-00-00-00", "to", "2025-01-01-01-00-00"))));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new FindRequest(List.of("find", "query"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new ListRequest(List.of("list"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new MarkRequest(List.of("mark", "1"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new QuizRequest(List.of("quiz"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new TodoRequest(List.of("todo", "task"), Map.of())));

        assertThrows(InvalidCommandException.class, () ->
                mode.respondTo(new UnmarkRequest(List.of("unmark", "1"), Map.of())));
    }
}
