package tuesday.ui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import tuesday.processor.Command;
import tuesday.processor.CommandProcessor;
import tuesday.processor.CommandResponse;
import tuesday.processor.Storage;
import tuesday.processor.UserInputParser;
import tuesday.task.TaskList;

/**
 * Controls commands and preserves the visible conversation when the window is resized.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Image tuesdayImage = new Image(getClass().getResourceAsStream("/images/Tuesday.png"));
    private Image userImage = new Image(getClass().getResourceAsStream("/images/TonyStark.png"));
    private TaskList tasks = new TaskList();
    private final CommandProcessor commandProcessor;
    private final String storageFilePath;
    private final DialogFactory dialogFactory;
    private final BiConsumer<String, Throwable> fatalErrorHandler;
    /** Opens external links using the running JavaFX application. */
    private Consumer<String> linkOpener;

    /**
     * The first visible message before a resize, or {@code null} when no message is visible.
     */
    private Node readingAnchor = null;

    /**
     * The distance in pixels from the top of {@link #readingAnchor} to the top of the viewport.
     */
    private double readingOffset = 0.0;

    /**
     * Whether a scene size change is waiting to be handled in the next layout pulse.
     */
    private boolean isResizePending = false;

    /**
     * Whether the next layout pulse should reveal the latest response.
     */
    private boolean isScrollToBottomPending = false;

    /**
     * Prevents recursive layout handling while the reading position is being restored.
     */
    private boolean isHandlingResize = false;

    /**
     * Marks scene size changes for handling and retains its identity for removal when the scene changes.
     */
    private final InvalidationListener resizeListener = observable -> isResizePending = true;

    /**
     * Handles pending layout changes and retains its identity for removal when the scene changes.
     */
    private final Runnable resizePulseListener = this::handlePendingResize;

    /**
     * Creates the main controller using the application's saved-task path.
     */
    public MainWindow() {
        this(Storage.FILE_PATH);
    }

    /**
     * Creates a controller that loads and saves using the same isolated path.
     */
    MainWindow(String storageFilePath) {
        this(storageFilePath, (text, picture, isUser) -> isUser
                ? DialogBox.getUserDialog(text, picture) : DialogBox.getTuesdayDialog(text, picture),
                UiErrors::showFatal);
    }

    /**
     * Supplies dialog creation and fatal reporting for deterministic UI failure tests.
     */
    MainWindow(String storageFilePath, DialogFactory dialogFactory, BiConsumer<String, Throwable> fatalErrorHandler) {
        this.storageFilePath = storageFilePath;
        commandProcessor = new CommandProcessor(storageFilePath);
        this.dialogFactory = dialogFactory;
        this.fatalErrorHandler = fatalErrorHandler;
    }

    /**
     * Initializes conversation sizing and resize tracking, then loads saved tasks.
     */
    @FXML
    public void initialize() {
        initializeConversationBackground();
        initializeResizeTracking();

        tasks = loadTasks(storageFilePath);
    }

    /**
     * Supplies browser access separately from command processing and storage.
     *
     * @param linkOpener the action used to open a web address.
     */
    public void setLinkOpener(Consumer<String> linkOpener) {
        this.linkOpener = linkOpener;
    }

    /**
     * Opens the fixed guide address when its hyperlink is activated.
     */
    private void openUserGuide() {
        if (linkOpener != null) {
            linkOpener.accept(Strings.USER_GUIDE_URL);
        }
    }

    /**
     * Allows an empty first run but prevents editing after other load failures.
     */
    static TaskList loadTasks(String filePath) {
        TaskList loadedTasks = new TaskList();
        try {
            Storage.loadData(filePath, loadedTasks);
        } catch (NoSuchFileException e) {
            // There is no saved data on the first run.
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to load saved tasks.", e);
        }
        return loadedTasks;
    }

    /**
     * Sets the minimum conversation height to fill the scroll viewport.
     * Allows longer conversations to grow beyond the viewport and scroll normally.
     */
    private void initializeConversationBackground() {
        dialogContainer.minHeightProperty().bind(
                Bindings.createDoubleBinding(() -> scrollPane.getViewportBounds().getHeight(),
                        scrollPane.viewportBoundsProperty()));
    }

    /**
     * Registers resize tracking on the scene containing {@link #scrollPane}.
     * Removes listeners from the previous scene when the scroll pane moves to another scene.
     */
    private void initializeResizeTracking() {
        scrollPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.widthProperty().removeListener(resizeListener);
                oldScene.heightProperty().removeListener(resizeListener);
                oldScene.removePreLayoutPulseListener(resizePulseListener);
            }

            if (newScene != null) {
                newScene.widthProperty().addListener(resizeListener);
                newScene.heightProperty().addListener(resizeListener);
                newScene.addPreLayoutPulseListener(resizePulseListener);
                isResizePending = true;
            }
        });
    }

    /**
     * Saves the first visible message and the distance scrolled into it.
     * A message reference preserves the reading position when text wrapping changes the content height.
     */
    private void captureReadingPosition() {
        readingAnchor = null;

        double scrollRange = Math.max(0,
                dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight());
        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        double scrollOffset = (valueRange <= 0)
                ? 0
                : (scrollPane.getVvalue() - scrollPane.getVmin()) / valueRange * scrollRange;

        for (Node message : dialogContainer.getChildren()) {
            if (message.getBoundsInParent().getMaxY() > scrollOffset) {
                readingAnchor = message;
                readingOffset = Math.max(0, scrollOffset - message.getBoundsInParent().getMinY());
                break;
            }
        }
    }

    /**
     * Applies pending layout changes and restores the scroll position.
     * Runs before the normal scene layout pass so width and height changes are handled together.
     * Restores the reading anchor after layout, or reveals the latest response after a submission.
     */
    private void handlePendingResize() {
        Scene scene = scrollPane.getScene();
        if (scene == null || isHandlingResize || (!isResizePending && !isScrollToBottomPending)) {
            return;
        }

        isHandlingResize = true;
        try {
            captureReadingPosition();
            isResizePending = false;
            settleLayout(scene);
            restoreScrollAfterLayout();
        } finally {
            isHandlingResize = false;
        }
    }

    /**
     * Settles wrapping and scrollbar changes before the scroll position is restored.
     */
    private static void settleLayout(Scene scene) {
        Parent root = scene.getRoot();
        root.applyCss();
        root.layout();

        // A scrollbar appearing or disappearing can require another pass to settle the text width.
        root.layout();
    }

    /**
     * Reveals a newly submitted response or restores the position of the message being read.
     */
    private void restoreScrollAfterLayout() {
        if (isScrollToBottomPending) {
            scrollPane.setVvalue(scrollPane.getVmax());
            isScrollToBottomPending = false;
        } else {
            restoreReadingPosition();
        }
    }

    /**
     * Restores the saved message and offset within the available scroll range.
     * Clamps the offset when the message becomes shorter and scrolls to the top when all content fits.
     */
    private void restoreReadingPosition() {
        double scrollRange = Math.max(0,
                dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight());
        if (scrollRange == 0 || readingAnchor == null) {
            scrollPane.setVvalue(scrollPane.getVmin());
            return;
        }

        double messageHeight = readingAnchor.getBoundsInParent().getHeight();
        double offset = Math.min(readingOffset, Math.max(0, messageHeight - 1));
        double targetOffset = readingAnchor.getBoundsInParent().getMinY() + offset;
        double fraction = Math.max(0, Math.min(1, targetOffset / scrollRange));
        scrollPane.setVvalue(scrollPane.getVmin() + fraction * (scrollPane.getVmax() - scrollPane.getVmin()));
    }

    /**
     * Requests scrolling to the latest response after the new messages have been laid out.
     */
    private void scrollToLatestMessage() {
        isScrollToBottomPending = true;
        Platform.requestNextPulse();
    }

    /**
     * Processes input, displays the conversation, and reveals the newest response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResponse response = commandProcessor.processResponse(input, tasks);
        if (!displayConversation(input, response)) {
            return;
        }
        if (UserInputParser.getCommand(input) == Command.BYE) {
            scheduleExit();
        }
    }

    /**
     * Displays both sides of a conversation only after both dialogs have been created successfully.
     *
     * @return True after displaying the conversation, or false after reporting a dialog failure.
     */
    private boolean displayConversation(String input, CommandResponse response) {
        DialogBox userDialog;
        DialogBox tuesdayDialog;
        try {
            userDialog = dialogFactory.create(input, userImage, true);
            tuesdayDialog = dialogFactory.create(response.text(), tuesdayImage, false);
            if (response.showUserGuide()) {
                tuesdayDialog.showUserGuideLink(this::openUserGuide);
            }
        } catch (IllegalStateException e) {
            handleConversationFailure(e);
            return false;
        }
        dialogContainer.getChildren().addAll(userDialog, tuesdayDialog);
        userInput.clear();
        scrollToLatestMessage();
        return true;
    }

    /**
     * Stops input after a display failure without retrying a command that may already have been saved.
     */
    private void handleConversationFailure(IllegalStateException failure) {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        fatalErrorHandler.accept(
                "Tuesday could not display the conversation. Your command may already be saved.", failure);
    }

    /**
     * Allows the farewell to remain visible for two seconds before exiting.
     */
    private static void scheduleExit() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Creates one side of a conversation without coupling error-handling tests to FXML failures.
     */
    @FunctionalInterface
    interface DialogFactory {
        /**
         * Creates a user or Tuesday dialog.
         *
         * @param text the message to display.
         * @param picture the speaker's picture.
         * @param isUser whether the message was entered by the user.
         * @return the initialized dialog.
         */
        DialogBox create(String text, Image picture, boolean isUser);
    }
}
