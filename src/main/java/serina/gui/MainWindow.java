package serina.gui;

import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import serina.CommandResult;
import serina.ResponseMessage;
import serina.ResponseType;
import serina.Serina;

/**
 * Controls Serina's main chat window.
 */
public class MainWindow {
    private static final double BOTTOM_TOLERANCE = 0.005;
    private static final double COMMAND_GROUP_MARGIN = 8;
    private static final double COMPACT_HEADER_WIDTH = 480;
    private static final double PORTRAIT_SIZE = 40;
    private static final double WIDE_HEADER_WIDTH = 720;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Button helpButton;
    @FXML
    private StackPane appHeader;
    @FXML
    private ImageView headerBackground;
    @FXML
    private ImageView serinaPortrait;
    @FXML
    private ImageView cutterPortrait;
    @FXML
    private Label headerSubtitle;
    @FXML
    private HBox captainIdentity;

    private Serina serina;
    private long scheduledScrollVersion;
    private boolean isCommandScrollPending;

    /**
     * Creates the controller used by the main-window FXML document.
     */
    public MainWindow() {
    }

    /**
     * Initializes responsive scrolling and input focus after FXML fields are injected.
     */
    @FXML
    private void initialize() {
        configureHeaderImages();
        appHeader.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            updateHeaderVisibility(newWidth.doubleValue());
        });
        updateHeaderVisibility(appHeader.getWidth());

        scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            if (isCommandScrollPending || oldBounds.equals(newBounds)) {
                return;
            }

            boolean wasAtBottom = isScrolledToBottom();
            ScrollAnchor anchor = captureScrollAnchor();
            scheduleScroll(() -> {
                if (wasAtBottom) {
                    scrollPane.setVvalue(1.0);
                } else {
                    restoreScrollAnchor(anchor);
                }
            });
        });
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Loads, sizes, and crops the decorative header artwork.
     */
    private void configureHeaderImages() {
        headerBackground.setImage(loadImage("/images/chat-background.png"));
        headerBackground.fitWidthProperty().bind(appHeader.widthProperty());
        headerBackground.fitHeightProperty().bind(appHeader.heightProperty());
        configurePortrait(serinaPortrait, "/images/serina-avatar.png", PORTRAIT_SIZE);
        configurePortrait(cutterPortrait, "/images/user-avatar.png", cutterPortrait.getFitWidth());
    }

    /**
     * Configures a circular, centered crop for one header portrait.
     */
    private static void configurePortrait(ImageView imageView, String resourcePath, double portraitSize) {
        Image image = loadImage(resourcePath);
        if (image == null) {
            imageView.setManaged(false);
            imageView.setVisible(false);
            return;
        }
        imageView.setImage(image);
        imageView.setViewport(createSquareViewport(image));
        imageView.setClip(new Circle(portraitSize / 2, portraitSize / 2, portraitSize / 2));
    }

    /**
     * Returns a centered square crop that prevents portrait distortion.
     */
    private static Rectangle2D createSquareViewport(Image image) {
        double sideLength = Math.min(image.getWidth(), image.getHeight());
        double minimumX = (image.getWidth() - sideLength) / 2;
        double minimumY = (image.getHeight() - sideLength) / 2;
        return new Rectangle2D(minimumX, minimumY, sideLength, sideLength);
    }

    /**
     * Shows secondary identity details only when enough header width is available.
     */
    private void updateHeaderVisibility(double headerWidth) {
        setVisibleAndManaged(headerSubtitle, headerWidth >= COMPACT_HEADER_WIDTH);
        setVisibleAndManaged(captainIdentity, headerWidth >= WIDE_HEADER_WIDTH);
    }

    /**
     * Updates whether a node is drawn and participates in layout.
     */
    private static void setVisibleAndManaged(Node node, boolean isVisible) {
        node.setVisible(isVisible);
        node.setManaged(isVisible);
    }

    /** Returns an optional image resource, or {@code null} when decorative artwork is unavailable. */
    private static Image loadImage(String resourcePath) {
        java.net.URL resource = MainWindow.class.getResource(resourcePath);
        return resource == null ? null : new Image(resource.toExternalForm());
    }

    /**
     * Injects Serina's command-processing backend and displays startup messages.
     *
     * @param serina Backend used to process commands.
     */
    public void setSerina(Serina serina) {
        this.serina = Objects.requireNonNull(serina);
        appendSerinaDialog(ResponseMessage.normal(serina.getGreeting()));
        for (ResponseMessage response : serina.getStartupResponses()) {
            appendSerinaDialog(response);
        }
        scheduleScroll(() -> scrollPane.setVvalue(0.0));
    }

    /**
     * Sends the current input to Serina.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.clear();
            userInput.requestFocus();
            return;
        }

        CommandResult result = submitCommand(input);
        boolean hasError = result.getMessages().stream()
                .anyMatch(message -> message.getType() == ResponseType.ERROR);
        if (hasError) {
            userInput.setText(input);
            userInput.positionCaret(input.length());
        } else {
            userInput.clear();
        }
    }

    /**
     * Shows Serina's help response while preserving unfinished input.
     */
    @FXML
    private void handleHelp() {
        String draft = userInput.getText();
        int caretPosition = userInput.getCaretPosition();

        submitCommand("help");

        userInput.setText(draft);
        userInput.positionCaret(Math.min(caretPosition, draft.length()));
        if (!userInput.isDisabled()) {
            userInput.requestFocus();
        }
    }

    /**
     * Processes one command and displays its command-and-response group.
     */
    private CommandResult submitCommand(String input) {
        appendUserDialog(input);
        CommandResult result = serina.executeCommand(input);

        List<DialogBox> responseDialogs = result.getMessages().stream()
                .map(this::appendSerinaDialog)
                .toList();
        if (!responseDialogs.isEmpty()) {
            scheduleResponseScroll(responseDialogs.get(0), responseDialogs.get(responseDialogs.size() - 1));
        }

        if (result.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            helpButton.setDisable(true);
            // Closing the last window lets JavaFX shut down and the Gradle run task finish.
            userInput.getScene().getWindow().hide();
        } else {
            userInput.requestFocus();
        }
        return result;
    }

    /**
     * Appends a command entered by the user.
     */
    private void appendUserDialog(String message) {
        DialogBox dialogBox = DialogBox.createUserDialog(message);
        if (!dialogContainer.getChildren().isEmpty()) {
            VBox.setMargin(dialogBox, new Insets(COMMAND_GROUP_MARGIN, 0, 0, 0));
        }
        dialogContainer.getChildren().add(dialogBox);
    }

    /**
     * Appends a typed response generated by Serina.
     *
     * @return Dialog box that was appended.
     */
    private DialogBox appendSerinaDialog(ResponseMessage message) {
        DialogBox dialogBox = DialogBox.createSerinaDialog(message);
        dialogContainer.getChildren().add(dialogBox);
        return dialogBox;
    }

    /**
     * Schedules scrolling that reveals a complete response group when possible.
     */
    private void scheduleResponseScroll(DialogBox firstResponse, DialogBox lastResponse) {
        isCommandScrollPending = true;
        scheduleScroll(() -> {
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            Bounds firstBounds = firstResponse.getBoundsInParent();
            Bounds lastBounds = lastResponse.getBoundsInParent();
            double groupTop = firstBounds.getMinY();
            double groupBottom = lastBounds.getMaxY();
            double currentTop = getVisibleTop();

            if (groupBottom - groupTop > viewportHeight) {
                setVisibleTop(groupTop);
            } else if (groupTop < currentTop) {
                setVisibleTop(groupTop);
            } else if (groupBottom > currentTop + viewportHeight) {
                setVisibleTop(groupBottom - viewportHeight);
            }
            isCommandScrollPending = false;
        });
    }

    /**
     * Runs one current scroll request after JavaFX has laid out newly added content.
     */
    private void scheduleScroll(Runnable scrollAction) {
        long requestVersion = ++scheduledScrollVersion;
        Platform.runLater(() -> {
            if (requestVersion != scheduledScrollVersion) {
                return;
            }
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollAction.run();
        });
    }

    /**
     * Captures the top visible message and offset before resize-driven text reflow.
     */
    private ScrollAnchor captureScrollAnchor() {
        double visibleTop = getVisibleTop();
        for (Node child : dialogContainer.getChildren()) {
            Bounds childBounds = child.getBoundsInParent();
            if (childBounds.getMaxY() >= visibleTop) {
                return new ScrollAnchor(child, visibleTop - childBounds.getMinY());
            }
        }
        return null;
    }

    /**
     * Restores a previously captured visible message and offset after resize.
     */
    private void restoreScrollAnchor(ScrollAnchor anchor) {
        if (anchor == null || !dialogContainer.getChildren().contains(anchor.node)) {
            return;
        }
        setVisibleTop(anchor.node.getBoundsInParent().getMinY() + anchor.offset);
    }

    /**
     * Returns whether the transcript is currently at its bottom edge.
     */
    private boolean isScrolledToBottom() {
        return scrollPane.getVvalue() >= scrollPane.getVmax() - BOTTOM_TOLERANCE;
    }

    /**
     * Returns the vertical content coordinate at the top of the viewport.
     */
    private double getVisibleTop() {
        double scrollableHeight = getScrollableHeight();
        if (scrollableHeight <= 0) {
            return 0;
        }
        double scrollFraction = (scrollPane.getVvalue() - scrollPane.getVmin())
                / (scrollPane.getVmax() - scrollPane.getVmin());
        return scrollFraction * scrollableHeight;
    }

    /**
     * Scrolls to the supplied vertical content coordinate.
     */
    private void setVisibleTop(double visibleTop) {
        double scrollableHeight = getScrollableHeight();
        if (scrollableHeight <= 0) {
            scrollPane.setVvalue(scrollPane.getVmin());
            return;
        }

        double clampedTop = Math.max(0, Math.min(visibleTop, scrollableHeight));
        double scrollFraction = clampedTop / scrollableHeight;
        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        scrollPane.setVvalue(scrollPane.getVmin() + scrollFraction * valueRange);
    }

    /**
     * Returns the distance through which the transcript can be scrolled.
     */
    private double getScrollableHeight() {
        double contentHeight = dialogContainer.getBoundsInLocal().getHeight();
        return Math.max(0, contentHeight - scrollPane.getViewportBounds().getHeight());
    }

    /**
     * Identifies the content node kept at the viewport top while the window resizes.
     */
    private static final class ScrollAnchor {
        private final Node node;
        private final double offset;

        private ScrollAnchor(Node node, double offset) {
            this.node = node;
            this.offset = offset;
        }
    }
}
