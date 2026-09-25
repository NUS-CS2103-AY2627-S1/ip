import exception.FriedbergException;
import friedberg.Friedberg;

/**
 * Entrypoint class for starting the Cli
 */
public class Cli {
    public static void main(String[] args) {
        try {
            Friedberg chatbot = new Friedberg();
            chatbot.run();
        } catch (FriedbergException e) {
            System.out.println(e.getMessage());
        }
    }

}
