package answerme.ui.gui;

import java.io.IOException;

import answerme.AnswerMe;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Configures and displays the primary AnswerMe window.
 */
public class Main extends Application {
    private AnswerMe answerMe = new AnswerMe(false);
    private Image botImage = new Image(this.getClass().getResourceAsStream("/images/AnswerMe.jpg"));

    /**
     * Creates and displays the GUI application window.
     *
     * @param stage the primary stage from JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("AnswerMe");
            stage.setMinHeight(420);
            stage.setMinWidth(420);
            stage.setWidth(550);
            stage.getIcons().add(botImage);

            MainWindow controller = fxmlLoader.getController();
            controller.setAnswerMe(answerMe);
            controller.showWelcome();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

