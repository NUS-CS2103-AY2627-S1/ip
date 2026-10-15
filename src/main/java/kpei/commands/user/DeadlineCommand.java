package kpei.commands.user;

import kpei.commands.parser.ParsedCommand;
import kpei.commands.flags.DateFlag;
import kpei.commands.flags.StringFlag;
import kpei.commands.flags.TimeFlag;
import kpei.datatypes.Deadline;
import kpei.datatypes.Task;
import kpei.exceptions.BertException;
import kpei.exceptions.MissingArgumentException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DeadlineCommand extends Command<Task> {
    private final StringFlag descriptionFlag;
    private final DateFlag byDateFlag;
    private final TimeFlag byTimeFlag;

    public DeadlineCommand() {
        commandType = "deadline";
        aliases = List.of("d", "deadline");
        maxArgs = 3;
        descriptionFlag = new StringFlag("description", 0, List.of("/d", "-d", "--description"),
                "Task description.");
        byDateFlag = new DateFlag("by-date", 1, List.of("/b", "-b", "--by-date"),
                "Deadline date.");
        byTimeFlag = new TimeFlag("by-time", 2, List.of("/bb", "-bb", "--by-time"),
                LocalTime.MIDNIGHT, "Deadline time.");
    }

    public Task parse(ParsedCommand parsedCommand) throws BertException {
        checkArgCount(parsedCommand);

        try {
            String description = descriptionFlag.parse(parsedCommand);
            LocalDate byDate = byDateFlag.parse(parsedCommand);
            LocalTime byTime = byTimeFlag.parse(parsedCommand);
            Deadline deadline = new Deadline(description, byDate, byTime);
            return deadline;
        } catch (MissingArgumentException e) {
            throw new BertException(e.getMessage() + "\n" + getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return String.format("Syntax: %s %s %s %s", commandType, descriptionFlag.formatArgument(),
                byDateFlag.formatArgument(), byTimeFlag.formatArgument())
                + String.format("\n%s\n%s\n%s", descriptionFlag.getHelp(), byDateFlag.getHelp(), byTimeFlag.getHelp());
    }
}
