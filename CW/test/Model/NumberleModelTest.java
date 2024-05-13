package Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * JML style comments for NumberleModelTest class.
 * @invariant model != null -> model is always initialized before tests
 */
public class NumberleModelTest {
    private NumberleModel model;

    /**
     * Sets up the test fixture.
     * Called before every test case method.
     * @pre. none
     * @post. model != null -> ensures that the model is not null after initialization
     */
    @Before
    public void setUp() {
        model = new NumberleModel();
        model.initialize();
    }

    /**
     * Tests the entire game flow when verification checks are enabled,
     * random equations are disabled, and the display equation is shown.
     * @requires model != null && model.getTarget().equals("2*3-6=0")
     *           && model.isVerifyEquation() && model.isDisplayEquation()
     *           && !model.isRandomEquality()
     *           // Requires that the model is initialized with specific settings
     * @ensures (model.isGameWon() && model.isGameOver()) == true
     *          // Ensures that the game is won and over at the end of the test
     */
    @Test
    public void testGameWinFlow() {
        // Test enabled check, not enabled random equation when the entire game flow
        model.setTarget("2*3-6=0");
        model.setVerifyEquation(true);
        model.setDisplayEquation(true);
        model.setRandomEquality(false);

        /**
         * @pre input != null
         *           // Requires that the input string is not null
         * @post \result == (\exists int[] compareList; model.getCompareList().contains(compareList);
         *          \forall int i; 0 <= i && i < compareList.length; compareList[i] == compared[i])
         *          // Ensures that the result is an array that matches the expected comparison array
         */
        // At first guess, the length is correct but not an exact match
        model.processInput("5+15=20");
        int[] compare1 = model.getCompareList().get(0);
        int[] compared = new int[]{0, 0, 0, 0, 2, 2, 1};
        for (int i = 0; i < compare1.length; i ++){
            assertEquals("Partial matches should return a specific array", compared[i], compare1[i]);
        }

        // Second guess, wrong length
        int result2 = model.processInput("10+10");
        int sizeOfCompareList= model.getCompareList().size();
        assertEquals("Formatting errors should return 2", 2, result2);
        assertEquals("No equality comparison is made, so the list does not grow", 1, sizeOfCompareList);

        // Third guess, the length is correct but not the equation
        int result3 = model.processInput("777=777");
        sizeOfCompareList= model.getCompareList().size();
        assertEquals("Not the equation should return 3", 3, result3);
        assertEquals("No equality comparison is made, so the list does not grow", 1, sizeOfCompareList);

        // Third, fourth and fifth guesses, the length is correct and the format is correct but not equal
        String[] invalidEquations = {"10+5=14", "2*2-2=5", "100/1=0"};
        for (String equation : invalidEquations) {
            int result = model.processInput(equation);
            assertEquals("Invalid equations should return 4", 4, result);
        }
        assertEquals("No equality comparison is made, so the list does not grow", 1, sizeOfCompareList);

        // Sixth guess. Right guess
        int result4 = model.processInput("2*3-6=0");
        sizeOfCompareList= model.getCompareList().size();
        assertEquals("Perfect matches should return 1", 1, result4);
        assertEquals("After two comparisons, the list length should be 2", 2, sizeOfCompareList);
        assertTrue("The game should be won", model.isGameWon());
        assertTrue("The game should be over", model.isGameOver());
    }

    /**
     * Tests the game lose flow by processing inputs that do not match the target equation,
     * and verifying the classification of characters.
     * @requires model != null && model.getTarget().equals("12+3=15")
     *           && !model.isVerifyEquation() && !model.isDisplayEquation()
     *           && !model.isRandomEquality()
     *           // Requires that the model is initialized with specific settings
     * @ensures !model.isGameWon() && model.isGameOver()
     *          // Ensures that the game is lost and over at the end of the test
     */
    @Test
    public void testGameLoseFlow() {
        // Test character classification
        model.setTarget("12+3=15");
        model.setVerifyEquation(false);
        model.setDisplayEquation(false);
        model.setRandomEquality(false);

        /**
         * @pre input != null && input.equals("11+5=16")
         *           // Requires that the input string is not null and equals "11+5=16"
         * @post \exists ArrayList<Character>[] classList; model.getClassList() == classList
         *          && classList[0].contains('6') && classList[1].contains('1')
         *          && classList[2].contains('5') && classList[3].contains('2')
         *          // Ensures that the classification list is not null and contains specific characters
         */
        // First guess, part match, part wrong
        model.processInput("11+5=16");
        ArrayList<Character>[] classList = model.getClassList();
        assertNotNull("The category list should not be null", classList);
        assertTrue("Non-existent character should contain '6'", classList[0].contains('6'));
        assertTrue("Hit character should contain '1'", classList[1].contains('1'));
        assertTrue("Characters present but missed should contain '5'", classList[2].contains('5'));
        assertTrue("Unguessed characters should contain '2'", classList[3].contains('2'));

        // Second guess, parts that exceed the length of the target equation should not be compared and classified
        model.processInput("123456789");
        classList = model.getClassList();
        assertNotNull("The category list should not be null", classList);
        assertTrue("Non-existent character should contain '4'", classList[0].contains('4'));
        assertTrue("Hit character should contain '2'", classList[1].contains('2'));
        assertTrue("Characters present but missed should contain '3'", classList[2].contains('3'));
        assertTrue("Unguessed characters should contain '8'", classList[3].contains('8'));
        assertTrue("Unguessed characters should contain '9'", classList[3].contains('9'));

        // Test the close but incorrect equation 4 times until the game is lost
        String[] invalidEquations = new String[]{"12+3=14", "3*5=15", "1234=15", "30/2=15"};
        for (String equation : invalidEquations) {
            model.processInput(equation);
        }
        int sizeOfCompareList= model.getCompareList().size();
        assertEquals("Having guessed 6 times resulted in losing the game, the list length should be 6", 6, sizeOfCompareList);
        assertFalse("The game should be lost", model.isGameWon());
        assertTrue("The game should be over", model.isGameOver());

    }

    /**
     * Tests the random equation generation to ensure that each new game has a unique target number.
     * @requires model != null && model.isRandomEquality()
     *           // Requires that the model is initialized and random equality is set to true
     * @ensures \forall String equation; model.getTargetNumber() != equation
     *          // Ensures that each generated target number is unique
     */
    @Test
    public void testRandomEquationGeneration() {
        // Test random equation generation
        model.setRandomEquality(true);
        String equation1 = model.getTargetNumber();
        assertNotNull("Random equations should not be null", equation1);
        model.startNewGame();
        String equation2 = model.getTargetNumber();
        assertNotNull("Random equations should not be null", equation2);
        assertNotEquals("The random equations generated continuously should be different", equation1, equation2);
        model.setRandomEquality(false);
        model.startNewGame();
        String equation3 = model.getTargetNumber();
        assertNotNull("Equations should not be null", equation3);
        assertEquals("The non-random equations generated continuously should be the same", equation2, equation3);
    }

    /**
     * Tests the processInput method to ensure it returns the correct error code for invalid input format.
     * @requires model != null && model.isVerifyEquation()
     *           // Requires that the model is initialized and equation verification is set to true
     * @ensures model.processInput("abcdefg") == 3
     *          // Ensures that the method returns 3 for invalid input format
     */
    @Test
    public void testProcessInvalidInputFormat() {
        // The test handles invalid input formats
        model.setVerifyEquation(true);
        int result = model.processInput("abcdefg");
        assertEquals("Invalid input format, should return 3", 3, result);
    }

    /**
     * Tests the setFlags methods to ensure that the boolean flags are set correctly.
     * @requires model != null
     *           // Requires that the model is initialized
     * @ensures model.getVerifyEquation() == true && model.getDisplayEquation() == true
     *          && model.getRandomEquality() == true
     *          // Ensures that the flags are set to true after calling the set methods
     */
    @Test
    public void testSetFlags() {
        // Test setting flag
        assertFalse("verifyEquation should be false before setVerifyEquation is true", model.getVerifyEquation());
        model.setVerifyEquation(true);
        assertTrue("verifyEquation should be true after setVerifyEquation is true", model.getVerifyEquation());

        assertFalse("displayEquation should be false before setDisplayEquation is true", model.getDisplayEquation());
        model.setDisplayEquation(true);
        assertTrue("displayEquation should be true after setDisplayEquation is true", model.getDisplayEquation());

        assertFalse("randomEquality should be false before setRandomEquality is true", model.getRandomEquality());
        model.setRandomEquality(true);
        assertTrue("randomEquality should be true after setRandomEquality is true", model.getRandomEquality());
    }
}