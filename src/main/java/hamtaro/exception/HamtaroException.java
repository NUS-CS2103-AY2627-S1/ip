package hamtaro.exception;

/** Represents an error caused by an invalid user command. */
public class HamtaroException extends Exception{
    public HamtaroException(String message){
        super(message);
    }
}
