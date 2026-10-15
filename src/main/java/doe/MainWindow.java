package doe;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window and forwards each submitted line to Doe.
 */
public class MainWindow extends AnchorPane {
    private static final Rectangle2D FULL_BANNER_VIEWPORT =
            new Rectangle2D(0, 0, 2172, 724);
    private static final double FULL_BANNER_HEIGHT = 140;
    private static final double COMPACT_WIDTH = 560;
    private static final PseudoClass COMPACT = PseudoClass.getPseudoClass("compact");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox terminal;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private StackPane bannerPanel;
    @FXML
    private ImageView bannerImage;
    @FXML
    private HBox commandBar;

    private Doe chatbot;
    /** Coalesces width and height changes into one scroll restoration after layout. */
    private boolean isResizePending;
    private final Image userImage = loadImage("/images/UserPixel.png");
    private final Image doeImage = loadImage("/images/DoePixel.png");

    /** Adapts the layout to the window without scrolling when existing messages reflow. */
    @FXML
    public void initialize() {
        bannerImage.setImage(loadImage("/images/TerminalBannerOption1.png"));
        bannerImage.setViewport(FULL_BANNER_VIEWPORT);
        bannerImage.setFitHeight(FULL_BANNER_HEIGHT);
        bannerImage.fitWidthProperty().bind(bannerPanel.widthProperty());
        bannerPanel.setMinHeight(FULL_BANNER_HEIGHT);
        bannerPanel.setPrefHeight(FULL_BANNER_HEIGHT);
        bannerPanel.setMaxHeight(FULL_BANNER_HEIGHT);

        terminal.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            preserveScrollOnResize();
            updateResponsiveLayout();
        });
        terminal.heightProperty().addListener((observable, oldHeight, newHeight) -> preserveScrollOnResize());
        updateResponsiveLayout();
    }

    /** Preserves the relative reading position while resizing reflows the conversation. */
    private void preserveScrollOnResize() {
        if (isResizePending) {
            return;
        }
        isResizePending = true;
        double scrollPosition = scrollPane.getVvalue();
        Platform.runLater(() -> {
            terminal.getParent().applyCss();
            terminal.getParent().layout();
            scrollPane.setVvalue(scrollPosition);
            isResizePending = false;
        });
    }

    /**
     * Supplies the application instance that owns Doe's task and command logic.
     *
     * @param doeApp Existing Doe application instance.
     */
    public void setDoe(Doe doeApp) {
        chatbot = doeApp;
        if (!chatbot.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getWarningDialog(chatbot.getStartupWarning(), doeImage));
        }
        dialogContainer.getChildren().add(DialogBox.getDoeDialog(chatbot.getWelcomeMessage(), doeImage));
    }

    /** Sends the text field contents to Doe and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (chatbot == null) {
            return;
        }

        String response = chatbot.getResponse(input);
        DialogBox responseDialog = chatbot.wasLastResponseWarning()
                ? DialogBox.getWarningDialog(response, doeImage)
                : DialogBox.getDoeDialog(response, doeImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), responseDialog);
        userInput.clear();
        // Only a submitted command should bring the latest reply into view.
        Platform.runLater(this::layoutConversationAndShowLatest);
    }

    /** Lays out newly added dialogs before scrolling the conversation to its latest reply. */
    private void layoutConversationAndShowLatest() {
        terminal.getParent().applyCss();
        terminal.getParent().layout();
        scrollPane.setVvalue(1.0);
    }

    /** Leaves more room for text and input controls in narrow windows. */
    private void updateResponsiveLayout() {
        boolean isCompact = terminal.getWidth() < COMPACT_WIDTH;
        terminal.getParent().pseudoClassStateChanged(COMPACT, isCompact);
        double margin = isCompact ? 10 : 22;
        VBox.setMargin(bannerPanel, new Insets(isCompact ? 8 : 18, margin, isCompact ? 8 : 16, margin));
        VBox.setMargin(scrollPane, new Insets(0, margin, 0, margin));
        VBox.setMargin(commandBar, new Insets(isCompact ? 8 : 16, margin, isCompact ? 10 : 20, margin));
        commandBar.setSpacing(isCompact ? 8 : 14);
        sendButton.setPrefWidth(isCompact ? 76 : 112);
    }

    private static Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}
