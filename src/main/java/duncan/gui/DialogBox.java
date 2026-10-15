package duncan.gui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import duncan.Ui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents one turn of the conversation. The two speakers are drawn in
 * deliberately different shapes, since this is a person talking to a program
 * rather than two people: the user's words sit in a compact bubble on the
 * right, while Duncan's replies are full-width panels on the left that can
 * colour errors, warnings and the parts of a task list.
 *
 * <p>All colours and fonts live in {@code /css/duncan.css}; this class only
 * says which style classes each piece of text gets.
 */
public class DialogBox extends HBox {
    /** How much of the window's width the user's bubble may take before its text wraps. */
    private static final double USER_BUBBLE_MAX_WIDTH_RATIO = 0.78;

    /** Put on every piece of text; JavaFX gives a {@code Text} made in code no style class of its own. */
    private static final String STYLE_TEXT = "dialog-text";
    private static final String STYLE_USER_BUBBLE = "bubble-user";
    private static final String STYLE_DUNCAN_BUBBLE = "bubble-duncan";
    private static final String STYLE_SEVERITY_PREFIX = "severity-";
    private static final String STYLE_KIND_PREFIX = "kind-";
    private static final String LINE_BREAK = "\n";

    @FXML
    private TextFlow bubble;

    private DialogBox() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns a dialog box for something the user typed, in a compact bubble on the right. */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox();
        dialogBox.showUserText(text);
        return dialogBox;
    }

    /**
     * Returns a dialog box for something Duncan said, as a full-width panel on the left.
     *
     * @param lines Duncan's reply, one entry per line; the panel is styled by the most severe line.
     */
    public static DialogBox getDuncanDialog(List<Ui.MessageLine> lines) {
        DialogBox dialogBox = new DialogBox();
        dialogBox.showDuncanLines(lines);
        return dialogBox;
    }

    private void showUserText(String text) {
        setAlignment(Pos.TOP_RIGHT);
        bubble.getStyleClass().add(STYLE_USER_BUBBLE);
        bubble.maxWidthProperty().bind(widthProperty().multiply(USER_BUBBLE_MAX_WIDTH_RATIO));
        bubble.getChildren().add(createText(text));
    }

    private void showDuncanLines(List<Ui.MessageLine> lines) {
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(bubble, Priority.ALWAYS);
        bubble.getStyleClass().addAll(STYLE_DUNCAN_BUBBLE,
                toSeverityStyleClass(Ui.getHighestSeverity(lines)));

        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                // Styled like the text around it, so the break takes the same line height.
                bubble.getChildren().add(createText(LINE_BREAK));
            }
            addLine(lines.get(i));
        }
    }

    /** Adds one line to the bubble, split into differently coloured pieces if it is a task. */
    private void addLine(Ui.MessageLine line) {
        String severityStyleClass = toSeverityStyleClass(line.severity());
        for (TaskLineParser.Segment segment : TaskLineParser.parse(line.text())) {
            Text piece = createText(segment.text());
            piece.getStyleClass().addAll(toKindStyleClass(segment.kind()), severityStyleClass);
            bubble.getChildren().add(piece);
        }
    }

    private static Text createText(String content) {
        Text text = new Text(content);
        text.getStyleClass().add(STYLE_TEXT);
        return text;
    }

    private static String toSeverityStyleClass(Ui.Severity severity) {
        return STYLE_SEVERITY_PREFIX + severity.name().toLowerCase(Locale.ROOT);
    }

    private static String toKindStyleClass(TaskLineParser.Kind kind) {
        return STYLE_KIND_PREFIX + kind.name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
