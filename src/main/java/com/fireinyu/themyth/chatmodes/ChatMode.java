package com.fireinyu.themyth.chatmodes;

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
import com.fireinyu.themyth.responses.ExitResponse;
import com.fireinyu.themyth.responses.Response;

/**
 * Request-Response model of the app. Responds to different types of Requests by returning Responses.
 *
 * @see Request
 * @see Response
 */
public abstract class ChatMode {

    /**
     * Provides a Response to a Request.
     *
     * @param request the Request to respond to.
     * @return the Response.
     * @see Request
     * @see Response
     */
    public Response respondTo(Request request) {
        return switch (request) {
            case InitRequest r -> respondToInit(r);
            case CloseRequest r -> respondToCloseEvent(r);
            case ExitRequest r -> respondToExit(r);
            case ListRequest r -> respondToList(r);
            case AtRequest r -> respondToAt(r);
            case DueRequest r -> respondToDue(r);
            case MarkRequest r -> respondToMark(r);
            case UnmarkRequest r -> respondToUnmark(r);
            case TodoRequest r -> respondToTodo(r);
            case DeadlineRequest r -> respondToDeadline(r);
            case EventRequest r -> respondToEvent(r);
            case DeleteRequest r -> respondToDelete(r);
            case FindRequest r -> respondToFind(r);
            case QuizRequest r -> respondToQuiz(r);
            case AnswerRequest r -> respondToAnswer(r);
            default -> respondToRemaining(request);
        };
    }

    /**
     * Handles an {@link AnswerRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToAnswer(AnswerRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles an {@link AtRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToAt(AtRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles the close request.
     *
     * @param request The close request.
     * @return A generic response indicating no handler is present.
     */
    protected Response respondToCloseEvent(CloseRequest request) {
        return new Response("Curtain down, beauties! But someone forgot my grand finale encore! 💅", true);
    }

    /**
     * Handles a {@link DeadlineRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToDeadline(DeadlineRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles a {@link DeleteRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToDelete(DeleteRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles a {@link DueRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToDue(DueRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles an {@link EventRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToEvent(EventRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles an {@link ExitRequest}.
     *
     * @param request The request to handle.
     * @return An {@link ExitResponse} with a goodbye message.
     */
    protected Response respondToExit(ExitRequest request) {
        return new ExitResponse("Ciao, gorgeous! Try not to weep without my sparkle in your life~ 💋✨");
    }

    /**
     * Handles a {@link FindRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToFind(FindRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles the initialization request.
     *
     * @param request The initialization request.
     * @return A generic response indicating no handler is present.
     */
    protected Response respondToInit(InitRequest request) {
        return new Response("Darling, I have arrived, but where is my dramatic entrance music?! ✨");
    }

    /**
     * Handles a {@link ListRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToList(ListRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles any request that is not explicitly handled by other methods in this chat mode.
     * Throws an {@link InvalidCommandException} by default.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToRemaining(Request request) {
        throw new InvalidCommandException(request);
    }

    /**
     * Handles a {@link MarkRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToMark(MarkRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles a {@link TodoRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToTodo(TodoRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles an {@link UnmarkRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToUnmark(UnmarkRequest request) {
        return respondToRemaining(request);
    }

    /**
     * Handles an {@link QuizRequest}.
     *
     * @param request The request to handle.
     * @return A {@link Response} object.
     */
    protected Response respondToQuiz(QuizRequest request) {
        return respondToRemaining(request);
    }
}
