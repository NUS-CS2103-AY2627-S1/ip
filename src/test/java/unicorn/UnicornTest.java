package unicorn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import unicorn.task.TaskList;

public class UnicornTest {
    private final TaskList tasks = new TaskList();
    private final Unicorn unicorn = new Unicorn(tasks, ignoredTasks -> { });

    @Test
    public void getWelcomeMessage_returnsPrismaIntroduction() {
        assertEquals("Hello! I'm Prisma, your wise tech unicorn. "
                        + "Tell me what is on your quest list, and we'll make some magic.",
                unicorn.getWelcomeMessage());
    }

    @Test
    public void getResponse_hi_greetingAndAvailableCommandsDisplayed() {
        String response = unicorn.getResponse("hi");

        assertTrue(response.startsWith("Hi! Prisma the tech unicorn is online"));
        assertTrue(response.contains("  hi"));
        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by DATE"));
        assertTrue(response.contains("event DESCRIPTION /from START /to END"));
        assertTrue(response.contains("list"));
        assertTrue(response.contains("find KEYWORD"));
        assertTrue(response.contains("mark NUMBER"));
        assertTrue(response.contains("unmark NUMBER"));
        assertTrue(response.contains("delete NUMBER"));
        assertTrue(response.contains("bye"));
        assertEquals("hi", unicorn.getCommandType());
    }

    @Test
    public void getResponse_addAndListTasks_tasksAreStoredAndDisplayed() {
        assertTrue(unicorn.getResponse("todo read book").contains("[T] [ ] read book"));
        assertTrue(unicorn.getResponse("deadline submit report /by 2026-09-10").contains("[D] [ ] submit report"));

        String response = unicorn.getResponse("list");

        assertTrue(response.contains("1. [T] [ ] read book"));
        assertTrue(response.contains("2. [D] [ ] submit report"));
    }

    @Test
    public void getResponse_markUnmarkAndDelete_taskIsUpdated() {
        unicorn.getResponse("todo read book");

        assertTrue(unicorn.getResponse("mark 1").contains("[T] [X] read book"));
        assertTrue(unicorn.getResponse("unmark 1").contains("[T] [ ] read book"));
        assertTrue(unicorn.getResponse("delete 1").contains("quest log now holds 0 quests"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_findTask_onlyMatchingTasksAreDisplayed() {
        unicorn.getResponse("todo read book");
        unicorn.getResponse("todo buy groceries");

        String response = unicorn.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("buy groceries"));
    }

    @Test
    public void getResponse_invalidCommands_helpfulErrorsAreDisplayed() {
        assertTrue(unicorn.getResponse("mark abc").contains("valid quest number"));
        assertTrue(unicorn.getResponse("delete 1").contains("not appeared"));
        assertTrue(unicorn.getResponse("deadline report /by tomorrow").contains("Please use"));
        assertTrue(unicorn.getResponse("unknown").contains("signal was unclear"));
    }

    @Test
    public void getResponse_missingOrUnexpectedArguments_errorDisplayed() {
        String[] invalidCommands = {
            "", "todo", "find", "mark", "unmark", "delete", "deadline", "event", "hi there", "list extra",
            "bye now"
        };

        for (String invalidCommand : invalidCommands) {
            assertTrue(unicorn.getResponse(invalidCommand).startsWith("⚠ "),
                    "An invalid command should display a warning: " + invalidCommand);
            assertEquals("error", unicorn.getCommandType());
        }
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_malformedDeadline_errorDisplayedAndTaskNotAdded() {
        String[] invalidDeadlines = {
            "deadline submit report",
            "deadline /by 2026-09-30",
            "deadline submit report /by",
            "deadline submit report /by tomorrow",
            "deadline submit report /by 2026-09-30 /by 2026-10-01",
            "deadline submit report /by 2026-09-30 /by"
        };

        for (String invalidDeadline : invalidDeadlines) {
            assertTrue(unicorn.getResponse(invalidDeadline).startsWith("⚠ "),
                    "A malformed deadline should display a warning: " + invalidDeadline);
            assertEquals("error", unicorn.getCommandType());
        }
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_malformedEvent_errorDisplayedAndTaskNotAdded() {
        String[] invalidEvents = {
            "event project meeting",
            "event project meeting /from 2pm",
            "event project meeting /to 3pm /from 2pm",
            "event project meeting /from /to 3pm",
            "event project meeting /from 2pm /to",
            "event project meeting /from 2pm /from 3pm /to 4pm",
            "event project meeting /from 2pm /to 3pm /to 4pm",
            "event project meeting /from 2pm /to 3pm /from",
            "event project meeting /from 2pm /to 3pm /to"
        };

        for (String invalidEvent : invalidEvents) {
            assertTrue(unicorn.getResponse(invalidEvent).startsWith("⚠ "),
                    "A malformed event should display a warning: " + invalidEvent);
            assertEquals("error", unicorn.getCommandType());
        }
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_invalidEventDatesOrPeriod_errorDisplayedAndTaskNotAdded() {
        String[] invalidEvents = {
            "event launch /from 2026-02-30 0900 /to 2026-03-01 0900",
            "event launch /from 2026-09-30 1000 /to 2026-09-30 1000",
            "event launch /from 2026-09-30 1100 /to 2026-09-30 1000"
        };

        for (String invalidEvent : invalidEvents) {
            assertTrue(unicorn.getResponse(invalidEvent).startsWith("⚠ "),
                    "An invalid event period should display a warning: " + invalidEvent);
        }
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_duplicateTaskWithCosmeticDifferences_errorDisplayedAndTaskNotAdded() {
        unicorn.getResponse("todo Read   Book");

        String response = unicorn.getResponse("todo read book");

        assertTrue(response.contains("already in your quest log"));
        assertEquals("error", unicorn.getCommandType());
        assertEquals(1, tasks.size());
    }

    @Test
    public void getResponse_controlCharacter_errorDisplayedAndTaskNotAdded() {
        String response = unicorn.getResponse("todo read\nbook");

        assertTrue(response.contains("control characters"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_wellFormedEvent_taskIsAdded() {
        String response = unicorn.getResponse("event project meeting /from 2pm /to 3pm");

        assertTrue(response.contains("[E] [ ] project meeting (from: 2pm to: 3pm)"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getResponse_invalidCommand_responseIsClassifiedAndMarkedAsError() {
        String response = unicorn.getResponse("teleport home");

        assertTrue(response.startsWith("⚠ "));
        assertEquals("error", unicorn.getCommandType());

        unicorn.getResponse("deadline report /by tomorrow");
        assertEquals("error", unicorn.getCommandType());

        unicorn.getResponse("todo read book");
        assertEquals("todo", unicorn.getCommandType());
    }

    @Test
    public void getResponse_saveFails_changeIsRolledBack() {
        Unicorn failingUnicorn = new Unicorn(tasks, ignoredTasks -> {
            throw new IOException("Test save failure");
        });

        String response = failingUnicorn.getResponse("todo read book");

        assertTrue(response.contains("could not save your quests"));
        assertEquals(0, tasks.size());
    }

    @Test
    public void getResponse_supportedCommands_usePrismaPersonality() {
        assertTrue(unicorn.getResponse("list").contains("quest log is clear"));
        assertTrue(unicorn.getResponse("todo read book").contains("added to the rainbow"));
        assertTrue(unicorn.getResponse("mark 1").contains("quest conquered"));
        assertTrue(unicorn.getResponse("unmark 1").contains("Quest reopened"));
        assertTrue(unicorn.getResponse("find book").contains("unicorn senses"));
        assertTrue(unicorn.getResponse("bye").contains("Prisma"));
    }
}
