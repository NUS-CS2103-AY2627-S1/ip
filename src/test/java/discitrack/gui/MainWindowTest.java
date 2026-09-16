package discitrack.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.DisciTrack;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Window;

/**
 * Exercises the real FXML controller with isolated task data and captures layouts for review.
 */
public class MainWindowTest {
    @TempDir
    public Path tempDir;

    @Test
    public void desk_buttonsAndCommands_shareTaskState() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "JavaFX layout verification requires a display.");
        CompletableFuture<Void> completed = new CompletableFuture<>();
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1100, 760);
                DisciTrack app = new DisciTrack(tempDir.resolve("tasks.txt").toString());
                MainWindow controller = loader.getController();
                controller.setDisciTrack(app);
                TextField input = (TextField) root.lookup("#userInput");
                Button send = (Button) root.lookup("#sendButton");
                root.applyCss();
                root.layout();
                input.setText("todo");
                send.fire();
                assertFalse(root.lookup("#taskContainer").isVisible());
                assertFalse(root.lookup("#taskContainer").isManaged());
                input.setText("todo Review lecture notes");
                send.fire();
                assertTrue(app.getTasks().size() == 1);
                root.applyCss();
                root.layout();
                Button mark = root.lookupAll(".button").stream().filter(node -> node instanceof Button)
                        .map(node -> (Button) node).filter(button -> button.getText().equals("Mark done"))
                        .findFirst().orElseThrow();
                mark.fire();
                assertTrue(app.getTasks().getFirst().isDone());
                snapshot(root, "desk-completion");
                input.setText("unmark 1");
                send.fire();
                assertFalse(app.getTasks().getFirst().isDone());
                input.setText("nonsense");
                send.fire();
                assertTrue(app.hasResponseError());
                assertTrue(input.getText().equals("nonsense"));
                Button clear = (Button) root.lookup("#clearCommandButton");
                assertTrue(clear.isVisible());
                snapshot(root, "desk-clear-command");
                int responseCount = root.lookupAll(".response-card").size();
                clear.fire();
                assertTrue(input.getText().isEmpty());
                assertFalse(clear.isVisible());
                assertTrue(send.isDisabled());
                assertTrue(root.lookupAll(".response-card").size() == responseCount);
                assertTrue(app.getTasks().size() == 1);
                assertFalse(root.lookup("#taskContainer").isVisible());
                assertFalse(root.lookup("#taskContainer").isManaged());
                assertTrue(root.lookup(".error-card") != null);
                input.setText("list");
                send.fire();
                assertFalse(app.hasResponseError());
                assertTrue(root.lookup("#taskContainer").isVisible());
                assertTrue(new DisciTrack(tempDir.resolve("tasks.txt").toString()).getTasks().size() == 1);
                input.setText("todo Exercise");
                send.fire();
                assertTrue(root.lookupAll(".task-row").size() == 2);
                TextField search = (TextField) root.lookup("#searchInput");
                search.setText("EXERCISE");
                Button searchButton = (Button) root.lookup("#searchButton");
                searchButton.fire();
                assertTrue(root.lookupAll(".task-row").size() == 1);
                click(root, "Mark done");
                assertTrue(app.getTasks().get(1).isDone());
                assertFalse(app.getTasks().getFirst().isDone());
                assertTrue(search.getText().isEmpty());
                assertTrue(root.lookupAll(".task-row").size() == 1);
                assertTrue(root.lookupAll(".response-card").size() > 1);
                assertTrue(root.lookupAll(".user-command").stream().map(node -> ((Label) node).getText())
                        .anyMatch(text -> text.equals("todo Review lecture notes")));
                root.applyCss();
                root.layout();
                ScrollPane history = (ScrollPane) root.lookup("#scrollPane");
                assertTrue(history.getContent().getLayoutBounds().getHeight()
                        > history.getViewportBounds().getHeight());
                history.setVvalue(0);
                assertTrue(history.getVvalue() == 0);
                assertTrue(root.lookupAll(".task-board").size() == 1);
                click(root, "Unmark");
                assertFalse(app.getTasks().get(1).isDone());
                search.setText("exercise");
                searchButton.fire();
                click(root, "Tag");
                TextField tagInput = (TextField) root.lookup("#tagInput");
                tagInput.setText("#invalid");
                click(root, "Add tag");
                assertTrue(app.hasResponseError());
                assertTrue(root.lookup("#tagInput") == tagInput);
                assertTrue(root.lookup("#taskContainer").isVisible());
                assertTrue(tagInput.getText().equals("#invalid"));
                tagInput.setText("health");
                click(root, "Add tag");
                assertTrue(app.getTasks().get(1).getTags().contains("health"));
                search.setText("exercise");
                searchButton.fire();
                click(root, "Untag");
                click(root, "Remove tag");
                assertTrue(app.getTasks().get(1).getTags().isEmpty());
                search.setText("exercise");
                searchButton.fire();
                click(root, "Delete");
                assertTrue(app.getTasks().size() == 1);
                assertTrue(app.getTasks().getFirst().getActivity().equals("Review lecture notes"));
                assertTrue(root.lookupAll(".task-row").isEmpty());
                assertTrue(search.getText().isEmpty());
                click(root, "Show all");
                assertTrue(search.getText().isEmpty());
                assertTrue(root.lookupAll(".task-row").size() == 1);
                snapshot(root, "desk-wide");
                root.resize(660, 580);
                snapshot(root, "desk-compact");
                search.setText("missing");
                searchButton.fire();
                assertTrue(root.lookupAll(".task-row").isEmpty());
                input.setText("todo New task after search");
                send.fire();
                assertTrue(search.getText().isEmpty());
                assertTrue(root.lookupAll(".task-row").size() == 2);
                assertTrue(root.lookupAll(".response-card").size() > 1);
                input.setText("delete 2");
                send.fire();
                input.setText("delete 1");
                send.fire();
                click(root, "View tasks");
                assertTrue(app.getTasks().isEmpty());
                Label coach = (Label) root.lookup("#coachMessage");
                assertTrue(coach.getText().contains("Zero tasks"));
                assertTrue(root.lookupAll(".card-title").stream().map(node -> ((Label) node).getText())
                        .anyMatch(text -> text.contains("All clear")));
                snapshot(root, "desk-empty");
                assertTrue(scene.getRoot() == root);
                verifyInvalidForm(root, app);
                completed.complete(null);
            } catch (Throwable error) {
                completed.completeExceptionally(error);
            }
        });
        completed.get(30, TimeUnit.SECONDS);
    }

    private void verifyInvalidForm(Parent root, DisciTrack app) {
        CompletableFuture<Void> checked = new CompletableFuture<>();
        Platform.runLater(() -> {
            Window window = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElseThrow();
            Parent form = window.getScene().getRoot();
            try {
                ComboBox<?> type = (ComboBox<?>) form.lookup("#taskType");
                type.getSelectionModel().select(1);
                TextField description = (TextField) form.lookup("#taskDescription");
                TextField date = (TextField) form.lookup("#taskDate");
                description.setText("Keep my input");
                date.setText("2026-02-30");
                click(form, "Add task");
                assertTrue(window.isShowing());
                assertTrue(description.getText().equals("Keep my input"));
                assertTrue(date.getText().equals("2026-02-30"));
                assertTrue(form.lookup(".form-error").isVisible());
                assertTrue(app.getTasks().isEmpty());
                snapshot(form, "desk-form-error");
                date.setText("2026-09-30");
                click(form, "Add task");
                assertFalse(window.isShowing());
                assertTrue(app.getTasks().size() == 1);
                checked.complete(null);
            } catch (Throwable error) {
                checked.completeExceptionally(error);
                window.hide();
            }
        });
        click(root, "+ Add task");
        checked.join();
    }

    private void click(Parent root, String text) {
        root.applyCss();
        root.layout();
        Button button = root.lookupAll(".button").stream().filter(node -> node instanceof Button)
                .map(node -> (Button) node).filter(candidate -> candidate.getText().equals(text))
                .findFirst().orElseThrow();
        button.fire();
    }

    private void snapshot(Parent root, String name) throws Exception {
        root.applyCss();
        root.layout();
        ScrollPane scroll = (ScrollPane) root.lookup("#scrollPane");
        if (scroll != null) {
            scroll.setVvalue(1);
        }
        root.layout();
        WritableImage image = root.snapshot(null, null);
        BufferedImage output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                output.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }
        Path directory = Path.of("build", "gui-review");
        Files.createDirectories(directory);
        ImageIO.write(output, "png", directory.resolve(name + ".png").toFile());
    }
}
