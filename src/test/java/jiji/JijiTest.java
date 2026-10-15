package jiji;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.gui.JijiResponse;

/**
 * Unit tests for {@link Jiji}.
 */
public class JijiTest {

    private static final String TEST_STORAGE_PATH = "data/test_jiji.txt";
    private Jiji jiji;

    @BeforeEach
    public void setUp() {
        File testFile = new File(TEST_STORAGE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
        jiji = new Jiji(TEST_STORAGE_PATH);
    }

    @AfterEach
    public void tearDown() {
        File testFile = new File(TEST_STORAGE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void getGreeting_returnsExpectedGreeting() {
        assertNotNull(jiji.getGreeting());
        assertTrue(jiji.getGreeting().contains("Jiji"));
    }

    @Test
    public void getResponseDetails_validCommand_returnsSuccessResponse() {
        JijiResponse response = jiji.getResponseDetails("todo read book");
        assertNotNull(response);
        assertFalse(response.isError());
        assertTrue(response.getMessage().contains("read book"));
    }

    @Test
    public void getResponseDetails_invalidCommand_returnsErrorResponse() {
        JijiResponse response = jiji.getResponseDetails("invalid_command_xyz");
        assertNotNull(response);
        assertTrue(response.isError());
        assertTrue(response.getMessage().contains("don't know what that means"));
    }

    @Test
    public void getResponseDetails_emptyInput_returnsErrorResponse() {
        JijiResponse response = jiji.getResponseDetails("   ");
        assertNotNull(response);
        assertTrue(response.isError());
        assertEquals("Please enter a valid command!", response.getMessage());
    }

    @Test
    public void getResponse_validCommand_returnsMatchingText() {
        String directResponse = jiji.getResponse("todo return book");
        assertTrue(directResponse.contains("return book"));
    }
}
