package sanyueqi.ui;

public class SYQException extends Exception {
    String message;

    public SYQException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}