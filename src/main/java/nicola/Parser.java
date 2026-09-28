package nicola;

/**
 * parse user input into command word and details
 */
public class Parser {

    /**
     * extract the command word out from an input.
     *
     * @param input the user's input
     * @return the command word which is the first word in the input
     */
    public String getCommandWord(String input) {
        assert input != null : "Input should not be null";

        if (input.contains(" ")) {
            return input.substring(0, input.indexOf(" "));
        }

        return input;
    }

    /**
     * extract the details out from the input.
     *
     * @param input the user's input
     * @return the details after the command word
     */
    public String getDetails(String input) {
        assert input != null : "Input should not be null";

        if (input.contains(" ")) {
            return input.substring(input.indexOf(" ") + 1).trim();
        }
        return " ";
    }
}
