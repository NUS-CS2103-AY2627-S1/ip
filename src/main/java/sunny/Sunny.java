package sunny;

import java.util.Scanner;

import sunny.taskboard.Taskboard;
import sunny.ui.UI;
import sunny.utility.UserParser;
import sunnyexception.IncorrectDateFormatException;
import sunnyexception.SunnyException;
import sunnyexception.TaskEmptyDescException;
import sunnyexception.UnrecognisedTaskException;
import sunnyexception.InsufficientInfoException;
import sunnyexception.TaskOutOfBoundsException;
import sunnyexception.TooManyKeywordsException;
import sunnyexception.TooManyTasksException;

/**
 * The main class that begins the program
 */
public class Sunny {
        private UserParser parser = UserParser.getInstance();
        private UI ui = UI.getInstance();
        private SunnyVoice sunnyVoice = SunnyVoice.getInstance();

        public Sunny() {
                Taskboard.getInstance().load();
        }

        /**
         * Returns the response for a greeting upon running the chatbot.
         *
         * @return the greeting to be returned.
         */
        public String getGreeting(){
                return sunnyVoice.getText("greetings");
        }

        /**
         * Returns the text needed for the window title.
         *
         * @return the window's title.
         */
        public String getTitle() { return sunnyVoice.getText("titles"); }

        /**
         * The main method the application uses to get the chatbot's response.
         *
         * @param input the user's input as a String.
         * @return the response the chatbot would give.
         */
        public String getResponse(String input) {
                try {
                        parser.parseUserInput(input, ui);
                        return ui.getLastResponse();
                } catch (UnrecognisedTaskException | TaskEmptyDescException | InsufficientInfoException |
                         TooManyTasksException | TaskOutOfBoundsException | TooManyKeywordsException |
                         IncorrectDateFormatException e) {
                        ui.setLastResponse(e.getMessage());
                        return ui.getLastResponse();
                } catch (NumberFormatException e) { //if something other than an integer was used, or the integer is too large/small
                        ui.setLastResponse(sunnyVoice.getException("notInteger"));
                        parser.setCommandType("error");
                        return ui.getLastResponse();
                } catch (SunnyException e) { //no catch for out of bounds to see if code was the issue rather than user
                        parser.setCommandType("error");
                        throw new RuntimeException(e);
                }
        }

        /**
         * Returns the command type from the user's input, give by the parser
         *
         * @return the command type as a String.
         */
        public String getCommandType() {
                if (parser.getCommandType() == null) {
                        return "error";
                }
                return parser.getCommandType();
        }
        /**
         * Runs the main program in CLI
         *
         * @param args command-line arguments passed to the program
         */
        public static void main(String[] args) {
                Taskboard taskboard = Taskboard.getInstance();
                UI ui = UI.getInstance();
                SunnyVoice sunnyVoice = SunnyVoice.getInstance();
                Scanner scanner = new Scanner(System.in);

                taskboard.load();

                System.out.println("____________________________________________________________");
                sunnyVoice.speak("greetings");
                System.out.println("____________________________________________________________");

                ui.run(scanner);
                scanner.close();
    }
}
