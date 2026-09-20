package atom.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import atom.exception.AtomInvalidDateException;
import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;

/** Represents a command that lists tasks occurring on a date. */
public class ScheduleCommand extends Command {
    private static final String COMMAND_NAME = "schedule";
    private static final int EXPECTED_ARGUMENTS = 1;
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    /** Instantiates a schedule command. */
    public ScheduleCommand(String ... args)
            throws AtomMismatchedArgumentsException, AtomInvalidTypeException {
        super(args, EXPECTED_ARGUMENTS);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        try {
            LocalDate date = LocalDate.parse(args[0].strip());
            String scheduled = tasks.schedule(date).toString();
            if (scheduled.isEmpty()) {
                return "No tasks scheduled for " + date + ".";
            }
            return "Tasks scheduled for " + date.format(OUTPUT_DATE_FORMAT) + ":\n" + scheduled;
        } catch (DateTimeParseException e) {
            throw new AtomInvalidDateException();
        }
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected String getOutputMessage(String ... outputArgs) {
        return outputArgs[0];
    }
}
