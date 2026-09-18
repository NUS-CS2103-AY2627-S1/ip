package bibi;

/**
 * One of Bibi's answers, together with whether it reports a problem.
 *
 * <p>The console shows every line the same way, so it has never needed to know
 * the difference. A window can do better: an error the user must act on should
 * not look identical to a confirmation they can ignore. Carrying the distinction
 * back with the text is what lets the GUI style the two apart, without the GUI
 * having to guess by reading the words.
 *
 * @param text what Bibi said, ready to display
 * @param isError whether the text reports something that went wrong
 */
public record Reply(String text, boolean isError) {
}
