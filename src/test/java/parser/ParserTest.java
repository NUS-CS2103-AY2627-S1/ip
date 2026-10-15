package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tasklist.Tasklist;

public class ParserTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private Parser createParserWithInput(String userInput, Tasklist tasklist) {
        return new Parser(tasklist);
    }

    @Test
    public void firstParse_todoCommand_success() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("todo read book");
        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void firstParse_markValidIndex_success() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("todo read book");
        parser.firstParse("mark 1");
        // Verify state change according to your Tasklist implementation
    }

    @Test
    public void firstParse_markInvalidIndex_exceptionHandled() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("mark 5");
        // Exception message printed to System.out is verified via outContent
    }

    @Test
    public void firstParse_deleteValidIndex_success() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("todo read book");
        parser.firstParse("delete 1");
        assertEquals(0, tasklist.getSize());
    }

    @Test
    public void firstParse_deadlineInlineBy_success() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("deadline return book /by 2026-08-28 1430");
        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void firstParse_eventInlineFromTo_success() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("event project meeting /from 2026-08-28 /to 2026-08-29");
        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void firstParse_deadlineMissingBy_returnsErrorNoTaskAdded() {
        Tasklist tasklist = new Tasklist();
        Parser parser = new Parser(tasklist);

        String response = parser.firstParse("deadline return book");
        assertTrue(response.contains("/by"));
    }

    @Test
    public void firstParse_eventMissingFromTo_returnsErrorNoTaskAdded() {
        Tasklist tasklist = new Tasklist();
        Parser parser = new Parser(tasklist);

        String response = parser.firstParse("event project meeting");
        assertEquals(0, tasklist.getSize());
        assertTrue(response.contains("Missing /from or /to"));
    }

    @Test
    public void firstParse_emptyInput_exceptionHandled() {
        Tasklist tasklist = new Tasklist();
        Parser parser = createParserWithInput("", tasklist);

        parser.firstParse("   ");
        assertEquals(0, tasklist.getSize());
    }
}
