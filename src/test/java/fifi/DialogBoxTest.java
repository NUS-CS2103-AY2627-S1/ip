package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Tests error presentation and command recovery on the JavaFX application thread.
 */
public class DialogBoxTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Starts JavaFX when the environment provides a graphical display.
     */
    @BeforeAll
    public static void startJavaFx() throws Exception {
        boolean isLinux = System.getProperty("os.name").startsWith("Linux");
        assumeTrue(!isLinux || System.getenv("DISPLAY") != null,
                "JavaFX presentation tests require a graphical display.");
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX did not start.");
    }

    @Test
    public void getFifiDialog_errorThenSuccess_onlyErrorHasHighlight() throws Exception {
        runOnJavaFxThread(() -> {
            Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
            WritableImage avatar = new WritableImage(1, 1);
            DialogBox error = DialogBox.getFifiDialog(fifi.getChatResponse("blah"), avatar);
            DialogBox success = DialogBox.getFifiDialog(fifi.getChatResponse("todo Oops! read book"), avatar);
            VBox root = layOutDialogs(error, success);

            assertEquals(Pos.TOP_LEFT, error.getAlignment());
            Label heading = (Label) error.lookup(".error-heading");
            assertNotNull(heading);
            assertEquals("Error", heading.getText());
            Label message = (Label) error.lookup(".error-message");
            assertTrue(message.getText().contains("Valid commands include"));
            assertTrue(message.isWrapText());

            VBox errorBox = (VBox) error.lookup(".error-box");
            assertNotNull(errorBox.getBorder());
            assertEquals(Color.web("#fff1f0"), errorBox.getBackground().getFills().getFirst().getFill());
            assertEquals(Color.web("#8a1c13"), message.getTextFill());
            assertTrue(heading.getFont().getStyle().contains("Bold"));
            assertTrue(errorBox.getWidth() <= 250.0);
            assertTrue(message.getWidth() < errorBox.getWidth());
            assertTrue(message.getLayoutY() + message.getHeight() <= errorBox.getHeight());

            assertNull(success.lookup(".error-heading"));
            assertNull(success.lookup(".error-box"));
            assertTrue(((Label) success.lookup(".label")).getText().contains("Oops! read book"));
            assertEquals(1, root.lookupAll(".error-box").size());
        });
    }

    @Test
    public void getFifiDialog_validThenInvalidDate_messageAndErrorStatusMatch() throws Exception {
        runOnJavaFxThread(() -> {
            Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
            WritableImage avatar = new WritableImage(1, 1);
            DialogBox success = DialogBox.getFifiDialog(fifi.getChatResponse("list"), avatar);
            DialogBox error = DialogBox.getFifiDialog(fifi.getChatResponse("show 2025-02-30"), avatar);
            layOutDialogs(success, error);

            assertNull(success.lookup(".error-box"));
            Label message = (Label) error.lookup(".error-message");
            assertEquals("Oops! Please use yyyy-MM-dd for dates.",
                    message.getText());
        });
    }

    @Test
    public void getUserDialog_errorWords_userMessageRemainsNormalAndRightAligned() throws Exception {
        runOnJavaFxThread(() -> {
            DialogBox user = DialogBox.getUserDialog("Error: Oops!", new WritableImage(1, 1));
            layOutDialogs(user);

            assertEquals(Pos.TOP_RIGHT, user.getAlignment());
            assertEquals("Error: Oops!", ((Label) user.lookup(".label")).getText());
            assertFalse(user.getChildren().isEmpty());
            assertNull(user.lookup(".error-box"));
        });
    }

    @Test
    public void start_corruptedSaveFile_startupAndBlockedCommandAreHighlighted() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Files.writeString(dataFile, "T");
        Fifi fifi = new Fifi(dataFile.toString());
        runOnJavaFxThread(() -> {
            Stage stage = new Stage();
            stage.setOpacity(0);
            try {
                new Main(fifi).start(stage);
                Parent root = stage.getScene().getRoot();
                root.applyCss();
                root.layout();
                assertEquals(1, root.lookupAll(".error-box").size());
                Label startupMessage = (Label) root.lookup(".error-message");
                assertTrue(startupMessage.getText().contains("line 1"));
                TextField input = (TextField) root.lookup(".text-field");
                input.setText("todo new task");
                input.fireEvent(new ActionEvent());
                root.applyCss();
                root.layout();
                assertEquals(2, root.lookupAll(".error-box").size());
                assertTrue(root.lookupAll(".error-message").stream()
                        .map(node -> ((Label) node).getText())
                        .anyMatch(message -> message.contains("Tasks cannot be changed")));
            } finally {
                stage.close();
            }
        });
        assertEquals("T", Files.readString(dataFile));
    }

    @Test
    public void start_validThenInvalidCommand_packagedAvatarsRemainCorrect() throws Exception {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
        runOnJavaFxThread(() -> {
            Stage stage = new Stage();
            stage.setOpacity(0);
            try {
                new Main(fifi).start(stage);
                Parent root = stage.getScene().getRoot();
                root.applyCss();
                root.layout();
                VBox dialogs = (VBox) ((ScrollPane) root.lookup(".scroll-pane")).getContent();
                DialogBox greeting = (DialogBox) dialogs.getChildren().getFirst();
                ImageView fifiAvatar = (ImageView) greeting.getChildren().getFirst();
                assertEquals(Main.class.getResource("/fifi.png").toExternalForm(), fifiAvatar.getImage().getUrl());
                assertFalse(fifiAvatar.getImage().isError());
                assertTrue(fifiAvatar.getImage().getWidth() > 0);
                assertTrue(fifiAvatar.isPreserveRatio());

                TextField input = (TextField) root.lookup(".text-field");
                input.setText("todo Prepare slides for the project meeting");
                input.fireEvent(new ActionEvent());
                DialogBox userDialog = (DialogBox) dialogs.getChildren().get(1);
                ImageView userAvatar = (ImageView) userDialog.getChildren().getLast();
                assertEquals(Main.class.getResource("/user.png").toExternalForm(), userAvatar.getImage().getUrl());
                assertFalse(userAvatar.getImage().isError());
                assertTrue(userAvatar.getImage().getWidth() > 0);
                assertTrue(userAvatar.isPreserveRatio());

                input.setText("show 2026-02-30");
                input.fireEvent(new ActionEvent());
                DialogBox errorDialog = (DialogBox) dialogs.getChildren().getLast();
                assertEquals(fifiAvatar.getImage(), ((ImageView) errorDialog.getChildren().getFirst()).getImage());
                assertNotNull(errorDialog.lookup(".error-box"));
            } finally {
                stage.close();
            }
        });
    }

    @Test
    public void start_fxmlActions_preservesInteraction() throws Exception {
        Fifi fifi = new Fifi(temporaryDirectory.resolve("fifi.txt").toString());
        runOnJavaFxThread(() -> {
            Stage stage = new Stage();
            stage.setOpacity(0);
            try {
                new Main(fifi).start(stage);
                Parent root = stage.getScene().getRoot();
                TextField input = (TextField) root.lookup("#userInput");
                Button send = (Button) root.lookup("#sendButton");
                ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
                VBox dialogs = (VBox) scroll.getContent();
                assertTrue(stage.isResizable());
                input.setText("   ");
                send.fire();
                assertEquals(1, dialogs.getChildren().size());
                input.setText("show 2026-02-30");
                send.fire();
                assertNotNull(dialogs.getChildren().getLast().lookup(".error-box"));
                input.setText("todo read book");
                input.fireEvent(new ActionEvent());
                assertEquals("", input.getText());
                assertTrue(fifi.getResponse("list").contains("read book"));
                for (int i = 0; i < 12; i++) {
                    input.setText("list");
                    send.fire();
                }
                root.applyCss();
                root.layout();
                assertEquals(1.0, scroll.getVvalue());
                root.resize(600.0, 800.0);
                root.applyCss();
                root.layout();
                assertTrue(input.getWidth() > 300.0);
                assertTrue(scroll.getWidth() > 500.0);
                input.setText("bye extra");
                send.fire();
                assertFalse(input.isDisabled());
                input.setText("bye");
                send.fire();
                assertTrue(fifi.isExit());
                assertFalse(stage.isShowing());
            } finally {
                stage.close();
            }
        });
    }

    private static VBox layOutDialogs(DialogBox... dialogs) {
        VBox root = new VBox(dialogs);
        Scene scene = new Scene(root, 370.0, 600.0);
        scene.getStylesheets().add(Main.class.getResource("/styles/dialog.css").toExternalForm());
        root.applyCss();
        root.layout();
        return root;
    }

    private static void runOnJavaFxThread(Runnable assertions) throws Exception {
        FutureTask<Void> task = new FutureTask<>(assertions, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
