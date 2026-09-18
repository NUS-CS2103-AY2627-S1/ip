package milo.command;

import java.util.Arrays;
import java.util.stream.Stream;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents the "undo" command, which puts the task list back the way it was
 * before the most recent command that changed it.
 * <p>
 * One step only: the list keeps a single snapshot, so undo cannot be repeated
 * to walk further back, and using it clears the snapshot. Storing the whole
 * list rather than an inverse operation per command keeps every command free
 * of undo-specific code; the cost is that the snapshot grows with the list,
 * which is not a concern at the sizes this chatbot deals with.
 */
public class UndoCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        if (!tasks.restorePrevious()) {
            throw new MiloException("Nothing to undo yet — I only remember "
                    + "the last thing that changed your list.");
        }

        // The save file mirrors the list, so it has to follow the list back.
        storage.save(tasks.asArrayList());

        ui.showResponse(Stream.concat(
                Stream.of("Rewound. Here's where things stand:"),
                Arrays.stream(tasks.getDisplayLines()))
                .toArray(String[]::new));
    }
}
