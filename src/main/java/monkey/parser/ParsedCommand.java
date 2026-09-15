package monkey.parser;

import monkey.command.Command;

/** A command keyword together with the text that follows it. */
public record ParsedCommand(Command command, String arguments) {
}
