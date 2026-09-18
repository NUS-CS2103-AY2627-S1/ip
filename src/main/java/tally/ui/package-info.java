/**
 * Talking to the user.
 *
 * <p>{@link tally.ui.Ui} serves both front ends. On the console it reads the
 * commands typed and prints what Tally says, fencing each message between
 * horizontal rules; for the window it gathers the same words for the caller to
 * show in a bubble instead. Keeping the wording here lets the rest of the code
 * decide what to say without knowing where it appears.
 */
package tally.ui;
