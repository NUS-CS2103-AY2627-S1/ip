package es;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TaskListFindTest {
    @Test
    void find_matchesCaseInsensitivelyAndPreservesIndexes() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy bread"));
        tasks.add(new Todo("return BOOK"));

        assertEquals(new ArrayList<>(java.util.List.of(0, 2)), tasks.find("book"));
        assertEquals(new ArrayList<>(java.util.List.of(1)), tasks.find("BREAD"));
        assertEquals(new ArrayList<>(), tasks.find("pro"));
        assertEquals(new ArrayList<>(), tasks.find("train"));
    }
}
