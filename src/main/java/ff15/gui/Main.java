package ff15.gui;

import java.io.IOException;

import ff15.FF15;
import ff15.ai.LangChainAiHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application behind FF15's window. JavaFX hands it a stage to fill;
 * this class loads the window described in {@code MainWindow.fxml} onto that
 * stage and hands its controller a real {@link FF15} to talk to, so the window
 * drives the same chatbot the console session does. That chatbot is given a
 * real AI to talk to, which the console session goes without.
 */
public class Main extends Application {
    private final FF15 ff15 = new FF15(LangChainAiHelper.fromEnvironment());

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();

            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/michael-scott.png")));
            stage.setTitle("FF15 \u00b7 Assistant to the Regional Manager");
            // Free to grow; the floor keeps the composer and a few lines of chat usable.
            stage.setMinWidth(360);
            stage.setMinHeight(400);

            fxmlLoader.<MainWindow>getController().setFf15(ff15);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
