package sallman.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sallman.Sallman;

/**
 * The JavaFX application that puts a window around the chatbot.
 * <p>
 * Based on the SE-EDU JavaFX tutorial (https://se-education.org/guides/tutorials/javaFx.html),
 * extended here with the window's minimum size, icon and a clean exit when its
 * layout cannot be loaded.
 */
public class Main extends Application {

    /**
     * Builds the saLLMan window from its layout, gives it its title, icon and
     * minimum size, connects it to the chatbot, and shows it.
     * <p>
     * The chatbot keeps its tasks in the file named on the command line, if
     * any, just as the console version does; it is told not to print to the
     * console, since the window shows its replies instead.
     * <p>
     * If the layout cannot be loaded there is nothing to show, so the reason is
     * printed and JavaFX is shut down rather than left running without a window.
     *
     * @param stage the window JavaFX provides for the application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("saLLMan");
            // The sparkle is the same icon the chatbot's replies carry, so the
            // taskbar entry is recognizably the same assistant.
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/sallman.png")));

            // Below roughly this size the layout stops making sense: the input
            // row and a line or two of conversation need the height, and the input
            // box needs room beside the Send button to show what is typed.
            stage.setMinHeight(240);
            stage.setMinWidth(320);

            Sallman sallman = new Sallman(Sallman.dataPathFrom(getParameters().getRaw()), false);
            fxmlLoader.<MainWindow>getController().setSallman(sallman);
            stage.show();
        } catch (IOException e) {
            // Without its layout there is no window to show anything in, and
            // JavaFX would otherwise keep running with nothing on screen.
            System.err.println("saLLMan could not load its window: " + e.getMessage());
            Platform.exit();
        }
    }
}
