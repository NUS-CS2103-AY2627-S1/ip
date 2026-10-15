package wwaffle.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import wwaffle.task.Task;

/**
 * Displays one user or WWaffle message with a compact speaker avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private StackPane avatar;
    @FXML
    private ImageView portrait;
    @FXML
    private Label timestamp;
    @FXML
    private Label speaker;
    @FXML
    private TextFlow dialog;
    @FXML
    private VBox bubble;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }
        setMessage(text);
        timestamp.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        // Leave space for the avatar and padding while allowing long replies to wrap.
        bubble.maxWidthProperty().bind(Bindings.max(0, widthProperty().subtract(74)));
    }

    /**
     * Creates a right-aligned message written by the user.
     *
     * @param text User's command.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.speaker.setText("You · Coffee");
        dialogBox.portrait.setImage(new Image(DialogBox.class.getResourceAsStream("/images/coffee-circle.png")));
        dialogBox.avatar.getStyleClass().add("user-avatar");
        dialogBox.bubble.getStyleClass().add("user-bubble");
        dialogBox.timestamp.setVisible(false);
        dialogBox.timestamp.setManaged(false);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a left-aligned message written by WWaffle.
     *
     * @param text WWaffle's response.
     * @return WWaffle dialog box.
     */
    public static DialogBox getWWaffleDialog(String text) {
        if (text.equals("Anytime. 🧁")) {
            return getThanksDialog(text);
        }
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.speaker.setText("WWaffle");
        if (text.equals("Hey there. ☕")
                || text.startsWith("Task added. 🧁\n") || text.startsWith("Task marked. 🤩\n")) {
            Font.loadFont(DialogBox.class.getResourceAsStream("/fonts/Inter.ttf"), 15);
            dialogBox.bubble.getStyleClass().add("greeting-bubble");
        }
        dialogBox.portrait.setImage(new Image(DialogBox.class.getResourceAsStream("/images/waffle-circle.png")));
        dialogBox.avatar.getStyleClass().add("wwaffle-avatar");
        dialogBox.bubble.getStyleClass().add("wwaffle-bubble");
        if (text.startsWith("Deadline added. 🧁\n")) {
            dialogBox.bubble.getStyleClass().add("goodbye-bubble");
            dialogBox.bubble.getChildren().add(1, new MusicCard("/images/olivia-brutal.png",
                    "OLIVIA RODRIGO · BRUTAL", "GOD, IT'S BRUTAL OUT HERE."));
        }
        if (text.startsWith("Event added. 🧁\n")) {
            dialogBox.bubble.getStyleClass().add("goodbye-bubble");
            dialogBox.bubble.getChildren().add(1, new MusicCard("/images/sabrina-event.png",
                    "SABRINA CARPENTER · NEVER GETTING LAID", "I THINK THIS SCHEDULE\nCOULD BE VERY NICE."));
        }
        if (text.startsWith("Task deleted. ☀️\n")) {
            dialogBox.bubble.getStyleClass().add("goodbye-bubble");
            dialogBox.bubble.getChildren().add(1, new MusicCard("/images/lorde-liability.png",
                    "LORDE · LIABILITY", "WATCH ME DISAPPEAR INTO THE SUN."));
        }
        if (text.startsWith("⚠️")) {
            dialogBox.bubble.getStyleClass().add("error-bubble");
            dialogBox.speaker.setText("WWaffle · Please check your command");
        }
        return dialogBox;
    }

    /**
     * Uses an installed emoji font for whole Unicode graphemes, keeping body typography intact.
     * Variation selectors and joined emoji stay together instead of becoming separate glyphs.
     *
     * @param message Plain-text message, including any emoji.
     */
    private void setMessage(String message) {
        List<String> families = Font.getFamilies();
        String emojiFamily = List.of("Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji")
                .stream().filter(families::contains).findFirst().orElse("System");
        Matcher graphemes = Pattern.compile("\\X").matcher(message);
        StringBuilder body = new StringBuilder();
        while (graphemes.find()) {
            String grapheme = graphemes.group();
            int codePoint = grapheme.codePointAt(0);
            boolean isEmoji = codePoint >= 0x1F000 || codePoint >= 0x2600 && codePoint <= 0x27BF;
            if (isEmoji) {
                appendBody(body.toString());
                body.setLength(0);
                Text emoji = new Text(grapheme);
                emoji.setFont(Font.font(emojiFamily, 26));
                dialog.getChildren().add(emoji);
            } else {
                body.append(grapheme);
            }
        }
        appendBody(body.toString());
        dialog.setAccessibleText(message);
    }

    private void appendBody(String content) {
        if (!content.isEmpty()) {
            Text text = new Text(content);
            text.getStyleClass().add("body-text");
            dialog.getChildren().add(text);
        }
    }

    /**
     * Creates a compact mascot goodbye without music or message-card decorations.
     *
     * @param response Concise goodbye response.
     * @return Horizontal Minion and goodbye text.
     */
    public static DialogBox getGoodbyeDialog(String response) {
        DialogBox dialogBox = new DialogBox(response);
        Font.loadFont(DialogBox.class.getResourceAsStream("/fonts/Inter.ttf"), 20);
        Image image = new Image(DialogBox.class.getResourceAsStream("/images/minion-goodbye.png"));
        ImageView mascot = new ImageView(image);
        mascot.setFitHeight(110);
        mascot.setPreserveRatio(true);
        mascot.setSmooth(true);
        mascot.setAccessibleText("Minions saying bye bye and farewell");
        dialogBox.getChildren().clear();
        dialogBox.bubble.getChildren().remove(dialogBox.dialog);
        dialogBox.getChildren().addAll(mascot, dialogBox.dialog);
        dialogBox.setAlignment(Pos.CENTER_LEFT);
        dialogBox.setSpacing(24);
        dialogBox.getStyleClass().add("goodbye-mascot");
        dialogBox.dialog.setMinWidth(0);
        HBox.setHgrow(dialogBox.dialog, javafx.scene.layout.Priority.ALWAYS);
        return dialogBox;
    }

    /**
     * Displays the appreciation mascot beside the concise thank-you response.
     *
     * @param response Appreciation text.
     * @return Compact horizontal reaction with no chat-card decorations.
     */
    private static DialogBox getThanksDialog(String response) {
        DialogBox dialogBox = new DialogBox(response);
        Font.loadFont(DialogBox.class.getResourceAsStream("/fonts/Inter.ttf"), 20);
        Image image = new Image(DialogBox.class.getResourceAsStream("/images/minion-thanks.png"));
        ImageView mascot = new ImageView(image);
        // Focus on the mascot rather than shrinking it to include the large blank heading area.
        mascot.setViewport(new Rectangle2D(image.getWidth() * 0.10, image.getHeight() * 0.32,
                image.getWidth() * 0.79, image.getHeight() * 0.66));
        mascot.setFitHeight(110);
        mascot.setPreserveRatio(true);
        mascot.setSmooth(true);
        mascot.setAccessibleText("You're welcome Minion with raised arms");
        dialogBox.getChildren().clear();
        dialogBox.bubble.getChildren().remove(dialogBox.dialog);
        dialogBox.getChildren().addAll(mascot, dialogBox.dialog);
        dialogBox.setAlignment(Pos.CENTER_LEFT);
        dialogBox.setSpacing(24);
        dialogBox.getStyleClass().add("thanks-mascot");
        dialogBox.dialog.setMinWidth(0);
        HBox.setHgrow(dialogBox.dialog, javafx.scene.layout.Priority.ALWAYS);
        return dialogBox;
    }

    /**
     * Replaces the list's plain-text body with a read-only checklist snapshot.
     *
     * @param response Existing response, retained for its heading and empty state.
     * @param tasks Tasks in command-number order.
     * @return A normal bot reply containing checklist rows.
     */
    public static DialogBox getTaskListDialog(String response, List<Task> tasks) {
        if (tasks.isEmpty()) {
            return getWWaffleDialog(response);
        }
        DialogBox box = getWWaffleDialog("Here's your list. ☕");
        box.bubble.getStyleClass().add("goodbye-bubble");
        box.bubble.getChildren().add(1, new MusicCard("/images/lorde-supercut.png",
                "LORDE · SUPERCUT", "IN MY HEAD, I PLAY A SUPERCUT OF US."));
        box.bubble.getChildren().add(3, new TaskChecklist(tasks));
        return box;
    }

    /**
     * Shows a successful mark confirmation and a checklist row with its original task number.
     *
     * @param response Existing mark response.
     * @param task Completed task.
     * @param number Current one-based task number.
     * @return Mark response with a completed checklist row.
     */
    public static DialogBox getMarkedTaskDialog(String response, Task task, int number) {
        DialogBox box = getWWaffleDialog(response.split("\n", 2)[0]);
        Font.loadFont(DialogBox.class.getResourceAsStream("/fonts/Inter.ttf"), 15);
        box.bubble.getStyleClass().add("greeting-bubble");
        box.bubble.getChildren().add(2, new TaskChecklist(List.of(task), number));
        return box;
    }

    /**
     * Shows the unmark music card and a checklist row for the reopened task.
     *
     * @param response Existing unmark response.
     * @param task Reopened task.
     * @param number Current one-based task number.
     * @return Music response with the actual incomplete task.
     */
    public static DialogBox getUnmarkedTaskDialog(String response, Task task, int number) {
        DialogBox box = getWWaffleDialog(response.split("\n", 2)[0]);
        box.bubble.getStyleClass().add("goodbye-bubble");
        box.bubble.getChildren().add(1, new MusicCard("/images/olivia-unmark.png",
                "OLIVIA RODRIGO · 1 STEP FORWARD, 3 STEPS BACK",
                "IT'S ALWAYS ONE STEP FORWARD AND THREE STEPS BACK."));
        box.bubble.getChildren().add(3, new TaskChecklist(List.of(task), number));
        return box;
    }

    /**
     * Presents the clear confirmation with the supplied Gatsby celebration image.
     *
     * @param response Successful clear response.
     * @return Clear confirmation in the established image-card style.
     */
    public static DialogBox getClearDialog(String response) {
        DialogBox box = getWWaffleDialog(response);
        box.bubble.getStyleClass().add("goodbye-bubble");
        box.bubble.getChildren().add(1, new MusicCard("/images/gatsby-clear.png",
                "THE GREAT GATSBY", "CHEERS TO A CLEAN SLATE."));
        return box;
    }

    /**
     * Renders an added or deleted task using the same checklist as the list response.
     * Keeps the existing music card and separates the count from task content.
     *
     * @param response Successful response containing heading, task, and count.
     * @param task Actual affected task, captured before deletion when necessary.
     * @param number Task number at the time of the operation.
     * @return Reply with a structured task row and separate count.
     */
    public static DialogBox getTaskChangeDialog(String response, Task task, int number) {
        DialogBox box = getWWaffleDialog(response);
        box.dialog.getChildren().clear();
        box.setMessage(response.substring(0, response.indexOf('\n')));
        Font.loadFont(DialogBox.class.getResourceAsStream("/fonts/Inter.ttf"), 15);
        box.bubble.getStyleClass().add("greeting-bubble");
        int position = box.bubble.getChildren().indexOf(box.dialog) + 1;
        box.bubble.getChildren().add(position, new TaskChecklist(List.of(task), number));
        Label count = new Label(response.substring(response.lastIndexOf('\n') + 1));
        count.getStyleClass().add("checklist-meta");
        count.setWrapText(true);
        box.bubble.getChildren().add(position + 1, count);
        return box;
    }

    /**
     * Shows sorted or matching tasks using the shared checklist presentation.
     *
     * @param response Existing response, including its empty state.
     * @param tasks Tasks in display order.
     * @param numbers Actual command numbers for each row.
     * @return Normal reply with checklist rows instead of raw task strings.
     */
    public static DialogBox getChecklistResponse(String response, List<Task> tasks, List<Integer> numbers) {
        if (tasks.isEmpty()) {
            return getWWaffleDialog(response);
        }
        String heading = response.split("\n", 2)[0];
        DialogBox box = getWWaffleDialog(heading);
        box.applySortHeadingEmphasis(heading);
        box.bubble.getStyleClass().add("checklist-bubble");
        box.bubble.getChildren().add(2, new TaskChecklist(tasks, numbers));
        return box;
    }

    /** Bolds the sort key in a successful sorted-list heading. */
    private void applySortHeadingEmphasis(String heading) {
        String prefix = "Here are your little problems, neatly arranged by ";
        if (!heading.startsWith(prefix)) {
            return;
        }
        int keyEnd = heading.indexOf('.', prefix.length());
        if (keyEnd < 0) {
            return;
        }
        dialog.getChildren().clear();
        appendBody(prefix);
        Text sortKey = new Text(heading.substring(prefix.length(), keyEnd));
        sortKey.getStyleClass().addAll("body-text", "sort-key");
        dialog.getChildren().add(sortKey);
        setMessage(heading.substring(keyEnd));
        dialog.setAccessibleText(heading);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_RIGHT);
        getStyleClass().add("user-dialog");
    }
}
