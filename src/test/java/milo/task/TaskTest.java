package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_newTask_notDoneByDefault() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStatusFlag());
    }

    @Test
    void getDescription_returnsValueGivenToConstructor() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    void markAsDone_newTask_statusIconBecomesX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("1", task.getStatusFlag());
    }

    @Test
    void markAsNotDone_doneTask_statusIconBecomesSpace() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getStatusFlag());
    }

    @Test
    void markAsDone_calledTwice_staysDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void toString_newTask_showsEmptyBracketAndDescription() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void toString_doneTask_showsXBracket() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    void toFileFormat_newTask_matchesPipeSeparatedFormat() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.toFileFormat());
    }

    @Test
    void toFileFormat_doneTask_flagIsOne() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toFileFormat());
    }
}
