package yapbot.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import yapbot.storage.Storage;

/**
 * End-to-end GUI test, driven through TestFX against a real window: it types
 * into the real text field and clicks the real send button, exercising the
 * same path a person testing the window by hand would. Needs a focused
 * interactive desktop session to run reliably.
 */
public class MainWindowTestFX extends ApplicationTest {

    private static final String FXML_PATH = "/view/MainWindow.fxml";

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Storage.setTargetPathForTesting(Files.createTempFile("yapBotTestFx", ".txt"));

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(FXML_PATH));
        AnchorPane root = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        controller.setYapBotGui(new YapBotGui());

        this.stage = stage;
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }

    @AfterEach
    public void resetStorage() {
        Storage.resetTargetPathForTesting();
    }

    @BeforeEach
    public void waitForWindowToBecomeFocusable() {
        // The very first window a test class creates can take the OS a
        // moment to actually activate/focus after show(); without this,
        // whichever test method runs first can have its typed input land
        // before the window is ready to receive it.
        sleep(300);
    }

    @Test
    public void windowOpens_showsUisGreetingWording() {
        // Regression test: the GUI used to show its own hardcoded greeting
        // string instead of Ui's, so editing Ui's greeting had no visible
        // effect in the GUI. This checks the window actually displays it.
        assertTrue(anyLabelContains("Hey! I'm YapBot"));
    }

    @Test
    public void typingTodoCommand_showsConfirmationInConversation() {
        clickOn("#userInput");
        write("todo read book");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyLabelContains("Great, I've added read book to the list"));
    }

    @Test
    public void typingByeCommand_showsGoodbyeAndClosesWindow() throws Exception {
        clickOn("#userInput");
        write("bye");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyLabelContains("Nice work today! See you soon!"));
        WaitForAsyncUtils.waitFor(3, TimeUnit.SECONDS, () -> !stage.isShowing());
    }

    @Test
    public void typingUnrecognizedCommand_showsConfusedIcon() {
        clickOn("#userInput");
        write("gibberish");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyImageViewShows(DialogBox.CONFUSED_ICON));
    }

    @Test
    public void typingDuplicateTodo_showsConfusedIcon() {
        clickOn("#userInput");
        write("todo read book");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
        write("todo read book");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyImageViewShows(DialogBox.CONFUSED_ICON));
    }

    @Test
    public void typingInvalidTaskNumber_showsConfusedIcon() {
        // Any rejected/invalid input should get the confused icon, not just
        // the two cases (duplicate task, unrecognized command) it originally
        // shipped with — "mark 99" on an empty list is a validation error
        // from a different code path (TaskList#validateIndex), covered here
        // to prove the icon choice generalizes rather than special-casing.
        clickOn("#userInput");
        write("mark 99");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyImageViewShows(DialogBox.CONFUSED_ICON));
    }

    @Test
    public void typingTodoCommand_showsDefaultIcon() {
        clickOn("#userInput");
        write("todo read book");
        push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(anyImageViewShows(DialogBox.DEFAULT_ICON));
    }

    private boolean anyLabelContains(String text) {
        Set<Node> labels = lookup(".label").queryAll();
        return labels.stream()
                .map(node -> (Label) node)
                .anyMatch(label -> label.getText().contains(text));
    }

    private boolean anyImageViewShows(Image icon) {
        Set<Node> imageViews = lookup(".image-view").queryAll();
        return imageViews.stream()
                .map(node -> (ImageView) node)
                .anyMatch(imageView -> imageView.getImage() == icon);
    }
}
