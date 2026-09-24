package hampster.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import hampster.exception.HampsterException;
import hampster.parser.TagParser;
import hampster.task.TaskList;
import hampster.task.ToDo;
import hampster.ui.Ui;

/** Command that creates a todo task. */
public class ToDoCommand extends Command {
    private final String description;
    private final String tag;

    /** Creates a todo command from the user's input parts. */
    public ToDoCommand(String[] parts) throws HampsterException {
        String input = Arrays.stream(parts)
                .skip(1)
                .collect(Collectors.joining(" "))
                .trim();

        TagParser.ParsedInput parsedInput = TagParser.parseOption(input);
        description = parsedInput.description();
        tag = parsedInput.tag();

        if (description.isEmpty()) {
            throw new HampsterException(
                    "I require a task description before I add it to my master plan.");
        }

        if (description.contains("|")) {
            throw new HampsterException(
                    "A task description cannot contain '|'; it would break my dossier.");
        }
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(new ToDo(false, description, tag));

        ui.showMessage(
            "\tExcellent. Another task for my evil little empire.",
            "\t" + tasks.get(tasks.size() - 1),
            "\tMy empire now contains " + tasks.size() + " tasks.");
    }
}
