/**
 * Tally, a chatbot for keeping a tally of tasks, in a window or in a terminal.
 *
 * <p>{@link tally.Tally} holds the parts together and decides which to call, and
 * {@link tally.Launcher} starts the window without extending Application itself.
 * {@link tally.TallyException} carries anything Tally has to tell the user it
 * could not do, so a message written for a person travels with the failure.
 */
package tally;
