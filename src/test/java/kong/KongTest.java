package kong;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KongTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponseExecutesCommandAndKeepsUpdatedTaskList() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String addResponse = kong.getResponse("todo read book");
        String listResponse = kong.getResponse("list");

        assertTrue(addResponse.contains("Splendid addition! I have inscribed this task into your archives:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
        assertFalse(kong.isLastError());
    }

    @Test
    void getResponseReportsParsingErrorWithoutRequestingExit() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse("todo");

        assertTrue(response.contains("todo <description>"));
        assertFalse(kong.isExitRequested());
        assertTrue(kong.isLastError());
    }

    @Test
    void getResponseReportsDeleteCommandForInvalidDeleteNumber() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse("delete abc");

        assertTrue(response.contains("A delete command needs to be followed by a number."));
        assertTrue(kong.isLastError());
    }

    @Test
    void getResponseRecordsExitRequest() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse("bye");

        assertTrue(response.contains("Cheerio! Until our next scholarly consultation."));
        assertTrue(kong.isExitRequested());
    }

    @Test
    void getResponseListsTasksInChronologicalInsertionOrder() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());
        kong.getResponse("deadline later /by 2019-10-20");
        kong.getResponse("deadline earlier /by 2019-10-10");

        String response = kong.getResponse("list");

        assertTrue(response.indexOf("earlier") < response.indexOf("later"));
    }

    @Test
    void getResponseRejectsDuplicateTask() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());
        kong.getResponse("todo read book");

        String duplicateResponse = kong.getResponse("todo read book");

        assertTrue(duplicateResponse.contains("This task already exists in your list."));
        assertTrue(kong.isLastError());
    }

    @Test
    void getResponseReportsErrorForExtraArgumentInList() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse("list all");

        assertTrue(response.contains("The list command does not take any arguments."));
        assertTrue(kong.isLastError());
    }

    @Test
    void loadTasks_storageIoException_fallsBackToEmptyList() {
        Kong kong = new Kong(temporaryDirectory.toString());

        String response = kong.getResponse("list");

        assertTrue(response.contains("Your archive is pristine. There are currently no tasks in your list."));
    }

    @Test
    void getResponse_executesMarkUnmarkFindAndOnDateCommands() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());
        kong.getResponse("todo read book");
        kong.getResponse("deadline return book /by 2019-10-15");

        String markResponse = kong.getResponse("mark 1");
        assertTrue(markResponse.contains("Capital progress! I have marked this task as completed:"));

        String unmarkResponse = kong.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains(
                "Back to the drawing board! I have restored this task to pending status:"));

        String findResponse = kong.getResponse("find book");
        assertTrue(findResponse.contains("Eureka! Here are the matching tasks found in your archives:"));

        String onDateResponse = kong.getResponse("on 2019-10-15");
        assertTrue(onDateResponse.contains(
                "Consulting the ledger. Here are the deadlines and events on this date:"));
    }

    @Test
    void getResponse_unknownCommand_reportsErrorAndSetsLastError() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse("foobar");

        assertTrue(response.contains("Confound it! I do not recognise that command in my lexicon."));
        assertTrue(kong.isLastError());
    }

    @Test
    void getResponse_nullInput_reportsErrorAndSetsLastError() {
        Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = kong.getResponse(null);

        assertTrue(response.contains("Please enter a command."));
        assertTrue(kong.isLastError());
    }

    @Test
    void run_withExhaustedInputStream_terminatesWithoutException() {
        InputStream originalIn = System.in;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8));
            System.setIn(in);
            Kong kong = new Kong(temporaryDirectory.resolve("data/tasks.txt").toString());

            kong.run();
        } finally {
            System.setIn(originalIn);
        }
    }
}
