package luckynoslacky.luckyui;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads documented CLI scenarios as Java test fixtures.
 */
final class CliTestCaseFixtures {
    private static final Path TEST_PLAN =
            Path.of("test", "ui-test-plan.md");
    private static final String TEXT_FENCE = "```text";
    private static final String FENCE = "```";

    private CliTestCaseFixtures() {
        // Prevent instantiation of this utility class.
    }

    /** Loads every CLI test case from the Markdown test plan. */
    static List<CliTestCase> load() {
        try {
            return parse(Files.readAllLines(TEST_PLAN));
        } catch (IOException exception) {
            fail("Unable to read CLI test plan: " + exception.getMessage());
            return List.of();
        }
    }

    /** Parses test-case input and expected-output code blocks. */
    private static List<CliTestCase> parse(List<String> lines) {
        List<CliTestCase> cases = new ArrayList<>();
        String caseName = null;
        String input = null;
        String expectedOutput = null;
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.startsWith("## Test Case: ")) {
                if (caseName != null) {
                    cases.add(createCase(caseName, input, expectedOutput));
                }
                caseName = line.substring("## Test Case: ".length());
                input = null;
                expectedOutput = null;
            } else if (line.equals("### Input")) {
                input = readCodeBlock(lines, ++index, TEXT_FENCE);
                index = findClosingFence(lines, index);
            } else if (line.equals("### Expected output")) {
                expectedOutput = readCodeBlock(lines, ++index, TEXT_FENCE);
                index = findClosingFence(lines, index);
            }
        }
        if (caseName != null) {
            cases.add(createCase(caseName, input, expectedOutput));
        }
        return cases;
    }

    /** Reads the contents of a fenced Markdown code block. */
    private static String readCodeBlock(
            List<String> lines,
            int openingFenceIndex,
            String openingFence) {
        while (openingFenceIndex < lines.size()
                && lines.get(openingFenceIndex).isBlank()) {
            openingFenceIndex++;
        }
        if (openingFenceIndex >= lines.size()
                || !lines.get(openingFenceIndex).equals(openingFence)) {
            fail("Missing " + openingFence + " for CLI test plan block.");
        }
        List<String> contents = new ArrayList<>();
        for (int index = openingFenceIndex + 1; index < lines.size(); index++) {
            if (lines.get(index).equals(FENCE)) {
                return String.join("\n", contents);
            }
            contents.add(lines.get(index));
        }
        fail("Unclosed code block in CLI test plan.");
        return "";
    }

    /** Finds the closing fence corresponding to a code block. */
    private static int findClosingFence(List<String> lines, int startIndex) {
        for (int index = startIndex + 1; index < lines.size(); index++) {
            if (lines.get(index).equals(FENCE)) {
                return index;
            }
        }
        fail("Unclosed code block in CLI test plan.");
        return lines.size();
    }

    /** Creates and validates one fixture. */
    private static CliTestCase createCase(
            String name,
            String input,
            String expectedOutput) {
        if (input == null || expectedOutput == null) {
            fail("Incomplete CLI test plan case: " + name);
        }
        return new CliTestCase(
                name,
                TEST_PLAN + " - Test Case: " + name,
                input + "\n",
                expectedOutput);
    }
}
