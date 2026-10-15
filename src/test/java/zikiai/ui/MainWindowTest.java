package zikiai.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import zikiai.Zikiai;
import zikiai.storage.Storage;

/**
 * Exercises the real FXML controllers on the JavaFX thread with isolated storage.
 */
@Tag("gui")
class MainWindowTest {
    @TempDir
    private Path directory;

    @BeforeAll
    static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    @Test
    void handleUserInput_sendAndEnter_updateDialogsAndPreserveState() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Zikiai bot = new Zikiai(new Storage(directory.resolve("tasks.txt")));
            loader.<MainWindow>getController().setZikiai(bot);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            try {
                TextField input = (TextField) root.lookup("#userInput");
                Button send = (Button) root.lookup("#sendButton");
                VBox dialogs = (VBox) root.lookup("#dialogContainer");
                assertEquals(1, dialogs.getChildren().size());
                send.fire();
                assertEquals(1, dialogs.getChildren().size());
                input.setText("todo read book");
                send.fire();
                assertEquals("", input.getText());
                assertEquals(3, dialogs.getChildren().size());
                HBox userDialog = (HBox) dialogs.getChildren().get(1);
                HBox zikiaiDialog = (HBox) dialogs.getChildren().get(2);
                assertTrue(userDialog.getStyleClass().contains("user-dialog"));
                assertFalse(userDialog.lookup("#displayPicture").isVisible());
                assertTrue(zikiaiDialog.getStyleClass().contains("zikiai-dialog"));
                assertTrue(zikiaiDialog.lookup("#displayPicture").isVisible());
                ImageView zikiaiAvatar = (ImageView) zikiaiDialog.lookup("#displayPicture");
                assertEquals(23.0, ((Circle) zikiaiAvatar.getClip()).getRadius());
                assertTrue(((Label) root.lookup("#commandHint")).getText().contains("priority 1 high"));
                input.setText("mark 99");
                input.fireEvent(new ActionEvent());
                assertTrue(lastReply(dialogs).contains("That task number does not exist."));
                assertFalse(input.isDisabled());
                input.setText("list");
                send.fire();
                assertEquals("Here are your tasks:\n1.[T][ ] read book", lastReply(dialogs));
                root.applyCss();
                root.layout();
                ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
                scroll.setVvalue(1.0);
                root.layout();
                saveSnapshot(root);
                input.setText("bye");
                send.fire();
                assertEquals("Okay, bye bye! See you again lah.", lastReply(dialogs));
                assertTrue(input.isDisabled());
                assertTrue(send.isDisabled());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(test);
        test.get(20, TimeUnit.SECONDS);
    }

    @Test
    void setZikiai_loadFailure_disablesControlsAndShowsError() throws Exception {
        Files.writeString(directory.resolve("tasks.txt"), "corrupt\n");
        FutureTask<Void> test = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setZikiai(new Zikiai(new Storage(directory.resolve("tasks.txt"))));
            new Scene(root);
            root.applyCss();
            root.layout();
            assertTrue(((TextField) root.lookup("#userInput")).isDisabled());
            assertTrue(((Button) root.lookup("#sendButton")).isDisabled());
            assertTrue(lastReply((VBox) root.lookup("#dialogContainer")).contains("line 1 is invalid"));
            return null;
        });
        Platform.runLater(test);
        test.get(20, TimeUnit.SECONDS);
    }

    private String lastReply(VBox dialogs) {
        return ((Label) dialogs.getChildren().getLast().lookup("#dialog")).getText();
    }

    private void saveSnapshot(AnchorPane root) throws Exception {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        Path file = Path.of(System.getProperty("gui.snapshot"));
        Files.createDirectories(file.getParent());
        ImageIO.write(image, "png", file.toFile());
    }
}
