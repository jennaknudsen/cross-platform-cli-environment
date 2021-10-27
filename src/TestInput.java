import java.util.Scanner;

/**
 * Test class that reads in a line from STDIN, and
 * then echoes that back to the user. When piped with
 * another program, it will instead read in the first
 * line of that program's output.
 * @author Jonas Knudsen
 */
public class TestInput {

    /**
     * Main function of this program.
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("Now inside TestInput program!");
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) {
            String s = scanner.nextLine();
            System.out.println("You said, \"" + s + "\"");
        }

        System.out.println("Done executing program.");
    }
}
