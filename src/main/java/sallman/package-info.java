/**
 * The saLLMan chatbot: a command-line task tracker.
 * <p>
 * This package holds the parts that wire the app together. {@link sallman.Ui}
 * talks to the user, {@link sallman.Storage} reads and writes the saved list,
 * {@link sallman.Parser} turns typed input into commands, and
 * {@link sallman.TaskList} holds the tasks. {@link sallman.Sallman} owns one
 * of each and runs the command loop over them.
 */
package sallman;
