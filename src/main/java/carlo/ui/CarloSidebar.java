package carlo.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import carlo.persona.CarloMessage;
import carlo.persona.CarloMood;
import carlo.task.Task;
import carlo.task.TaskGroup;
import carlo.task.TaskSchedule;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

/**
 * The left-hand panel of the main window: Carlo's speech bubble, his photo
 * in a ring coloured by his current {@link CarloMood}, a couple of quick
 * stats, and the command bar for adding tasks.
 */
public class CarloSidebar extends VBox {
    private static final String IMAGE_PATH = "/images/carlo.png";
    private static final double AVATAR_RADIUS = 64;
    /** Gap between Carlo's photo and the mood ring drawn around it. */
    private static final double RING_GAP = 5;

    private final Label speechLabel = new Label();
    private final Label comingUpValue = new Label("0");
    private final Label doneValue = new Label("0");
    private final TextField commandField = new TextField();
    private final Button undoButton = new Button("Undo");

    /**
     * Creates the sidebar.
     *
     * @param onCommand called with the text typed into the command bar when
     *        the user presses Enter; returns {@code true} if the command
     *        worked, in which case the command bar is cleared
     * @param onOpenForm called when the user asks for the add-task form
     * @param onUndo called when the user presses the Undo button in the speech bubble
     */
    public CarloSidebar(Predicate<String> onCommand, Runnable onOpenForm, Runnable onUndo) {
        getStyleClass().add("sidebar");
        setSpacing(14);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(createSpeechBubble(onUndo), createAvatar(), createStats(), spacer,
                createCommandBar(onCommand, onOpenForm));
        say(new CarloMessage("", CarloMood.CHEERFUL));
    }

    /**
     * Shows a message in Carlo's speech bubble and updates his mood colour.
     *
     * @param message what Carlo says, and the mood he says it in
     */
    public void say(CarloMessage message) {
        say(message, false);
    }

    /**
     * Shows a message in Carlo's speech bubble, updates his mood colour, and
     * shows or hides the Undo button underneath the message.
     *
     * @param message what Carlo says, and the mood he says it in
     * @param isUndoable whether to offer an Undo button for the change the message is about
     */
    public void say(CarloMessage message, boolean isUndoable) {
        undoButton.setVisible(isUndoable);
        undoButton.setManaged(isUndoable);
        speechLabel.setText(message.text());
        for (CarloMood mood : CarloMood.values()) {
            getStyleClass().remove(mood.getStyleClass());
        }
        // The mood class goes on the whole sidebar, so carlo.css can recolour
        // both the speech bubble and the ring around the photo from one place.
        getStyleClass().add(message.mood().getStyleClass());
    }

    /**
     * Updates the quick stats shown under Carlo's photo.
     *
     * @param tasks all of the user's tasks
     * @param now the current date and time
     */
    public void updateStats(List<Task> tasks, LocalDateTime now) {
        long comingUp = tasks.stream()
                .map(task -> TaskSchedule.groupOf(task, now))
                .filter(group -> group == TaskGroup.SOON || group == TaskGroup.THIS_WEEK)
                .count();
        long done = tasks.stream().filter(Task::isDone).count();
        comingUpValue.setText(String.valueOf(comingUp));
        doneValue.setText(String.valueOf(done));
    }

    /**
     * Moves the keyboard cursor into the command bar.
     */
    public void focusCommandBar() {
        commandField.requestFocus();
    }

    private VBox createSpeechBubble(Runnable onUndo) {
        speechLabel.setWrapText(true);
        speechLabel.getStyleClass().add("speech-text");
        speechLabel.setMaxWidth(Double.MAX_VALUE);

        undoButton.getStyleClass().add("undo-button");
        undoButton.setTooltip(new Tooltip("Undo the last change (Ctrl+Z)"));
        undoButton.setOnAction(event -> onUndo.run());

        VBox bubble = new VBox(4, speechLabel, undoButton);
        bubble.setAlignment(Pos.CENTER_RIGHT);
        bubble.getStyleClass().add("speech-bubble");

        // A small downward-pointing triangle that makes the box look like a speech bubble.
        Polygon tail = new Polygon(0, 0, 18, 0, 9, 10);
        tail.getStyleClass().add("speech-tail");
        tail.setTranslateY(-2);

        VBox speech = new VBox(bubble, tail);
        speech.setAlignment(Pos.TOP_CENTER);
        return speech;
    }

    /**
     * Creates Carlo's photo, cropped to a circle, with a coloured ring around it.
     */
    private StackPane createAvatar() {
        Image image = new Image(getClass().getResourceAsStream(IMAGE_PATH));
        ImageView photo = new ImageView(image);

        // Crop the photo to a square first, so it is not squashed when shown
        // in a circle. Portrait photos keep their top part, where the face is.
        double side = Math.min(image.getWidth(), image.getHeight());
        double left = (image.getWidth() - side) / 2;
        photo.setViewport(new Rectangle2D(left, 0, side, side));
        photo.setFitWidth(AVATAR_RADIUS * 2);
        photo.setFitHeight(AVATAR_RADIUS * 2);
        photo.setSmooth(true);
        photo.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));

        Circle ring = new Circle(AVATAR_RADIUS + RING_GAP);
        ring.getStyleClass().add("mood-ring");

        StackPane avatar = new StackPane(ring, photo);
        avatar.getStyleClass().add("avatar");
        return avatar;
    }

    private HBox createStats() {
        HBox stats = new HBox(8, createStatTile("Coming up", comingUpValue), createStatTile("Done", doneValue));
        stats.setAlignment(Pos.CENTER);
        return stats;
    }

    private VBox createStatTile(String title, Label value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        value.getStyleClass().add("stat-value");

        VBox tile = new VBox(2, titleLabel, value);
        tile.getStyleClass().add("stat-tile");
        HBox.setHgrow(tile, Priority.ALWAYS);
        tile.setMaxWidth(Double.MAX_VALUE);
        return tile;
    }

    private VBox createCommandBar(Predicate<String> onCommand, Runnable onOpenForm) {
        commandField.setPromptText("deadline essay /by 2026-10-15");
        commandField.setOnAction(event -> {
            if (onCommand.test(commandField.getText())) {
                commandField.clear();
            }
        });
        HBox.setHgrow(commandField, Priority.ALWAYS);

        Button formButton = new Button("+");
        formButton.getStyleClass().add("form-button");
        formButton.setTooltip(new Tooltip("Add a task using a form (Ctrl+N)"));
        formButton.setOnAction(event -> onOpenForm.run());

        Label hint = new Label("Type help for examples, or press + for a form");
        hint.getStyleClass().add("hint");
        hint.setWrapText(true);

        return new VBox(6, new HBox(6, commandField, formButton), hint);
    }
}
