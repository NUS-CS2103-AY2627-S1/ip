import java.util.*;

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
        List<String> tasks = new ArrayList<>();

        while (true) {
            System.out.print("You: ");
            String input = scan.nextLine();

            if (input.equals("bye") || input.equals("bye ")) {
                System.out.println(exit);
                break;
            } else if (input.equals("list") || input.equals("list ")) {
                for (int i = 0; i < tasks.size(); i++) {
                    String s = (i + 1) + ". " + tasks.get(i); 
                    System.out.println(s);
                }
            } 
            else {
                System.out.println("added: " + input);
                tasks.add(input);
            }
        }
        scan.close();

        
        


    }
}
