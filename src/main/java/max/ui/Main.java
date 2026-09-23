package max.ui;

import java.io.IOException;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setMax(new Max());
            Scene scene = new Scene(ap);
            stage.setTitle("Max");
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(360);
            stage.setResizable(true);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
