package com.fireinyu.themyth.chatmodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fireinyu.themyth.requests.AnswerRequest;
import com.fireinyu.themyth.requests.MarkRequest;
import com.fireinyu.themyth.requests.QuizRequest;
import com.fireinyu.themyth.requests.TodoRequest;
import com.fireinyu.themyth.requests.events.CloseRequest;
import com.fireinyu.themyth.requests.events.InitRequest;
import com.fireinyu.themyth.responses.Response;

/**
 * Unit tests for {@link QuizMode}.
 */
public class QuizModeTest {

    @TempDir
    Path tempDir;

    /**
     * Tests default constructor of QuizMode.
     */
    @Test
    public void defaultConstructor_instantiatesSuccessfully() {
        QuizMode mode = new QuizMode();
        assertNotNull(mode.getTaskList());
    }

    /**
     * Tests initialization, presenting quiz questions, answering, and closing.
     */
    @Test
    public void quizMode_lifecycleAndInteractions() {
        Path taskFile = tempDir.resolve("quiz-tasks.csv");
        QuizMode mode = new QuizMode(taskFile);

        Response initResponse = mode.respondTo(new InitRequest());
        assertEquals("The archives are unlocked and looking drop-dead gorgeous! All tasks loaded, honey! 💅✨",
                initResponse.getBody());
        assertEquals(1, mode.getTaskList().size());

        // Quiz request displays active quiz
        Response quizResponse = mode.respondTo(new QuizRequest(List.of("quiz"), Map.of()));
        assertFalse(quizResponse.getBody().isEmpty());

        // Marking the QuizTask triggers takeQuiz
        Response markQuizTask = mode.respondTo(new MarkRequest(List.of("mark", "1"), Map.of()));
        assertEquals(quizResponse.getBody(), markQuizTask.getBody());
        assertEquals(Response.Mood.NORMAL, markQuizTask.getMood());

        // Add a regular task and mark it
        mode.respondTo(new TodoRequest(List.of("todo", "study"), Map.of()));
        Response markRegularTask = mode.respondTo(new MarkRequest(List.of("mark", "2"), Map.of()));
        assertTrue(markRegularTask.getBody().contains(
                "Slay, honey! Slay! That task is officially conquered and looking iconic:"));
        assertEquals(Response.Mood.HAPPY, markRegularTask.getMood());

        // Wrong answer branch
        Response wrongAnswer = mode.respondTo(new AnswerRequest(List.of("answer", "999"), Map.of()));
        assertTrue(wrongAnswer.getBody().startsWith(
                "Oh honey, bless your gorgeous little heart, but that was NOT it!"));
        assertEquals(Response.Mood.ANGRY, wrongAnswer.getMood());

        // Correct answer branch (attempting options 0 to 4 until correct branch is exercised)
        boolean gotRight = false;
        for (int ans = 0; ans < 10; ans++) {
            Response answerRes = mode.respondTo(
                    new AnswerRequest(List.of("answer", String.valueOf(ans % 4)), Map.of())
            );
            if (answerRes.getBody().startsWith("DING DING DING!")) {
                assertEquals(Response.Mood.HAPPY, answerRes.getMood());
                gotRight = true;
                break;
            }
        }
        assertTrue(gotRight);

        // Close request removes the quiz task and saves
        Response closeResponse = mode.respondTo(new CloseRequest());
        assertEquals("Curtain down, beauties! But someone forgot my grand finale encore! 💅",
                closeResponse.getBody());
    }
}
