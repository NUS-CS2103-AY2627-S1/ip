package tungtung;

/** Handles console output for Tung Tung. */
public class Ui {
    /** Displays the standard divider. */
    public void printDivider() {
        System.out.println("_____________________________________________________________");
    }

    /** Displays the application greeting. */
    public void showGreeting() {
        printDivider();
        TungTung.printGreetingBanner();
        printDivider();
    }

    /** Displays the application farewell. */
    public void showFarewell() {
        printDivider();
        System.out.println("Bye! Tung Tung Sagone!");
        printDivider();
    }
}
