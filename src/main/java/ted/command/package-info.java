/**
 * The things a user can ask Ted to do.
 * <p>
 * {@link ted.command.CommandType} names the kinds of command and the keyword
 * for each, and every kind has its own subclass of
 * {@link ted.command.Command} that carries out the work. A command is built by
 * {@link ted.Parser} only once the user's input makes sense, so the classes
 * here can act without checking the input again.
 */
package ted.command;
