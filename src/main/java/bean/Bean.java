package bean;

import java.io.IOException;

import bean.command.CommandParser;
import bean.exception.BeanListOutOfBoundsException;
import bean.exception.InvalidSyntaxException;
import bean.exception.UnknownCommandException;
import bean.storage.MemoryHandler;
import bean.task.BeanList;
import bean.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Starts the Bean command-line task manager. */
public class Bean extends Application {
    private static final BeanList TASKS = new BeanList();
    private static final MemoryHandler MEMORY_HANDLER = new MemoryHandler("memory.txt");
    private static final Image WINDOW_ICON = new Image(
            Bean.class.getResourceAsStream("/images/BeanUser.jpg"));

    @Override
    public void start(Stage stage) {

        MEMORY_HANDLER.readMemory(TASKS);
        Parameters param = getParameters();

        initGui(stage, param);
    }

    /** Saves the current task list when the JavaFX application is closing. */
    @Override
    public void stop() {
        MEMORY_HANDLER.writeMemory(TASKS);
    }

    private static void initGui(Stage stage, Parameters param) {

        try {
            stage.initStyle(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(Bean.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setParams(param);
            controller.showIntroMessage();
            Scene scene = new Scene(ap);
            stage.setTitle("Bean");
            stage.getIcons().add(WINDOW_ICON);
            stage.setScene(scene);
            positionWindowOnScreen(stage);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start the Bean interface", e);
        }
    }

    /** Sizes and centers the window within the usable area of the primary screen. */
    private static void positionWindowOnScreen(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.sizeToScene();
        stage.setWidth(Math.min(stage.getWidth(), visualBounds.getWidth()));
        stage.setHeight(Math.min(stage.getHeight(), visualBounds.getHeight()));
        stage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - stage.getHeight()) / 2);
    }

    /**
     * Generates a response for the user's chat message.
     */
    public static String getResponse(String input) {
        try {
            return CommandParser.getCommand(input, TASKS);
        } catch (UnknownCommandException e) {
            return e.getMessage();
        } catch (BeanListOutOfBoundsException e) {
            return e.getMessage();
        } catch (InvalidSyntaxException e) {
            return e.getMessage();
        }
    }
}
