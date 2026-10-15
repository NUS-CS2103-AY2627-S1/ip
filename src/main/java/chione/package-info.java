/**
 * Chione, a command-line chatbot that keeps a list of tasks.
 *
 * <p>This package holds the program's own moving parts. {@link chione.Chione} is
 * the conversation loop; {@link chione.Parser} reads what the user types;
 * {@link chione.Ui} says everything back; {@link chione.Storage} keeps the list
 * between runs; and {@link chione.DateTimes} is the one place that knows what a
 * date looks like. {@link chione.ChioneException} carries anything that went
 * wrong, already phrased for the user to read.
 *
 * <p>What a task <em>is</em> lives in {@link chione.task}, and what the user can
 * <em>ask for</em> lives in {@link chione.command}.
 *
 * <p>A line of input travels the same route every time: Chione reads it, Parser
 * turns it into a command, the command carries itself out against the task list,
 * tells the Ui what happened, and saves if it changed anything.
 */
package chione;
