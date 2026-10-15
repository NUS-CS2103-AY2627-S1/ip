package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckystorage.CsvSaver;

/** Tests task-list display and search behavior provided by {@link TaskMaster}. */
class TaskMasterListingTest {
    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);
    private static final LocalDateTime EVENT_START =
            LocalDateTime.of(2026, 8, 6, 14, 0);
    private static final LocalDateTime EVENT_END =
            LocalDateTime.of(2026, 8, 6, 16, 0);

    @TempDir
    Path temporaryDirectory;

    /** Verifies the response for an empty task list. */
    @Test
    void listTasks_emptyTaskList_returnsEmptyTaskListMessage() {
        TaskMaster taskMaster = createTaskMaster();

        assertEquals("Chill lah bro got nothing yet lah!",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that a valid task is added and listed. */
    @Test
    void addTask_validTask_includesTaskInList() {
        TaskMaster taskMaster = createTaskMaster();

        taskMaster.addTask(new TodoTask("read book"));

        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that listing preserves task insertion order. */
    @Test
    void listTasks_multipleTasks_preservesOrder() {
        TaskMaster taskMaster = createTaskMaster();

        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new TodoTask("return book"));

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] read book\n"
                        + "2.[📌][❗] return book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that a previously returned list view is not mutated later. */
    @Test
    void listTasks_viewBeforeMutation_preservesEarlierSnapshot() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("first task"));

        TaskList earlierView = taskMaster.listTasks();
        taskMaster.addTask(new TodoTask("second task"));

        assertEquals(List.of("first task"),
                earlierView.getTaskViews().stream().map(TaskView::description).toList());
        assertEquals(List.of("first task", "second task"),
                taskMaster.listTasks().getTaskViews().stream()
                        .map(TaskView::description).toList());
    }

    /** Verifies that all supported task types are formatted in a list. */
    @Test
    void listTasks_differentTaskTypes_formatsAllTypes() {
        TaskMaster taskMaster = createTaskMaster();

        taskMaster.addTask(new TodoTask("borrow book"));
        taskMaster.addTask(new DeadlineTask("return book", DEADLINE));
        taskMaster.addTask(new EventTask("project meeting", EVENT_START, EVENT_END));

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] borrow book\n"
                        + "2.[⏳][❗] return book (by: Sun Dec 06 2026, 11.59pm)\n"
                        + "3.[📆][❗] project meeting (from: Thu Aug 06 2026, 2.00pm"
                        + " to: Thu Aug 06 2026, 4.00pm)",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that search returns deadlines and events on a date. */
    @Test
    void findTasks_matchingDate_returnsDeadlinesAndEvents() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 23, 59)));
        taskMaster.addTask(new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 25, 14, 0),
                LocalDateTime.of(2026, 8, 27, 16, 0)));

        assertEquals("Nah, all these things you need to do on: Aug 26 2026\n"
                        + "2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)\n"
                        + "3.[📆][❗] project meeting (from: Tue Aug 25 2026, 2.00pm"
                        + " to: Thu Aug 27 2026, 4.00pm)",
                LuckyNoTaskResponses.listed(
                        taskMaster.findTasks(LocalDateTime.of(2026, 8, 26, 0, 0))).message());
    }

    /** Verifies that search excludes ToDos and dates without matches. */
    @Test
    void findTasks_noMatchingDateOrTodoOnly_returnsEmptyTaskListMessage() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 23, 59)));

        assertEquals("Chill lah bro got nothing yet lah!",
                LuckyNoTaskResponses.listed(
                        taskMaster.findTasks(LocalDateTime.of(2026, 8, 25, 0, 0))).message());
        assertEquals("Chill lah bro got nothing yet lah!",
                LuckyNoTaskResponses.listed(
                        taskMaster.findTasks(LocalDateTime.of(2026, 8, 27, 0, 0))).message());
    }

    /** Verifies that description searches ignore letter case. */
    @Test
    void findTasks_descriptionQuery_matchesDescriptionsCaseInsensitively() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 23, 59)));
        taskMaster.addTask(new TodoTask("buy bread"));

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][❗] read book\n"
                        + "2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)",
                LuckyNoTaskResponses.listed(taskMaster.findTasks("BOOK")).message());
    }

    /** Verifies that description and date filters are applied together. */
    @Test
    void findTasks_descriptionAndDateQueries_returnsIntersection() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.addTask(new TodoTask("read book"));
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 23, 59)));
        taskMaster.addTask(new EventTask(
                "book meeting",
                LocalDateTime.of(2026, 8, 27, 14, 0),
                LocalDateTime.of(2026, 8, 27, 16, 0)));

        assertEquals("Nah, all these things you need to do on: Aug 26 2026\n"
                        + "2.[⏳][❗] return book (by: Wed Aug 26 2026, 11.59pm)",
                LuckyNoTaskResponses.listed(taskMaster.findTasks(
                        "book", LocalDateTime.of(2026, 8, 26, 0, 0))).message());
    }

    /** Creates a task master backed by a temporary CSV file. */
    private TaskMaster createTaskMaster() {
        return new TaskMaster(new CsvSaver(temporaryDirectory.resolve("tasks.csv")));
    }
}
