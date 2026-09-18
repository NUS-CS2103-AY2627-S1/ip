/**
 * Provides Alfred the Butler, a chatbot that keeps a list of tasks.
 * {@link alfred.AlfredTheButler} holds the command loop; {@link alfred.Ui}
 * reads the line and prints every reply, {@link alfred.Parser} turns the line
 * into a {@link alfred.command.Command}, and {@link alfred.Storage} keeps the
 * list on disk.
 */
package alfred;
