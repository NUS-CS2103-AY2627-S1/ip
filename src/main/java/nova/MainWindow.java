package nova;

import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window: the header with the product name, the task
 * count and the theme switch, the chat area, and the input row that recalls past
 * commands.
 */
public class MainWindow extends AnchorPane {
    private static final String STYLE_DARK = "dark";
    private static final String THEME_DARK_LABEL = "☾ Dark";
    private static final String THEME_LIGHT_LABEL = "☀ Light";
    private static final String CHIP_NO_TASKS = "No tasks";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button themeButton;
    @FXML
    private Label taskChip;

    private final CommandHistory history = new CommandHistory();
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image novaImage = new Image(this.getClass().getResourceAsStream("/images/DaNova.png"));

    private Nova nova;
    private Settings settings;
    private Parent themeTarget;

    /**
     * Sets up the chat window: auto-scrolls to the newest dialog, listens for the
     * keys that recall or complete a command, and focuses the input field.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        userInput.requestFocus();
    }

    /**
     * Injects the chatbot instance used to generate responses.
     *
     * @param nova the chatbot
     */
    public void setNova(Nova nova) {
        this.nova = nova;
    }

    /**
     * Injects the remembered choices and the node that carries the theme, then
     * shows the greeting once the chatbot has loaded its tasks.
     *
     * @param settings the remembered settings
     * @param themeTarget the scene root the theme style class is applied to
     */
    public void setSettings(Settings settings, Parent themeTarget) {
        this.settings = settings;
        this.themeTarget = themeTarget;
        applyTheme(settings.isDarkTheme());
        updateTaskChip();
        String greeting = Nova.MESSAGE_GREETING + "\n" + Ui.MESSAGE_COMMAND_HINT;
        if (!nova.getStartupWarning().isEmpty()) {
            greeting = greeting + "\n" + nova.getStartupWarning();
        }
        addDialog(DialogBox.getNovaDialog(CommandResult.info(greeting), novaImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user input and the other
     * containing the chatbot's reply, and appends them to the dialog container.
     * Clears the user input after processing, and exits the app on "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        history.add(input);
        CommandResult result = nova.executeCommand(input);
        addDialog(DialogBox.getUserDialog(input, userImage));
        addDialog(DialogBox.getNovaDialog(result, novaImage));
        userInput.clear();
        updateTaskChip();
        if (nova.isExitCommand(input)) {
            Platform.exit();
        }
    }

    /** Switches between the light and the dark theme, and remembers the choice. */
    @FXML
    public void toggleTheme() {
        boolean useDark = !settings.isDarkTheme();
        settings.setDarkTheme(useDark);
        applyTheme(useDark);
    }

    /**
     * Applies a theme by swapping the style class on the node that is really in
     * the scene. With an FXML controller this object is not the scene root, so the
     * class has to go on the root the loader built, otherwise nothing repaints.
     *
     * @param useDark whether the dark theme should be shown
     */
    private void applyTheme(boolean useDark) {
        if (themeTarget != null) {
            themeTarget.getStyleClass().remove(STYLE_DARK);
            if (useDark) {
                themeTarget.getStyleClass().add(STYLE_DARK);
            }
        }
        updateThemeButton();
    }

    /**
     * Handles the keys that make the input field behave like a command line: up
     * and down recall earlier commands, Tab completes a command word.
     *
     * @param event the key press
     */
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                userInput.setText(history.previous());
                moveCaretToEnd();
                event.consume();
                break;
            case DOWN:
                userInput.setText(history.next());
                moveCaretToEnd();
                event.consume();
                break;
            case TAB:
                completeCommandWord();
                event.consume();
                break;
            default:
                break;
        }
    }

    /** Completes a half-typed command word when only one command matches it. */
    private void completeCommandWord() {
        String typed = userInput.getText();
        if (typed.isEmpty() || typed.contains(" ")) {
            return;
        }
        List<String> matches = Nova.getCommandWords().stream()
                .filter(word -> word.startsWith(typed))
                .toList();
        if (matches.size() == 1) {
            userInput.setText(matches.get(0) + " ");
            moveCaretToEnd();
        }
    }

    /** Shows how many tasks the list holds, and how many of them are done. */
    private void updateTaskChip() {
        if (nova == null) {
            return;
        }
        int total = nova.getTaskCount();
        if (total == 0) {
            taskChip.setText(CHIP_NO_TASKS);
            return;
        }
        String noun = total == 1 ? " task" : " tasks";
        int done = nova.getDoneCount();
        taskChip.setText(total + noun + (done > 0 ? ", " + done + " done" : ""));
    }

    /** Points the theme button at the theme the user would switch to. */
    private void updateThemeButton() {
        if (settings == null) {
            return;
        }
        boolean isDark = settings.isDarkTheme();
        themeButton.setText(isDark ? THEME_LIGHT_LABEL : THEME_DARK_LABEL);
        String hint = isDark ? "Switch to the light theme (Ctrl+D)" : "Switch to the dark theme (Ctrl+D)";
        themeButton.setTooltip(new Tooltip(hint));
    }

    /** Moves the caret to the end of the recalled or completed text. */
    private void moveCaretToEnd() {
        userInput.positionCaret(userInput.getText().length());
    }

    /** Adds a dialog box that keeps its text within the width of the chat area. */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.limitBubbleWidth(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }
}
