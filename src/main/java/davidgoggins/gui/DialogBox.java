package davidgoggins.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import davidgoggins.ui.Ui;

/**
 * One message in the conversation, styled by who sent it.
 *
 * <p>The two sides are deliberately different: the user's command is a compact chip
 * on the right with no picture, while the chatbot's reply is a wide card with a small
 * avatar, since replies carry most of the text. Errors get a red card and a shake.
 */
public class DialogBox extends HBox {

    /** The tag shown above a warning, which is less urgent than an error. */
    private static final String WARNING_LABEL = "HEADS UP";

    /** The widest a user's chip may grow, as a fraction of the conversation's width. */
    private static final double USER_BUBBLE_WIDTH_RATIO = 0.8;

    /**
     * Matches a line that shows a task, e.g. {@code 1.[T][X] read book} in a list or
     * {@code [D][ ] return book (by: 2026-09-10)} in a confirmation.
     */
    private static final Pattern TASK_LINE = Pattern.compile("^(\\d+\\.)?\\[[TDE]\\]\\[[ X]\\].*");

    /** How far, in pixels, an error card moves to each side while it shakes. */
    private static final double SHAKE_DISTANCE = 6.0;

    /** How long each movement of the shake takes. */
    private static final Duration SHAKE_STEP = Duration.millis(45);

    /** The number of movements in one shake; even, so the card ends where it started. */
    private static final int SHAKE_STEP_COUNT = 6;

    @FXML
    private StackPane avatar;

    @FXML
    private ImageView displayPicture;

    @FXML
    private VBox bubble;

    /**
     * Creates an empty dialog box from {@code DialogBox.fxml}.
     *
     * <p>Private because the static factory methods below say which kind of message
     * the box holds, and each fills it in differently.
     */
    private DialogBox() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // Only thrown if the FXML is missing or malformed, which is a packaging
            // fault rather than anything the user can act on.
            e.printStackTrace();
        }
    }

    /**
     * Returns a compact chip for something the user typed, aligned to the right.
     *
     * <p>The user always knows who they are, so the chip has no picture, and it is only
     * as wide as the command needs so the chatbot's replies keep most of the space.
     *
     * @param text the words the user typed
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox();
        box.getChildren().remove(box.avatar);
        box.setAlignment(Pos.TOP_RIGHT);
        box.bubble.getStyleClass().add("user-bubble");
        box.bubble.getChildren().add(createLabel(text, "message-text"));
        box.bubble.maxWidthProperty().bind(box.widthProperty().multiply(USER_BUBBLE_WIDTH_RATIO));
        return box;
    }

    /**
     * Returns a wide card for the chatbot's reply, aligned to the left.
     *
     * @param text the chatbot's reply
     * @param image the chatbot's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getBotDialog(String text, Image image) {
        DialogBox box = new DialogBox();
        box.displayPicture.setImage(image);
        clipToCircle(box.displayPicture);
        box.setAlignment(Pos.TOP_LEFT);
        box.bubble.getStyleClass().add("reply-bubble");
        // The card takes every spare pixel of width, so long replies wrap less.
        HBox.setHgrow(box.bubble, Priority.ALWAYS);
        box.bubble.setMaxWidth(Double.MAX_VALUE);
        box.bubble.getChildren().addAll(createReplyLabels(text));
        return box;
    }

    /**
     * Returns a red, shaking card for a reply that reports an error.
     *
     * <p>An error is the one reply the user must not skim past, so it gets a tag, a
     * different color and a brief shake to draw the eye.
     *
     * @param text the explanation of what went wrong, without any error prefix
     * @param image the chatbot's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox box = getBotDialog(text, image);
        box.addTag(Ui.ERROR_LABEL, "error-bubble", "error-tag");
        box.shake();
        return box;
    }

    /**
     * Returns a highlighted card for a problem the chatbot worked around.
     *
     * <p>Used for trouble with the save file, which the user did not cause but should
     * know about. It is marked out like an error, in yellow and without the shake.
     *
     * @param text the warning, one or more lines
     * @param image the chatbot's picture
     * @return the dialog box to add to the conversation
     */
    public static DialogBox getWarningDialog(String text, Image image) {
        DialogBox box = getBotDialog(text, image);
        box.addTag(WARNING_LABEL, "warning-bubble", "warning-tag");
        return box;
    }

    /**
     * Restyles the bubble and puts a tag above its text.
     *
     * @param tagText the words on the tag
     * @param bubbleStyleClass the style class that recolors the bubble
     * @param tagStyleClass the style class of the tag
     */
    private void addTag(String tagText, String bubbleStyleClass, String tagStyleClass) {
        bubble.getStyleClass().add(bubbleStyleClass);
        bubble.getChildren().add(0, createLabel(tagText, tagStyleClass));
    }

    /**
     * Crops the picture to a circle, so it sits inside the round avatar ring.
     *
     * <p>Done in code because CSS can round a region's background but cannot clip
     * an image.
     *
     * @param picture the image view to crop
     */
    private static void clipToCircle(ImageView picture) {
        double radius = picture.getFitWidth() / 2;
        picture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Returns the reply split into labels, with runs of task lines in their own label.
     *
     * <p>Task lines are shown in a monospace font so the {@code [T][X]} boxes line up,
     * while ordinary sentences keep the easier-to-read proportional font.
     *
     * @param text the reply, one or more lines
     * @return the labels to show, in order
     */
    private static List<Label> createReplyLabels(String text) {
        List<Label> labels = new ArrayList<>();
        List<String> run = new ArrayList<>();
        boolean isTaskRun = false;

        for (String line : text.split("\\R")) {
            boolean isTaskLine = TASK_LINE.matcher(line).matches();
            if (isTaskLine != isTaskRun && !run.isEmpty()) {
                labels.add(createLabel(String.join("\n", run), styleFor(isTaskRun)));
                run.clear();
            }
            run.add(line);
            isTaskRun = isTaskLine;
        }
        labels.add(createLabel(String.join("\n", run), styleFor(isTaskRun)));
        return labels;
    }

    /** Returns the style class for a run of task lines or of ordinary text. */
    private static String styleFor(boolean isTaskRun) {
        return isTaskRun ? "task-lines" : "message-text";
    }

    /**
     * Returns a wrapping label with the given text and style class.
     *
     * <p>The minimum height is tied to the preferred height, because a wrapped label
     * is otherwise squeezed to one line and cut off with an ellipsis.
     */
    private static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().add(styleClass);
        return label;
    }

    /** Shakes the box side to side a few times and leaves it where it started. */
    private void shake() {
        TranslateTransition shake = new TranslateTransition(SHAKE_STEP, bubble);
        shake.setFromX(-SHAKE_DISTANCE);
        shake.setToX(SHAKE_DISTANCE);
        shake.setCycleCount(SHAKE_STEP_COUNT);
        shake.setAutoReverse(true);
        shake.setOnFinished(event -> bubble.setTranslateX(0));
        shake.play();
    }
}
