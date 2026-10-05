package kaykay.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kaykay.Kaykay;

/**
 * Controls Kaykay's main JavaFX window defined in {@code MainWindow.fxml}.
 */
public final class MainWindow extends AnchorPane {
    private static final String DEFAULT_FILE_PATH = "data/kaykay.txt";

    /** Semantic presentation applied to the buffered Kaykay response. */
    private enum ResponseType {
        NORMAL,
        SUCCESS,
        ERROR
    }

    /** Displays the conversation messages. */
    @FXML
    private ScrollPane scrollPane;

    /** Contains the conversation's dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** Accepts a command from the user. */
    @FXML
    private TextField userInput;

    /** Submits the command entered by the user. */
    @FXML
    private Button sendButton;

    /** Buffers rendered Kaykay output before it is shown in one dialog. */
    private final StringBuilder responseBuffer = new StringBuilder();

    /** Semantic type of the response being buffered. */
    private ResponseType responseType = ResponseType.NORMAL;

    /** Renders chatbot output into the response buffer with its semantic state. */
    private final Ui ui = new Ui(
            this::captureResponse, this::captureErrorResponse, this::captureSuccessResponse, false);

    /** Processes commands and manages the task data. */
    private final Kaykay kaykay = new Kaykay(DEFAULT_FILE_PATH, ui);

    /** Initializes the controls injected from the FXML view. */
    @FXML
    private void initialize() {
        assert scrollPane != null : "FXML loader must inject the scroll pane";
        assert dialogContainer != null : "FXML loader must inject the dialog container";
        assert userInput != null : "FXML loader must inject the user input field";
        assert sendButton != null : "FXML loader must inject the send button";

        dialogContainer.getChildren().add(DialogBox.getKaykayDialog(
                "Kaykay online!" + System.lineSeparator() + "What's our next mission?"));
        kaykay.showInitialDataLoadError();
        String loadingError = takeResponse();
        responseType = ResponseType.NORMAL;
        if (!loadingError.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getKaykayErrorDialog(loadingError));
        }
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /** Processes the entered command and appends the resulting dialog boxes. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (!userText.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(userText));
        }

        boolean shouldExit = kaykay.shouldExitAfterProcessingCommand(userText);
        if (shouldExit) {
            kaykay.showFarewell();
        }

        ResponseType bufferedResponseType = responseType;
        String kaykayText = takeResponse();
        responseType = ResponseType.NORMAL;
        if (!kaykayText.isEmpty()) {
            DialogBox kaykayDialog = DialogBox.getKaykayDialog(kaykayText);
            if (bufferedResponseType == ResponseType.SUCCESS) {
                kaykayDialog = DialogBox.getKaykaySuccessDialog(kaykayText);
            } else if (bufferedResponseType == ResponseType.ERROR) {
                kaykayDialog = DialogBox.getKaykayErrorDialog(kaykayText);
            }
            dialogContainer.getChildren().add(kaykayDialog);
        }
        userInput.clear();

        if (shouldExit) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    /** Captures one rendered UI line for the current chatbot response. */
    private void captureResponse(String line) {
        responseBuffer.append(line).append(System.lineSeparator());
    }

    /** Captures one error line and marks the current chatbot response as an error. */
    private void captureErrorResponse(String line) {
        responseType = ResponseType.ERROR;
        captureResponse(line);
    }

    /** Captures one success line and marks the current chatbot response as successful. */
    private void captureSuccessResponse(String line) {
        responseType = ResponseType.SUCCESS;
        captureResponse(line);
    }

    /** Returns the buffered response and clears it for the next command. */
    private String takeResponse() {
        String response = responseBuffer.toString().stripTrailing();
        responseBuffer.setLength(0);
        return response;
    }
}
