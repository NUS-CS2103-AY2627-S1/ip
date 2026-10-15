package anders;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import anders.parser.Parser;
import anders.storage.Storage;
import anders.ui.Ui;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/** Checks real FXML layouts and GUI interactions without accessing the user's task file. */
public class MainWindowTest {
    private static final String LONG_REPLY = "Lantern lit. Here are the tasks on your trail:\n"
            + "1.[D][ ] Finish the report with a detailed explanation of the experiment "
            + "(by: Oct 02 2026 6.00 pm) (tags: #school #project)\n"
            + "2.[T][ ] Read chapter 1 and summarize the most useful ideas\n"
            + "3.[E][ ] Study group (from: Oct 02 2026 2.00 pm to: Oct 02 2026 4.00 pm)";

    @TempDir
    private Path temporaryDirectory;

    @BeforeAll
    public static void startToolkit() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX must start before GUI tests");
    }

    @AfterAll
    public static void stopToolkit() {
        Platform.exit();
    }

    @Test
    public void initialize_personality_showsNameGreetingAndLantern() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new ArrayList<>(), 460, 640);
            assertEquals("Anders", ((Label) window.lookup("#appName")).getText());
            assertEquals("Your lantern keeper", ((Label) window.lookup("#tagline")).getText());
            assertTrue(messageAt(window, 0).getText().contains(Ui.WELCOME_MESSAGE));
            assertEquals("Anders", ((Label) messages(window).getChildren().getFirst()
                    .lookup("#speaker")).getText());
            assertEquals("Anders's lantern", window.lookup("#brandIcon").getAccessibleText());
            saveSnapshot(window, "anders-welcome.png");
        });
    }

    @Test
    public void start_stage_usesAndersTitleAndWindowIcon() throws Exception {
        runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                new Main().start(stage);
                assertEquals("Anders - Your lantern keeper", stage.getTitle());
                assertEquals(1, stage.getIcons().size());
                assertFalse(stage.getIcons().getFirst().isError());
            } finally {
                stage.close();
            }
        });
    }

    @Test
    public void handleInput_realSession_displaysThemedSuccessAndErrorReplies() throws Exception {
        runOnFxThread(() -> {
            Anders bot = new Anders(new Storage(temporaryDirectory.resolve("tasks.txt").toString()));
            MainWindow window = new MainWindow(bot::getResponse);
            new Scene(window, 460, 640);
            window.resize(460, 640);
            window.applyCss();
            window.layout();
            TextField input = (TextField) window.lookup("#input");
            input.setText("todo read chapter 1");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 2).getText().contains("A new trail marker. I've added this task:"));
            assertTrue(messageAt(window, 2).getText().contains("Your trail holds 1 task."));

            input.setText("mark 1");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 4).getText().contains("One more light along the path. Task marked as done:"));
            window.applyCss();
            window.layout();
            ScrollPane scroll = (ScrollPane) window.lookup("#scrollPane");
            scroll.setVvalue(1);
            saveSnapshot(window, "anders-conversation.png");

            input.setText("todo");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 6).getText().startsWith("A little fog on the path."));
            assertTrue(messageAt(window, 6).getText().contains("The description of a todo cannot be empty."));
        });
    }

    @Test
    public void handleInput_blankCommand_doesNotSubmit() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            MainWindow window = createWindow(commands, 460, 640);
            TextField input = (TextField) window.lookup("#input");
            Button send = (Button) window.lookup("#sendButton");

            assertTrue(send.isDisabled());
            input.setText("   ");
            input.fireEvent(new ActionEvent());

            assertTrue(send.isDisabled());
            assertTrue(commands.isEmpty());
            assertEquals(1, messages(window).getChildren().size());
        });
    }

    @Test
    public void handleInput_enterAndSend_submitOnceAndClearInput() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            MainWindow window = createWindow(commands, 460, 640);
            TextField input = (TextField) window.lookup("#input");
            Button send = (Button) window.lookup("#sendButton");

            input.setText("  list  ");
            assertFalse(send.isDisabled());
            input.fireEvent(new ActionEvent());
            assertEquals("", input.getText());
            assertTrue(send.isDisabled());

            input.setText("find chapter");
            send.fire();

            assertEquals(List.of("list", "find chapter"), commands);
            assertEquals("", input.getText());
            assertEquals(input, window.getScene().getFocusOwner());
            assertEquals(5, messages(window).getChildren().size());
            assertEquals("list", messageAt(window, 1).getText());
            assertEquals(LONG_REPLY, messageAt(window, 2).getText());
        });
    }

    @Test
    public void handleHistory_navigation_preservesDraftAndStopsAtBounds() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new ArrayList<>(), 460, 640);
            TextField input = (TextField) window.lookup("#input");
            input.setText("list");
            input.fireEvent(new ActionEvent());
            input.setText("find chapter");
            input.fireEvent(new ActionEvent());
            input.setText("todo unfinished draft");

            press(input, KeyCode.UP);
            assertEquals("find chapter", input.getText());
            press(input, KeyCode.UP);
            press(input, KeyCode.UP);
            assertEquals("list", input.getText());
            press(input, KeyCode.DOWN);
            assertEquals("find chapter", input.getText());
            press(input, KeyCode.DOWN);
            press(input, KeyCode.DOWN);
            assertEquals("todo unfinished draft", input.getText());
            assertEquals(input.getLength(), input.getCaretPosition());
        });
    }

    @Test
    public void handleHistory_emptyHistory_preservesInput() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new ArrayList<>(), 460, 640);
            TextField input = (TextField) window.lookup("#input");
            input.setText("my draft");

            press(input, KeyCode.UP);
            press(input, KeyCode.DOWN);

            assertEquals("my draft", input.getText());
        });
    }

    @Test
    public void showTasks_andHelp_preserveDraft() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            MainWindow window = createWindow(commands, 460, 640);
            TextField input = (TextField) window.lookup("#input");
            input.setText("todo unfinished draft");

            click(window, "tasksButton");
            assertEquals(List.of("list"), commands);
            assertEquals("todo unfinished draft", input.getText());

            click(window, "helpButton");
            assertEquals(List.of("list"), commands);
            assertEquals("todo unfinished draft", input.getText());
            assertTrue(messageAt(window, 3).getText().contains("deadline report /by 2/10/2026 1800"));
            assertTrue(messageAt(window, 3).getText().contains("find #<tag>"));
        });
    }

    @Test
    public void showHelp_headingsAndNumberGuidance_areBoldAndExamplesRemainPlain() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new ArrayList<>(), 460, 1120);
            click(window, "helpButton");
            window.applyCss();
            window.layout();
            Label help = messageAt(window, 1);
            TextFlow formattedMessage = (TextFlow) help.getGraphic();
            List<String> expectedHeadings = List.of("Command guide", "Add tasks", "View tasks",
                    "Update a task", "Use task numbers from list.", "Use tags");
            List<String> actualHeadings = new ArrayList<>();
            StringBuilder displayedText = new StringBuilder();

            for (var node : formattedMessage.getChildren()) {
                Text line = (Text) node;
                assertEquals(help.getFont().getSize(), line.getFont().getSize(),
                        "Formatted help should retain the message font size");
                String lineText = line.getText().stripTrailing();
                boolean isBold = line.getFont().getStyle().toLowerCase(Locale.ROOT).contains("bold");
                assertEquals(expectedHeadings.contains(lineText), isBold,
                        "Only section headings and task-number guidance should be bold: " + lineText);
                if (isBold) {
                    actualHeadings.add(lineText);
                }
                displayedText.append(line.getText());
            }
            assertEquals(expectedHeadings, actualHeadings);
            assertTrue(help.getText().contains("Replace <...> with your own text; leave out the brackets."));
            assertTrue(help.getText().contains("<number> can be any number from 1 to your task count."));
            assertTrue(help.getText().contains("With 2 tasks, replace <number> with 1 or 2."));
            assertTrue(help.getText().contains("After deleting, run list again: task numbers change."));
            assertTrue(help.getText().contains("2/10/2026 1800 means 2 Oct 2026 at 6 pm."));
            List<String> examples = help.getText().lines()
                    .filter(line -> line.startsWith("Example: "))
                    .map(line -> line.substring("Example: ".length()))
                    .toList();
            assertEquals(7, examples.size());
            for (String example : examples) {
                assertDoesNotThrow(() -> Parser.parse(example), "The example must be a valid command: " + example);
            }
            assertEquals(help.getText(), displayedText.toString(), "Copying must preserve the original plain text");
            saveSnapshot(window, "command-guide-460.png");

            window.resize(360, 400);
            window.applyCss();
            window.layout();
            assertTrue(help.getHeight() >= formattedMessage.prefHeight(help.getWidth()) - 1,
                    "Formatted help must wrap without truncating its content");
            assertTrue(formattedMessage.getWidth() <= help.getWidth() + 1);
            assertInsideWindow(window, formattedMessage.localToScene(formattedMessage.getBoundsInLocal()), false);
        });
    }

    @Test
    public void dialog_alignmentAndFormatting_keepRepliesReadable() throws Exception {
        runOnFxThread(() -> {
            DialogBox reply = DialogBox.getBotDialog("Heading\n     1.[T][ ] read book\n       Detail");
            DialogBox user = DialogBox.getUserDialog("todo read  two chapters");
            Label replyText = (Label) reply.lookup("#message");

            assertEquals("Anders", ((Label) reply.lookup("#speaker")).getText());
            assertEquals("You", ((Label) user.lookup("#speaker")).getText());
            assertEquals("Anders's lantern", reply.lookup("#avatar").getAccessibleText());
            assertEquals("Your compass", user.lookup("#avatar").getAccessibleText());
            assertEquals(Pos.TOP_LEFT, reply.getAlignment());
            assertEquals(Pos.TOP_RIGHT, user.getAlignment());
            assertEquals("Heading\n1.[T][ ] read book\nDetail", replyText.getText());
            assertEquals("todo read  two chapters", ((Label) user.lookup("#message")).getText());
            assertTrue(replyText.isWrapText());
            assertEquals("Copy message", replyText.getContextMenu().getItems().getFirst().getText());
            assertEquals(user.lookup("#avatar"), user.getChildren().getLast());
        });
    }

    @Test
    public void layout_narrowAndWideWindows_wrapWithoutClipping() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new ArrayList<>(), 460, 640);
            click(window, "tasksButton");

            for (int width : List.of(360, 460, 720)) {
                window.resize(width, width == 360 ? 400 : 640);
                window.applyCss();
                window.layout();
                ScrollPane scroll = (ScrollPane) window.lookup("#scrollPane");
                TextField input = (TextField) window.lookup("#input");
                Label reply = messageAt(window, 2);

                assertTrue(messages(window).getWidth() <= scroll.getViewportBounds().getWidth() + 1);
                assertTrue(reply.getWidth() > 200, "Replies should use most of the available width");
                assertTrue(reply.getHeight() >= reply.prefHeight(reply.getWidth()) - 1,
                        "Wrapped reply text must not be truncated vertically");
                assertTrue(input.getWidth() > 200, "The composer must remain usable");
                for (String id : List.of("appName", "tagline", "tasksButton", "helpButton")) {
                    var control = window.lookup("#" + id);
                    assertInsideWindow(window, control.localToScene(control.getBoundsInLocal()));
                    if (control instanceof Label label) {
                        assertTrue(label.getWidth() >= label.prefWidth(-1) - 1, "Brand text must not be clipped");
                    }
                }
                assertInsideWindow(window, input.localToScene(input.getBoundsInLocal()));
                assertInsideWindow(window, reply.localToScene(reply.getBoundsInLocal()), false);
                saveSnapshot(window, "conversation-" + width + ".png");
            }
        });
    }

    @Test
    public void layout_unbrokenText_staysWithinViewport() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = new MainWindow(command -> "x".repeat(500));
            new Scene(window, 360, 400);
            window.resize(360, 400);
            click(window, "tasksButton");
            window.applyCss();
            window.layout();

            Label reply = messageAt(window, 2);
            ScrollPane scroll = (ScrollPane) window.lookup("#scrollPane");
            assertTrue(messages(window).getWidth() <= scroll.getViewportBounds().getWidth() + 1);
            assertTrue(reply.getHeight() >= reply.prefHeight(reply.getWidth()) - 1);
            assertInsideWindow(window, reply.localToScene(reply.getBoundsInLocal()), false);
        });
    }

    @Test
    public void appendReply_longOutput_revealsBeginningAndAllowsManualScroll() throws Exception {
        MainWindow[] windows = new MainWindow[1];
        runOnFxThread(() -> {
            MainWindow window = new MainWindow(command -> LONG_REPLY.repeat(12));
            new Scene(window, 360, 400);
            window.resize(360, 400);
            click(window, "tasksButton");
            windows[0] = window;
        });
        // The second FX event runs after the queued scroll-to-reply layout.
        runOnFxThread(() -> {
            MainWindow window = windows[0];
            ScrollPane scroll = (ScrollPane) window.lookup("#scrollPane");
            VBox conversation = messages(window);
            double scrolledPixels = scroll.getVvalue()
                    * (conversation.getHeight() - scroll.getViewportBounds().getHeight());
            double replyTop = conversation.getChildren().get(2).getLayoutY();

            assertTrue(scroll.getVvalue() > 0 && scroll.getVvalue() < 1);
            assertEquals(replyTop - 8, scrolledPixels, 1);
            saveSnapshot(window, "long-reply-360.png");
            assertFalse(scroll.vvalueProperty().isBound(), "Older messages must remain scrollable");
            scroll.setVvalue(0);
            assertEquals(0, scroll.getVvalue());
        });
    }

    private static MainWindow createWindow(List<String> commands, int width, int height) {
        MainWindow window = new MainWindow(command -> {
            commands.add(command);
            return LONG_REPLY;
        });
        new Scene(window, width, height);
        window.resize(width, height);
        window.applyCss();
        window.layout();
        return window;
    }

    private static VBox messages(MainWindow window) {
        return (VBox) window.lookup("#messages");
    }

    private static Label messageAt(MainWindow window, int index) {
        return (Label) messages(window).getChildren().get(index).lookup("#message");
    }

    private static void click(MainWindow window, String id) {
        Button button = (Button) window.lookup("#" + id);
        button.fire();
    }

    private static void press(TextField input, KeyCode code) {
        input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
    }

    private static void assertInsideWindow(MainWindow window, Bounds bounds) {
        assertInsideWindow(window, bounds, true);
    }

    private static void assertInsideWindow(MainWindow window, Bounds bounds, boolean shouldCheckHeight) {
        assertTrue(bounds.getMinX() >= 0 && bounds.getMaxX() <= window.getWidth());
        if (shouldCheckHeight) {
            assertTrue(bounds.getMinY() >= 0 && bounds.getMaxY() <= window.getHeight());
        }
    }

    /** Saves the actual JavaFX rendering so the layout can also be reviewed visually. */
    private static void saveSnapshot(MainWindow window, String filename) {
        WritableImage snapshot = window.snapshot(null, null);
        int width = (int) snapshot.getWidth();
        int height = (int) snapshot.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        try {
            Path directory = Path.of("build", "reports", "gui");
            Files.createDirectories(directory);
            ImageIO.write(image, "png", directory.resolve(filename).toFile());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save GUI preview", exception);
        }
    }

    private static void runOnFxThread(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void initialize_unreadableStorage_displaysWarningAndKeepsInputUsable() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(file);
        runOnFxThread(() -> {
            MainWindow window = new MainWindow(new Anders(new Storage(file.toString())));
            new Scene(window, 460, 640);
            window.resize(460, 640);
            window.applyCss();
            window.layout();
            assertTrue(messageAt(window, 1).getText().contains("I couldn't read tasks from"));
            assertTrue(messageAt(window, 1).getText().contains("saving is disabled"));
            TextField input = (TextField) window.lookup("#input");
            input.setText("todo new task");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 3).getText().contains("Changes are only available in this session"));
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 5).getText().contains("1.[T][ ] new task"));
        });
    }

    @Test
    public void handleInput_invalidEvent_displaysErrorThenAcceptsValidCommand() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = new MainWindow(new Anders(
                    new Storage(temporaryDirectory.resolve("tasks.txt").toString())));
            new Scene(window, 460, 640);
            window.resize(460, 640);
            window.applyCss();
            window.layout();
            TextField input = (TextField) window.lookup("#input");
            input.setText("event meeting /from 2026-10-02 1600 /to 2026-10-02 1400");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 2).getText().contains("An event must end at or after it starts."));
            input.setText("todo next task");
            input.fireEvent(new ActionEvent());
            assertTrue(messageAt(window, 4).getText().contains("Your trail holds 1 task."));
        });
    }

}
