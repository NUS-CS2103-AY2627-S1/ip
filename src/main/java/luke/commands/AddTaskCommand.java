package luke.commands;

import java.time.LocalDateTime;
import java.util.EnumMap;

import luke.Luke;
import luke.datetime.DateTimeParser;
import luke.exceptions.InvalidArgumentException;
import luke.exceptions.InvalidFlagException;
import luke.exceptions.UserInputException;
import luke.tasks.Flag;
import luke.tasks.TaskTypes;

/**
 * Creates a task of one configured {@link TaskTypes}. The same command class is
 * reused for todos, deadlines, and events because they share the same add flow:
 * validate the description, validate flags, add to the list, and show feedback.
 */
public class AddTaskCommand implements Command {
    private final TaskTypes taskType;

    /**
     * Creates an add-task command for the given task type.
     *
     * @param taskType the task type this command should create
     */
    public AddTaskCommand(TaskTypes taskType) {
        assert taskType != null : "AddTaskCommand requires a task type.";
        this.taskType = taskType;
    }

    /**
     * Adds a task after checking that the description is present, all required
     * flags are supplied, and no unsupported flags were provided.
     *
     * @param bot the chatbot, used for output and access to the task list
     * @param argument the task description before the first flag
     * @param flags parsed flag values from the user input
     * @throws UserInputException if the description or flags are invalid
     */
    @Override
    public void execute(
            Luke bot,
            String argument,
            EnumMap<Flag, String> flags) throws UserInputException {
        assert bot != null : "AddTaskCommand requires a chatbot.";
        assert argument != null : "Command argument should be parsed as a string.";
        assert flags != null : "Command flags should be parsed as a flag map.";

        if (argument.isBlank()) {
            throw InvalidArgumentException.missingDescription(taskType.toString().toLowerCase());
        }

        for (Flag flag : flags.keySet()) {
            if (!taskType.getFlags().contains(flag)) {
                throw InvalidFlagException.unsupported(flag.name().toLowerCase());
            }
        }

        for (Flag requiredFlag : taskType.getFlags()) {
            if (!flags.containsKey(requiredFlag)) {
                throw InvalidFlagException.missingRequired(requiredFlag.name().toLowerCase());
            }
        }
        assert hasRequiredFlags(flags) : "Required flags should be present after validation.";
        formatDateTimeFlags(flags);
        validateEventRange(flags);

        bot.getItems().add(argument, taskType, flags);
        int size = bot.getItems().size();
        assert size > 0 : "Adding a task should leave the list non-empty.";
        bot.say("Got it. I've added this task:\n"
                + "  %s\n".formatted(bot.getItems().formatOneItem(size - 1))
                + "Now you have %d tasks in the list.".formatted(size)
        );
    }

    @Override
    public boolean shouldSaveItemList() {
        return true;
    }

    /**
     * Formats date/time flag values while preserving unrecognized values.
     *
     * @param flags parsed flag values to update
     */
    private static void formatDateTimeFlags(EnumMap<Flag, String> flags) {
        for (Flag flag : flags.keySet()) {
            flags.put(flag, DateTimeParser.formatDateOrTimeFromFlag(flags.get(flag)));
        }
    }

    /**
     * Rejects an event when both endpoints are recognized and the end does not
     * occur after the start. Unrecognized date text remains supported.
     *
     * @param flags formatted event flags
     * @throws InvalidArgumentException if the event has an invalid time range
     */
    private void validateEventRange(EnumMap<Flag, String> flags)
            throws InvalidArgumentException {
        if (taskType != TaskTypes.EVENT) {
            return;
        }

        LocalDateTime start = DateTimeParser.parseSortKey(flags.get(Flag.FROM));
        LocalDateTime end = DateTimeParser.parseSortKey(flags.get(Flag.TO));
        if (start != null && end != null && !end.isAfter(start)) {
            throw InvalidArgumentException.invalidEventRange();
        }
    }

    private boolean hasRequiredFlags(EnumMap<Flag, String> flags) {
        return flags.keySet().containsAll(taskType.getFlags());
    }
}
