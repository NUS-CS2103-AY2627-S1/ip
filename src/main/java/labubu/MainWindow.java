package labubu;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/** Composes the conversation area and command input into the main window. */
public class MainWindow extends BorderPane {
    private static final String INVALID_DETAILS_MESSAGE = "Oops! Invalid task details.";
    private static final String INVALID_INDEX_MESSAGE = "Oops! Invalid task index.";
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Oops! Invalid date/time. Use dd/MM/yyyy or dd/MM/yyyy HH:mm.";
    private static final String INVALID_TIME_RANGE_MESSAGE = "Oops! An event must start before it ends.";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Hmm, I don't recognise that command.";

    private final VBox conversation = new VBox(10);
    private final ScrollPane messages = new ScrollPane(conversation);
    private final VBox commandArea = new VBox();
    private final InputField inputField = new InputField(this::handleCommand);
    private final Labubu labubu;
    private HelpPanel helpPanel;

    /** Creates the main window for a Labubu instance. */
    public MainWindow(Labubu labubu) {
        this.labubu = labubu;
        conversation.getStyleClass().add("conversation");
        messages.setFitToWidth(true);
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollToLatest());
        setCenter(messages);
        commandArea.getChildren().add(inputField);
        setBottom(commandArea);
        addMessage(new Ui().getIntro(), false);
    }

    private void handleCommand(String command) {
        addMessage(command, true);
        if (command.equalsIgnoreCase("help")) {
            toggleHelpPanel();
        } else {
            String response = labubu.processGuiCommand(command);
            addMessage(response, false, isErrorResponse(response));
            if (isExitCommand(command)) {
                Platform.exit();
            }
        }
    }

    private void addMessage(String text, boolean fromUser) {
        addMessage(text, fromUser, false);
    }

    private void addMessage(String text, boolean fromUser, boolean isError) {
        conversation.getChildren().add(new SpeechBubble(text, fromUser, isError));
        scrollToLatest();
    }

    private void scrollToLatest() {
        Platform.runLater(() -> messages.setVvalue(messages.getVmax()));
    }

    private boolean isErrorResponse(String response) {
        return INVALID_DETAILS_MESSAGE.equals(response)
                || INVALID_INDEX_MESSAGE.equals(response)
                || INVALID_DATE_TIME_MESSAGE.equals(response)
                || INVALID_TIME_RANGE_MESSAGE.equals(response)
                || UNKNOWN_COMMAND_MESSAGE.equals(response);
    }

    private boolean isExitCommand(String command) {
        return command.equalsIgnoreCase("bye")
                || command.equalsIgnoreCase("quit")
                || command.equalsIgnoreCase("exit");
    }

    private void toggleHelpPanel() {
        if (helpPanel == null) {
            helpPanel = new HelpPanel(new Ui().getHelp(), this::hideHelpPanel);
            helpPanel.prefWidthProperty().bind(commandArea.widthProperty());
            commandArea.getChildren().add(0, helpPanel);
        } else {
            hideHelpPanel();
        }
    }

    private void hideHelpPanel() {
        commandArea.getChildren().remove(helpPanel);
        helpPanel = null;
    }
}
