package gongrilla.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.Gongrilla;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Verifies that failed commands are highlighted without affecting later successful replies. */
class DialogBoxTest {
    @TempDir
    private Path temporaryDirectory;

    @BeforeAll
    static void startToolkit() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(10, TimeUnit.SECONDS));
    }

    @Test
    void mainWindow_bio_isCollapsedBeforeMessagesAndExpands() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            MainWindow window = loader.getController();
            window.setGongrilla(new Gongrilla(temporaryDirectory.resolve("bio.txt")));
            new Scene(root);
            root.resize(400, 600);
            root.applyCss();
            root.layout();
            VBox messages = (VBox) root.lookup("#dialogContainer");
            TitledPane bio = (TitledPane) messages.getChildren().get(0);
            assertEquals("About Gongrilla", bio.getText());
            assertFalse(bio.isExpanded());
            assertEquals(2, messages.getChildren().size());
            double collapsedHeight = bio.getHeight();
            bio.setExpanded(true);
            root.layout();
            assertTrue(bio.getHeight() > collapsedHeight);
            VBox paragraphs = (VBox) bio.getContent();
            assertEquals(9, paragraphs.getChildren().size());
            assertEquals("neigh.", ((Label) paragraphs.getChildren().get(8)).getText());
            return null;
        });
        Platform.runLater(test);
        test.get(15, TimeUnit.SECONDS);
    }

    @Test
    void getGongrillaDialog_help_expandsSectionsAndWrapsAtWindowWidth() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("help.txt"));
            DialogBox box = DialogBox.getGongrillaDialog(gongrilla.getResponse("help"), null, "HelpCommand");
            VBox root = new VBox(box);
            new Scene(root);
            root.resize(370, 900);
            root.applyCss();
            root.layout();
            Accordion accordion = (Accordion) box.lookup(".accordion");
            assertEquals(Set.of("Start here", "Add tasks", "Update tasks", "Dates:", "Examples:"),
                    accordion.getPanes().stream().map(TitledPane::getText).collect(Collectors.toSet()));
            TitledPane initialSection = accordion.getExpandedPane();
            assertEquals("Start here", initialSection.getText());
            TitledPane examples = accordion.getPanes().stream()
                    .filter(pane -> "Examples:".equals(pane.getText()))
                    .findFirst().orElseThrow(() -> new AssertionError("Missing Examples section"));
            accordion.setExpandedPane(examples);
            root.layout();
            assertTrue(examples.isExpanded());
            assertFalse(initialSection.isExpanded());
            VBox rows = (VBox) examples.getContent();
            Label example = rows.getChildren().stream()
                    .filter(Label.class::isInstance).map(Label.class::cast)
                    .filter(label -> label.getText().startsWith("event lunch"))
                    .findFirst().orElseThrow(() -> new AssertionError("Missing event example"));
            assertTrue(accordion.getWidth() <= 370);
            root.resize(760, 900);
            root.layout();
            return null;
        });
        Platform.runLater(test);
        test.get(15, TimeUnit.SECONDS);
    }

    @Test
    void getGongrillaDialog_invalidThenValidCommand_highlightsOnlyError() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("tasks.txt"));
            String error = gongrilla.getResponse("unknown command");
            DialogBox errorBox = DialogBox.getGongrillaDialog(error, null, gongrilla.getCommandType());
            Label errorLabel = (Label) errorBox.lookup("#dialog");
            assertTrue(errorLabel.getStyleClass().contains("error-label"));
            assertEquals("ERROR\n" + error, errorLabel.getText());

            String success = gongrilla.getResponse("todo read book");
            DialogBox successBox = DialogBox.getGongrillaDialog(success, null, gongrilla.getCommandType());
            Label successLabel = (Label) successBox.lookup("#dialog");
            assertFalse(successLabel.getStyleClass().contains("error-label"));
            assertTrue(successLabel.getStyleClass().contains("add-label"));
            assertEquals(success, successLabel.getText());
            return null;
        });
        Platform.runLater(test);
        test.get(15, TimeUnit.SECONDS);
    }
}
