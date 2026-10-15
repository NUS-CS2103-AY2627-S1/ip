package samantha.command;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import samantha.model.Note;

/**
 * Represents the command that displays all notes.
 */
public class ListNotesCommand extends Command {

    /**
     * Creates a command that lists notes.
     */
    public ListNotesCommand() {
    }

    /**
     * Displays every note in its current order.
     *
     * @param context current application state and persistence handlers
     * @return formatted note list
     */
    @Override
    public String execute(CommandContext context) {
        List<Note> noteSnapshot = context.getNotes().asList();
        String noteLines = IntStream.range(0, noteSnapshot.size())
                .mapToObj(index -> String.format("%d. %s", index + 1, noteSnapshot.get(index)))
                .collect(Collectors.joining("\n"));
        return ("Here are your notes:\n" + noteLines).stripTrailing();
    }
}
