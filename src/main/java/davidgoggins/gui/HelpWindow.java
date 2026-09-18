package davidgoggins.gui;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * A separate window that shows the help page.
 *
 * <p>Built in code rather than FXML, since it holds a single text view. Owned by the
 * main window, so it closes with it; non-modal, so the user can keep typing meanwhile.
 */
public class HelpWindow {

    /** The text shown in the help window's title bar. */
    private static final String TITLE = "David Goggins - Help";

    /** The stylesheet shared with the main window. */
    private static final String STYLESHEET = "/css/main.css";

    /** The window's initial width, in pixels; the user can resize it. */
    private static final double WIDTH = 600.0;

    /** The window's initial height, in pixels; the user can resize it. */
    private static final double HEIGHT = 380.0;

    /** The one window reused every time help is asked for, so repeats never stack up. */
    private final Stage stage = new Stage();

    /**
     * Creates the help window, hidden until {@link #showOrFocus()} is called.
     *
     * @param owner the main window, which this window closes with
     * @param helpText the help page to show, one command per line
     */
    public HelpWindow(Window owner, String helpText) {
        TextArea helpArea = new TextArea(helpText);
        helpArea.setEditable(false);
        helpArea.getStyleClass().add("help-text");

        // The padding around the text lets the dotted backdrop and the card's shadow show.
        StackPane root = new StackPane(helpArea);
        root.getStyleClass().add("help-window");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        // The same stylesheet as the main window, so the two windows look alike.
        scene.getStylesheets().add(HelpWindow.class.getResource(STYLESHEET).toExternalForm());

        stage.initOwner(owner);
        stage.setTitle(TITLE);
        stage.setScene(scene);
    }

    /** Shows the window, or brings it to the front if it is already open. */
    public void showOrFocus() {
        stage.show();
        stage.toFront();
    }
}
