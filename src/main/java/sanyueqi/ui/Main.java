package sanyueqi.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A GUI for Syq using FXML.
 */
public class Main extends Application {

    private SanYueQi syq = new SanYueQi();

    @Override
    public void start(Stage stage) {
        try {
            Font.loadFont(
                    MainWindow.class.getResourceAsStream("/fonts/Felleria.ttf"),
                    14
            );

            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane ap = fxmlLoader.load();

            Scene scene = new Scene(ap);

            stage.setTitle("SanYueQi");

            stage.setResizable(false);

            stage.setScene(scene);

            fxmlLoader.<MainWindow>getController().setSyq(syq);

            stage.show();

            fxmlLoader.<MainWindow>getController().makeInitResponseStatusDialog();

            fxmlLoader.<MainWindow>getController().makeFinalResponseStatusDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}