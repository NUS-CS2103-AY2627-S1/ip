package nyonbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UiTest {
    @Test
    void showDate_validDateTime_returnsDisplayFormat() {
        assertEquals("05 Sep 2026 0907",
                Ui.showDate(LocalDateTime.of(2026, 9, 5, 9, 7)));
    }
}
