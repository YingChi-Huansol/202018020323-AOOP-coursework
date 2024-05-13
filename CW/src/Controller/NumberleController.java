package Controller;

import Model.Interface.INumberleModel;
import View.NumberleView;

import java.util.ArrayList;

/**
 * Controller for the Numberle game following the MVC pattern.
 * It mediates the communication between the view and the model.
 * Invariants:
 * - model should not be null after construction.
 * - view can be null initially but should be set before processing input.
 */
public class NumberleController {
    private final INumberleModel model;
    private NumberleView view;

    /**
     * Constructor for Controller.NumberleController.
     * @param model The model of the Numberle game.
     * Precondition: model should not be null.
     * Postcondition: this.model is initialized.
     */
    public NumberleController(INumberleModel model) {
        assert model != null : "Model cannot be null";
        this.model = model;
    }

    /**
     * Sets the view for the controller.
     * @param view The view of the Numberle game.
     * Precondition: view should not be null.
     * Postcondition: this.view is set.
     */
    public void setView(NumberleView view) {
        assert view != null : "View cannot be null";
        this.view = view;
    }

    /**
     * Processes the user input.
     * @param input The user's guess.
     * Precondition: input should not be null or empty.
     * Postcondition: The view is updated based on the input's validity and game state.
     */
    public void processInput(String input) {
        assert input != null && !input.isEmpty() : "Input cannot be null or empty";
        int statusCode = model.processInput(input);
        if (statusCode > 1)
            view.showInvalidInputMessage(statusCode);
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return model.isGameOver();
    }

    /**
     * Checks if the game is won.
     * @return true if the game is won, false otherwise.
     */
    public boolean isGameWon() {
        return model.isGameWon();
    }

    /**
     * Gets the target number for the game.
     * @return The target number.
     */
    public String getTargetWord() {
        return model.getTargetNumber();
    }

    /**
     * Gets the remaining attempts.
     * @return The number of remaining attempts.
     */
    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    /**
     * Starts a new game.
     * Postcondition: The game state is reset.
     */
    public void startNewGame() {
        model.startNewGame();
    }

    /**
     * Gets the value of the "Verify Equation" setting.
     * @return true if equation verification is enabled, false otherwise.
     */
    public boolean getVerifyEquation() {
        return model.getVerifyEquation();
    }

    /**
     * Gets the value of the "Display Equation" setting.
     * @return true if equation display is enabled, false otherwise.
     */
    public boolean getDisplayEquation(){
        return model.getDisplayEquation();
    }

    /**
     * Gets the value of the "Random Equality" setting.
     * @return true if random equality is enabled, false otherwise.
     */
    public boolean getRandomEquality() {
        return model.getRandomEquality();
    }

    /**
     * Sets the value of the "Verify Equation" setting.
     * @param verifyEquation true to enable equation verification, false otherwise.
     * Precondition: None.
     * Postcondition: The model's verifyEquation state is set.
     */
    public void setVerifyEquation(boolean verifyEquation) {
        model.setVerifyEquation(verifyEquation);
    }

    /**
     * Sets the value of the "Display Equation" setting.
     * @param displayEquation true to enable equation display, false otherwise.
     * Precondition: None.
     * Postcondition: The model's displayEquation state is set.
     */
    public void setDisplayEquation(boolean displayEquation) {
        model.setDisplayEquation(displayEquation);
    }

    /**
     * Sets the value of the "Random Equality" setting.
     * @param randomEquality true to enable random equality, false otherwise.
     * Precondition: None.
     * Postcondition: The model's randomEquality state is set.
     */
    public void setRandomEquality(boolean randomEquality) {
        model.setRandomEquality(randomEquality);
    }

    /**
     * Gets the list of user guesses.
     * @return An ArrayList of Strings representing the user's guesses.
     * Precondition: None.
     * Postcondition: Returns the current list of guesses.
     */
    public ArrayList<String> getGuessList() {
        return model.getGuessList();
    }

    /**
     * Gets the list of comparisons for each guess.
     * @return An ArrayList of int arrays representing the comparison of each guess against the target number.
     * Precondition: None.
     * Postcondition: Returns the current list of comparisons.
     */
    public ArrayList<int[]> getCompareList() {
        return model.getCompareList();
    }

    public ArrayList<Character>[] getClassList() {
        return model.getClassList();
    }
}