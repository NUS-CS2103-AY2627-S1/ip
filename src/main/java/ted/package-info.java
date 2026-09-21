/**
 * The Ted chatbot: a task list you talk to, in a window or in a terminal, that
 * remembers what it is told.
 * <p>
 * The classes here are the moving parts of one conversation, each with a
 * single job:
 * <ul>
 *   <li>{@link ted.Ted} wires the others together and runs the conversation</li>
 *   <li>{@link ted.Ui} words Ted's replies, and reads commands in the terminal</li>
 *   <li>{@link ted.Parser} reads a typed line as a command</li>
 *   <li>{@link ted.Storage} loads and saves the task list</li>
 *   <li>{@link ted.TedException} carries a problem the user can recover from</li>
 *   <li>{@link ted.Launcher}, {@link ted.Main}, {@link ted.MainWindow} and
 *       {@link ted.DialogBox} make up Ted's window</li>
 * </ul>
 * The tasks themselves live in {@link ted.task}, and the commands that act on
 * them in {@link ted.command}.
 */
package ted;
