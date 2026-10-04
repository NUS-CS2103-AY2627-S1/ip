package unicorn.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void showWelcome_displaysPrismaProductName() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            new Ui().showWelcome("PRISMA");

            String welcomeMessage = output.toString(StandardCharsets.UTF_8);
            assertTrue(welcomeMessage.contains("Hello! I'm Prisma, your wise tech unicorn."));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
