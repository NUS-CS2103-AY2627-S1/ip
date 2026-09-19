package yawned;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the backend responses supplied to the graphical user interface. */
class YawnedTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommands_updatesTasksAndReturnsResponses() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Noted. I've tucked this into your task list:\n  [T][ ] read book\nYou now have 1 task(s).",
                yawned.getResponse("todo read book"));
        assertEquals("Finally, I can sleep in peace without this task bothering me..."
                        + " marked complete: [T][X] read book",
                yawned.getResponse("mark 1"));
        assertEquals("Here's what's keeping you busy:\n1.[T][X] read book", yawned.getResponse("list"));
    }

    @Test
    void getResponse_aliasCommands_returnCanonicalCommandResponses() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Noted. I've tucked this into your task list:\n  [T][ ] read book\nYou now have 1 task(s).",
                yawned.getResponse("T read book"));
        assertEquals("Finally, I can sleep in peace without this task bothering me..."
                        + " marked complete: [T][X] read book",
                yawned.getResponse("m 1"));
        assertEquals("Back on the radar. I've marked this incomplete:\n  [T][ ] read book", yawned.getResponse("U 1"));
        assertEquals("I found these before my attention drifted:\n1.[T][ ] read book", yawned.getResponse("f book"));
        assertEquals("One less thing to carry around. Removed:\n  [T][ ] read book\n0 task(s) remain.",
                yawned.getResponse("del 1"));
        assertEquals("Nothing on the list. A rare moment of peace.", yawned.getResponse("L"));
    }

    @Test
    void getResponse_customAliases_canBeCreatedReplacedRemovedAndReloaded() {
        Path saveFile = temporaryDirectory.resolve("Yawned.txt");
        Yawned yawned = new Yawned(saveFile);

        assertEquals("All set. Alias 'hw' now runs 'todo'. *yawn* Remembering another alias is tiring.",
                yawned.getResponse("alias HW todo"));
        assertEquals("Noted. I've tucked this into your task list:\n  [T][ ] finish assignment"
                        + "\nYou now have 1 task(s).",
                yawned.getResponse("hW finish assignment"));
        assertEquals("All set. Alias 'hw' now runs 'deadline'. *yawn* Remembering another alias is tiring.",
                yawned.getResponse("alias hw deadline"));

        Yawned reloadedYawned = new Yawned(saveFile);
        assertEquals("Noted. I've tucked this into your task list:\n  [D][ ] submit report (by: JAN 01 2026 0900)"
                        + "\nYou now have 2 task(s).",
                reloadedYawned.getResponse("HW submit report /by 2026-01-01 0900"));
        assertEquals("All set. Alias 'hw' has been removed. One less thing to remember - what a relief.",
                reloadedYawned.getResponse("alias remove Hw"));
        assertEquals("*yawn* I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, "
                        + "find, or alias.",
                reloadedYawned.getResponse("hw finish assignment"));
    }

    @Test
    void getResponse_customAliases_rejectInvalidNamesAndTargets() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("I need an alias name made of letters only.", yawned.getResponse("alias hw-1 todo"));
        assertEquals("That alias name is already reserved, even I can't nap through that rule.",
                yawned.getResponse("alias todo deadline"));
        assertEquals("That alias name is already reserved, even I can't nap through that rule.",
                yawned.getResponse("alias t deadline"));
        assertEquals("That alias name is already reserved, even I can't nap through that rule.",
                yawned.getResponse("alias alias todo"));
        assertEquals("Aliases can only run a standard task command.", yawned.getResponse("alias hw t"));
        assertEquals("Aliases can only run a standard task command.", yawned.getResponse("alias hw sleep"));
        assertEquals("I couldn't find an alias named 'hw'.", yawned.getResponse("alias remove hw"));
    }

    @Test
    void getResponse_aliasList_showsBuiltInsAndCustomAliasesInCreationOrder() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Here are the shortcuts I know:\n\nBuilt-in:\n  t -> todo\n  d -> deadline\n  e -> event"
                        + "\n  l -> list\n  m -> mark\n  u -> unmark\n  del -> delete\n  f -> find"
                        + "\n\nNo custom aliases yet. Add one with: alias <name> <command>",
                yawned.getResponse("alias list"));
        yawned.getResponse("alias hw todo");
        yawned.getResponse("alias proj deadline");
        assertEquals("Here are the shortcuts I know:\n\nBuilt-in:\n  t -> todo\n  d -> deadline\n  e -> event"
                        + "\n  l -> list\n  m -> mark\n  u -> unmark\n  del -> delete\n  f -> find"
                        + "\n\nYour aliases:\n  hw -> todo\n  proj -> deadline",
                yawned.getResponse("alias list"));
    }

    @Test
    void getResponse_invalidOrUnknownCommand_returnsValidationMessage() {
        Yawned yawned = new Yawned(temporaryDirectory.resolve("Yawned.txt"));

        assertEquals("Which task should I mark? For example: mark 2", yawned.getResponse("mark"));
        assertEquals("*yawn* I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, "
                        + "find, or alias.", yawned.getResponse("dance"));
    }
}
