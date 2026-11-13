package nova.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nova.Nova;

/**
 * Controller for Nova's main JavaFX window.
 */
public class MainWindow {
    private static final String ERROR_PREFIX = "OOPS!!!";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nova nova;

    /**
     * Configures bindings that should be active once the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Nova instance used to process user commands.
     *
     * @param nova Nova instance backing this interface.
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        addNovaDialog("Hello! I'm Nova.\nWhat can I do for you?", false);
    }

    /**
     * Processes the text currently entered by the user.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || nova == null) {
            return;
        }

        addUserDialog(input);
        String response = nova.getResponse(input);
        addNovaDialog(response, response.startsWith(ERROR_PREFIX));
        userInput.clear();

        if (input.equals("bye")) {
            Platform.exit();
        }
    }

    private void addUserDialog(String text) {
        dialogContainer.getChildren().add(createDialog("You", text, Pos.CENTER_RIGHT, "user-dialog"));
    }

    private void addNovaDialog(String text, boolean isError) {
        String styleClass = isError ? "error-dialog" : "nova-dialog";
        dialogContainer.getChildren().add(createDialog("Nova", text, Pos.CENTER_LEFT, styleClass));
    }

    private HBox createDialog(String speaker, String text, Pos alignment, String styleClass) {
        Label label = new Label(speaker + ":\n" + text);
        label.setWrapText(true);
        label.getStyleClass().add(styleClass);

        HBox dialog = new HBox(label);
        dialog.setAlignment(alignment);
        dialog.getStyleClass().add("dialog-row");
        return dialog;
    }
}
