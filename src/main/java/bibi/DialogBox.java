package bibi;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * One turn of the conversation.
 *
 * <p>The two speakers are deliberately not shown alike. This is a person talking
 * to a tool, not two people talking to each other, and laying both sides out the
 * same way makes the app look like a chat between equals while wasting the space
 * that Bibi's longer answers actually need.
 *
 * <p>So Bibi's replies run the full width of the window behind a small icon, and
 * the user's own lines sit on the right as a short tinted chip with no picture at
 * all: the user knows who they are, and a portrait of them earns none of the room
 * it would take. A reply that reports a problem is styled apart again, so an
 * error cannot be mistaken for a confirmation at a glance.
 */
public class DialogBox extends HBox {
    private static final String DIALOG_BOX_FXML_PATH = "/view/DialogBox.fxml";

    /** How much of the window's width one of the user's chips may occupy. */
    private static final double USER_CHIP_WIDTH_FRACTION = 0.75;

    /**
     * Pixels allowed on top of a measured line, so that a fractional width is
     * never rounded down into clipping the last character or the descenders of
     * the last line.
     */
    private static final double MEASUREMENT_ALLOWANCE = 2.0;

    @FXML
    private TextArea dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box showing one line of the conversation.
     *
     * @param text what was said
     */
    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML_PATH));
            // This object is both the root and the controller, which is what lets
            // a dialog box be created in code and still be laid out by FXML.
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load " + DIALOG_BOX_FXML_PATH, exception);
        }

        dialog.setText(text);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        dialog.setMaxHeight(Region.USE_PREF_SIZE);
        dialog.widthProperty().addListener((observable, oldWidth, newWidth) -> resizeDialog());
        dialog.fontProperty().addListener((observable, oldFont, newFont) -> resizeDialog());
        dialog.insetsProperty().addListener((observable, oldInsets, newInsets) -> resizeDialog());
        dialog.addEventFilter(ScrollEvent.SCROLL, this::forwardScroll);
    }

    /**
     * Returns the box for something the user typed.
     *
     * <p>Shown as a chip on the right with no picture. It is capped at a fraction
     * of the window rather than a fixed width, so it stays a chip as the window
     * grows instead of stretching into a banner.
     *
     * @param text what the user typed
     * @return the finished box
     */
    public static DialogBox forUser(String text) {
        DialogBox box = new DialogBox(text);

        box.displayPicture.setVisible(false);
        box.displayPicture.setManaged(false);
        box.setAlignment(Pos.TOP_RIGHT);
        box.getStyleClass().add("user-row");
        box.dialog.getStyleClass().add("user-chip");
        box.dialog.maxWidthProperty().bind(box.widthProperty()
                .multiply(USER_CHIP_WIDTH_FRACTION));
        return box;
    }

    /**
     * Returns the box for one of Bibi's replies.
     *
     * @param reply what Bibi said, and whether it reports a problem
     * @param icon the small picture shown beside the reply
     * @return the finished box
     */
    public static DialogBox forBibi(Reply reply, Image icon) {
        DialogBox box = new DialogBox(reply.text());

        box.displayPicture.setImage(icon);
        box.setAlignment(Pos.TOP_LEFT);
        box.getStyleClass().add("bibi-row");
        box.dialog.getStyleClass().add(reply.isError() ? "error-text" : "bibi-text");

        // Letting the text take the leftover width is what makes a long reply
        // wrap into the window rather than being squeezed into a column.
        box.dialog.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box.dialog, Priority.ALWAYS);
        return box;
    }

    /**
     * Sizes selectable text to its wrapped content instead of adding an inner scrollbar.
     *
     * <p>The measurement uses the same font and available width as the control.
     * Repeating it when those change also accommodates resizing and larger fonts.
     */
    private void resizeDialog() {
        double horizontalInsets = dialog.getInsets().getLeft() + dialog.getInsets().getRight();
        double verticalInsets = dialog.getInsets().getTop() + dialog.getInsets().getBottom();
        Text measurement = new Text(dialog.getText());
        measurement.setFont(dialog.getFont());
        // TextArea's skin uses these bounds, which include the font's full line height.
        measurement.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        dialog.setPrefWidth(Math.ceil(measurement.getLayoutBounds().getWidth())
                + horizontalInsets + MEASUREMENT_ALLOWANCE);
        measurement.setWrappingWidth(Math.max(1,
                dialog.getWidth() - horizontalInsets - MEASUREMENT_ALLOWANCE));
        double preferredHeight = Math.ceil(measurement.getLayoutBounds().getHeight())
                + verticalInsets + MEASUREMENT_ALLOWANCE;
        if (dialog.getPrefHeight() != preferredHeight) {
            dialog.setPrefHeight(preferredHeight);
            // Width can change during HBox layout. Its parent must then recalculate
            // this row's height after that pass, including when a wider reply shrinks.
            Platform.runLater(this::requestLayout);
        }
    }

    /**
     * Scrolls the transcript when the pointer is over selectable message text.
     *
     * <p>Each message fits its contents, so its internal text area must not trap
     * wheel events intended for the surrounding conversation.
     *
     * @param event the scrolling gesture over a message
     */
    private void forwardScroll(ScrollEvent event) {
        Node container = getParent();
        if (container != null) {
            // Bubble through the outer viewport, which owns JavaFX's wheel handler.
            Event.fireEvent(container, event.copyFor(container, container));
            event.consume();
        }
    }
}
