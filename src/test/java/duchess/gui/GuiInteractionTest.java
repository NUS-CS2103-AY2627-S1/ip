package duchess.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import duchess.Duchess;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Exercises the real FXML controls on machines with an available graphical display. */
@EnabledIfSystemProperty(named = "duchess.guiTests", matches = "true")
public class GuiInteractionTest {
    /** Verifies empty Enter repeats, wrapped submission, search feedback, and readable layout. */
    @Test
    public void conversation_feedbackRegressions_remainUsable() throws Exception {
        CompletableFuture<Void> result = new CompletableFuture<>();
        Platform.startup(() -> {
            Stage stage = new Stage();
            try {
                Composer composer = new Composer();
                TextArea input = (TextArea) composer.lookup("#userInput");
                Button send = (Button) composer.lookup("#sendButton");
                AtomicInteger submissions = new AtomicInteger();
                composer.setSubmitAction(() -> {
                    submissions.incrementAndGet();
                    composer.clearInput();
                });
                for (int index = 0; index < 500; index++) {
                    input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER,
                            false, false, false, false));
                }
                assertEquals(0, submissions.get());
                assertTrue(send.isDisabled());
                composer.prepareInput("   ");
                send.fire();
                assertEquals(0, submissions.get());
                composer.prepareInput("todo a long description\ncontinued here");
                assertTrue(input.isWrapText());
                assertEquals("todo a long description continued here", composer.getInput());
                input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER,
                        false, false, false, false));
                assertEquals(1, submissions.get());
                assertEquals("", composer.getInput());
                composer.setInputDisabled(true);
                assertTrue(send.isDisabled());

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
                VBox root = loader.load();
                MainWindow controller = loader.getController();
                controller.setDuchess(new Duchess());
                Scene scene = new Scene(root, 720, 780);
                scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
                TextArea command = (TextArea) root.lookup("#userInput");
                Button submit = (Button) root.lookup("#sendButton");
                QuickCommands shortcuts = (QuickCommands) root.lookup("#quickCommands");
                Set<String> commands = shortcuts.lookupAll(".quick-button").stream()
                        .map(node -> (String) node.getUserData()).collect(Collectors.toSet());
                assertEquals(Set.of("todo", "deadline", "event", "list", "find", "mark", "unmark",
                        "delete", "stats", "help", "bye"), commands);
                for (var node : shortcuts.lookupAll(".quick-button")) {
                    Button shortcut = (Button) node;
                    String name = (String) shortcut.getUserData();
                    if (!Set.of("list", "stats", "help", "bye").contains(name)) {
                        shortcut.fire();
                        assertEquals(name + " ", command.getText());
                    }
                }
                command.setText("todo prepare demo slides");
                submit.fire();
                command.setText("deadline submit proposal /by 2026-09-18");
                submit.fire();
                root.applyCss();
                root.layout();
                savePreview(root);
                for (var node : shortcuts.lookupAll(".quick-button")) {
                    if (Set.of("list", "stats", "help").contains(node.getUserData())) {
                        int messageCount = root.lookupAll(".dialog-label").size();
                        ((Button) node).fire();
                        assertEquals(messageCount + 2, root.lookupAll(".dialog-label").size());
                        assertEquals("", command.getText());
                    }
                }
                command.setText("find nonexistent-keyword");
                submit.fire();
                assertTrue(root.lookupAll(".dialog-label").stream()
                        .anyMatch(node -> ((Label) node).getText().startsWith("No matching tasks found.")));
                command.setText("todo " + "long description ".repeat(30));
                submit.fire();
                root.applyCss();
                root.layout();
                assertFalse(command.isDisabled());
                assertTrue(command.getWidth() > 100);
                stage.setWidth(1200);
                root.resize(1200, 780);
                root.applyCss();
                root.layout();
                assertTrue(root.lookup("#conversationView").getLayoutBounds().getWidth() <= 880);
                stage.setWidth(420);
                root.resize(420, 560);
                root.applyCss();
                root.layout();
                assertTrue(command.getWidth() > 100);
                assertTrue(root.lookup("#conversationView").getLayoutBounds().getHeight() > 80);
                for (var node : shortcuts.lookupAll(".quick-button")) {
                    assertTrue(node.getBoundsInParent().getMaxX() <= shortcuts.getWidth());
                    assertTrue(node.getBoundsInParent().getMaxY() <= shortcuts.getHeight());
                }
                for (var node : shortcuts.lookupAll(".quick-button")) {
                    if ("bye".equals(node.getUserData())) {
                        ((Button) node).fire();
                    }
                }
                assertTrue(command.isDisabled());
                assertTrue(stage.isShowing());
                result.complete(null);
            } catch (Throwable failure) {
                result.completeExceptionally(failure);
            } finally {
                stage.close();
                Platform.exit();
            }
        });
        result.get(20, TimeUnit.SECONDS);
    }

    /** Saves the actual rendered JavaFX scene for visual inspection after the test. */
    private static void savePreview(VBox root) throws IOException {
        WritableImage snapshot = root.snapshot(null, null);
        BufferedImage image = new BufferedImage((int) snapshot.getWidth(), (int) snapshot.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, snapshot.getPixelReader().getArgb(x, y));
            }
        }
        ImageIO.write(image, "png", new File(System.getProperty("duchess.preview")));
    }
}
