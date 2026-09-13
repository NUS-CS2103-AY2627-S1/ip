package nelson.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests routing of normal and error messages to JavaFX callbacks. */
public class GuiUiTest {
    @Test
    public void show_multipleMessages_routesEachNormalMessage() {
        ArrayList<String> normalMessages = new ArrayList<>();
        ArrayList<String> errorMessages = new ArrayList<>();
        GuiUi ui = new GuiUi(normalMessages::add, errorMessages::add);

        ui.show("first", "second");

        assertEquals(List.of("first", "second"), normalMessages);
        assertEquals(List.of(), errorMessages);
    }

    @Test
    public void showError_errorMessage_routesOnlyToErrorCallback() {
        ArrayList<String> normalMessages = new ArrayList<>();
        ArrayList<String> errorMessages = new ArrayList<>();
        GuiUi ui = new GuiUi(normalMessages::add, errorMessages::add);

        ui.showError("invalid move");

        assertEquals(List.of(), normalMessages);
        assertEquals(List.of("invalid move"), errorMessages);
    }
}
