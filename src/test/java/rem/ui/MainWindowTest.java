package rem.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rem.Rem;

public class MainWindowTest {
    @TempDir
    Path directory;

    @BeforeAll
    public static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(ready::countDown);
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void chat_submitResizeAndExit_loadsResourcesAndUsesRealCommands() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            new Scene(root);
            loader.<MainWindow>getController().setRem(new Rem(directory.resolve("rem.txt").toString()));
            TextField input = (TextField) loader.getNamespace().get("userInput");
            Button send = (Button) loader.getNamespace().get("sendButton");
            VBox dialogs = (VBox) loader.getNamespace().get("dialogContainer");
            ScrollPane scroll = (ScrollPane) loader.getNamespace().get("scrollPane");
            HBox emptyState = (HBox) loader.getNamespace().get("emptyState");
            Label sleepIndicator = (Label) loader.getNamespace().get("sleepIndicator");
            ImageView portrait = (ImageView) loader.getNamespace().get("portrait");
            assertTrue(emptyState.isVisible());
            assertTrue(emptyState.isManaged());
            assertEquals("Got a task for us?", input.getPromptText());
            Field timerField = MainWindow.class.getDeclaredField("idleTimer");
            timerField.setAccessible(true);
            PauseTransition timer = (PauseTransition) timerField.get(loader.getController());
            assertEquals(45, timer.getDuration().toSeconds());
            timer.getOnFinished().handle(new ActionEvent());
            assertTrue(sleepIndicator.isVisible());
            assertEquals(0.4, portrait.getOpacity());
            input.setText("todo read book");
            assertFalse(sleepIndicator.isVisible());
            assertEquals(1.0, portrait.getOpacity());
            send.fire();
            assertFalse(emptyState.isVisible());
            assertFalse(emptyState.isManaged());
            input.setText("note 1 Borrow it from Alice");
            send.fire();
            input.setText("list");
            input.fireEvent(new ActionEvent());
            assertEquals(7, dialogs.getChildren().size());
            assertEquals("", input.getText());
            input.setText("unfinished draft");
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false));
            assertEquals("list", input.getText());
            assertEquals(input.getLength(), input.getCaretPosition());
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false));
            assertEquals("unfinished draft", input.getText());
            assertEquals(7, dialogs.getChildren().size());
            HBox reply = (HBox) dialogs.getChildren().get(6);
            assertTrue(((Label) reply.lookup("#dialog")).getText().contains("1.[T][ ] read book"));
            assertTrue(((Label) reply.lookup("#dialog")).getText()
                    .contains("Note: Borrow it from Alice"));
            ImageView image = (ImageView) root.lookup("ImageView");
            assertFalse(image.getImage().isError());
            assertEquals(40, image.getFitWidth());
            input.setText("   ");
            assertTrue(send.isDisabled());
            input.fireEvent(new ActionEvent());
            assertEquals(7, dialogs.getChildren().size());
            input.setText("todos");
            send.fire();
            assertEquals("todos", input.getText());
            VBox error = (VBox) dialogs.getChildren().get(8).lookup("#message");
            assertTrue(error.getStyleClass().contains("error-message"));
            assertTrue(error.lookup("#errorHeading").isVisible());
            assertTrue(error.getAccessibleText().startsWith("Error:"));
            input.setText("list");
            send.fire();
            assertEquals("", input.getText());
            assertFalse(dialogs.getChildren().get(10).lookup("#errorHeading").isManaged());
            input.setText("todo " + "A long description that needs wrapping. ".repeat(8)
                    + " /note " + "A detailed note. ".repeat(10));
            send.fire();
            Label longReply = (Label) dialogs.getChildren().get(12).lookup("#dialog");
            for (int width : new int[]{380, 800}) {
                root.resize(width, width == 380 ? 400 : 600);
                root.applyCss();
                root.layout();
                assertEquals(Color.BLACK, root.getBackground().getFills().get(0).getFill());
                assertTrue(input.getWidth() > 100);
                assertTrue(scroll.getWidth() <= width);
                assertTrue(reply.getBoundsInParent().getMaxX() <= dialogs.getWidth());
                Label replyText = (Label) reply.lookup("#dialog");
                assertTrue(replyText.getWidth() > 100);
                assertTrue(replyText.getWidth() < width);
                assertEquals(Color.web("#38212a"), error.getBackground().getFills().get(0).getFill());
                assertTrue(longReply.getHeight() >= longReply.prefHeight(longReply.getWidth()) - 1);
                assertTrue(longReply.getHeight() > 60);
            }
            for (int i = 0; i < 15; i++) {
                input.setText("list");
                send.fire();
            }
            root.layout();
            assertEquals(1.0, scroll.getVvalue());
            assertTrue(((Label) reply.lookup("#dialog")).isWrapText());
            scroll.setVvalue(0.25);
            root.resize(400, 500);
            root.layout();
            // JavaFX adjusts the offset as text reflows, but must not jump to the latest message.
            assertTrue(scroll.getVvalue() > 0);
            assertTrue(scroll.getVvalue() < 0.9);
            scroll.setVvalue(0.5);
            double overflow = dialogs.getHeight() - scroll.getViewportBounds().getHeight();
            scroll.fireEvent(createScroll(40));
            assertEquals(0.5 - 60 / overflow, scroll.getVvalue(), 0.001);
            scroll.fireEvent(createScroll(-40));
            assertEquals(0.5, scroll.getVvalue(), 0.001);
            scroll.fireEvent(createScroll(100000));
            assertEquals(0, scroll.getVvalue());
            scroll.fireEvent(createScroll(-100000));
            assertEquals(1, scroll.getVvalue());
            input.setText("find missing");
            send.fire();
            root.layout();
            root.resize(800, 600);
            root.layout();
            VBox shortMessage = (VBox) dialogs.getChildren().getLast().lookup("#message");
            assertTrue(shortMessage.getWidth() < dialogs.getWidth() * 0.94);
            input.setText("delete 2");
            send.fire();
            input.setText("delete 1");
            send.fire();
            assertTrue(emptyState.isVisible());
            assertTrue(emptyState.isManaged());
            timer.getOnFinished().handle(new ActionEvent());
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT,
                    false, false, false, false));
            assertFalse(sleepIndicator.isVisible());
            input.setText("bye");
            send.fire();
            assertTrue(input.isDisabled());
            assertTrue(send.isDisabled());
            assertTrue(sleepIndicator.isVisible());
            return null;
        });
        Platform.runLater(check);
        check.get(20, TimeUnit.SECONDS);
    }

    private static ScrollEvent createScroll(double deltaY) {
        return new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0,
                false, false, false, false, false, false, 0, deltaY, 0, deltaY,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                ScrollEvent.VerticalTextScrollUnits.NONE, 0, 0, null);
    }
}
