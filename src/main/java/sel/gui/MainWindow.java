package sel.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sel.Response;
import sel.Sel;

/**
 * Controller for the main Sel chat window.
 */
public class MainWindow {

    /** How long the command panel takes to slide open or closed. */
    private static final Duration SLIDE_DURATION = Duration.millis(200);

    /** Label of the toggle button while the command panel is closed. */
    private static final String HELP_CLOSED_LABEL = "^  help";

    /** Label of the toggle button while the command panel is open. */
    private static final String HELP_OPEN_LABEL = "v  help";

    /**
     * First message shown in the chat: who Sel is, what Sel is for, and
     * where to find the command list. It is kept here rather than in
     * {@link sel.ui.Ui} because it points at a button that only the GUI
     * has; the command-line greeting stays in {@code Ui.showWelcome}.
     */
    private static final String WELCOME_MESSAGE =
            "Sup, I'm Sel.\n\n"
            + "I keep your task list in order so you don't have to. \n\nTell me "
            + "about your todos, your deadlines and your events, and I'll "
            + "track them: mark them done, unmark them, delete them, or "
            + "search them by keyword. Everything is saved to disk, so your "
            + "list is still here the next time you open me.\n\n"
            + "No idea what to type? Hit the '^ help' button at the bottom "
            + "left and I'll show you every command I understand.";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private VBox helpPanel;

    @FXML
    private VBox helpContent;

    @FXML
    private Button helpToggle;

    private Sel sel;

    /** Whether the sliding command panel is currently pulled out. */
    private boolean isHelpOpen = false;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/user.png"));

    private final Image selImage = new Image(
            this.getClass().getResourceAsStream("/images/sel.png"));

    /**
     * Configures the dialog area after the FXML components are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        clipHelpPanel();
    }

    /**
     * Clips the command panel to its own bounds. Without this, the panel's
     * contents would still be painted over the chat area while the panel
     * is only partly slid out.
     */
    private void clipHelpPanel() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(helpPanel.widthProperty());
        clip.heightProperty().bind(helpPanel.heightProperty());
        helpPanel.setClip(clip);
    }

    /**
     * Adds one or more dialog boxes to the chat, letting each of them
     * re-wrap its text as the chat area changes width.
     *
     * @param dialogBoxes dialog boxes to add
     */
    private void addDialogs(DialogBox... dialogBoxes) {
        for (DialogBox dialogBox : dialogBoxes) {
            dialogBox.bindTextWidthTo(scrollPane.widthProperty());
        }
        dialogContainer.getChildren().addAll(dialogBoxes);
    }

    /**
     * Supplies the chatbot logic to this controller.
     *
     * @param sel the chatbot instance used to process commands
     */
    public void setSel(Sel sel) {
        this.sel = sel;
        addDialogs(DialogBox.getSelDialog(WELCOME_MESSAGE, selImage));

        // Problems with the save file used to be printed to the terminal,
        // where a GUI user would never see them. Show them in the chat as
        // an error instead, so a lost or unreadable save file is obvious.
        sel.getStartupWarning().ifPresent(warning ->
                addDialogs(DialogBox.getSelErrorDialog(warning, selImage)));
    }

    /**
     * Slides the command panel out of (or back into) the input bar.
     * Animating the panel's preferred height makes the chat area above it
     * shrink, so the panel appears to grow upwards.
     */
    @FXML
    private void handleToggleHelp() {
        isHelpOpen = !isHelpOpen;
        helpToggle.setText(isHelpOpen ? HELP_OPEN_LABEL : HELP_CLOSED_LABEL);

        if (isHelpOpen) {
            // Must be visible before the animation starts, or nothing shows.
            helpPanel.setVisible(true);
        }

        // prefHeight(-1) asks the contents how tall they need to be, so the
        // panel fits its command list without a hard-coded height.
        double targetHeight = isHelpOpen ? helpContent.prefHeight(-1) : 0;

        // Both heights are animated: a VBox shrinks a child down to its
        // minimum height when space is tight, so animating prefHeight
        // alone would leave the panel only partly open in a short window.
        Timeline slide = new Timeline(
                new KeyFrame(
                        SLIDE_DURATION,
                        new KeyValue(helpPanel.prefHeightProperty(), targetHeight),
                        new KeyValue(helpPanel.minHeightProperty(), targetHeight)));

        if (!isHelpOpen) {
            slide.setOnFinished(event -> helpPanel.setVisible(false));
        }

        slide.play();
    }

    /**
     * Sends the current text field contents to Sel and displays both
     * the user's message and Sel's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String trimmedInput = input.trim();
        Response response = sel.getResponse(trimmedInput);

        addDialogs(
                DialogBox.getUserDialog(trimmedInput, userImage),
                createSelDialog(response));

        userInput.clear();

        if (trimmedInput.equals("bye")) {
            startClosingCountdown();
        }
    }

    /**
     * Creates the dialog box for one of Sel's replies, using the error
     * styling when Sel could not carry out the command.
     *
     * @param response Sel's reply
     * @return the dialog box to display
     */
    private DialogBox createSelDialog(Response response) {
        if (response.isError()) {
            return DialogBox.getSelErrorDialog(response.text(), selImage);
        }
        return DialogBox.getSelDialog(response.text(), selImage);
    }

    /**
     * Displays a three-second countdown before closing the application.
     */
    private void startClosingCountdown() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        addDialogs(
                DialogBox.getSelDialog("App closing in", selImage));

        Timeline countdown = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        event -> showCountdownNumber("3")),
                new KeyFrame(
                        Duration.seconds(1),
                        event -> showCountdownNumber("2")),
                new KeyFrame(
                        Duration.seconds(2),
                        event -> showCountdownNumber("1")),
                new KeyFrame(
                        Duration.seconds(3),
                        event -> closeApplication())
        );

        countdown.play();
    }

    /**
     * Displays one countdown number as a new Sel dialog.
     *
     * @param number countdown number to display
     */
    private void showCountdownNumber(String number) {
        addDialogs(DialogBox.getSelDialog(number, selImage));
    }

    /**
     * Closes the application window.
     */
    private void closeApplication() {
        userInput.getScene().getWindow().hide();
    }
}
