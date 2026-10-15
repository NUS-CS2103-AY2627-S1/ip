package ayre.gui;

import ayre.Ayre;
import ayre.AyreResponse;
import ayre.enums.AyreStatus;
import ayre.enums.ReplyType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final double MIN_HEIGHT = 35;
    private static final double MAX_HEIGHT = 120;
    private static final Image USER_IMAGE = new Image("/images/Raven.png");
    private static final Image AYRE_IMAGE = new Image("/images/Ayre.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextArea userInput;

    private Ayre ayre;

    /**
     * Initialize the dialog display area and user input area of the GUI.
     */
    @FXML
    public void initialize() {
        // sets dialog display to scroll to the bottommost newest dialog bubble
        this.dialogContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> this.scrollPane.setVvalue(1.0));
        });
        // listens to keystrokes for user hitting ENTER key to handle user input
        this.userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) { // SHIFT-ENTER will not trigger this
                event.consume();
                handleUserInput();
            }
        });
        // auto expands user input area to hold user text better
        this.userInput.textProperty().addListener((obs, oldText, newText) -> {
            Platform.runLater(() -> {
                this.userInput.setMinHeight(computeContentHeight(this.userInput));
            });
        });
        // auto adjusts user input area if user modifies the window width
        this.userInput.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            Platform.runLater(() -> {
                this.userInput.setMinHeight(computeContentHeight(this.userInput));
            });
        });
    }

    /**
     * Opens the user guide when the Help menu item is selected.
     */
    @FXML
    private void handleHelp() {
        HelpWindow.show();
    }

    /**
     * Spits out an estimate for the height of the user input area to best display user text.
     * Garbage hack, but it probably won't be used much so whatever.
     *
     * @param userInput User input area.
     * @return Estimated height of text box.
     */
    private double computeContentHeight(TextArea userInput) {
        Text text = new Text(userInput.getText());
        text.setFont(userInput.getFont());
        text.setWrappingWidth(userInput.getWidth() - 10);
        double textHeight = text.getLayoutBounds().getHeight();
        return Math.clamp(textHeight + 10, MIN_HEIGHT, MAX_HEIGHT);
    }

    /**
     * Injects the Ayre instance and adds relevant startup dialog.
     *
     * @param ayre Injected instance.
     */
    public void setAyre(Ayre ayre) {
        this.ayre = ayre;
        dialogContainer.getChildren().addAll(
                DialogBox.getAyreDialog(this.ayre.getLoadMessage(), AYRE_IMAGE),
                DialogBox.getAyreDialog("""
                <<Main System: Activating Support Mode.>>
                     █████╗ ██╗   ██╗██████╗ ███████╗
                    ██╔══██╗╚██╗ ██╔╝██╔══██╗██╔════╝
                    ███████║ ╚████╔╝ ██████╔╝█████╗
                    ██╔══██║  ╚██╔╝  ██╔══██╗██╔══╝
                    ██║  ██║   ██║   ██║  ██║███████╗
                    ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚══════╝
                    .~"~.__.~"~.__.~"~.__.~"~.__.~"~.
                """, AYRE_IMAGE),
                DialogBox.getAyreDialog("~ Hello, Raven. What shall we do today?", AYRE_IMAGE));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Ayre's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        AyreResponse response = ayre.getResponse(input);
        if (response.status() == AyreStatus.TERMINATE) {
            HelpWindow.close();
            Platform.exit();
            return;
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                getAyreDialog(response)
        );
        userInput.clear();
    }

    /**
     * Creates the appropriate Ayre dialog box for a backend response.
     *
     * @param response Response returned by Ayre.
     * @return Normal or error-styled Ayre dialog box.
     */
    private DialogBox getAyreDialog(AyreResponse response) {
        if (response.type() == ReplyType.ERROR) {
            return DialogBox.getAyreErrorDialog(response.message(), AYRE_IMAGE);
        }
        return DialogBox.getAyreDialog(response.message(), AYRE_IMAGE);
    }
}
