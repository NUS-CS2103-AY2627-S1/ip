package kafka.javafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kafka.Kafka;
import kafka.KafkaResponse;
import kafka.javafx.components.DialogBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextArea userInput;

    private Kafka kafka;

    private final Image userImage = new Image(MainWindow.class.getResourceAsStream("/images/gigachad.png"));
    private final Image kafkaImage = new Image(MainWindow.class.getResourceAsStream("/images/franzkafka.jpg"));

    /**
     * Keeps the conversation scrolled to its latest response.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML must inject scrollPane";
        assert dialogContainer != null : "FXML must inject dialogContainer";
        assert userInput != null : "FXML must inject userInput";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // TextArea retains literal tabs; Enter still submits a command instead of adding a line.
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                handleUserInput();
            }
        });
    }

    /**
     * Sets the Kafka instance used to process input.
     *
     * @param kafka Kafka instance.
     */
    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
        greetUponStart();
    }

    /**
     * Displays a command and its response, handling recovery and exit actions in the GUI.
     */
    @FXML
    private void handleUserInput() {
        assert kafka != null : "Kafka must be set before processing user input";
        String input = userInput.getText();
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        KafkaResponse response = getResponseWithRecovery(input);
        displayResponse(response);
        userInput.clear();
        if (response.action() == KafkaResponse.Action.EXIT) {
            Platform.exit();
        }
    }

    /**
     * Handles storage recovery and retries the original command after successful recovery.
     */
    private KafkaResponse getResponseWithRecovery(String input) {
        KafkaResponse response = kafka.getResponse(input);
        if (response.action() != KafkaResponse.Action.CONFIRM_STORAGE_OVERWRITE
                || !shouldOverwriteStorage(response.message())) {
            return response;
        }

        KafkaResponse recoveryResponse = kafka.recoverStorage();
        if (recoveryResponse.isError()) {
            return recoveryResponse;
        }
        displayResponse(recoveryResponse);
        return kafka.getResponse(input);
    }

    /**
     * Asks for recovery in an owned dialog; closing it preserves the saved file.
     */
    private boolean shouldOverwriteStorage(String message) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                message + "\n\nOverwrite the corrupted file with an empty task list?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Recover saved tasks");
        confirmation.setHeaderText("Your saved tasks could not be loaded");
        confirmation.initOwner(userInput.getScene().getWindow());
        Button yesButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.YES);
        Button noButton = (Button) confirmation.getDialogPane().lookupButton(ButtonType.NO);
        yesButton.setDefaultButton(false);
        noButton.setDefaultButton(true);
        confirmation.setOnShown(event -> noButton.requestFocus());
        return confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    /**
     * Appends a reply using the style indicated by its error status.
     */
    private void displayResponse(KafkaResponse response) {
        DialogBox responseDialog = response.isError()
                ? DialogBox.getErrorDialog(response.message(), kafkaImage)
                : DialogBox.getKafkaDialog(response.message(), kafkaImage);
        dialogContainer.getChildren().add(responseDialog);
    }

    /**
     * Adds Kafka's greeting as the first message in the conversation.
     */
    private void greetUponStart() {
        String greeting = kafka.greet();
        dialogContainer.getChildren().add(DialogBox.getKafkaDialog(greeting, kafkaImage));
    }
}

