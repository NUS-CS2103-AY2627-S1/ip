/**
 * The window Tally can be talked to through, as an alternative to the console.
 *
 * <p>{@link tally.gui.Main} builds the stage from the FXML in the resources,
 * {@link tally.gui.MainWindow} is the controller that hands what the user typed to
 * {@link tally.Tally#getResponse(String)} and shows the reply, and
 * {@link tally.gui.DialogBox} renders one turn of the conversation.
 *
 * <p>Nothing here is launched directly. {@link tally.Launcher} starts it instead,
 * because a class extending Application cannot be the main class of a JAR that
 * bundles JavaFX: the runtime refuses to load it as an unnamed module.
 */
package tally.gui;
