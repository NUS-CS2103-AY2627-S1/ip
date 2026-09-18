package computa;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import computa.storage.Storage;
import computa.ui.Ui;

/** Tests the command-processing adapter shared by the console and GUI. */
class ComputaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void processCommand_todoThenList_sendsResponsesToConfiguredUi() {
        ArrayList<String> output = new ArrayList<>();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Computa computa = new Computa(storage, new Ui(output::add));

        assertTrue(computa.processCommand("todo read book"));
        assertTrue(computa.processCommand("list"));

        assertTrue(output.stream().anyMatch(line -> line.contains("read book")));
        assertTrue(output.stream().anyMatch(line -> line.contains("1.[T][ ] read book")));
    }

    @Test
    void processCommand_bye_returnsFalseAndSendsFarewell() {
        ArrayList<String> output = new ArrayList<>();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Computa computa = new Computa(storage, new Ui(output::add));

        assertFalse(computa.processCommand("bye"));

        assertTrue(output.stream().anyMatch(line -> line.contains("Noooo don't go")));
    }

    @Test
    void processCommand_sort_ordersDatedTasksBeforeUndatedTasks() {
        ArrayList<String> output = new ArrayList<>();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Computa computa = new Computa(storage, new Ui(output::add));

        computa.processCommand("deadline later /by 2020-01-10");
        computa.processCommand("todo buy milk");
        computa.processCommand("deadline sooner /by 2020-01-01");
        computa.processCommand("sort");

        String sortedTaskMessage = output.stream()
                .filter(line -> line.contains("1.[D][ ] sooner (by: Jan 01 2020)"))
                .findFirst()
                .orElseThrow();
        int soonerIndex = sortedTaskMessage.indexOf("1.[D][ ] sooner (by: Jan 01 2020)");
        int laterIndex = sortedTaskMessage.indexOf("2.[D][ ] later (by: Jan 10 2020)");
        int todoIndex = sortedTaskMessage.indexOf("3.[T][ ] buy milk");
        assertTrue(output.contains("Tasks sorted by date."));
        assertTrue(soonerIndex >= 0 && soonerIndex < laterIndex);
        assertTrue(laterIndex < todoIndex);
    }

    @Test
    void processCommand_extraSpacesAndRepeatedParameters_handlesInputSafely() {
        ArrayList<String> output = new ArrayList<>();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Computa computa = new Computa(storage, new Ui(output::add));

        assertTrue(computa.processCommand("  todo   read book  "));
        assertTrue(computa.processCommand("deadline report /by Monday /by Tuesday"));
        assertTrue(computa.processCommand("event meeting /to 2020-01-01 /from 2020-01-01"));
        assertTrue(computa.processCommand("event review /from 2020-01-01 /to 2020-01-01"));
        assertTrue(computa.processCommand("list"));

        assertTrue(output.stream().anyMatch(line -> line.contains("1.[T][ ] read book")));
        assertTrue(output.stream().anyMatch(line -> line.contains("A deadline needs a description")));
        assertTrue(output.stream().anyMatch(line -> line.contains("An event needs a description")));
        assertTrue(output.contains("Hmph! An event must end after it starts."));
        assertFalse(output.stream().anyMatch(line -> line.contains("2.[")));
    }

    @Test
    void processCommand_duplicateTask_rejectsDuplicateAndPreservesTaskList() {
        ArrayList<String> output = new ArrayList<>();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Computa computa = new Computa(storage, new Ui(output::add));

        computa.processCommand("todo read book");
        computa.processCommand("todo read book");
        computa.processCommand("list");

        assertTrue(output.stream().anyMatch(line -> line.contains("already have that task")));
        assertTrue(output.stream().anyMatch(line -> line.contains("1.[T][ ] read book")));
        assertFalse(output.stream().anyMatch(line -> line.contains("2.[T][ ] read book")));
    }
}
