package panda.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for Panda's main chat window.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private CheckBox sortByDateCheckBox;
    @FXML
    private Button sortDirectionButton;

    private final PandaService pandaService = new PandaService();
    private final Image poImage = loadAvatar("/images/po-avatar.png");
    private final Image platypusImage = loadAvatar("/images/platypus-avatar.png");

    /** Binds the chat history to the scroll position. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sortDirectionButton.setDisable(true);
        addPandaDialog("Hello! I'm Panda.\nWhat can I do for you?");
        if (pandaService.getStartupWarning() != null) {
            addPandaDialog(pandaService.getStartupWarning());
        }
    }

    /** Sends the current text to Panda when Enter or Send is pressed. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(new DialogBox(input, platypusImage, false));
        String response = pandaService.getResponse(input);
        if (response.equals(PandaService.BYE_RESPONSE)) {
            addPandaDialog("Bye. Hope to see you again soon!");
            userInput.setDisable(true);
        } else {
            addPandaDialog(response);
        }
        userInput.clear();
    }

    /** Updates the task display after the date-sorting checkbox changes. */
    @FXML
    private void handleDateSorting() {
        boolean isSortingEnabled = sortByDateCheckBox.isSelected();
        sortDirectionButton.setDisable(!isSortingEnabled);
        addPandaDialog(pandaService.setDateSortingEnabled(isSortingEnabled));
    }

    /** Reverses the selected date-sorting direction and displays the updated task order. */
    @FXML
    private void handleSortDirection() {
        addPandaDialog(pandaService.toggleDateSortDirection());
        sortDirectionButton.setText(pandaService.getSortDirection().getDisplayName());
    }

    private void addPandaDialog(String response) {
        dialogContainer.getChildren().add(new DialogBox(response, poImage, true));
    }

    private Image loadAvatar(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}
