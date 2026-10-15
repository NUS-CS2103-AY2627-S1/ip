package sunny;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SunnyVoiceTest {
    private SunnyVoice sunnyVoice = SunnyVoice.getInstance();
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
    void getExceptionWithInvalidPoolReturnsNull() {
        SunnyVoice voice = SunnyVoice.getInstance();

        assertNull(voice.getException("invalidPool"));
    }

    @Test
    void getExceptionTooManyReturnsValidMessage() {
        SunnyVoice voice = SunnyVoice.getInstance();

        List<String> expected = List.of("Your taskboard can only hold so many!",
                "I can't add any more!",
                "Go get a bigger taskboard!");

        String result = voice.getException("tooManyRemarks");

        assertTrue(expected.contains(result));
    }

    @Test
    void speakGreetingsPrintsValidGreeting() {
        SunnyVoice voice = SunnyVoice.getInstance();
        List<String> expected = List.of("Yeah, it's me, Sunny.\nWhat do you want?", "What now?\nCan you go bother someone else?");

        voice.speak("greetings");

        assertTrue(expected.contains(output.toString().trim()));
    }

    @Test
    void speakListNumPrintsValidRemark() {
        SunnyVoice voice = SunnyVoice.getInstance();

        List<String> remarks = voice.getListException("listNumberRemarks");
        System.out.println(remarks.get(0) + 2 + remarks.get(1));

        String result = output.toString().trim();

        List<String> expected = List.of("Now you have 2 tasks in your list. Whoop de doo.",
                "Go do your 2 tasks already!");

        assertTrue(expected.contains(result));
    }

    @Test
    void speakListNumInvalidPoolPrintsNothing() {
        SunnyVoice voice = SunnyVoice.getInstance();

        List<String> remarks = voice.getListException("listNumberRemarks");
        System.out.println(remarks.get(0) + 2 + remarks.get(1));

        assertTrue(output.toString().isEmpty());
    }
}
