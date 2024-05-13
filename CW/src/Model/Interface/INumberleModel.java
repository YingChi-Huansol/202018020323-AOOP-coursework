package Model.Interface;

import java.util.ArrayList;

/**
 * The Model.Interface.INumberleModel interface defines the core functionalities of the model component
 * in the MVC pattern for the Numberle game. It declares methods to manage the game state,
 * process user input, and retrieve game information.
 *
 * Invariant:
 * - MAX_ATTEMPTS should be a positive integer representing the maximum number of attempts allowed.
 */
public interface INumberleModel {
    // Maximum number of attempts a player has to guess the correct number or equation.
    int MAX_ATTEMPTS = 6;

    /**
     * Initializes or resets the game state to start a new game.
     * Precondition: None
     * Postcondition: The game state is initialized and ready for a new game.
     */
    void initialize();

    /**
     * Processes the user's input and updates the game state accordingly.
     * Precondition: input is a non-null string representing a valid guess.
     * Postcondition: Returns an integer indicating the result of processing the input.
     *
     * @param input The user's guess to be processed.
     * @return An integer code representing the outcome of the input processing.
     */
    int processInput(String input);

    /**
     * Checks if the game is over, which occurs when no attempts remain or the game has been won.
     * Precondition: None
     * Postcondition: Returns true if the game is over, false otherwise.
     *
     * @return boolean indicating whether the game is over.
     */
    boolean isGameOver();

    /**
     * Checks if the game has been won.
     * Precondition: None
     * Postcondition: Returns true if the game has been won, false otherwise.
     *
     * @return boolean indicating whether the game has been won.
     */
    boolean isGameWon();

    /**
     * Retrieves the target number or equation for the game.
     * Precondition: None
     * Postcondition: Returns the current target number or equation.
     *
     * @return The target number or equation.
     */
    String getTargetNumber();

    /**
     * Retrieves the remaining attempts left for the user.
     * Precondition: None
     * Postcondition: Returns the remaining attempts as an integer.
     *
     * @return The remaining attempts.
     */
    int getRemainingAttempts();

    /**
     * Starts a new game by initializing the game state.
     * Precondition: None
     * Postcondition: The game state is reset, and a new game begins.
     */
    void startNewGame();

    /**
     * Retrieves the list of guesses made by the user.
     * Precondition: None
     * Postcondition: Returns an ArrayList containing the user's guesses.
     *
     * @return The list of guesses.
     */
    ArrayList<String> getGuessList();

    /**
     * Retrieves the list of comparison results for each guess.
     * Precondition: None
     * Postcondition: Returns an ArrayList containing int arrays representing comparison results.
     *
     * @return The list of comparison results.
     */
    ArrayList<int[]> getCompareList();

    /**
     * Retrieves the flag indicating whether equations should be displayed.
     * Precondition: None
     * Postcondition: Returns the value of the displayEquation flag.
     *
     * @return boolean indicating if equations should be displayed.
     */
    boolean getDisplayEquation();

    /**
     * Retrieves the flag indicating whether equations should be verified for correctness.
     * Precondition: None
     * Postcondition: Returns the value of the verifyEquation flag.
     *
     * @return boolean indicating if equations should be verified.
     */
    boolean getVerifyEquation();

    /**
     * Retrieves the flag indicating whether the equality in the target number should be random.
     * Precondition: None
     * Postcondition: Returns the value of the randomEquality flag.
     *
     * @return boolean indicating if the equality should be random.
     */
    boolean getRandomEquality();

    /**
     * Retrieves the classification lists used for tracking character matches.
     * Precondition: None
     * Postcondition: Returns the array of ArrayLists representing character classifications.
     *
     * @return ArrayList[] containing classification lists.
     */
    ArrayList[] getClassList();

    /**
     * Sets the flag to verify the correctness of the equation.
     * Precondition: None
     * Postcondition: The verifyEquation flag is updated.
     *
     * @param verifyEquation The new value for the verifyEquation flag.
     */
    void setVerifyEquation(boolean verifyEquation);

    /**
     * Sets the flag to display the equation.
     * Precondition: None
     * Postcondition: The displayEquation flag is updated.
     *
     * @param displayEquation The new value for the displayEquation flag.
     */
    void setDisplayEquation(boolean displayEquation);

    /**
     * Sets the flag to use random equality in the target number.
     * Precondition: None
     * Postcondition: The randomEquality flag is updated.
     *
     * @param randomEquality The new value for the randomEquality flag.
     */
    void setRandomEquality(boolean randomEquality);

    /**
     * Sets the target number or equation for the game.
     * Precondition: The target should be a String.
     * Postcondition: The targetNumber is updated.
     *
     * @param s The new target number or equation.
     */
    void setTarget(String s);
}