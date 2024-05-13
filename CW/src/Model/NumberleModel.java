package Model;// Model.NumberleModel.java
import CustomClass.EquationGenerator;
import Model.Interface.INumberleModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The Model.NumberleModel class represents the model component in the MVC pattern.
 * It maintains the game state for the Numberle game, including the target number,
 * current guess, list of guesses, and comparison results.
 *
 * @invariant ("The target number must always be a valid equation or number.")
 *             targetNumber.matches("[0-9]+") || targetNumber.matches("valid equation regex");
 * @invariant ("The remaining attempts should be non-negative.")
 *             remainingAttempts >= 0;
 * @invariant ("The guessList and compareList should have the same size.")
 *             guessList.size() == compareList.size();
 * @invariant ("The game is won if and only if the currentGuess equals the targetNumber.")
 *             gameWon == targetNumber.equals(currentGuess.toString());
 */
public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber; // The target number or equation to guess
    private StringBuilder currentGuess; // The current guess input by the user
    private ArrayList<String> guessList; // List of all guesses made
    private ArrayList<int[]> compareList; // List of comparison results for each guess
    private ArrayList<Character>[] classList; // Used for storing character classifications
    private int remainingAttempts; // Number of attempts left for the user
    private boolean gameWon; // Indicates if the game has been won
    //@ requires newRandom != null;
    //@ ensures rand != null;
    private final Random rand = new Random(); // Random number generator

    //@ ensures verifyEquation == true;
    private boolean verifyEquation = false; // Flag to verify the correctness of equations

    //@ ensures displayEquation == false;
    private boolean displayEquation = false; // Flag to display the equation

    //@ ensures randomEquality == false;
    private boolean randomEquality = false; // Flag to use random equality in the target number

    //@ requires generator != null;
    //@ ensures (\result instanceof EquationGenerator);
    private final EquationGenerator generator = new EquationGenerator(); // Generates equations

    /**
     * Sets the flag to verify the correctness of the equation.
     *
     * @param verifyEquation The new value for the verifyEquation flag.
     * @pre The method can be called without a precondition.
     * @post The verifyEquation flag is updated to the value of the parameter.
     */
    public void setVerifyEquation(boolean verifyEquation) {
        assert remainingAttempts >= 0 : "Remaining attempts must be non-negative.";
        this.verifyEquation = verifyEquation;
    }

    /**
     * Sets the flag to display the equation.
     *
     * @param displayEquation The new value for the displayEquation flag.
     * @pre The method can be called without a precondition.
     * @post The displayEquation flag is updated to the value of the parameter.
     */
    public void setDisplayEquation(boolean displayEquation) {
        assert remainingAttempts >= 0 : "Remaining attempts must be non-negative.";
        this.displayEquation = displayEquation;
    }

    /**
     * Sets the flag to use random equality in the target number.
     *
     * @param randomEquality The new value for the randomEquality flag.
     * @pre The method can be called without a precondition.
     * @post The randomEquality flag is updated to the value of the parameter.
     */
    public void setRandomEquality(boolean randomEquality) {
        assert remainingAttempts >= 0 : "Remaining attempts must be non-negative.";
        this.randomEquality = randomEquality;
    }

    /**
     * Initializes the game state, including setting up the target number,
     * initializing the current guess, and preparing data structures.
     *
     * @pre The method can be called without a precondition.
     * @post guessList and compareList are empty.
     * @post classList is initialized with empty ArrayLists.
     * @post targetNumber is set based on the file or generated equation.
     * @post remainingAttempts is set to the maximum allowed attempts.
     * @post gameWon is false.
     */
    @Override
    public void initialize() {
        guessList = new ArrayList<>();
        compareList = new ArrayList<>();
        classList = new ArrayList[4];
        // Read the equation from the file.
        targetNumber = getEquationFromFile();
//        // This section of code is used to generate random equations and should be used as an alternative to the getEquationFromFile method.
//        if (randomEquality)
//            generator.generateEquation();
//        else {
//            if (targetNumber == null)
//                generator.generateEquation();
//        }
//        targetNumber = generator.getEquation();

        currentGuess = new StringBuilder("       ");
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;

        // The assertion list is initialized to empty
        assert guessList.isEmpty() : "guessList Should be empty";
        assert compareList.isEmpty() : "compareList Should be empty";

        for (int i = 0; i < 4; i ++) {
            classList[i] = new ArrayList<>();
            assert classList[i] != null : "The elements in the classList should be initialized";
        }

        // Asserts that targetNumber is not empty
        assert targetNumber != null : "targetNumber should not be empty";

        // Apply characters to classList[3]
        String a = "0123456789+-*/=";
        for (int i = 0; i < a.length(); i ++) {
            classList[3].add(a.charAt(i));
        }

        // Assert that remainingAttempts are set correctly
        assert remainingAttempts == MAX_ATTEMPTS : "remainingAttempts should be set to MAX_ATTEMPTS";
        // assert the game unwon
        assert !gameWon : "gameWon should be initialized to false";

        // Display the target equation if needed
        if (displayEquation)
            System.out.println("Target is:" + getTargetNumber());
        // Notifying the observer that the model has changed
        setChanged();
        notifyObservers();
    }

    /**
     * Processes the user input, updates the game state, and provides feedback.
     *
     * @param input The user's input (equation or number).
     * @requires ("The input must be a non-null and non-empty string representing a valid equation or number.")
     *           input != null && !input.isEmpty() && isValidEquationOrNumber(input);
     * @ensures ("The current guess is updated based on the input.")
     *          currentGuess.toString().equals(input);
     * @ensures ("guessList contains the input, formatted as needed.")
     *          guessList.contains(input.replaceAll("/", "÷").replaceAll("\\*", "×"));
     * @ensures ("compareList contains the comparison results for the guess.")
     *          compareList.size() == guessList.size();
     * @ensures ("remainingAttempts is decremented.")
     *          remainingAttempts < \old(remainingAttempts);
     * @ensures ("If the input matches the targetNumber, gameWon is set to true.")
     *          (input.replaceAll("÷", "/").replaceAll("×", "*").equals(targetNumber)) ==> gameWon;
     * @ensures ("If the input is invalid, appropriate messages are displayed and the observer(s) are notified of the state change.")
     *          // This is handled within the method body and is not directly represented in JML.
     * @return An integer code representing the result of processing the input:
     *         - 1: Correct guess
     *         - 2: Invalid input length
     *         - 3: Invalid input format (not a valid equation)
     *         - 4: Invalid equation (does not satisfy the equation rules)
     */
    @Override
    public int processInput(String input) {
        // Prerequisite assertion: The input should be a valid equation or number
        assert input != null : "Input should not be null";
        assert !input.isEmpty() : "The input should not be empty";

        if (verifyEquation) {// Check that the input length is correct
            if (input.length() != 7) {
                System.out.println("Invalid input length. Please try again.");
                setChanged();
                notifyObservers();
                return 2;
            }

            // Use regular expressions to check that the input conforms to the form of ordinary equations
            if (!input.replaceAll("÷","/").replaceAll("×","*").matches("^(?!\\b(\\d{1,3})\\b=\\1$)(\\d{1,3}[+\\-*/]){0,2}-?\\d{1,3}=(\\d{1,3}[+\\-*/]){0,2}-?\\d{1,3}$")) {
                System.out.println("Invalid input. Please enter a valid equation.");
                setChanged();
                notifyObservers();
                return 3;
            }

            // Check for validity
            if (checkEquation(input.replaceAll("÷","/").replaceAll("×","*"))) {
                System.out.println("The input equation is valid.");
            } else {
                System.out.println("The input equation is invalid.");
                return 4;
            }
        }
        remainingAttempts--;
        // Update current guesses
        currentGuess = new StringBuilder(input);
        guessList.add(input.replaceAll("/","÷").replaceAll("\\*","×"));

        // Match the target with the guessed character
        int[] compared = compareStrings(getTargetNumber(),input.replaceAll("÷","/").replaceAll("×","*"));
        compareList.add(compared);

        // Postcondition assertion
        assert remainingAttempts < MAX_ATTEMPTS : "The number of remaining attempts should be reduced";
        assert guessList.contains(input.replaceAll("/","÷").replaceAll("\\*","×")) : "The guessList should contain the input";
        assert compareList.size() == guessList.size() : "The size of compareList should be the same as that of guessList";
        assert currentGuess.toString().equals(input) : "currentGuess should be updated to the value entered";

        // Check if your guesses are correct
        if (input.replaceAll("÷","/").replaceAll("×","*").equals(targetNumber)) {
            gameWon = true;
            showHistory();
            System.out.println("Congratulations! You've guessed the right equality.");
        } else {
            System.out.println("Incorrect guess. Your match is:");
            if (displayEquation)
                System.out.println("Target is:" + getTargetNumber());
            showHistory();
            if (!isGameOver())
                System.out.print("Try again.");
            System.out.println("You have " + getRemainingAttempts() + " more chances.");
        }

        // If the guess is correct, the game-winning flag should be true
        assert !input.replaceAll("÷", "/").replaceAll("×", "*").equals(targetNumber) || gameWon : "If the input matches targetNumber, gameWon should be true";

        // Notifying the observer that the model has changed
        setChanged();
        notifyObservers();

        return 1;
    }

    /**
     * Displays the history of guesses and their comparison results.
     *
     * @pre The method can be called without a precondition.
     * @post The history of guesses and comparison results is printed to the console.
     */
    private void showHistory() {
        assert compareList != null : "The comparison list should not be null.";
        assert guessList != null : "The guess list should not be null.";

        for (int[] row : compareList) {
            assert row != null : "The row in the comparison list should not be null.";
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
        for (String row : guessList) {
            assert row != null : "The row in the guess list should not be null.";
            for (int i = 0; i < row.length(); i++) {
                System.out.print(row.charAt(i) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Checks if the given input string is a valid equation by comparing the evaluated results of both sides.
     *
     * @param input The input string containing the equation to check.
     * @requires ("The input must be a non-null and non-empty string representing an equation.")
     *           input != null && !input.isEmpty();
     * @ensures ("Returns true if the equation is valid, which means the evaluated results of both sides are equal.")
     *          \result == (evaluateExpression(leftSide) == evaluateExpression(rightSide));
     * @return boolean indicating whether the equation is valid.
     */
    private boolean checkEquation(String input) {
        assert input != null : "Input equation should not be null.";
        assert !input.isEmpty() : "Input equation should not be empty.";

        // Remove the equal sign and split the equation
        String[] parts = input.split("=");
        if (parts.length != 2) {
            System.out.println("Invalid equation format. Please enter a valid equation.");
            return false;
        }

        String leftSide = parts[0].trim();
        String rightSide = parts[1].trim();

        // Evaluates the value of the expression on the left
        int leftValue = evaluateExpression(leftSide);
        // Evaluates the value of the expression on the right
        int rightValue = evaluateExpression(rightSide);

        return leftValue == rightValue;
    }

    /**
     * Evaluates the given mathematical expression and returns the result.
     *
     * @param expression The mathematical expression to evaluate (e.g., "2 + 3 * 4").
     * @requires ("The expression must be a non-null and non-empty string representing a valid mathematical expression.")
     *           expression != null && !expression.isEmpty();
     * @ensures ("The result of evaluating the expression is returned.")
     *          \result == (\old(evaluateExpression(expression)));
     * @return The result of evaluating the expression.
     */
    private int evaluateExpression(String expression) {
        // Precondition: The expression should be a non-null and non-empty string.
        assert expression != null : "The expression cannot be null.";
        assert !expression.isEmpty() : "The expression cannot be empty.";

        // Remove all spaces
        expression = expression.replaceAll("\\s+", "");
        // Split the expression into tokens (numbers and operators)
        List<String> tokens = new ArrayList<>(Arrays.asList(expression.split("(?=[+*/-])|(?<=[+*/-])")));

        // Precondition: The tokens list should not be empty after splitting.
        assert !tokens.isEmpty() : "The tokens list cannot be empty.";

        // Handle multiplication and division
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("*") || token.equals("/")) {
                int result = calculateResult(Integer.parseInt(tokens.get(i - 1)), Integer.parseInt(tokens.get(i + 1)), token.charAt(0));
                // Replace the calculated part
                tokens.set(i - 1, Integer.toString(result));
                tokens.remove(i); // Remove the operator
                tokens.remove(i); // Remove the second number
                i--; // Backtrack one step to handle consecutive multiplications/divisions
            }
        }

        // Handle addition and subtraction
        String char1 = tokens.get(0);
        int result;
        if (!char1.equals("-")) {
            result = Integer.parseInt(tokens.get(0));
            for (int i = 1; i < tokens.size(); i += 2) {
                result = calculateResult(result, Integer.parseInt(tokens.get(i + 1)), tokens.get(i).charAt(0));
            }
        } else {
            result = -Integer.parseInt(tokens.get(1));
            for (int i = 2; i < tokens.size(); i += 2) {
                result = calculateResult(result, Integer.parseInt(tokens.get(i + 1)), tokens.get(i).charAt(0));
            }
        }

        return result;
    }

    /**
     * Compares two strings and returns an array of integers indicating the match status.
     * Each index in the result array corresponds to the character in the input string.
     * A value of 1 indicates an exact match, 2 indicates a character match but in a different position,
     * and 0 indicates no match.
     *
     * @param target The target string to compare against.
     * @param input The input string to be compared.
     * @requires ("Both target and input must be non-null strings.")
     *           target != null && input != null;
     * @requires ("classList[3] must contain all characters that can be used in the input.")
     *           classList[3] != null;
     * @ensures ("Returns an array of integers representing the match status for each character in the input.")
     *          \result != null;
     * @ensures ("classList[0] contains characters found in the input but not in the target.")
     *          (\forall char c; input.contains(c) && !target.contains(c); classList[0].contains(c));
     * @ensures ("classList[1] contains characters with an exact match.")
     *          (\forall int i; 0 <= i && i < input.length(); input.charAt(i) == target.charAt(i) ==> classList[1].contains(input.charAt(i)));
     * @ensures ("classList[2] contains characters that match but are in different positions.")
     *          (\forall int i; 0 <= i && i < input.length(); (\exists int j; 0 <= j && j < target.length(); input.charAt(i) == target.charAt(j) && i != j) ==> classList[2].contains(input.charAt(i)));
     * @ensures ("classList[3] contains characters not yet matched.")
     *          (\forall char c; !input.contains(c) || !target.contains(c); classList[3].contains(c));
     * @return An array of integers indicating the match status for each character in the input.
     */
    private int[] compareStrings(String target, String input) {
        // Precondition: The target and input strings should not be null.
        assert target != null : "Target string cannot be null.";
        assert input != null : "Input string cannot be null.";

        // Precondition: The classList[3] should contain characters for input.
        assert classList[3] != null : "classList[3] should not be null.";

        // Initialize the result array with zeros
        int[] result = new int[target.length()];

        // Mark characters from input that are present in classList[3]
        // boolean[] matched = new boolean[target.length()];

        int minEquationLength = Math.min(target.length(), input.length());
        for (int i = 0; i < minEquationLength; i++){
            if (classList[3].contains(input.charAt(i))){
                classList[0].add(input.charAt(i));
                classList[3].remove(Character.valueOf(input.charAt(i)));
            }
        }

        // Check for exact matches (value 1)
        for (int i = 0; i < input.length(); i++) {
            if (i < target.length() && input.charAt(i) == target.charAt(i)) {
                result[i] = 1;
                if (!classList[1].contains(target.charAt(i))) {
                    classList[1].add(target.charAt(i));
                    classList[0].remove(Character.valueOf(target.charAt(i)));
                }
                //matched[i] = true;
            }
        }

        // Check for character matches but different positions (value 2)
        for (int i = 0; i < minEquationLength; i++) {
            if (result[i] != 1) { // If the current position is not an exact match
                for (int j = 0; j < target.length(); j++) {
                    if (input.charAt(i) == target.charAt(j)) {//!matched[j] &&
                        result[i] = 2;
                        if (!classList[2].contains(target.charAt(j))) {
                            classList[2].add(target.charAt(j));
                            classList[0].remove(Character.valueOf(target.charAt(j)));
                        }
                        //matched[j] = true; // 标记为已匹配，避免重复匹配
                        break;
                    }
                }
            }
        }

        // Postcondition: The result array should be non-null.
        assert result != null : "Result array cannot be null.";

        return result;
    }

    /**
     * Calculates the result of a binary operation between two integers.
     *
     * @param a The first operand.
     * @param b The second operand (must not be zero if operator is '/').
     * @param operator The operator ('+', '-', '*', '/').
     * @requires ("The second operand 'b' must not be zero if the operator is division.")
     *           operator != '/' || b != 0;
     * @ensures ("Returns the result of the operation, or Integer.MAX_VALUE if division by zero occurs.")
     *          (\result == a + b && operator == '+') ||
     *          (\result == a - b && operator == '-') ||
     *          (\result == a * b && operator == '*') ||
     *          ((operator == '/' && b != 0) ==> \result == a / b) ||
     *          ((operator == '/' && b == 0) ==> \result == Integer.MAX_VALUE);
     * @return The result of the operation.
     */
    private int calculateResult(int a, int b, char operator) {
        // Precondition: 'b' should not be zero if the operator is division.
        assert operator != '/' || b != 0 : "Second operand must not be zero for division.";
        // The switch statement handles the calculation based on the operator.
        int result = switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b; // Division by zero is checked by the precondition.
            default -> throw new IllegalArgumentException("Invalid operator");
        };

        // Postcondition: The result should be within the range of integers.
        assert Integer.MIN_VALUE <= result && result <= Integer.MAX_VALUE : "Result out of range.";

        return result;
    }

    /**
     * Sets the target number or equation for the game.
     *
     * @param target The new target number or equation.
     * @requires ("The target must be a non-null and non-empty string.")
     *           target != null && !target.isEmpty();
     * @ensures ("The targetNumber is updated to the new target.")
     *          targetNumber.equals(target);
     */
    public void setTarget(String target){
        // Precondition: The target should be a valid equation or number.
        assert target != null && !target.isEmpty() : "Target must be a non-null and non-empty string.";
        targetNumber = target;
        // Postcondition: The targetNumber is updated.
        assert targetNumber.equals(target) : "Target number not updated correctly.";
    }

    /**
     * Checks if the game is over, which occurs when no attempts remain or the game has been won.
     *
     * @return boolean indicating whether the game is over.
     * @ensures ("Returns true if the game is over, which is when no attempts remain or the game has been won.")
     *          \result == (remainingAttempts <= 0 || gameWon);
     */
    @Override
    public boolean isGameOver() {
        // The method checks if the game is over based on remaining attempts or if the game has been won.
        boolean gameOver = remainingAttempts <= 0 || gameWon;

        // Postcondition: The method returns a boolean indicating the game's over status.
        assert gameOver == (remainingAttempts <= 0 || gameWon) : "Game over status incorrect.";

        return gameOver;
    }

    /**
     * Checks if the game has been won.
     *
     * @return boolean indicating whether the game has been won.
     * @ensures ("Returns true if the game has been won, false otherwise.")
     *          \result == gameWon;
     */
    @Override
    public boolean isGameWon() {
        // The method checks if the game has been won.
        // Postcondition: The method returns a boolean indicating the game's won status.
        assert gameWon == this.gameWon : "Game won status incorrect.";

        return gameWon;
    }

    /**
     * Retrieves the target number or equation for the game.
     *
     * @return The target number or equation.
     * @ensures ("Returns the current target number or equation.")
     *          \result.equals(targetNumber);
     */
    @Override
    public String getTargetNumber() {
        // The method retrieves the target number or equation for the game.
        // Postcondition: The method returns the current target number or equation.
        assert targetNumber != null : "Target number cannot be null.";

        return targetNumber;
    }

    /**
     * Retrieves the remaining attempts left for the user.
     *
     * @return The number of remaining attempts as an integer.
     * @ensures ("The returned number of remaining attempts is non-negative.")
     *          \result >= 0;
     */
    @Override
    public int getRemainingAttempts() {
        // Postcondition: The remaining attempts should be non-negative.
        assert remainingAttempts >= 0 : "Remaining attempts cannot be negative.";
        return remainingAttempts;
    }

    /**
     * Starts a new game by initializing the game state.
     *
     * @ensures ("The game state is reset, and a new game begins.")
     */
    @Override
    public void startNewGame() {
        System.out.println("Game is start now! You have a total of 6 chances.");
        initialize();
    }

    /**
     * Retrieves the list of guesses made by the user.
     *
     * @return An ArrayList containing the user's guesses.
     * @ensures ("The returned list is not null and contains the user's guesses.")
     *          \result != null;
     */
    @Override
    public ArrayList<String> getGuessList() {
        // Postcondition: The guess list should not be null.
        assert guessList != null : "Guess list cannot be null.";
        return guessList;
    }

    /**
     * Retrieves the list of comparison results for each guess.
     *
     * @return An ArrayList containing int arrays representing comparison results.
     * @ensures ("The returned list is not null and contains the comparison results for each guess.")
     *          \result != null;
     */
    @Override
    public ArrayList<int[]> getCompareList() {
        // Postcondition: The compare list should not be null.
        assert compareList != null : "Compare list cannot be null.";
        return compareList;
    }

    /**
     * Retrieves the flag indicating whether equations should be displayed.
     *
     * @return boolean indicating if equations should be displayed.
     * @ensures ("Returns the value of the displayEquation flag.")
     *          \result == displayEquation;
     */
    @Override
    public boolean getDisplayEquation() {
        return displayEquation;
    }

    /**
     * Retrieves the flag indicating whether equations should be verified for correctness.
     *
     * @return boolean indicating if equations should be verified.
     * @ensures ("Returns the value of the verifyEquation flag.")
     *          \result == verifyEquation;
     */
    @Override
    public boolean getVerifyEquation() {
        return verifyEquation;
    }

    /**
     * Retrieves the flag indicating whether the equality in the target number should be random.
     *
     * @return boolean indicating if the equality should be random.
     * @ensures ("Returns the value of the randomEquality flag.")
     *          \result == randomEquality;
     */
    @Override
    public boolean getRandomEquality() {
        return randomEquality;
    }

    /**
     * Retrieves an equation from a file or the current target number based on the randomEquality flag.
     *
     * @return String containing a valid equation or null if an error occurs.
     * @requires ("The file \"equations.txt\" must exist and be readable.")
     *           Files.exists(Paths.get("equations.txt")) && Files.isReadable(Paths.get("equations.txt"));
     * @ensures ("Returns a valid equation from the file or the current target number.")
     *          (\result != null) && (randomEquality ? allEquations.contains(\result) : \result.equals(targetNumber));
     */
    private String getEquationFromFile() {
        List<String> allEquations;
        try {
            allEquations = Files.readAllLines(Paths.get("equations.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            // Postcondition: If an exception occurs, the method returns null.
            assert false : "IOException occurred while reading the file.";
            return null; // Or handle the error appropriately.
        }

        // Postcondition: The list of all equations should not be empty.
        assert allEquations != null && !allEquations.isEmpty() : "List of equations cannot be empty.";

        if (randomEquality)
            return allEquations.get(rand.nextInt(allEquations.size()));
        else {
            if (targetNumber != null)
                return targetNumber;
            else
                return allEquations.get(rand.nextInt(allEquations.size()));
        }
    }

    /**
     * Retrieves the classification lists used for tracking character matches.
     *
     * @return ArrayList[] containing classification lists.
     * @ensures ("Returns the array of ArrayLists representing character classifications.")
     *          \result != null;
     */
    @Override
    public ArrayList[] getClassList(){
        // Postcondition: The class list should not be null.
        assert classList != null : "Class list cannot be null.";
        return classList;
    }
}
