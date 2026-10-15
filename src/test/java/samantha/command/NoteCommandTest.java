package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;
import samantha.model.Note;
import samantha.model.NoteList;
import samantha.model.TaskList;
import samantha.storage.NoteStorage;
import samantha.storage.Storage;

class NoteCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void noteCommands_addEditListDeleteAndPersistNotes() throws Exception {
        NoteList notes = new NoteList();
        CommandContext context = context(notes);

        assertEquals("I'll keep this note:\n"
                + "  waist size: 32 inches\n"
                + "Now you have 1 notes in the list.",
                new AddNoteCommand("waist size: 32 inches").execute(context));
        assertEquals("Here are your notes:\n1. waist size: 32 inches",
                new ListNotesCommand().execute(context));

        assertEquals("I've updated this note:\n  waist size: 31 inches",
                new EditNoteCommand(1, "waist size: 31 inches").execute(context));
        assertEquals("I've taken that off the list:\n"
                + "  waist size: 31 inches\n"
                + "Now you have 0 notes in the list.",
                new DeleteNoteCommand(1).execute(context));
        assertEquals(java.util.List.of(), new NoteStorage(noteFile()).load());
    }

    @Test
    void noteCommands_invalidIdsAndContent_leaveNotesUnchanged() throws Exception {
        NoteList notes = new NoteList();
        notes.add(new Note("original"));
        CommandContext context = context(notes);

        assertThrows(InputException.class, () -> new EditNoteCommand(2, "replacement").execute(context));
        assertThrows(TaskValidationException.class, () -> new EditNoteCommand(1, " ").execute(context));
        assertEquals("original", notes.getNote(1).getContent());
    }

    @Test
    void findCommand_matchesTasksAndNotesInSeparateSections() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new samantha.model.Todo("watch a movie"));
        NoteList notes = new NoteList();
        notes.add(new Note("movie title: Inception"));

        String output = new FindCommand("movie")
                .execute(new CommandContext(tasks, notes, taskStorage(), noteStorage()));

        assertEquals("Here are the matching tasks in your list:\n"
                + "1. [T][ ] watch a movie\n\n"
                + "Here are the matching notes in your list:\n"
                + "1. movie title: Inception", output);
    }

    private CommandContext context(NoteList notes) {
        return new CommandContext(new TaskList(), notes, taskStorage(), noteStorage());
    }

    private Storage taskStorage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt"));
    }

    private NoteStorage noteStorage() {
        return new NoteStorage(noteFile());
    }

    private Path noteFile() {
        return temporaryDirectory.resolve("notes.txt");
    }
}
