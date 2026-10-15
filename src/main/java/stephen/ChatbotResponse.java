package stephen;

/** Contains the chatbot's response text and the state needed to present it in an interface. */
public record ChatbotResponse(String message, boolean isExit, boolean isError) {
}
