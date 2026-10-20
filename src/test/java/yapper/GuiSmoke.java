package yapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Exercises the actual FXML window on a desktop and captures a documentation image.
 */
public final class GuiSmoke {
    private GuiSmoke() {
    }

    /**
     * Tests both input paths with isolated storage, then saves a full-scene screenshot.
     *
     * @param args Unused.
     * @throws Exception If the GUI fails to launch, respond, or finish.
     */
    public static void main(String[] args) throws Exception {
        Path storage = Files.createTempDirectory("yapper-gui-").resolve("tasks.txt");
        System.setProperty("yapper.data", storage.toString());
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> {
            Stage stage = new Stage();
            try {
                new Main().start(stage);
                TextField input = (TextField) stage.getScene().lookup("#commandInput");
                Button send = (Button) stage.getScene().lookup("#sendButton");
                VBox messages = (VBox) stage.getScene().lookup("#messages");
                int initial = messages.getChildren().size();
                input.setText("todo read a book");
                input.fireEvent(new ActionEvent());
                input.setText("list");
                send.fire();
                if (messages.getChildren().size() != initial + 4 || !Files.exists(storage)) {
                    throw new AssertionError("Enter/send did not produce responses and save the task.");
                }
                HBox lastRow = (HBox) messages.getChildren().get(messages.getChildren().size() - 1);
                VBox lastBubble = (VBox) lastRow.getChildren().get(0);
                Label lastResponse = (Label) lastBubble.getChildren().get(1);
                if (!lastResponse.getText().contains("[T][ ] read a book")
                        || !Files.readString(storage).contains("read a book")) {
                    throw new AssertionError("The list response or saved task is missing the submitted task.");
                }
                input.setText("   ");
                send.fire();
                if (messages.getChildren().size() != initial + 4) {
                    throw new AssertionError("Blank input created a message.");
                }
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                Files.createDirectories(Path.of("docs"));
                ImageIO.write(SwingFXUtils.fromFXImage(stage.getScene().snapshot(null), null),
                        "png", Path.of("docs/Ui.png").toFile());
                input.setText("bye");
                send.fire();
                if (!input.isDisabled() || !send.isDisabled()) {
                    throw new AssertionError("The goodbye command did not finish the conversation.");
                }
            } catch (Throwable exception) {
                failure.set(exception);
            } finally {
                stage.close();
                finished.countDown();
            }
        });
        if (!finished.await(30, TimeUnit.SECONDS)) {
            throw new AssertionError("GUI smoke test timed out.");
        }
        Platform.exit();
        if (failure.get() != null) {
            throw new AssertionError("GUI smoke test failed.", failure.get());
        }
        System.out.println("PASS: FXML launch, Enter/send, blank input, persistence, goodbye, and scene snapshot.");
    }
}
