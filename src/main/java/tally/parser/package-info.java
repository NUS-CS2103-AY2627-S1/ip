/**
 * Making sense of what the user typed.
 *
 * <p>{@link tally.parser.Command} names the words Tally answers to, and
 * {@link tally.parser.Parser} turns the rest of a line into the task or the
 * task number it describes. Anything unreadable is reported as a
 * {@link tally.TallyException} whose message is meant for the user to read.
 */
package tally.parser;
