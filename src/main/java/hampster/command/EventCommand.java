package hampster.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import hampster.exception.HampsterException;
import hampster.parser.DateTimeParser;
import hampster.parser.TagParser;
import hampster.task.Event;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that creates a task representing an event. */
public class EventCommand extends Command {
    private final String description;
    private final String tag;
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an event command from the user's input parts. */
    public EventCommand(String[] parts) throws HampsterException {
        String input = Arrays.stream(parts)
                .skip(1)
                .collect(Collectors.joining(" "))
                .trim();

        if (input.isEmpty()) {
            throw new HampsterException(
                    "An event needs a description before I add it to the secret dossier.");
        }

        TagParser.ParsedInput parsedInput = TagParser.parseOption(input);
        tag = parsedInput.tag();

        String[] eventParts = parsedInput.description().split("\\s+/from\\s+", 2);

        if (eventParts.length != 2) {
            throw new HampsterException(
                    "Events require a /from time so I know when the scheme begins.\n"
                            + "Try: event <description> /from <start> /to <end>");
        }

        description = eventParts[0].trim();

        if (description.contains("|")) {
            throw new HampsterException(
                    "An event description cannot contain '|'; it would break my dossier.");
        }

        String[] timeParts = eventParts[1].split("\\s+/to\\s+", 2);

        if (timeParts.length != 2) {
            throw new HampsterException(
                    "Events require a /to time so I know when the scheme ends.\n"
                            + "Try: event <description> /from <start> /to <end>");
        }

        try {
            from = DateTimeParser.parse(timeParts[0]);
            to = DateTimeParser.parse(timeParts[1]);

        } catch (DateTimeParseException e) {
            throw new HampsterException(e.getMessage());
        }

        if (description.isEmpty()) {
            throw new HampsterException(
                    "Tell me what the event actually is.");
        }

        if (!to.isAfter(from)) {
            throw new HampsterException(
                    "The event's /to time must be later than its /from time.");
        }
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(new Event(false, description, from, to, tag));

        ui.showMessage("\tEvent entered in the secret dossier. Deliciously organised.");
        ui.showMessage("\t" + tasks.get(tasks.size() - 1));
        ui.showMessage("\tMy empire now contains " + tasks.size() + " tasks.");
    }
}
