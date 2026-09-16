/**
 * One class per instruction the user can give.
 *
 * <p>Every class here extends {@link lebronjames.command.Command} and carries
 * out its own job in {@code execute}, so the application's run loop never has
 * to ask which command it is holding. Commands are built by
 * {@link lebronjames.parser.Parser} and are given the task list, user
 * interface, and storage at execution time rather than at construction time.
 */
package lebronjames.command;
