package com.fireinyu.themyth;

import com.fireinyu.themyth.chatmodes.ChatMode;
import com.fireinyu.themyth.exceptions.FatalException;
import com.fireinyu.themyth.exceptions.TweakingException;
import com.fireinyu.themyth.requests.Request;
import com.fireinyu.themyth.requests.RequestParser;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.requests.events.InterruptEvent;
import com.fireinyu.themyth.responses.ExceptionResponse;
import com.fireinyu.themyth.responses.FatalResponse;
import com.fireinyu.themyth.responses.Response;

/**
 * The main app object and entry point of The Myth chatbot.
 */
public class TheMyth {

    private final RequestParser parser = new RequestParser();
    private final ChatMode chatMode;

    /**
     * Initializes the application using the shared default chat mode.
     */
    public TheMyth() {
        this(Defaults.STARTMODE);
    }

    /**
     * Initializes an instance of The Myth app with a specified ChatMode.
     *
     * @param chatMode the chat mode to use.
     */
    public TheMyth(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    /**
     * Starts the app.
     *
     * @return status message after starting the app.
     * @see Response
     */
    public Response start() {
        return interruptCycle(new InitRequest());
    }

    /**
     * Stops the app.
     *
     * @return status message after stopping the app.
     * @see Response
     */
    public Response stop() {
        return interruptCycle(new CloseRequest());
    }

    /**
     * Handles user input and returns the close response if the command requests exit.
     *
     * @param input input.
     * @return response to input.
     */
    public Response handleInput(String input) {
        Response response;
        try {
            Request request = parser.parse(input);
            response = chatMode.respondTo(request);
        } catch (TweakingException e) {
            response = new ExceptionResponse(e);
        } catch (FatalException e) {
            response = new FatalResponse(e);
        }
        if (response.doExit()) {
            return stop();
        }
        return response;
    }

    /**
     * Runs a single interrupt cycle of The Myth in response to an InterruptEvent .
     * Interrupt cycles originate from within the program.
     * The cause and details of interrupt are encapsulated in the InterruptEvent.
     *
     * @param event the interrupt event that caused this interrupt cycle.
     * @return response to the interrupt event.
     * @see  InterruptEvent
     * @see  ChatMode
     * @see  Response
     */
    private Response interruptCycle(InterruptEvent event) {
        try {
            return chatMode.respondTo(event);
        } catch (TweakingException e) {
            return new ExceptionResponse(e);
        } catch (FatalException e) {
            return new FatalResponse(e);
        }
    }
}
