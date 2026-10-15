package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.TextContent;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Represents a request to leave the chatbot.
 */
public class LuckyNoByeCommand extends LuckyNoCommand {
    /**
     * Creates a command that requests termination of the chat loop.
     */
    public LuckyNoByeCommand() {
    }

    /**
     * Returns the chatbot's goodbye message.
     *
     * @return goodbye message
     */
    @Override
    protected ResponseContent executeContent() {
        return new TextContent(LuckyNoQuips.goodbye());
    }

    /**
     * Returns the neutral tone for the farewell response.
     *
     * @return neutral response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.NEUTRAL;
    }

    /**
     * Indicates that this command ends the chat loop.
     *
     * @return true because the bye command requests termination
     */
    @Override
    protected boolean shouldExit() {
        return true;
    }
}
