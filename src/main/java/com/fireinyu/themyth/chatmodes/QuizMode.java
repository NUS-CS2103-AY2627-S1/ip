package com.fireinyu.themyth.chatmodes;

import java.nio.file.Path;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.Quiz;
import com.fireinyu.themyth.exceptions.CheekySlackerException;
import com.fireinyu.themyth.requests.AnswerRequest;
import com.fireinyu.themyth.requests.DeleteRequest;
import com.fireinyu.themyth.requests.MarkRequest;
import com.fireinyu.themyth.requests.QuizRequest;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.Response;
import com.fireinyu.themyth.storage.QuizList;
import com.fireinyu.themyth.tasks.QuizTask;
import com.fireinyu.themyth.tasks.Task;

/**
 * Request-Response model that provides interactive trivia quiz capabilities in addition to task management.
 * Loads a collection of quiz questions, maintains an active quiz question, and allows the user to answer questions.
 *
 * @see TaskMode
 * @see Quiz
 * @see QuizTask
 */
public class QuizMode extends TaskMode {
    private final QuizTask quizTask = new QuizTask();
    private final QuizList quizzes = new QuizList();
    private Quiz activeQuiz;

    /**
     * Initializes a QuizMode with default configuration.
     */
    public QuizMode() {
        super();
    }

    /**
     * Initializes a QuizMode which syncs tasks with a given task file.
     *
     * @param taskFile Path to task file that syncs with this QuizMode.
     */
    public QuizMode(Path taskFile) {
        super(taskFile);
    }

    @Override
    protected Response respondToInit(InitRequest request) {
        Response response = super.respondToInit(request);
        quizzes.open(Defaults.QUIZFILE);
        activeQuiz = quizzes.getRandom();
        getTaskList().add(quizTask);
        return response;
    }

    @Override
    protected Response respondToDelete(DeleteRequest request) {
        int itemIndex = request.getArg(1, Integer.class) - 1;
        Task task = super.getTaskList().get(itemIndex);
        if (task == quizTask) {
            throw new CheekySlackerException();
        }
        return super.respondToDelete(request);
    }

    @Override
    protected Response respondToMark(MarkRequest request) {
        int itemIndex = request.getArg(1, Integer.class) - 1;
        Task task = super.getTaskList().get(itemIndex);
        if (task instanceof QuizTask) {
            return takeQuiz();
        }
        return super.mark(itemIndex);
    }

    @Override
    protected Response respondToCloseEvent(CloseRequest request) {
        getTaskList().remove(quizTask);
        return super.respondToCloseEvent(request);
    }

    @Override
    protected Response respondToQuiz(QuizRequest request) {
        return takeQuiz();
    }

    @Override
    protected Response respondToAnswer(AnswerRequest request) {
        Response response;
        if (request.getArg(1, Integer.class) == activeQuiz.getAnswerIndex()) {
            quizTask.mark();
            response = new Response(
                    "DING DING DING! You are beauty, you are grace, you nailed it right in the face! "
                    + "Absolutely iconic, honey! 🌟💖",
                    Response.Mood.HAPPY
            );
        } else {
            response = new Response(String.format(
                    "Oh honey, bless your gorgeous little heart, but that was NOT it! 🤦‍♀️ "
                    + "The tea is option %d! Better luck next time, babe! 💅",
                    activeQuiz.getAnswerIndex()),
                    Response.Mood.ANGRY);
        }
        activeQuiz = quizzes.getRandom();
        return response;
    }

    /**
     * Returns the current quiz question and its answer choices.
     */
    private Response takeQuiz() {
        return new Response(activeQuiz.toString());
    }

}
