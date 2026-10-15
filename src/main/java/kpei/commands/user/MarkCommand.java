package kpei.commands.user;

import kpei.commands.flags.IntFlag;
import kpei.commands.parser.ParsedCommand;
import kpei.exceptions.BertException;
import kpei.exceptions.MissingArgumentException;

import java.util.List;

public class MarkCommand extends Command<Integer> {
    private final IntFlag indexFlag;

    public MarkCommand() {
        commandType = "mark";
        aliases = List.of("m", "mark");
        maxArgs = 1;
        indexFlag = new IntFlag("index", 0, List.of("/i", "-i", "--index"), "Task index.");
    }

    public Integer parse(ParsedCommand parsedCommand) throws BertException {
        checkArgCount(parsedCommand);

        try {
            return indexFlag.parse(parsedCommand);
        } catch (MissingArgumentException e) {
            throw new BertException(e.getMessage() + "\n" + getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return String.format("Syntax: %s %s", commandType, indexFlag.formatArgument())
                + String.format("\n%s", indexFlag.getHelp());
    }
}
