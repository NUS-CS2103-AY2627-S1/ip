package kpei.commands.user;

import kpei.commands.flags.DateFlag;
import kpei.commands.flags.StringFlag;
import kpei.commands.flags.TimeFlag;
import kpei.commands.parser.ParsedCommand;
import kpei.datatypes.Event;
import kpei.datatypes.Task;
import kpei.exceptions.BertException;
import kpei.exceptions.MissingArgumentException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventCommand extends Command<Task> {
    private final StringFlag descriptionFlag;
    private final DateFlag fromDateFlag;
    private final TimeFlag fromTimeFlag;
    private final DateFlag toDateFlag;
    private final TimeFlag toTimeFlag;

    public EventCommand() {
        commandType = "event";
        aliases = List.of("e", "event");
        maxArgs = 5;
        descriptionFlag = new StringFlag("description", 0, List.of("/d", "-d", "--description"),
                "Task description.");
        fromDateFlag = new DateFlag("from-date", 1, List.of("/f", "-f", "--from-date"), "Start date.");
        fromTimeFlag = new TimeFlag("from-time", 2, List.of("/ff", "-ff", "--from-time"),
                LocalTime.MIDNIGHT, "Start time.");
        toDateFlag = new DateFlag("toDate", 3, List.of("/t", "-t", "--toDate"), "End date.");
        toTimeFlag = new TimeFlag("toTime", 4, List.of("/tt", "-tt", "--toTime"),
                LocalTime.MIDNIGHT, "End time.");
    }

    public Task parse(ParsedCommand parsedCommand) throws BertException {
        checkArgCount(parsedCommand);

        try {
            String description = descriptionFlag.parse(parsedCommand);
            LocalDate fromDate = fromDateFlag.parse(parsedCommand);
            LocalTime fromTime = fromTimeFlag.parse(parsedCommand);
            LocalDate toDate = toDateFlag.parse(parsedCommand);
            LocalTime toTime = toTimeFlag.parse(parsedCommand);
            return new Event(description, fromDate, fromTime, toDate, toTime);
        } catch (MissingArgumentException e) {
            throw new BertException(e.getMessage() + "\n" + getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return String.format("Syntax: %s %s %s %s %s %s", commandType, descriptionFlag.formatArgument(),
                fromDateFlag.formatArgument(), fromTimeFlag.formatArgument(), toDateFlag.formatArgument(),
                toTimeFlag.formatArgument())
                + String.format("\n%s\n%s\n%s\n%s\n%s", descriptionFlag.getHelp(), fromDateFlag.getHelp(),
                        fromTimeFlag.getHelp(), toDateFlag.getHelp(), toTimeFlag.getHelp());
    }
}
