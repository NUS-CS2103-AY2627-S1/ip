package spoon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SpoonTest {

    private static final String DATA_PATH = "./data/spoon.txt";
    private Spoon spoon;

    @BeforeEach
    void setUp() {
        cleanDataFile();
        spoon = new Spoon();
    }

    @AfterEach
    void tearDown() {
        cleanDataFile();
    }

    private void cleanDataFile() {
        File file = new File(DATA_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Nested
    class SystemCommands {

        @Test
        void getResponse_helpCommand_returnsHelpGuide() {
            String response = spoon.getResponse("help");
            assertTrue(response.contains("Here are the available commands:"));
            assertTrue(response.contains("todo <desc>"));
        }

        @Test
        void getResponse_byeCommand_returnsExitMessage() {
            String response = spoon.getResponse("bye");
            assertTrue(response.contains("Goodbye! Let's speak again soon!"));
        }

        @Test
        void getResponse_unknownCommand_returnsErrorMessage() {
            String response = spoon.getResponse("word");
            assertFalse(response.isBlank());
        }
    }

    @Nested
    class TaskLifecycleCommands {

        @Test
        void getResponse_addTodo_returnsSuccessAndIncreasesCount() {
            String response = spoon.getResponse("todo read book");
            assertTrue(response.contains("I've added this task to the list!"));
            assertTrue(response.contains("read book"));
            assertTrue(response.contains("Now, you have 1 task(s)!"));
        }

        @Test
        void getResponse_addDeadlineAndEvent_addsSuccessfully() {
            String deadlineRes = spoon.getResponse("deadline submit assignment /by 01/01/0001");
            assertTrue(deadlineRes.contains("I've added this task to the list!"));
            assertTrue(deadlineRes.contains("submit assignment"));

            String eventRes = spoon.getResponse("event orientation /from 01/01/0001 0001 /to 02/01/0001 0101");
            assertTrue(eventRes.contains("I've added this task to the list!"));
            assertTrue(eventRes.contains("orientation"));
        }

        @Test
        void getResponse_markAndUnmark_updatesStatus() {
            spoon.getResponse("todo borrow notes");

            String markRes = spoon.getResponse("mark 1");
            assertTrue(markRes.contains("YAYYYY, task complete!"));
            assertTrue(markRes.contains("[X]"));

            String unmarkRes = spoon.getResponse("unmark 1");
            assertTrue(unmarkRes.contains("Oops, there's more work to be done!"));
            assertTrue(unmarkRes.contains("[ ]"));
        }

        @Test
        void getResponse_deleteTask_removesTaskAndUpdatesCount() {
            spoon.getResponse("todo wash dishes");
            spoon.getResponse("todo clean room");

            String deleteRes = spoon.getResponse("delete 1");
            assertTrue(deleteRes.contains("Okay, task deleted!"));
            assertTrue(deleteRes.contains("wash dishes"));
            assertTrue(deleteRes.contains("Now, you have 1 task(s)!"));
        }

        @Test
        void getResponse_listTasks_showsAllCurrentTasks() {
            spoon.getResponse("todo first task");
            spoon.getResponse("todo second task");

            String listRes = spoon.getResponse("list");
            assertTrue(listRes.contains("Here's your list!"));
            assertTrue(listRes.contains("1. [T][ ] first task"));
            assertTrue(listRes.contains("2. [T][ ] second task"));
        }
    }

    @Nested
    class QueryAndFilterCommands {

        @Test
        void getResponse_findExistingKeyword_returnsMatchingTasks() {
            spoon.getResponse("todo read book");
            spoon.getResponse("todo return assignment");

            String findRes = spoon.getResponse("find book");
            assertTrue(findRes.contains("Filter criteria: contains book"));
            assertTrue(findRes.contains("read book"));
            assertFalse(findRes.contains("return assignment"));
        }

        @Test
        void getResponse_onDate_returnsMatchingEventsAndDeadlines() {
            spoon.getResponse("deadline essay /by 01/01/0001");
            spoon.getResponse("event party /from 01/01/0001 0001 /to 02/01/0001 0101");

            String onRes = spoon.getResponse("on 01/01/0001");
            assertTrue(onRes.contains("Filter criteria: on Jan 01 0001"));
            assertTrue(onRes.contains("essay"));
            assertTrue(onRes.contains("party"));
        }

        @Test
        void getResponse_byDate_returnsDeadlinesDueBeforeOrOnDate() {
            spoon.getResponse("deadline report /by 01/01/0001");

            String byRes = spoon.getResponse("by 01/01/0001");
            assertTrue(byRes.contains("Filter criteria: by Jan 01 0001"));
            assertTrue(byRes.contains("report"));
        }
    }

    @Nested
    class ErrorHandling {

        @Test
        void getResponse_missingArguments_returnsErrorMessage() {
            String todoError = spoon.getResponse("todo");
            assertFalse(todoError.isBlank());

            String deadlineError = spoon.getResponse("deadline task /by");
            assertFalse(deadlineError.isBlank());
        }

        @Test
        void getResponse_outOfBoundsIndex_returnsErrorMessage() {
            String errorResponse = spoon.getResponse("mark 10");
            assertFalse(errorResponse.isBlank());
        }

        @Test
        void getResponse_duplicateTask_returnsErrorMessage() {
            spoon.getResponse("todo identical task");
            String duplicateResponse = spoon.getResponse("todo identical task");
            assertFalse(duplicateResponse.isBlank());
            assertFalse(duplicateResponse.contains("I've added this task"));
        }
    }
}
