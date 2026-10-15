package ff15.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests what {@link AiAssistant} sends the AI and how it tidies the reply. The
 * AI is a lambda returning a fixed reply, so nothing here needs a network or a
 * key, and the lambda can note down the prompts it was sent.
 */
public class AiAssistantTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 29);

    /** The prompts the fake AI was last sent: the system prompt, then the user's. */
    private final String[] lastPrompts = new String[2];

    /** Returns an assistant whose AI records its prompts and always replies {@code reply}. */
    private AiAssistant assistantReplying(String reply) {
        return new AiAssistant((systemPrompt, userPrompt) -> {
            lastPrompts[0] = systemPrompt;
            lastPrompts[1] = userPrompt;
            return reply;
        }, () -> TODAY);
    }

    // --- answering questions ------------------------------------------------------

    @Test
    public void answerQuestion_sendsTheQuestionWithTheCommandGuide() throws Exception {
        assistantReplying("Use todo.").answerQuestion("how do I add a task?");

        assertEquals("how do I add a task?", lastPrompts[1]);
        assertTrue(lastPrompts[0].contains(AiAssistant.COMMAND_GUIDE), lastPrompts[0]);
    }

    @Test
    public void answerQuestion_replyWithSpaceAround_isStripped() throws Exception {
        assertEquals("Use todo.", assistantReplying("  \nUse todo.\n ").answerQuestion("q"));
    }

    @Test
    public void answerQuestion_blankReply_throwsException() {
        assertThrows(FF15Exception.class, () -> assistantReplying("  ").answerQuestion("q"));
    }

    @Test
    public void answerQuestion_aiUnavailable_passesItsReasonOn() {
        AiAssistant assistant = new AiAssistant(new UnavailableAiHelper("no key"));

        FF15Exception e = assertThrows(FF15Exception.class, () -> assistant.answerQuestion("q"));
        assertEquals("no key", e.getMessage());
    }

    // --- suggesting commands ------------------------------------------------------

    @Test
    public void suggestCommand_sendsTheWeekAheadSoRelativeDatesCanBeWorkedOut() throws Exception {
        assistantReplying("todo x").suggestCommand("remind me tomorrow");

        assertEquals("remind me tomorrow", lastPrompts[1]);
        assertTrue(lastPrompts[0].contains(AiAssistant.describeWeekAhead(TODAY)), lastPrompts[0]);
        assertTrue(lastPrompts[0].contains(AiAssistant.COMMAND_GUIDE), lastPrompts[0]);
    }

    @Test
    public void describeWeekAhead_namesTheDayOfEveryDate() {
        // 2026-09-29 is a Tuesday, so the Friday to come is 2026-10-02: the case the AI
        // got wrong when it was given the date alone and left to work out the weekday.
        assertEquals("Today is Tuesday 2026-09-29. The next seven days are: "
                + "Wednesday 2026-09-30, Thursday 2026-10-01, Friday 2026-10-02, Saturday 2026-10-03, "
                + "Sunday 2026-10-04, Monday 2026-10-05, Tuesday 2026-10-06.",
                AiAssistant.describeWeekAhead(TODAY));
    }

    @Test
    public void suggestCommand_plainReply_isReturnedAsIs() throws Exception {
        assertEquals("deadline report /by 2026-09-30",
                assistantReplying("deadline report /by 2026-09-30").suggestCommand("r"));
    }

    @Test
    public void suggestCommand_noCommandFits_throwsException() {
        for (String reply : new String[] {"NONE", "none", "`NONE`", ""}) {
            assertThrows(FF15Exception.class, () -> assistantReplying(reply).suggestCommand("fly"), reply);
        }
    }

    @Test
    public void cleanCommand_wrappedInBackticks_unwrapsIt() {
        assertEquals("todo read book", AiAssistant.cleanCommand("`todo read book`"));
    }

    @Test
    public void cleanCommand_wrappedInQuotes_unwrapsIt() {
        assertEquals("todo read book", AiAssistant.cleanCommand("\"todo read book\""));
    }

    @Test
    public void cleanCommand_inACodeFence_unwrapsIt() {
        assertEquals("list", AiAssistant.cleanCommand("```\nlist\n```"));
        assertEquals("list", AiAssistant.cleanCommand("```bash\nlist\n```"));
    }

    @Test
    public void cleanCommand_explanationOnLaterLines_keepsOnlyTheFirstLine() {
        assertEquals("mark 2", AiAssistant.cleanCommand("mark 2\nThis marks the second task as done."));
    }

    @Test
    public void cleanCommand_quotesInsideTheCommand_areKept() {
        assertEquals("todo read \"Dune\"", AiAssistant.cleanCommand("todo read \"Dune\""));
        assertEquals("todo 'fix' the printer", AiAssistant.cleanCommand("todo 'fix' the printer"));
    }
}
