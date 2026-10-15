package dude.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dude.command.core.CommandType;
import dude.task.Task;

/**
 * Coordinates the FXML controls in the main DUDE window.
 */
public class MainWindowController {
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private ScrollPane conversationScroll;
    @FXML
    private VBox conversationContainer;
    @FXML
    private TextField commandInput;
    @FXML
    private Button executeButton;
    @FXML
    private ListView<String> commandListView;

    private final GuiController controller;
    private final List<String> commandUsages = Arrays.stream(CommandType.values())
            .map(CommandType::getUsageMessage)
            .toList();

    /**
     * Creates the controller used by FXML.
     */
    public MainWindowController() {
        controller = new GuiController();
    }

    /**
     * Loads persisted tasks and prepares automatic conversation scrolling.
     */
    @FXML
    private void initialize() {
        controller.loadTasks();
        taskListView.setCellFactory(view -> new TaskCell());
        commandListView.setCellFactory(view -> new CommandCell());
        refreshTaskList();
        updateCommandSuggestions("");
        appendMessage("Welcome to DUDE. Enter a command below.", DialogBox.Kind.APP);
        if (!controller.getLoadMessage().isBlank()) {
            appendMessage(controller.getLoadMessage(), DialogBox.Kind.ERROR);
        }
        conversationContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollToBottom());
        conversationScroll.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                scrollToBottom());
        commandInput.textProperty().addListener((observable, oldText, newText) ->
                updateCommandSuggestions(newText));
        commandInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB
                    && commandListView.getSelectionModel().getSelectedItem() != null) {
                selectCommand();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP) {
                focusCommandSuggestions(event.getCode());
                event.consume();
            }
        });
        commandListView.setOnMouseClicked(event -> selectCommand());
        commandListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                selectCommand();
                event.consume();
            }
        });
        commandInput.requestFocus();
    }

    /**
     * Executes the command in the input field when the button or Enter is used,
     * closing the window when the command requests an exit.
     */
    @FXML
    private void executeCommand() {
        String command = commandInput.getText();
        if (command == null || command.isBlank()) {
            return;
        }
        appendMessage(command, DialogBox.Kind.USER);
        GuiResponse response = controller.execute(command);
        if (!response.text().isBlank()) {
            appendMessage(response.text(), response.error() ? DialogBox.Kind.ERROR : DialogBox.Kind.APP);
        }
        refreshTaskList();
        commandInput.clear();
        if (response.exit()) {
            closeWindow();
        } else {
            commandInput.requestFocus();
        }
    }

    /**
     * Closes the current JavaFX window.
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) executeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Appends one message to the conversation and scrolls to the latest message.
     *
     * @param message Message text.
     * @param kind    Visual message category.
     */
    private void appendMessage(String message, DialogBox.Kind kind) {
        conversationContainer.getChildren().add(new DialogBox(message, kind));
        scrollToBottom();
    }

    /**
     * Refreshes the structured task list from the command controller.
     */
    private void refreshTaskList() {
        taskListView.getItems().setAll(controller.getTasks());
    }

    /**
     * Updates command suggestions according to the current input prefix.
     *
     * @param input Current command input.
     */
    private void updateCommandSuggestions(String input) {
        commandListView.getItems().setAll(filterCommandUsages(input, commandUsages));
    }

    /**
     * Filters the command palette while keeping the matching usage visible after
     * the user starts entering that command's arguments.
     *
     * @param input Current command input.
     * @param usages Available command usage messages.
     * @return Usage messages relevant to the current command input.
     */
    static List<String> filterCommandUsages(String input, List<String> usages) {
        String normalizedInput = input == null ? "" : input.stripLeading().toLowerCase(Locale.ROOT);
        if (normalizedInput.isBlank()) {
            return usages;
        }

        String commandToken = normalizedInput.split("\\s+", 2)[0];
        boolean hasArguments = normalizedInput.length() > commandToken.length();
        if (hasArguments) {
            return usages.stream()
                    .filter(usage -> commandWord(usage).equals(commandToken))
                    .toList();
        }

        return usages.stream()
                .filter(usage -> commandWord(usage).startsWith(commandToken))
                .toList();
    }

    /**
     * Extracts the command word from a usage message.
     *
     * @param usage Usage message beginning with {@code Usage: }.
     * @return Command word.
     */
    private static String commandWord(String usage) {
        return usage.substring("Usage: ".length()).split("\\s", 2)[0]
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Inserts the selected command suggestion into the input field.
     */
    private void selectCommand() {
        String usage = commandListView.getSelectionModel().getSelectedItem();
        if (usage == null) {
            return;
        }
        String command = usage.substring("Usage: ".length()).split("\\s", 2)[0];
        commandInput.setText(commandUsages.stream().anyMatch(item -> item.equals(usage)
                && item.contains("<")) ? command + " " : command);
        commandInput.requestFocus();
        commandInput.positionCaret(commandInput.getText().length());
    }

    /**
     * Moves focus to the first or last command suggestion.
     *
     * @param direction Key direction that triggered navigation.
     */
    private void focusCommandSuggestions(KeyCode direction) {
        if (commandListView.getItems().isEmpty()) {
            return;
        }
        commandListView.requestFocus();
        int selectedIndex = direction == KeyCode.DOWN ? 0 : commandListView.getItems().size() - 1;
        commandListView.getSelectionModel().select(selectedIndex);
        commandListView.scrollTo(selectedIndex);
    }

    /**
     * Scrolls the conversation pane to its latest content after layout.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> {
            conversationScroll.applyCss();
            conversationScroll.layout();
            conversationScroll.setVvalue(conversationScroll.getVmax());
            Platform.runLater(() -> conversationScroll.setVvalue(conversationScroll.getVmax()));
        });
    }
}
