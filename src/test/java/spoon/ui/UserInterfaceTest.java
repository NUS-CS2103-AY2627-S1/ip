package spoon.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.command.Command;
import spoon.task.Deadline;
import spoon.task.Event;
import spoon.task.TaskList;
import spoon.task.ToDo;

public class UserInterfaceTest {

    private UserInterface ui;
    private TaskList emptyList;
    private TaskList populatedList;

    @BeforeEach
    void setUp() throws Exception {
        ui = new UserInterface();
        emptyList = new TaskList();

        populatedList = new TaskList();
        populatedList.add(new ToDo("read book"));
        populatedList.add(new Deadline("submit essay", "01/01/0001"));
        populatedList.add(new Event("orientation", "01/01/0001 0001", "02/01/0001 0101"));
    }

    @Nested
    class BasicDisplayTests {

        @Test
        void showDivider_returnsDividerWithLineSeparator() {
            String expected = "-".repeat(80) + System.lineSeparator();
            assertEquals(expected, ui.showDivider());
        }

        @Test
        void showStart_containsWelcomeBannerAndIntroduction() {
            String output = ui.showStart();
            assertTrue(output.contains("~~~ Welcome to Spoon ~~~"));
            assertTrue(output.contains("Hello, I'm Spoon"));
        }

        @Test
        void showError_appendsLineSeparatorToMessage() {
            String errorMsg = "Something went wrong";
            assertEquals(errorMsg + System.lineSeparator(), ui.showError(errorMsg));
        }

        @Test
        void showHelp_returnsHelpGuideCommands() {
            String helpText = ui.showHelp();
            assertTrue(helpText.contains("todo <desc>"));
            assertTrue(helpText.contains("deadline <desc> /by"));
            assertTrue(helpText.contains("event <desc> /from"));
            assertTrue(helpText.contains("bye"));
        }

        @Test
        void showExit_returnsGoodbyeMessage() {
            assertEquals("Goodbye! Let's speak again soon!" + System.lineSeparator(), ui.showExit());
        }
    }

    @Nested
    class StorageMessageTests {

        @Test
        void showLoadSuccess_returnsSuccessMessage() {
            assertEquals("Tasks loaded! Time to get to work!" + System.lineSeparator(), ui.showLoadSuccess());
        }

        @Test
        void showLoadingError_formatsErrorMessage() {
            String reason = "File not found";
            String expected = "Error reading storage file: File not found" + System.lineSeparator();
            assertEquals(expected, ui.showLoadingError(reason));
        }

        @Test
        void showWritingError_formatsErrorMessage() {
            String reason = "Permission denied";
            String expected = "Error writing to storage file: Permission denied" + System.lineSeparator();
            assertEquals(expected, ui.showWritingError(reason));
        }

        @Test
        void showSave_returnsSaveSuccessMessage() {
            assertEquals("Tasks saved! Ready for next time!" + System.lineSeparator(), ui.showSave());
        }
    }

    @Nested
    class TaskListDisplayTests {

        @Test
        void showTaskList_emptyList_returnsListEmptyMessage() {
            assertEquals("Nothing here yet..." + System.lineSeparator(), ui.showTaskList(emptyList));
        }

        @Test
        void showTaskList_populatedList_returnsNumberedItems() {
            String output = ui.showTaskList(populatedList);
            assertTrue(output.startsWith("Here's your list!"));
            assertTrue(output.contains("1. " + populatedList.get(0).toString()));
            assertTrue(output.contains("2. " + populatedList.get(1).toString()));
            assertTrue(output.contains("3. " + populatedList.get(2).toString()));
        }

        @Test
        void showAdded_formatsTaskAndTotalCount() {
            ToDo task = new ToDo("borrow book");
            String output = ui.showAdded(task, 4);

            assertTrue(output.contains("I've added this task to the list! :)"));
            assertTrue(output.contains(task.toString()));
            assertTrue(output.contains("Now, you have 4 task(s)! \uD83D\uDC4D"));
        }

        @Test
        void showDeleted_formatsTaskAndRemainingCount() {
            ToDo task = new ToDo("borrow book");
            String output = ui.showDeleted(task, 2);

            assertTrue(output.contains("Okay, task deleted!"));
            assertTrue(output.contains(task.toString()));
            assertTrue(output.contains("Now, you have 2 task(s)! \uD83D\uDC4D"));
        }

        @Test
        void showMarked_showsSuccessHeaderAndTask() {
            ToDo task = new ToDo("buy bread");
            task.complete();
            String output = ui.showMarked(task);

            assertTrue(output.contains("YAYYYY, task complete!"));
            assertTrue(output.contains(task.toString()));
        }

        @Test
        void showUnmarked_showsIncompleteHeaderAndTask() {
            ToDo task = new ToDo("buy bread");
            String output = ui.showUnmarked(task);

            assertTrue(output.contains("Oops, there's more work to be done!"));
            assertTrue(output.contains(task.toString()));
        }
    }

    @Nested
    class FilteredTasksDisplayTests {

        @Test
        void showFilteredTasks_byDateEmpty_returnsListEmpty() {
            LocalDate date = LocalDate.of(1, 1, 1);
            assertEquals("Nothing here yet..." + System.lineSeparator(),
                    ui.showFilteredTasks(emptyList, date, Command.ON));
        }

        @Test
        void showFilteredTasks_byDatePopulated_formatsWithHeaderAndNumbers() {
            LocalDate date = LocalDate.of(1, 1, 1);
            String output = ui.showFilteredTasks(populatedList, date, Command.ON);

            assertTrue(output.contains("Filter criteria: on Jan 01 0001"));
            assertTrue(output.contains("1. " + populatedList.get(0).toString()));
        }

        @Test
        void showFilteredTasks_byWordEmpty_returnsListEmpty() {
            assertEquals("Nothing here yet..." + System.lineSeparator(),
                    ui.showFilteredTasks(emptyList, "book"));
        }

        @Test
        void showFilteredTasks_byWordPopulated_formatsWithContainsHeader() {
            String output = ui.showFilteredTasks(populatedList, "book");

            assertTrue(output.contains("Filter criteria: contains book"));
            assertTrue(output.contains("1. " + populatedList.get(0).toString()));
        }
    }
}
