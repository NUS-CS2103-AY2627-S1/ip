package jiji.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.TaskList;
import jiji.ui.Ui;

public class HelpCommandTest {

    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void execute_generalHelp_containsAllKeyCommands() {
        HelpCommand command = new HelpCommand();
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("todo"));
        assertTrue(result.contains("deadline"));
        assertTrue(result.contains("event"));
        assertTrue(result.contains("list"));
        assertTrue(result.contains("mark"));
        assertTrue(result.contains("unmark"));
        assertTrue(result.contains("delete"));
        assertTrue(result.contains("find"));
        assertTrue(result.contains("help"));
        assertTrue(result.contains("bye"));
    }

    @Test
    public void execute_specificCommandHelp_containsCommandDetails() {
        HelpCommand command = new HelpCommand("deadline");
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("Command: deadline"));
        assertTrue(result.contains("Syntax: deadline <desc> /by <time>"));
        assertTrue(result.contains("Formats: yyyy-MM-dd"));
    }

    @Test
    public void execute_unknownCommandHelp_returnsUnknownGuidance() {
        HelpCommand command = new HelpCommand("nonexistent");
        String result = command.execute(tasks, ui, null);

        assertTrue(result.contains("Unknown command: nonexistent"));
        assertTrue(result.contains("Type 'help' to see the list"));
    }
}
