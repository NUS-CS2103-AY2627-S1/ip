package larry.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import larry.Larry;

/**
 * Checks actual FXML layout and scroll properties when a graphical display is available.
 */
@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class MainWindowTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void submit_blankInput_ignoredByEnterAndSend() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            new Scene(root);
            root.applyCss();
            MainWindow controller = loader.getController();
            controller.setLarry(new Larry(temporaryDirectory.resolve("tasks.txt")));
            TextField input = (TextField) root.lookup("#userInput");
            Button sendButton = (Button) root.lookup("#sendButton");
            VBox dialogs = (VBox) root.lookup("#dialogContainer");
            int initialCount = dialogs.getChildren().size();

            for (String blank : new String[]{"", "   ", "\t", "\u2003"}) {
                input.setText(blank);
                assertTrue(sendButton.isDisabled());
                input.fireEvent(new ActionEvent());
                sendButton.fire();
                assertEquals(initialCount, dialogs.getChildren().size());
            }
            input.setText("list");
            assertFalse(sendButton.isDisabled());
            sendButton.fire();
            assertEquals(initialCount + 2, dialogs.getChildren().size());
            assertTrue(sendButton.isDisabled());
            input.fireEvent(new ActionEvent());
            assertEquals(initialCount + 2, dialogs.getChildren().size());
            input.setText("list");
            input.setDisable(true);
            assertTrue(sendButton.isDisabled());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @BeforeAll
    static void startToolkit() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    void initialize_greetingButtonAndScrolling_remainUsableAfterLayout() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            AnchorPane root = FXMLLoader.load(MainWindow.class.getResource("/view/MainWindow.fxml"));
            new Scene(root);
            root.applyCss();
            VBox dialogs = (VBox) root.lookup("#dialogContainer");
            ScrollPane scrollPane = (ScrollPane) root.lookup("#scrollPane");
            Button sendButton = (Button) root.lookup("#sendButton");
            TextField input = (TextField) root.lookup("#userInput");
            Label greeting = (Label) dialogs.getChildren().getFirst().lookup("#dialog");
            assertTrue(greeting.getText().contains("todo read book"));
            assertTrue(greeting.getText().contains("list"));

            for (double width : new double[]{340.0, 460.0, 1200.0}) {
                root.resize(width, 620.0);
                root.layout();
                assertTrue(sendButton.getWidth() >= sendButton.prefWidth(-1));
                assertTrue(input.getBoundsInParent().getMaxX()
                        <= sendButton.getBoundsInParent().getMinX());
            }

            assertFalse(scrollPane.vvalueProperty().isBound());
            for (int i = 0; i < 30; i++) {
                dialogs.getChildren().add(new Label("A previous response " + i));
            }
            root.applyCss();
            root.layout();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            scrollPane.setVvalue(0.25);
            assertEquals(0.25, scrollPane.getVvalue());
            dialogs.getChildren().add(new Label("A new response"));
            root.layout();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue());
            scrollPane.setVvalue(0.5);
            assertEquals(0.5, scrollPane.getVvalue());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }
}
