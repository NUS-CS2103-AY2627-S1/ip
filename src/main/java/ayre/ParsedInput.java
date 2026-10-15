package ayre;

import java.util.List;

import ayre.enums.CommandType;

/**
 * A record to pass the parsed Command and List of arguments back to the main loop in Ayre::run().
 *
 * @param command Command given by the user.
 * @param args Arguments given by the user, tokenized as elements of a List.
 */
public record ParsedInput(CommandType command, List<String> args) {}
