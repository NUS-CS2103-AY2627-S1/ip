package bingbong.exception;

/**
 * Custom exception class used to handle errors specific to the chatbot application safely.
 */

public class BingBongException extends Exception {

    public BingBongException(String message) {
        super(message);
    }
}
