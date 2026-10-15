package luckynoslacky.luckyresponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import luckynoslacky.ResponseContent;
import luckynoslacky.TaskContent;
import luckynoslacky.TextContent;
import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckytask.TodoTask;

/** Tests structured task responses. */
class LuckyNoTaskResponsesTest {
    /** Verifies task additions retain prose, task data, and CLI text. */
    @Test
    void added_taskAndCount_returnsStructuredTaskResponse() {
        TaskContent response = LuckyNoTaskResponses.added(new TodoTask("read book"), 1);

        assertEquals("Got one more thing to remember ah: \n"
                        + "  [📌][❗] read book\n"
                        + "Now you got 1 tasks to settle.",
                response.message());
        assertEquals("read book", response.taskViews().getFirst().description());
    }

    /** Verifies a date search retains the original task number and header. */
    @Test
    void listed_dateSearch_returnsTypedTaskContentAndExactMessage() {
        TodoTask readBook = new TodoTask("read book");
        TaskList allTasks = new TaskList();
        allTasks.addTask(new TodoTask("ignored task"));
        allTasks.addTask(readBook);
        TaskList taskList = allTasks.createView(
                LocalDate.of(2026, 8, 26), task -> task == readBook);

        TaskContent response = assertInstanceOf(
                TaskContent.class, LuckyNoTaskResponses.listed(taskList));

        assertEquals("Nah, all these things you need to do on: Aug 26 2026\n"
                        + "2.[📌][❗] read book",
                response.message());
        assertEquals(2, response.taskViews().getFirst().taskNumber());
    }

    /** Verifies an empty list returns plain text instead of an empty task response. */
    @Test
    void listed_emptyTaskList_returnsEmptyListTextContent() {
        ResponseContent response = LuckyNoTaskResponses.listed(new TaskList());

        assertInstanceOf(TextContent.class, response);
        assertEquals("Chill lah bro got nothing yet lah!", response.message());
    }
}
