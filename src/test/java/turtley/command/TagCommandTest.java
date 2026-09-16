package turtley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;
import turtley.model.TaskList;
import turtley.model.ToDo;
import turtley.storage.Storage;
import turtley.ui.Ui;

/** Tests adding and removing tags from existing tasks. */
class TagCommandTest {

    @Test
    void tagCommand_validTags_addsAndDisplaysSortedTags() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task", List.of("#work")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new TagCommand("1 #fun #work").execute(tasks, new Ui(new PrintStream(output)),
                new Storage("build/tag-command-test.txt"));

        assertEquals(List.of("#fun", "#work"), tasks.get(0).getTags());
        assertEquals(String.join("\n",
                "____________________________________________________________",
                " Got it. I've tagged this task:",
                "   [T][ ] task [#fun] [#work]",
                "____________________________________________________________",
                ""), output.toString().replace(System.lineSeparator(), "\n"));
    }

    @Test
    void untagCommand_existingAndMissingTags_removesOnlyExistingTags() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task", List.of("#fun", "#work")));

        new UntagCommand("1 #missing #fun").execute(tasks, quietUi(),
                new Storage("build/untag-command-test.txt"));

        assertEquals(List.of("#work"), tasks.get(0).getTags());
    }

    @Test
    void tagCommand_missingTags_throwsFormatError() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new TagCommand("1").execute(new TaskList(), quietUi(),
                        new Storage("build/tag-command-test.txt")));

        assertEquals("Invalid format. Use: tag <task number> #tag1 [#tag2 ...]", exception.getMessage());
    }

    private static Ui quietUi() {
        return new Ui(new PrintStream(OutputStream.nullOutputStream()));
    }
}
