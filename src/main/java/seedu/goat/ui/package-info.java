/**
 * The two ways of talking to GOAT.
 * <p>
 * {@link seedu.goat.ui.Ui} prints replies to a terminal, while
 * {@link seedu.goat.ui.Main}, {@link seedu.goat.ui.MainWindow} and
 * {@link seedu.goat.ui.DialogBox} show the same replies in a JavaFX window. Neither
 * decides what a reply says: the wording comes from {@link seedu.goat.Goat}, so the two
 * interfaces cannot drift apart in what they tell the user.
 */
package seedu.goat.ui;
