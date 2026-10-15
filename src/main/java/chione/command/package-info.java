/**
 * The things the user can ask Chione to do.
 *
 * <p>{@link chione.command.CommandType} is the vocabulary -- the words that may
 * start a line. {@link chione.command.Command} is a request that has already
 * been understood, holding whatever it needs to be carried out, and knowing how
 * to carry it out when handed a task list, a user interface and a save file.
 *
 * <p>Keeping the two apart is what lets the conversation loop have no branch per
 * command: it asks the parser for a Command and tells it to run. A new command
 * is a word added to the enum and a class added here, and nothing else changes.
 */
package chione.command;
