package judey;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void startupErrorContent_providesRecoveryGuidance() {
        assertTrue(Main.startupErrorTitle().contains("Judey"));
        assertTrue(Main.startupErrorContent().contains("restart"));
    }
}
