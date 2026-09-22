package panda.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import panda.Panda;

/**
 * Exercises the main JavaFX window through its visible input and dialog controls.
 * The macOS GitHub runner cannot create JavaFX stages in its noninteractive session,
 * so macOS window behavior is covered by the manual GUI test plan.
 */
@DisabledOnOs(OS.MAC)
public class MainWindowTest {
    private static final int GUI_TIMEOUT_SECONDS = 15;

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void mainWindow_commandsAndBye_formatsResponsesAndClosesStage()
            throws InterruptedException {
        CountDownLatch toolkitStarted = new CountDownLatch(1);
        try {
            Platform.startup(toolkitStarted::countDown);
        } catch (IllegalStateException exception) {
            toolkitStarted.countDown();
        }
        assertTrue(toolkitStarted.await(GUI_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        CountDownLatch stageClosed = new CountDownLatch(1);
        AtomicReference<Throwable> testFailure = new AtomicReference<>();
        Platform.runLater(() -> runGuiScenario(stageClosed, testFailure));

        assertTrue(stageClosed.await(GUI_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "The Panda window did not close after the bye command.");
        assertNull(testFailure.get(), () -> "GUI scenario failed: " + testFailure.get());
    }

    /**
     * Loads the GUI, exercises representative commands, and records assertion failures.
     */
    private void runGuiScenario(CountDownLatch stageClosed,
            AtomicReference<Throwable> testFailure) {
        Stage stage = new Stage();
        stage.setOnHidden(event -> stageClosed.countDown());
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainWindow.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            Path dataFile = temporaryDirectory.resolve("tasks.txt");
            loader.<MainWindow>getController().setPanda(new Panda(dataFile.toString()));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            TextField userInput = (TextField) scene.lookup("#userInput");
            Button sendButton = (Button) scene.lookup("#sendButton");
            VBox dialogContainer = (VBox) scene.lookup("#dialogContainer");

            assertDialogText(dialogContainer, 0, "Ready to tackle the bamboo pile? (^_^)");
            submitAndAssert(userInput, sendButton, dialogContainer, "todo read book", """
                    Got it. I've added this task: (^_^)
                      [T][ ] read book
                    Now you have 1 task in the list.
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "mark 1", """
                    Nice! I've marked this task as done: (^_^)
                      [X] read book
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer,
                    "update 1 /name read Java book", """
                    Got it. I've updated this task: (^_^)
                      [T][X] read Java book
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "list", """
                    Here are the tasks in your list: (^_^)
                    1.[T][X] read Java book
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer, "unknown", """
                    OOPS!!! I'm bamboo-zled; I don't know what that means :-( (>_<)""");
            submitAndAssert(userInput, sendButton, dialogContainer, "delete 1", """
                    Noted. I've removed this task: (^_^)
                      [T][X] read Java book
                    Now you have 0 tasks in the list.
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer,
                    "deadline submit report /by 2026-09-15 17:00", """
                    Got it. I've added this task: (^_^)
                      [D][ ] submit report (by: Sep 15 2026 17:00)
                    Now you have 1 task in the list.
                    """);
            submitAndAssert(userInput, sendButton, dialogContainer,
                    "update 1 /by 2026-09-15 18:00", """
                    Got it. I've updated this task: (^_^)
                      [D][ ] submit report (by: Sep 15 2026 18:00)
                    """);
            verifyLayout(root, scene, dialogContainer, userInput);
            assertNull(scene.lookup("#addShortcut"));
            assertNull(scene.lookup("#todayShortcut"));
            assertNull(scene.lookup("#findShortcut"));
            assertNull(scene.lookup("#moreMenu"));
            submitAndAssert(userInput, sendButton, dialogContainer, "bye", """
                    Bye. Hope to see you again soon! (^_^)/""");
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
        } catch (Throwable throwable) {
            testFailure.set(throwable);
            stage.hide();
            Platform.exit();
        }
    }

    /**
     * Submits one command and checks its newly appended Panda dialog.
     */
    private void submitAndAssert(TextField userInput, Button sendButton,
            VBox dialogContainer, String command, String expectedResponse) {
        int pandaDialogIndex = dialogContainer.getChildren().size() + 1;
        userInput.setText(command);
        sendButton.fire();

        assertEquals(expectedResponse.startsWith("OOPS!!!") ? command : "", userInput.getText());
        assertDialogText(dialogContainer, pandaDialogIndex, expectedResponse);
    }

    /**
     * Checks dialog text while ignoring platform line-ending differences.
     */
    private void assertDialogText(VBox dialogContainer, int dialogIndex,
            String expectedText) {
        DialogBox dialogBox = (DialogBox) dialogContainer.getChildren().get(dialogIndex);
        assertEquals(normalizeLineEndings(expectedText.stripTrailing()),
                normalizeLineEndings(dialogBox.getDialogText()));
    }

    /**
     * Checks message identity, error emphasis, and wrapping at compact and wide sizes.
     */
    private void verifyLayout(BorderPane root, Scene scene, VBox dialogs, TextField input) {
        DialogBox userRow = (DialogBox) dialogs.getChildren().get(1);
        assertTrue(userRow.getChildren().stream().noneMatch(ImageView.class::isInstance));
        DialogBox pandaRow = (DialogBox) dialogs.getChildren().get(2);
        ImageView avatar = (ImageView) pandaRow.getChildren().getFirst();
        assertEquals(40, avatar.getFitWidth());
        assertEquals(40, avatar.getFitHeight());
        assertTrue(avatar.getClip() instanceof Circle);
        DialogBox errorRow = (DialogBox) dialogs.getChildren().get(10);
        DialogBox marked = (DialogBox) dialogs.getChildren().get(4);
        assertTrue(marked.lookup(".completed-task") != null);
        assertEquals("read book", ((Label) marked.lookup(".task-title")).getText());
        DialogBox reopened = DialogBox.getPandaDialog(
                "OK, I've marked this task as not done yet: (^_^)\n  [ ] read book", avatar.getImage());
        assertTrue(reopened.lookup(".reopened-task") != null);
        assertEquals("read book", ((Label) reopened.lookup(".task-title")).getText());
        DialogBox dates = DialogBox.getPandaDialog(
                "Here are the tasks in your list:\n"
                        + "2.[D][ ] report (by: Sep 15 2026 18:00)\n"
                        + "3.[E][X] meeting (from: Sep 15 2026 18:00 to: Sep 15 2026 19:00)",
                avatar.getImage());
        assertTrue(dates.lookup(".task-d") != null);
        assertTrue(dates.lookup(".task-e") != null);
        assertTrue(dates.lookupAll(".task-title").stream()
                .map(node -> ((Label) node).getText()).anyMatch("report"::equals));
        DialogBox empty = DialogBox.getPandaDialog("Here are the tasks in your list:", avatar.getImage());
        assertTrue(empty.lookup(".empty-message") != null);
        assertTrue(pandaRow.lookupAll(".task-card").size() == 1);
        assertTrue(pandaRow.lookup(".task-t") != null);
        DialogBox deleted = (DialogBox) dialogs.getChildren().get(12);
        assertTrue(deleted.lookup(".removed-task") != null);
        DialogBox listed = (DialogBox) dialogs.getChildren().get(8);
        assertTrue(listed.lookup(".task-card") != null);
        Label error = (Label) errorRow.getChildren().getLast();
        assertTrue(error.getStyleClass().contains("error-response"));

        ScrollPane scroll = (ScrollPane) scene.lookup("#scrollPane");
        root.resize(360, 320);
        root.applyCss();
        root.layout();
        double narrowInput = input.getWidth();
        double narrowViewport = scroll.getViewportBounds().getWidth();
        double narrowHeight = pandaRow.getHeight();
        root.resize(800, 700);
        root.layout();
        assertTrue(input.getWidth() > narrowInput);
        assertTrue(scroll.getViewportBounds().getWidth() > narrowViewport);
        assertTrue(pandaRow.getHeight() <= narrowHeight);
        for (javafx.scene.Node row : dialogs.getChildren()) {
            assertTrue(row.getBoundsInParent().getMaxX() <= scroll.getViewportBounds().getWidth() + 1);
        }
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
