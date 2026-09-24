package hampster.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import hampster.exception.HampsterException;
import hampster.parser.DateTimeParser;
import hampster.parser.TagParser;
import hampster.task.Deadline;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that creates a task with a deadline. */
public class DeadlineCommand extends Command {
    private final String description;
    private final String tag;
    private final LocalDateTime by;

    /** Creates a deadline command from the user's input parts. */
    public DeadlineCommand(String[] parts) throws HampsterException {
        String input = Arrays.stream(parts)
                .skip(1)
                .collect(Collectors.joining(" "))
                .trim();

        if (input.isEmpty()) {
            throw new HampsterException(
                    "A deadline needs a description before I add it to my master plan.");
        }

        TagParser.ParsedInput parsedInput = TagParser.parseOption(input);
        tag = parsedInput.tag();

        String[] deadlineParts = parsedInput.description().split("\\s+/by\\s+", 2);

        if (deadlineParts.length != 2) {
            throw new HampsterException(
                    "Deadlines require a /by marker so I know when to strike.\n"
                            + "Try: deadline <description> /by <date or time>");
        }

        description = deadlineParts[0].trim();

        if (description.isEmpty()) {
            throw new HampsterException(
                    "I require a deadline description for my master plan.");
        }

        if (description.contains("|")) {
            throw new HampsterException(
                    "A deadline description cannot contain '|'; it would break my dossier.");
        }

        try {
            by = DateTimeParser.parse(deadlineParts[1]);
        } catch (DateTimeParseException e) {
            throw new HampsterException(e.getMessage());
        }
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(new Deadline(false, description, by, tag));

        ui.showMessage("\tDeadline captured for my master plan.");
        ui.showMessage("\t" + tasks.get(tasks.size() - 1));
        ui.showMessage("\tMy empire now contains " + tasks.size() + " tasks.");

    }
}
