package anders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import anders.ui.Ui;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/** Displays a compact conversation with command entry, help, and keyboard history. */
public class MainWindow extends BorderPane {
    private static final String COMMAND_HELP = """
            Command guide
            A few signposts for your trail.
            Type one command, then press Enter.
            Replace <...> with your own text; leave out the brackets.

            Add tasks
            todo <task> — Add a task with no due date.
            Example: todo read chapter 1

            deadline <task> /by <date> — Add a task with a due date.
            Example: deadline report /by 2/10/2026 1800

            event <task> /from <start> /to <end>
            Add an event with a start and end time.
            Example: event study /from 2/10/2026 1400 /to 2/10/2026 1600
            Dates use day/month/year, with an optional 24-hour time.
            2/10/2026 1800 means 2 Oct 2026 at 6 pm.

            View tasks
            list — Show all tasks and their numbers.
            find <text> — Search task descriptions.
            Example: find chapter

            Update a task
            Use task numbers from list.
            <number> can be any number from 1 to your task count.
            With 2 tasks, replace <number> with 1 or 2.
            mark <number> — Mark a task as done.
            unmark <number> — Mark a task as not done.
            delete <number> — Remove a task.
            Example: mark 2
            After deleting, run list again: task numbers change.

            Use tags
            Tags group tasks. Replace #<tag> with a label like #school.
            tag <number> #<tag> — Add a tag to a task.
            untag <number> #<tag> — Remove a tag from a task.
            find #<tag> — Show tasks with that tag.
            Example: tag 2 #school
            To tag a new task, add /tags #school at the end.
            Example: todo revise notes /tags #school""";

    @FXML
    private Label appName;
    @FXML
    private Label tagline;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messages;
    @FXML
    private TextField input;
    @FXML
    private Button sendButton;

    private final Function<String, String> responder;
    private final List<String> history = new ArrayList<>();
    private int historyIndex;
    // Preserve unfinished input while the user browses previously submitted commands.
    private String draft = "";

    /** Creates the main GUI layout and connects it to the saved Anders session. */
    public MainWindow() {
        this(new Anders());
    }

    /** Creates a window for a session and displays any startup storage warning. */
    MainWindow(Anders anders) {
        this(anders::getResponse);
        if (!anders.getStartupWarning().isEmpty()) {
            appendReply(anders.getStartupWarning());
        }
    }

    /** Creates a window with a supplied responder so GUI tests do not touch saved tasks. */
    MainWindow(Function<String, String> responder) {
        this.responder = responder;
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout", exception);
        }
    }

    /** Initializes input feedback and a short, actionable welcome message. */
    @FXML
    private void initialize() {
        assert scrollPane != null && messages != null && input != null && sendButton != null
                : "FXML must inject all main-window controls before initialization";
        appName.setText(Ui.NAME);
        tagline.setText(Ui.TAGLINE);
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> input.getText().isBlank(),
                input.textProperty()));
        messages.getChildren().add(DialogBox.getBotDialog(
                Ui.WELCOME_MESSAGE + "\n\n"
                        + "Try todo read chapter 1 to begin.\n"
                        + "Tasks shows your trail. Commands lights the way."));
        Platform.runLater(input::requestFocus);
    }

    /** Processes typed input and keeps the keyboard ready for the next command. */
    @FXML
    private void handleInput() {
        String command = input.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        submitCommand(command);
        input.clear();
        draft = "";
        input.requestFocus();
    }

    /** Displays saved tasks without discarding a command being drafted. */
    @FXML
    private void showTasks() {
        submitCommand("list");
        input.requestFocus();
    }

    /** Displays command examples on demand while leaving the input draft intact. */
    @FXML
    private void showHelp() {
        appendReply(DialogBox.getBotDialog(COMMAND_HELP,
                List.of("Command guide", "Add tasks", "View tasks", "Update a task",
                        "Use task numbers from list.", "Use tags")));
        input.requestFocus();
    }

    /** Records a command and displays the reply returned by the application. */
    private void submitCommand(String command) {
        messages.getChildren().add(DialogBox.getUserDialog(command));
        appendReply(responder.apply(command));
        history.add(command);
        historyIndex = history.size();
    }

    /** Recalls previous commands and restores the draft after the newest entry. */
    @FXML
    private void handleHistory(KeyEvent event) {
        if (event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown()
                || (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN) || history.isEmpty()) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            if (historyIndex == history.size()) {
                draft = input.getText();
            }
            historyIndex = Math.max(0, historyIndex - 1);
        } else {
            if (historyIndex == history.size()) {
                return;
            }
            historyIndex = Math.min(history.size(), historyIndex + 1);
        }
        input.setText(historyIndex == history.size() ? draft : history.get(historyIndex));
        input.positionCaret(input.getLength());
        event.consume();
    }

    /** Reveals the start of a long reply, or the whole reply when it fits in the viewport. */
    private void appendReply(String text) {
        appendReply(DialogBox.getBotDialog(text));
    }

    /** Appends a prepared reply and scrolls to its first readable line. */
    private void appendReply(DialogBox reply) {
        messages.getChildren().add(reply);
        Platform.runLater(() -> {
            // Lay out wrapped text before converting the reply position into a scroll fraction.
            applyCss();
            layout();
            double scrollableHeight = messages.getHeight() - scrollPane.getViewportBounds().getHeight();
            double replyTop = Math.max(0, reply.getLayoutY() - 8);
            scrollPane.setVvalue(scrollableHeight <= 0 ? 0 : Math.min(1, replyTop / scrollableHeight));
        });
    }
}
