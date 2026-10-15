package luckynoslacky.luckyui;

/**
 * Represents one documented command-line acceptance-test scenario.
 *
 * @param name documented test-case name
 * @param markdownReference Markdown section containing the scenario
 * @param input commands supplied to the CLI
 * @param expectedOutput output expected from the CLI
 */
record CliTestCase(
        String name,
        String markdownReference,
        String input,
        String expectedOutput) {
}
