package sophon.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;

public class MainTest extends ApplicationTest {
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        new Main().start(stage);
    }

    @Test
    public void start_configuresResizableWindow() {
        assertEquals("Sophon · Deep Space Interface", stage.getTitle());
        assertTrue(stage.isResizable());
        assertEquals(620.0, stage.getMinHeight());
        assertEquals(460.0, stage.getMinWidth());
        assertTrue(stage.isShowing());
    }
}
