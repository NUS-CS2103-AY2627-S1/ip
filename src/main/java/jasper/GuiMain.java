package jasper;

import java.io.IOException;

import jasper.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Serves as GUI entry point for the Jasper application.
 */
public class GuiMain extends Application {
    /** Main application logic instance */
    private final Jasper jasper = new Jasper("data", "jasper.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            // stage.setMaxWidth(417); // Add if elements not automatically resizable horizontally
            MainWindow controller = fxmlLoader.getController();
            controller.setJasper(jasper); // inject the Jasper instance
            stage.setTitle("Jasper");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
