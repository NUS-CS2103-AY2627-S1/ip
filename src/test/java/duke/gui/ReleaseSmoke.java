package duke.gui;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import duke.Deadline;
import duke.Event;
import duke.SaveLocation;
import duke.Storage;
import duke.StorageException;
import duke.Task;
import duke.TaskMatch;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Exercises the release JAR's actual launcher, desktop commands, and text saving in a disposable folder.
 */
public final class ReleaseSmoke {
    private static volatile Throwable failure;

    private final Stage stage;

    private ReleaseSmoke(Stage stage) {
        this.stage = stage;
    }

    /**
     * Runs one release scenario. The Python runner creates a fresh working folder and enforces a timeout.
     *
     * @param args The scenario: normal, reload, or blocked.
     */
    public static void main(String[] args) {
        AtomicBoolean hasStarted = new AtomicBoolean();
        var watcher = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().factory());
        watcher.scheduleAtFixedRate(() -> {
            try {
                Platform.runLater(() -> {
                    if (Window.getWindows().isEmpty() || hasStarted.getAndSet(true)) {
                        return;
                    }
                    watcher.shutdown();
                    verifyWindow(args[0]);
                });
            } catch (IllegalStateException e) {
                // The launcher has not initialized JavaFX yet; the next scheduled check will try again.
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
        Launcher.main(new String[0]);
        if (failure != null) {
            throw new AssertionError("Release smoke failed", failure);
        }
    }

    /**
     * Runs the scenario on the JavaFX thread and records any failure before closing the window.
     */
    private static void verifyWindow(String scenario) {
        try {
            Stage window = (Stage) Window.getWindows().getFirst();
            new ReleaseSmoke(window).verify(scenario);
            System.out.println("PASS: release GUI " + scenario);
        } catch (Throwable error) {
            failure = error;
        } finally {
            Platform.exit();
        }
    }

    private void verify(String scenario) throws StorageException {
        check(stage.getTitle().contains("Wangsa"), "The window must use the product name");
        check(stage.isShowing(), "The release window must open");
        check(stage.getScene().lookup(".assistant-icon") != null, "Replies must use a simple speaker symbol");
        if (scenario.equals("blocked")) {
            check(lastReply().contains("couldn't load"), "Corrupt data must produce a startup error");
            send("todo must not overwrite data");
            check(lastReply().contains("unavailable"), "Task commands must be blocked after a load error");
            check(stage.getScene().lookup(".error-bubble") != null, "Storage errors must be visually distinct");
            send("help");
            check(lastReply().contains("todo DESCRIPTION"), "Offline help must remain available after a load error");
            verifyLayout(780, 550);
        } else if (scenario.equals("reload")) {
            check(lastReply().contains("[Done] submit report"), "Saved completion must survive a restart");
            check(lastReply().contains("[To do] team meeting"), "Saved events must survive a restart");
            check(savedTasks().size() == 2, "Saved deletions must survive a restart");
        } else {
            verifyCommands();
        }
        send("bye");
        check(lastReply().contains("See you next time"), "The bye command must exit normally");
    }

    /**
     * Runs command checks in the order needed to leave a known save file for the reload scenario.
     */
    private void verifyCommands() throws StorageException {
        verifyTaskCreation();
        verifyTaskChanges();
        verifyInvalidCommands();
        verifyComposerActions();
        verifyOfflineHelp();
        verifyLayout(780, 550);
        verifyLayout(1120, 700);
        verifyLayout(1700, 950);
        verifyLongTaskRows();
        verifyCommandHistory();
        verifyLongSearch();
    }

    /**
     * Checks that each task type is accepted and saved.
     */
    private void verifyTaskCreation() throws StorageException {
        check(lastReply().contains("empty"), "A fresh release must start with an empty task list");
        send("  todo read book  ");
        Node userIcon = stage.getScene().lookup(".user-icon");
        check(userIcon != null, "User messages must use a simple speaker symbol");
        check(userIcon.getAccessibleText().equals("Your message"), "Speaker symbols must have accessible labels");
        send("deadline submit report /by 2026-09-20");
        send("event team meeting /from Monday 2pm /to Monday 4pm");
        check(savedTasks().size() == 3, "All three task types must save");
    }

    /**
     * Checks search numbering, completion changes, ordering, and deletion.
     */
    private void verifyTaskChanges() throws StorageException {
        send("find REPORT");
        check(lastReply().contains("2. [To do] submit report"), "Search must retain the full-list task number");
        send("mark 2");
        check(savedTasks().get(1).isDone(), "Mark must save completion");
        send("unmark 2");
        check(!savedTasks().get(1).isDone(), "Unmark must save the reopened task");
        send("mark 2");
        send("sort");
        check(savedTasks().getFirst().getDescription().equals("submit report"), "Sort must save deadline order");
        send("delete 2");
        check(savedTasks().size() == 2, "Delete must save the smaller task list");
        send("list");
        check(lastReply().contains("1. [Done] submit report"), "List must show current numbering and status");
        check(lastReply().contains("2. [To do] team meeting"), "List must retain event details");
        send("find nothing-matches-this");
        check(lastReply().contains("No matching"), "An empty search must be explained");
    }

    /**
     * Checks that invalid commands leave saved tasks unchanged.
     */
    private void verifyInvalidCommands() throws StorageException {
        send("deadline impossible /by 2026-02-30");
        check(savedTasks().size() == 2, "Invalid dates must not add tasks");
        check(stage.getScene().lookup(".error-bubble") != null, "Command errors must be visually distinct");
        send("event repeated /from now /from later /to tomorrow");
        check(savedTasks().size() == 2, "Repeated markers must not add tasks");
        send("delete 999");
        check(savedTasks().size() == 2, "Invalid indexes must not delete tasks");
    }

    /**
     * Checks that submission and shortcut callbacks still reach the command handler.
     */
    private void verifyComposerActions() throws StorageException {
        TextField field = commandField();
        field.setText("list");
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false));
        check(field.getText().isEmpty(), "Enter must clear the submitted command");
        check(lastReply().contains("1. [Done] submit report"), "Enter must execute the command");

        suggestion("new todo").fire();
        check(field.getText().equals("todo "), "A task shortcut must prepare the command without submitting it");
        check(field.getCaretPosition() == field.getText().length(), "Shortcuts must place the caret at the end");
        check(savedTasks().size() == 2, "Preparing a shortcut must not change saved tasks");

        suggestion("list").fire();
        check(field.getText().isEmpty(), "The list shortcut must submit its command immediately");
        check(lastReply().contains("2. [To do] team meeting"), "The list shortcut must display saved tasks");
        Parent messages = (Parent) stage.getScene().lookup("#messages");
        int messageCount = messages.getChildrenUnmodifiable().size();
        send("   ");
        check(messages.getChildrenUnmodifiable().size() == messageCount,
                "Blank input must not add conversation messages");
    }

    /**
     * Returns a shortcut by its visible label so its actual button action can be exercised.
     */
    private Button suggestion(String label) {
        for (Node node : stage.getScene().getRoot().lookupAll(".suggestion-chip")) {
            Button button = (Button) node;
            if (button.getText().equals(label)) {
                return button;
            }
        }
        throw new AssertionError("Missing command shortcut: " + label);
    }

    /**
     * Checks that help is available without AI configuration.
     */
    private void verifyOfflineHelp() {
        send("help");
        check(lastReply().contains("todo DESCRIPTION"), "Built-in help must work without AI configuration");
        check(stage.getScene().getRoot().lookupAll(".help-section").size() == 4,
                "Help must group commands into four sections");
    }

    /**
     * Checks that arrow keys recall commands and return to an empty composer.
     */
    private void verifyCommandHistory() {
        TextField field = commandField();
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false));
        check(field.getText().equals("help"), "Up must recall the last successful command");
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false));
        check(field.getText().isEmpty(), "Down must return to an empty composer");
    }

    /**
     * Checks that a long search command fits the conversation at supported widths.
     */
    private void verifyLongSearch() {
        send("find " + "verylongword".repeat(30));
        verifyLayout(780, 550);
        verifyLayout(1120, 700);
    }

    /**
     * Checks responsive geometry directly so clipped controls and overflowing content are detected.
     */
    private void verifyLayout(double width, double height) {
        Region root = (Region) stage.getScene().getRoot();
        root.resize(width, height);
        root.applyCss();
        root.layout();
        verifyHeaderAlignment(root);
        verifyTypography(root);
        verifyComposer(root, height);
        verifyConversation(root);
        verifyHelpLayout(root, width);
        verifyTaskAlignment(root);
    }

    /**
     * Checks that the input and send button fit the resized window and share a height.
     */
    private void verifyComposer(Parent root, double height) {
        Node field = commandField();
        Node sendButton = root.lookup(".send-button");
        check(field.getBoundsInParent().getWidth() >= 200, "The command field must retain usable width");
        check(Math.abs(field.getLayoutBounds().getHeight() - sendButton.getLayoutBounds().getHeight()) < 1,
                "The input and send button must have equal heights");
        Bounds inputBounds = field.localToScene(field.getLayoutBounds());
        check(inputBounds.getMaxY() <= height, "The command field must fit inside the resized window");
    }

    /**
     * Checks that conversation rows fill the viewport and contain their message bubbles.
     */
    private void verifyConversation(Parent root) {
        Parent messages = (Parent) root.lookup("#messages");
        ScrollPane chat = (ScrollPane) root.lookup(".chat-scroll");
        check(Math.abs(messages.getLayoutBounds().getWidth() - chat.getViewportBounds().getWidth()) < 1,
                "The conversation must use the viewport width instead of a centered column");
        for (Node row : messages.getChildrenUnmodifiable()) {
            verifyMessageRow(row);
        }
    }

    /**
     * Checks speaker alignment and text wrapping within one conversation row.
     */
    private void verifyMessageRow(Node row) {
        Node icon = row.lookup(".message-icon");
        check(icon.getLayoutBounds().getWidth() == 36, "Speaker symbols must share the same column width");
        Node assistantBubble = row.lookup(".assistant-bubble");
        Node bubble = assistantBubble == null ? row.lookup(".user-bubble") : assistantBubble;
        check(Math.abs(icon.getLayoutY() - bubble.getLayoutY()) < 1,
                "Speaker symbols and message bubbles must align at the top");
        verifySpeakerPosition(row, icon, bubble);
        check(bubble.getBoundsInParent().getMaxX() <= row.getLayoutBounds().getWidth() + 1,
                "Long messages must stay inside their row");
        Node body = row.lookup(".message-body");
        check(body.getBoundsInParent().getMaxX() <= bubble.getLayoutBounds().getWidth() + 1,
                "Message text must wrap inside its bubble");
    }

    /**
     * Checks that each speaker stays on its own side and assistant replies keep a readable width.
     */
    private void verifySpeakerPosition(Node row, Node icon, Node bubble) {
        if (icon.getStyleClass().contains("assistant-icon")) {
            check(icon.getBoundsInParent().getMinX() <= 3,
                    "Wangsa replies must remain anchored to the left at every window size");
            check(bubble.getLayoutBounds().getWidth() <= 781,
                    "Assistant text must retain a readable width on wide screens");
        } else {
            check(row.getLayoutBounds().getWidth() - icon.getBoundsInParent().getMaxX() <= 3,
                    "User messages must remain anchored to the right at every window size");
        }
    }

    /**
     * Checks that help sections fit the window and use the appropriate number of columns.
     */
    private void verifyHelpLayout(Parent root, double width) {
        for (Node section : root.lookupAll(".help-section")) {
            Bounds sectionBounds = section.localToScene(section.getLayoutBounds());
            check(sectionBounds.getMaxX() <= width, "Help groups must not overflow the window");
        }
        verifyHelpColumns(root, width);
    }

    /** Checks the original bug in confirmations, lists, search results, and wrapped details. */
    private void verifyTaskAlignment(Parent root) {
        for (Node node : root.lookupAll(".task-content")) {
            Parent content = (Parent) node;
            Label title = (Label) content.lookup(".task-title");
            Bounds titleBounds = title.localToScene(title.getLayoutBounds());
            double previousBottom = titleBounds.getMaxY();
            for (Node child : content.getChildrenUnmodifiable()) {
                if (!child.getStyleClass().contains("task-detail")) {
                    continue;
                }
                Label detail = (Label) child;
                Bounds bounds = detail.localToScene(detail.getLayoutBounds());
                check(Math.abs(bounds.getMinX() - titleBounds.getMinX()) < 1,
                        "Due, From, and To must share the task title's left edge");
                check(bounds.getMinY() >= previousBottom,
                        "Wrapped task titles and details must not overlap");
                check(detail.getText().equals(detail.getText().stripLeading()),
                        "Task details must not rely on leading spaces for alignment");
                previousBottom = bounds.getMaxY();
            }
        }
    }

    /** Exercises three-digit search numbers and literal, wrapping text without changing saved tasks. */
    private void verifyLongTaskRows() {
        Parent messages = (Parent) stage.getScene().lookup("#messages");
        var messageList = (javafx.scene.layout.VBox) messages;
        Task deadline = new Deadline("ST2334 <notes> & " + "long description ".repeat(20),
                LocalDate.of(2026, 9, 30));
        Task event = new Event("meeting", "start time ".repeat(40), "end time ".repeat(40));
        TaskView view = TaskView.matches(List.of(new TaskMatch(10, deadline), new TaskMatch(100, event)));
        ChatMessage message = ChatMessage.tasks(view);
        messageList.getChildren().add(message);
        try {
            verifyLayout(780, 550);
            Label title = (Label) view.lookup(".task-title");
            check(title.getHeight() > title.getFont().getSize() * 2, "The long test title must actually wrap");
            check(view.getAccessibleText().contains("100. [To do] meeting"),
                    "Three-digit task numbers must retain their meaning");
            deadline.markAsDone();
            check(title.getText().startsWith("[To do]"), "Earlier replies must remain snapshots");
            verifyLayout(1120, 700);
            verifyLayout(1700, 950);
        } finally {
            messageList.getChildren().remove(message);
        }
    }

    /** Checks the rendered font and smoothing rather than only checking stylesheet text. */
    private void verifyTypography(Parent root) {
        Label brand = (Label) root.lookup(".brand-name");
        check(brand.getFont().getFamily().equals(Typography.selectFontFamily(Font.getFamilies())),
                "The interface must use an installed preferred font");
        check(brand.getFont().getSize() == 20, "The product heading must have clear visual emphasis");
        check(brand.getFont().getStyle().toLowerCase(Locale.ROOT).contains("bold"),
                "The product heading must resolve to a bold font face");
        check(commandField().getFont().getSize() == 14, "The input must use a readable font size");
        for (Node node : root.lookupAll(".message-body")) {
            if (node instanceof Label label) {
                check(!label.getFont().getStyle().toLowerCase(Locale.ROOT).contains("bold"),
                        "Body text must not accidentally resolve to a bold font face");
            }
        }
        for (Node node : root.lookupAll(".text")) {
            if (node instanceof Text text) {
                check(text.getFontSmoothingType() == FontSmoothingType.GRAY,
                        "Text must use grayscale smoothing to avoid coloured fringes");
            }
        }
    }

    /** Checks that help genuinely stacks at the smallest supported window size. */
    private void verifyHelpColumns(Parent root, double width) {
        Parent help = (Parent) root.lookup(".help-view");
        if (help == null) {
            return;
        }
        Parent groups = (Parent) help.getChildrenUnmodifiable().get(2);
        Node first = groups.getChildrenUnmodifiable().get(0);
        Node second = groups.getChildrenUnmodifiable().get(1);
        if (width <= 780) {
            check(Math.abs(first.getLayoutX() - second.getLayoutX()) < 1,
                    "Help must stack into one column in a narrow window");
            check(second.getLayoutY() >= first.getBoundsInParent().getMaxY(),
                    "Stacked help groups must not overlap");
        } else {
            check(second.getLayoutX() >= first.getBoundsInParent().getMaxX(),
                    "Help must use two columns when the window has enough room");
        }
    }

    /** Checks that the dot belongs to the first line and counts begin under the status text. */
    private void verifyHeaderAlignment(Parent root) {
        Node dot = root.lookup(".status-dot");
        Node status = root.lookup(".status-text");
        Node counts = root.lookup(".task-stats");
        Bounds dotBounds = dot.localToScene(dot.getLayoutBounds());
        Bounds statusBounds = status.localToScene(status.getLayoutBounds());
        Bounds countBounds = counts.localToScene(counts.getLayoutBounds());
        check(Math.abs(dotBounds.getCenterY() - statusBounds.getCenterY()) < 1,
                "The status dot must be vertically centered on the status heading");
        check(Math.abs(statusBounds.getMinX() - countBounds.getMinX()) < 1,
                "Status and task counts must share a left edge");
        check(countBounds.getMinY() >= statusBounds.getMaxY() + 2,
                "Task counts must have a clear gap below the status heading");
    }

    private void send(String command) {
        commandField().setText(command);
        ((Button) stage.getScene().lookup(".send-button")).fire();
    }

    private TextField commandField() {
        return (TextField) stage.getScene().lookup(".command-field");
    }

    private String lastReply() {
        Parent messages = (Parent) stage.getScene().lookup("#messages");
        Node body = messages.getChildrenUnmodifiable().getLast().lookup(".message-body");
        return body instanceof Label label ? label.getText() : body.getAccessibleText();
    }

    private List<Task> savedTasks() throws StorageException {
        return new Storage(SaveLocation.getDefaultFile()).loadTasks();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
