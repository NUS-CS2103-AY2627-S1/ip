package sunny;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SunnyTest {

    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();

        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void mainPrintsGreeting() {
        String input = "bye\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Sunny.main(new String[]{});

        List<String> expected = List.of("Yeah, it's me, Sunny.\nWhat do you want?", "What now?\nCan you go bother someone else?");

        String result = output.toString();

        assertTrue(result.contains("____________________________________________________________"));
        assertTrue(expected.stream().anyMatch(result::contains));
    }
}
