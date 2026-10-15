package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import luckynoslacky.TestChatbotFactory;

/** Tests persistence across two GUI launches using isolated task data. */
@ExtendWith(ApplicationExtension.class)
class GuiPersistenceTest {
    @TempDir
    private Path temporaryDirectory;

    private Path dataFile;
    private Stage stage;

    /** Launches the GUI using a temporary persistence file. */
    @Start
    void start(Stage stage) throws IOException {
        this.stage = stage;
        dataFile = temporaryDirectory.resolve("tasks.csv");
        startGui(stage);
    }

    /** Verifies a task survives closing and relaunching the GUI. */
    @Test
    void luckyNoGui_taskSavedThenRelaunched_restoresTask(FxRobot robot) {
        robot.clickOn("#userInput")
                .write("todo persisted gui task")
                .push(KeyCode.ENTER);
        assertTrue(Files.exists(dataFile));

        robot.interact(() -> {
            stage.close();
            Stage relaunchedStage = new Stage();
            try {
                startGui(relaunchedStage);
                stage = relaunchedStage;
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Unable to relaunch the test window.", exception);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#userInput")
                .write("list")
                .push(KeyCode.ENTER);

        assertTrue(stage.getScene().getRoot()
                .lookupAll(".personality-task-title").stream()
                .map(node -> ((Label) node).getText())
                .anyMatch(text -> text.contains("persisted gui task")));
    }

    /** Starts a GUI instance backed by the test's isolated data file. */
    private void startGui(Stage targetStage) throws IOException {
        new LuckyNoGui(() -> TestChatbotFactory.createWithDataFile(dataFile))
                .start(targetStage);
    }
}
