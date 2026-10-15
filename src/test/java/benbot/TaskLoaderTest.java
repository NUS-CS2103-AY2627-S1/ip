package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the command processor's task-list operations and responses. */
class TaskLoaderTest {
    @Test
    void constructor_nonPositiveCapacity_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskLoader(0));
        assertThrows(AssertionError.class, () -> new TaskLoader(-1));
    }

    @Test
    void getMaxTasks_configuredCapacity_returnsCapacity() {
        assertEquals(7, new TaskLoader(7).getMaxTasks());
    }

    @Test
    void addTask_todoDeadlineAndEvent_addsEachTask() {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = new Task[3];
        int[] taskCount = {0};

        loader.addTask("todo read book", tasks, taskCount, false);
        loader.addTask("deadline return book /by 1/12/2029", tasks, taskCount, false);
        loader.addTask("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                tasks, taskCount, false);

        assertEquals(3, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertEquals("deadline return book /by 1/12/2029", tasks[1].toStorageString());
        assertEquals("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                tasks[2].toStorageString());
    }

    @Test
    void addTask_markUnmarkAndDelete_updateRequestedTask() {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = new Task[3];
        int[] taskCount = {0};
        loader.addTask("todo first task", tasks, taskCount, false);
        loader.addTask("todo second task", tasks, taskCount, false);

        loader.addTask("mark 2", tasks, taskCount, false);
        assertTrue(tasks[1].isDone());

        loader.addTask("unmark 2", tasks, taskCount, false);
        assertFalse(tasks[1].isDone());

        loader.addTask("delete 1", tasks, taskCount, false);
        assertEquals(1, taskCount[0]);
        assertEquals("todo second task", tasks[0].toStorageString());
        assertNull(tasks[1]);
    }

    @Test
    void addTask_find_ignoresCaseAndDoesNotChangeTaskList() throws Exception {
        TaskLoader loader = new TaskLoader(3);
        Task[] tasks = {new Task("Read Book"), new Task("buy pen"), new Task("Book flight")};
        int[] taskCount = {3};

        String output = OutputCapture.capture(() ->
                loader.addTask("find BOOK", tasks, taskCount, true));

        assertEquals(3, taskCount[0]);
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] Read Book"));
        assertFalse(output.contains("2.[T][ ] buy pen"));
        assertTrue(output.contains("3.[T][ ] Book flight"));
        assertTrue(output.indexOf("1.[T][ ] Read Book")
                < output.indexOf("3.[T][ ] Book flight"));
    }

    @Test
    void addTask_listAndInvalidCommand_printExpectedResponses() throws Exception {
        TaskLoader loader = new TaskLoader(2);
        Task[] tasks = {new Task("read book"), null};
        int[] taskCount = {1};

        String listOutput = OutputCapture.capture(() ->
                loader.addTask("list", tasks, taskCount, true));
        String errorOutput = OutputCapture.capture(() ->
                loader.addTask("unknown", tasks, taskCount, true));

        assertTrue(listOutput.contains("Here are the tasks in your list:"));
        assertTrue(listOutput.contains("1.[T][ ] read book"));
        assertTrue(errorOutput.contains("I don't know what that means."));
        assertEquals(1, taskCount[0]);
    }

    @Test
    void addTask_blankCommand_reportsErrorAndLeavesTasksUnchanged() throws Exception {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() ->
                loader.addTask("", tasks, taskCount, true));

        assertEquals("Please enter a command." + System.lineSeparator(), output);
        assertEquals("Please enter a command.", loader.getLastResponse());
        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
    }

    @Test
    void addTask_whitespaceOnlyCommand_reportsErrorAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask(" \t  ", tasks, taskCount, false);

        assertEquals("Please enter a command.", loader.getLastResponse());
        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
    }

    @Test
    void addTask_surroundingWhitespace_stripsCommandBeforeDispatch() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask(" \t todo read book \t ", tasks, taskCount, false);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
    }

    @Test
    void addTask_outputDisabled_recordsResponseWithoutPrinting() throws Exception {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        String output = OutputCapture.capture(() ->
                loader.addTask("todo read book", tasks, taskCount, false));

        assertEquals("", output);
        assertTrue(loader.getLastResponse().contains("I've added this task"));
        assertEquals(1, taskCount[0]);
    }

    @Test
    void addTask_invalidTaskNumberAndFullList_leaveTasksUnchanged() throws Exception {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = {new Task("read book")};
        int[] taskCount = {1};

        String invalidNumberOutput = OutputCapture.capture(() ->
                loader.addTask("mark 2", tasks, taskCount, true));
        String fullListOutput = OutputCapture.capture(() ->
                loader.addTask("todo another task", tasks, taskCount, true));

        assertTrue(invalidNumberOutput.contains("That task number does not exist."));
        assertTrue(fullListOutput.contains("Sorry, the task list is full."));
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
    }

    @Test
    void addTask_malformedTaskNumbers_reportSpecificErrorsAndLeaveTasksUnchanged() {
        TaskLoader loader = new TaskLoader(2);
        Task[] tasks = {new Task("read book"), null};
        int[] taskCount = {1};

        loader.addTask("mark", tasks, taskCount, false);
        assertEquals("Use: mark TASK_NUMBER", loader.getLastResponse());

        loader.addTask("unmark one", tasks, taskCount, false);
        assertEquals("The task number must be a whole number.", loader.getLastResponse());

        loader.addTask("mark +1", tasks, taskCount, false);
        assertEquals("The task number must be a whole number.", loader.getLastResponse());

        loader.addTask("delete 0", tasks, taskCount, false);
        assertEquals("That task number does not exist.", loader.getLastResponse());

        loader.addTask("delete 1 extra", tasks, taskCount, false);
        assertEquals("Use: delete TASK_NUMBER", loader.getLastResponse());

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertFalse(tasks[0].isDone());
    }

    @Test
    void addTask_missingDescription_reportsUsageAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("todo", tasks, taskCount, false);
        assertEquals("Use: todo DESCRIPTION", loader.getLastResponse());

        loader.addTask("find", tasks, taskCount, false);
        assertEquals("Use: find KEYWORD", loader.getLastResponse());

        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
    }

    @Test
    void addTask_invalidDeadlineStructure_reportsUsageAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        String[] invalidCommands = {
            "deadline /by 1/12/2029",
            "deadline return book 1/12/2029",
            "deadline return book /by"
        };

        for (String command : invalidCommands) {
            loader.addTask(command, tasks, taskCount, false);
            assertEquals("Use: deadline DESCRIPTION /by DATE", loader.getLastResponse());
        }
        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
    }

    @Test
    void addTask_noMatchingTasks_returnsOnlyHeadingAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(2);
        Task[] tasks = {new Task("read book"), null};
        int[] taskCount = {1};

        loader.addTask("find holiday", tasks, taskCount, false);

        assertEquals("Here are the matching tasks in your list:", loader.getLastResponse());
        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
    }

    @Test
    void addTask_findKeywordWithRepeatedWhitespace_matchesNormalizedDescription() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = {new Task("read book")};
        int[] taskCount = {1};

        loader.addTask("find read \t  book", tasks, taskCount, false);

        assertTrue(loader.getLastResponse().contains("1.[T][ ] read book"));
        assertEquals(1, taskCount[0]);
    }

    @Test
    void addTask_duplicateCanonicalTasks_reportsErrorAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(6);
        Task[] tasks = new Task[6];
        int[] taskCount = {0};
        String[][] equivalentCommandPairs = {
            {"todo read book", "todo read   book"},
            {"deadline return book /by 1/12/2029", "deadline return  book /by 1/12/2029"},
            {
                "event study /from 2/12/2029 1800 /to 2/12/2029 2000",
                "event study /from 2/12/2029  1800 /to 2/12/2029 2000"
            }
        };

        for (String[] commandPair : equivalentCommandPairs) {
            loader.addTask(commandPair[0], tasks, taskCount, false);
            int taskCountBeforeDuplicate = taskCount[0];

            loader.addTask(commandPair[1], tasks, taskCount, false);

            assertEquals("That task is already in your list.", loader.getLastResponse());
            assertEquals(taskCountBeforeDuplicate, taskCount[0]);
        }
        assertEquals(3, taskCount[0]);
        assertNull(tasks[3]);
    }

    @Test
    void addTask_deleteLastTask_removesTaskWithoutShiftingPastArrayEnd() {
        TaskLoader loader = new TaskLoader(2);
        Task[] tasks = {new Task("read book"), new Task("buy pen")};
        int[] taskCount = {2};

        loader.addTask("delete 2", tasks, taskCount, false);

        assertEquals(1, taskCount[0]);
        assertEquals("todo read book", tasks[0].toStorageString());
        assertNull(tasks[1]);
        assertTrue(loader.getLastResponse().contains("Now you have 1 task in the list."));
    }

    @Test
    void addTask_byeWithoutArguments_requestsExit() {
        TaskLoader loader = new TaskLoader(1);

        assertTrue(loader.addTask("bye", new Task[1], new int[] {0}, false));
        assertFalse(loader.addTask("bye later", new Task[1], new int[] {0}, false));
    }

    @Test
    void addTask_invalidEventStructure_rejectsCommand() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        String[] invalidCommands = {
            "event /from 1/1/2029 /to 2/1/2029",
            "event meeting 1/1/2029 /to 2/1/2029",
            "event meeting /from 1/1/2029 2/1/2029",
            "event meeting /to 2/1/2029 /from 1/1/2029",
            "event meeting /from /to 2/1/2029",
            "event meeting /from 1/1/2029 /to"
        };

        for (String command : invalidCommands) {
            loader.addTask(command, tasks, taskCount, false);
            assertEquals("Use: event DESCRIPTION /from START /to END", loader.getLastResponse());
        }
        assertEquals(0, taskCount[0]);
    }

    @Test
    void addTask_repeatedSchedulingMarkers_reportsUsageAndLeavesTasksUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("deadline return book /by 1/12/2029 /by 2/12/2029",
                tasks, taskCount, false);
        assertEquals("Use: deadline DESCRIPTION /by DATE", loader.getLastResponse());

        loader.addTask("event meeting /from 1/12/2029 /from 2/12/2029 /to 3/12/2029",
                tasks, taskCount, false);
        assertEquals("Use: event DESCRIPTION /from START /to END", loader.getLastResponse());

        loader.addTask("event meeting /from 1/12/2029 /to 2/12/2029 /to 3/12/2029",
                tasks, taskCount, false);
        assertEquals("Use: event DESCRIPTION /from START /to END", loader.getLastResponse());
        assertEquals(0, taskCount[0]);
        assertNull(tasks[0]);
    }

    @Test
    void addTask_taskCountExceedsCapacity_throwsAssertionError() {
        TaskLoader loader = new TaskLoader(1);

        assertThrows(AssertionError.class, () ->
                loader.addTask("list", new Task[1], new int[] {2}, false));
    }

    @Test
    void addTask_invalidInternalState_throwsAssertionError() {
        TaskLoader loader = new TaskLoader(2);
        Task[] validTasks = new Task[2];
        int[] validTaskCount = {0};

        assertThrows(AssertionError.class, () ->
                loader.addTask(null, validTasks, validTaskCount, false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", null, validTaskCount, false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", new Task[1], validTaskCount, false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", validTasks, null, false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", validTasks, new int[0], false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", validTasks, new int[] {-1}, false));
        assertThrows(AssertionError.class, () ->
                loader.addTask("list", validTasks, new int[] {1}, false));
    }

    @Test
    void addTask_contactCommands_manageContactsSeparatelyFromTasks() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("contact Alice Tan /phone 91234567 /email alice@example.com",
                tasks, taskCount, false);
        loader.addTask("contact Bob /phone 87654321 /email bob@example.com",
                tasks, taskCount, false);
        loader.addTask("contacts", tasks, taskCount, false);

        assertEquals(0, taskCount[0]);
        assertTrue(loader.getLastResponse().contains(
                "1.[C] Alice Tan (phone: 91234567; email: alice@example.com)"));
        assertTrue(loader.getLastResponse().contains(
                "2.[C] Bob (phone: 87654321; email: bob@example.com)"));

        loader.addTask("delete-contact 1", tasks, taskCount, false);
        loader.addTask("contacts", tasks, taskCount, false);

        assertFalse(loader.getLastResponse().contains("Alice Tan"));
        assertTrue(loader.getLastResponse().contains("1.[C] Bob"));
    }

    @Test
    void addTask_invalidContactCommands_leaveContactsUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        String[] invalidCommands = {
            "contact Alice /email alice@example.com /phone 91234567",
            "contact Alice /phone /email alice@example.com",
            "contact Alice /phone 91234567 /email",
            "delete-contact one",
            "delete-contact 1"
        };

        for (String command : invalidCommands) {
            loader.addTask(command, tasks, taskCount, false);
        }
        loader.addTask("contacts", tasks, taskCount, false);

        assertEquals("Here are your contacts:", loader.getLastResponse());
    }

    @Test
    void addTask_noteCommands_manageNotesSeparatelyFromTasks() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("note Watch Arrival", tasks, taskCount, false);
        loader.addTask("note Waist size is 82 cm", tasks, taskCount, false);
        loader.addTask("notes", tasks, taskCount, false);

        assertEquals(0, taskCount[0]);
        assertTrue(loader.getLastResponse().contains("1.[N] Watch Arrival"));
        assertTrue(loader.getLastResponse().contains("2.[N] Waist size is 82 cm"));

        loader.addTask("delete-note 1", tasks, taskCount, false);
        loader.addTask("notes", tasks, taskCount, false);

        assertFalse(loader.getLastResponse().contains("Watch Arrival"));
        assertTrue(loader.getLastResponse().contains("1.[N] Waist size is 82 cm"));
    }

    @Test
    void addTask_invalidNoteCommands_leaveNotesUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("note", tasks, taskCount, false);
        loader.addTask("delete-note one", tasks, taskCount, false);
        loader.addTask("delete-note 1", tasks, taskCount, false);
        loader.addTask("notes", tasks, taskCount, false);

        assertEquals("Here are your notes:", loader.getLastResponse());
    }

    @Test
    void addTask_expenseCommands_manageExpensesSeparatelyFromTasks() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("expense lunch /amount 12.50", tasks, taskCount, false);
        loader.addTask("expense bus fare /amount 2.40", tasks, taskCount, false);
        loader.addTask("expenses", tasks, taskCount, false);

        assertEquals(0, taskCount[0]);
        assertTrue(loader.getLastResponse().contains("1.[$] lunch ($12.50)"));
        assertTrue(loader.getLastResponse().contains("2.[$] bus fare ($2.40)"));
        assertTrue(loader.getLastResponse().contains("Total expenses: $14.90"));

        loader.addTask("delete-expense 1", tasks, taskCount, false);
        loader.addTask("expenses", tasks, taskCount, false);

        assertFalse(loader.getLastResponse().contains("lunch"));
        assertTrue(loader.getLastResponse().contains("1.[$] bus fare ($2.40)"));
        assertTrue(loader.getLastResponse().contains("Total expenses: $2.40"));
    }

    @Test
    void addTask_invalidExpenseCommands_leaveExpensesUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};

        loader.addTask("expense lunch /amount nope", tasks, taskCount, false);
        loader.addTask("expense lunch /amount -1", tasks, taskCount, false);
        loader.addTask("delete-expense 1", tasks, taskCount, false);
        loader.addTask("expenses", tasks, taskCount, false);

        assertEquals("Here are your expenses:" + System.lineSeparator()
                + "Total expenses: $0.00", loader.getLastResponse());
    }

    @Test
    void addTask_deleteFinalEntities_usesPluralZeroCounts() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        loader.addTask("contact Alice /phone 91234567 /email alice@example.com",
                tasks, taskCount, false);
        loader.addTask("note Watch Arrival", tasks, taskCount, false);
        loader.addTask("expense lunch /amount 5.00", tasks, taskCount, false);

        loader.addTask("delete-contact 1", tasks, taskCount, false);
        assertTrue(loader.getLastResponse().endsWith("Now you have 0 contacts."));
        loader.addTask("delete-note 1", tasks, taskCount, false);
        assertTrue(loader.getLastResponse().endsWith("Now you have 0 notes."));
        loader.addTask("delete-expense 1", tasks, taskCount, false);
        assertTrue(loader.getLastResponse().endsWith("Now you have 0 expenses."));
    }

    @Test
    void addTask_malformedEntityNumbers_reportSpecificErrorsAndLeaveEntitiesUnchanged() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        loader.addTask("contact Alice /phone 91234567 /email alice@example.com",
                tasks, taskCount, false);
        loader.addTask("note Watch Arrival", tasks, taskCount, false);
        loader.addTask("expense lunch /amount 5.00", tasks, taskCount, false);

        loader.addTask("delete-contact", tasks, taskCount, false);
        assertEquals("Use: delete-contact CONTACT_NUMBER", loader.getLastResponse());
        loader.addTask("delete-note 1 extra", tasks, taskCount, false);
        assertEquals("Use: delete-note NOTE_NUMBER", loader.getLastResponse());
        loader.addTask("delete-expense one", tasks, taskCount, false);
        assertEquals("The expense number must be a whole number.", loader.getLastResponse());
        loader.addTask("delete-contact +1", tasks, taskCount, false);
        assertEquals("The contact number must be a whole number.", loader.getLastResponse());

        assertEquals(3, loader.getStoredItemCount(0));
    }

    @Test
    void getExtensionStorageCommands_mixedEntities_returnsGroupedImmutableCommands() {
        TaskLoader loader = new TaskLoader(1);
        Task[] tasks = new Task[1];
        int[] taskCount = {0};
        loader.addTask("expense lunch /amount 5", tasks, taskCount, false);
        loader.addTask("note Watch Arrival", tasks, taskCount, false);
        loader.addTask("contact Alice /phone 91234567 /email alice@example.com",
                tasks, taskCount, false);

        List<String> commands = loader.getExtensionStorageCommands();

        assertEquals(List.of(
                "contact Alice /phone 91234567 /email alice@example.com",
                "note Watch Arrival",
                "expense lunch /amount 5.00"), commands);
        assertEquals(4, loader.getStoredItemCount(1));
        assertThrows(UnsupportedOperationException.class, () -> commands.add("note another"));
    }
}
