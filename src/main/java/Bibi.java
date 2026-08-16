import java.util.Scanner;

public class Bibi {
    public static void main(String[] args) {
        String banner = "B B B B    i    b b b    i\n"
                + "B       B       b       b\n"
                + "B B B B   iii   b b b b  iii\n"
                + "B       B  i    b       b  i\n"
                + "B B B B  iii   b b b b  iii\n"
                + "Greetings Comrade! I'm Bibi, your ever present bot friend\n"
                + "What can I do for you today? Type bye to exit \n";
        String exit = "Goodbye! Till next time...";
        System.out.println(banner);

        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("You: ");
            String input = scan.nextLine();

            if (input.equals("bye") || input.equals("bye ")) {
                System.out.println(exit);
                break;
            }

            else {
                System.out.println(input);
            }
        }

        
        


    }
}
