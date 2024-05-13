import Model.NumberleModel;

import java.util.ArrayList;
import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        NumberleModel game = new NumberleModel();
        Scanner sc=new Scanner(System.in);
        String input;
        ArrayList<Character>[] classList;

        game.setDisplayEquation(true);
        game.setVerifyEquation(true);

        System.out.println("Type 'S(tart)' to Start the Game");
        System.out.println("Type 'Q(uit)' to quit");
        input = sc.nextLine();
        while (!(input.equals("quit") || input.equals("Quit") || input.equals("q") || input.equals("Q"))){
            if (input.equals("start") || input.equals("Start") || input.equals("s") || input.equals("S")) {
                game.startNewGame();
                //game.setTarget("7=4*2-1");
                while (!game.isGameOver()) {
                    System.out.print("Please input your guess:");
                    input = sc.nextLine();
                    //System.out.println(input);
                    if ((input.equals("new") || input.equals("New") || input.equals("n") || input.equals("N"))) {
                        game.startNewGame();
                        System.out.print("Please input your guess:");
                        input = sc.nextLine();
                    }
                    if ((input.equals("quit") || input.equals("Quit") || input.equals("q") || input.equals("Q")))
                        break;
                    game.processInput(input);
                    classList = game.getClassList();
                    System.out.println("Guessed. Not in the target:" + classList[0].toString());
                    System.out.println("Guessed. Hit:" + classList[1].toString());
                    System.out.println("Guessed. In the target but missed:" + classList[2].toString());
                    System.out.println("Unguessed:" + classList[3].toString());
                }
                if (!game.isGameWon())
                    System.out.println("You LOSE.");
            } else {
                System.out.println("Unknown command! Please check your input");
            }
            System.out.println("Type 'S(tart)' to Start the Game");
            System.out.println("Type 'Q(uit)' to quit");
            input = sc.nextLine();
        }
        System.out.println("You quit the game.");
    }
}
