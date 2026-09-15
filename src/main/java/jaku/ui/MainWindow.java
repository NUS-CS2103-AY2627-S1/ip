package jaku.ui;

import jaku.CommandResult;
import jaku.Jaku;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controller for Jaku's main chat window. */
public class MainWindow {
    /** Console divider emitted by the shared command service. */
    private static final String RESPONSE_DIVIDER = "(?m)^_{60}\\R?";

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    private Jaku jaku;

    /** Creates the FXML controller for Jaku's main window. */
    public MainWindow() {
    }

    /**
     * Injects Jaku's UI-independent command service.
     *
     * @param jaku service that processes GUI commands
     */
    public void setJaku(Jaku jaku) {
        this.jaku = jaku;
        addDialog(DialogBox.welcome("Hi, I'm Jaku. Let's make space for what matters.\n"
                + "Start with todo, deadline, event, or repeat."));
        jaku.getStartupNotice().ifPresent(this::addJakuDialog);
    }

    /** Scrolls new messages into view. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends the text-field command and shows Jaku's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        addDialog(DialogBox.user(input));
        addJakuDialog(jaku.processCommand(input));
        userInput.clear();
        userInput.requestFocus();
        if (jaku.isExitRequested()) {
            Platform.runLater(Platform::exit);
        }
    }

    /**
     * Removes console-only framing before displaying a response in the GUI.
     *
     * @param response response emitted by Jaku's shared command service
     * @return response suitable for a chat bubble
     */
    private String formatForGui(String response) {
        return response.replaceAll(RESPONSE_DIVIDER, "").strip();
    }

    /** Adds a Jaku response with its semantic message styling. */
    private void addJakuDialog(CommandResult result) {
        addDialog(DialogBox.reply(formatForGui(result.text()), result.kind()));
    }

    /** Adds a dialog whose bubble adapts to the available conversation width. */
    private void addDialog(DialogBox dialog) {
        dialog.bindMaximumBubbleWidth(scrollPane.widthProperty().subtract(88.0).multiply(0.72));
        dialogContainer.getChildren().add(dialog);
    }
}
