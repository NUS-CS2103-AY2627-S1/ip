package james.gui;

import james.James;
import james.command.CommandResponse;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Controls the main JavaFX conversation window.
 */
public class MainWindow extends AnchorPane {
    private static final String GREETING_MESSAGE =
            "James the ぱんどろぼう\nShh! I'm James, your bread thief.\n" +
            "I'll guard your tasks. The bread? No promises!";


    private static final double COMPACT_WINDOW_HEIGHT = 360;
    private static final PseudoClass COMPACT_HEADER = PseudoClass.getPseudoClass("compact");

    @FXML private AnchorPane windowRoot;
    @FXML private HBox bakeryHeader;
    @FXML private Label headerSubtitle;
    @FXML private ImageView headerArtwork;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private James james;

    /**
     * Initializes automatic scrolling, responsive dialog widths, and the compact header.
     */
    @FXML
    public void initialize() {
        headerSubtitle.managedProperty().bind(headerSubtitle.visibleProperty());
        windowRoot.heightProperty().addListener((observable, oldHeight, newHeight) ->
                updateHeader(newHeight.doubleValue()));
        updateHeader(windowRoot.getHeight());
        // Keep the scroll position in its valid range and free for manual scrolling.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
        dialogContainer.setFillWidth(true);
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double dialogWidth = Math.max(0, newWidth.doubleValue() - 10);
            dialogContainer.getChildren().forEach(child -> {
                if (child instanceof Region region) {
                    region.setPrefWidth(dialogWidth);
                }
            });
        });
    }

    /**
     * Gives short windows more conversation space and restores the full header when enlarged.
     *
     * @param height Current content height in pixels.
     */
    private void updateHeader(double height) {
        boolean isCompact = height < COMPACT_WINDOW_HEIGHT;
        headerSubtitle.setVisible(!isCompact);
        headerArtwork.setFitWidth(isCompact ? 28 : 48);
        headerArtwork.setFitHeight(isCompact ? 28 : 48);
        bakeryHeader.pseudoClassStateChanged(COMPACT_HEADER, isCompact);
    }

    /**
     * Sets the James instance and displays the greeting and any task-loading warnings.
     *
     * @param james Application backend used to process commands and retrieve loading warnings.
     */
    public void setJames(James james) {
        this.james = james;
        dialogContainer.getChildren().add(DialogBox.createJames(new CommandResponse(
                GREETING_MESSAGE,
                CommandResponse.Type.NORMAL, false)));
        if (!james.getLoadWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.createJames(new CommandResponse(
                    james.getLoadWarning(), CommandResponse.Type.ERROR, false)));
        }
    }

    /**
     * Processes the current input and appends both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim().stripTrailing();
        if (input.isEmpty()) {
            return;
        }
        CommandResponse response = james.getResponse(input);
        DialogBox userDialog = DialogBox.createUser(input);
        DialogBox jamesDialog = DialogBox.createJames(response);
        setDialogWidth(userDialog);
        setDialogWidth(jamesDialog);
        dialogContainer.getChildren().addAll(userDialog, jamesDialog);
        userInput.clear();
        if (response.isExit()) {
            Window window = userInput.getScene().getWindow();
            Platform.runLater(window::hide);
        }
    }

    /**
     * Fits a dialog to the conversation width while reserving its outer margin.
     *
     * @param dialog Dialog whose preferred width is updated.
     */
    private void setDialogWidth(Region dialog) {
        dialog.setPrefWidth(Math.max(0, dialogContainer.getWidth() - 10));
    }
}
