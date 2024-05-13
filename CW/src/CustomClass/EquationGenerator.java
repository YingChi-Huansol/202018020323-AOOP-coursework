package CustomClass;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The EquationGenerator class is responsible for generating random mathematical equations
 * that conform to specific validity criteria. It maintains the best equation generated so far.
 *
 * @invariant bestEquation != null ==> isValidEquation(bestEquation); // If bestEquation is not null, it must be a valid equation according to the game's rules.
 */
public class EquationGenerator {
    private final Random random = new Random();
    private String bestEquation;

    /**
     * @ensures // The postcondition ensures that a valid equation according to the game's rules is stored in bestEquation.
     *   (\exists String validEquation; isValidEquation(validEquation); bestEquation.equals(validEquation));
     * @assignable bestEquation; // The assignable clause specifies that bestEquation may be assigned a value during the execution of the method.
     * @return void
     */
    public void generateEquation() {
        int a, b, result;
        char operator;
        String equation;
        do {
            // Assert that the random object is not null.
            assert random != null : "Random object must not be null.";

            // Generate random numbers and an operator to form an equation.
            if (new Random().nextInt(10) <= 1) {
                a = random.nextInt(100); // 0-99
                b = random.nextInt(100); // 0-99
            } else {
                a = random.nextInt(15); // 0-14
                b = random.nextInt(15); // 0-14
            }
            operator = randomOperator();
            // Ensure division by zero does not occur.
            while (b == 0 && operator == '/')
                b = random.nextInt(50);
            result = calculateResult(a, b, operator);
            equation = a + "" + operator + "" + b + "=" + result;
            bestEquation = equation;
            // Assert that the equation is not null or empty.
            assert equation != null && !equation.isEmpty() : "Equation must not be null or empty.";
        } while ((operator == '/' && (a % b != 0)) || !isValidEquation(equation));
    }

    /**
     * @requires generateEquationCalled; // The generateEquation method must have been called first.
     * @ensures \result != null; // The postcondition ensures that the result is not null, indicating an equation has been generated.
     * @return The best generated equation.
     */
    public String getEquation(){
        // Assert that the bestEquation is not null or empty.
        assert bestEquation != null && !bestEquation.isEmpty() : "Best equation must not be null or empty.";
        return bestEquation;
    }

    /**
     * @ensures (\result == '+' || \result == '-' || \result == '*' || \result == '/'); // The postcondition ensures that the result is one of the four basic operators.
     * @return A random operator, one of '+', '-', '*', or '/'.
     */
    private char randomOperator() {
        char[] operators = {'+', '-', '*', '/'};
        char op = operators[new Random().nextInt(operators.length)];
        // Assert that the operator is valid.
        assert new String(operators).contains(String.valueOf(op)) : "Operator must be one of the valid operators.";
        return op;
    }

    /**
     * @ensures \result == '+' || \result == '-'; // The postcondition ensures that the result is either '+' or '-'.
     * @return A random operator, either '+' or '-'.
     */
    private char randomOperator2() {
        char[] operators = {'+', '-'};
        char op = operators[new Random().nextInt(operators.length)];
        // Assert that the operator is valid.
        assert new String(operators).contains(String.valueOf(op)) : "Operator must be one of the valid operators.";
        return op;
    }

    /**
     * @requires (operator == '+' || operator == '-' || operator == '*' || operator == '/'); // The operator must be one of '+', '-', '*', '/'.
     * @ensures // The postcondition ensures that the opposite operator is returned.
     *   ((operator == '+') ==> (\result == '-')) &&
     *   ((operator == '-') ==> (\result == '+')) &&
     *   ((operator == '*') ==> (\result == '/')) &&
     *   ((operator == '/') ==> (\result == '*'));
     * @param operator The operator for which to find the opposite.
     * @return The opposite operator.
     */
    private char getOppositeOperator(char operator) {
        char opposite = switch (operator) {
            case '+' -> '-';
            case '-' -> '+';
            case '*' -> '/';
            case '/' -> '*';
            default -> '\0'; // Invalid operator
        };
        // Assert that the opposite operator is valid.
        assert opposite != '\0' : "Opposite operator must be valid.";
        return opposite;
    }

    /**
     * @requires (operator != '/' || b != 0) && (operator == '+' || operator == '-' || operator == '*' || operator == '/'); // The operator must not be division if b is zero, and it must be a valid operator.
     * @ensures // The postcondition ensures that the result is correct according to the operation.
     *   (operator == '+' ==> \result == a + b) &&
     *   (operator == '-' ==> \result == a - b) &&
     *   (operator == '*' ==> \result == a * b) &&
     *   (operator == '/' ==> \result == a / b);
     * @param a The first integer.
     * @param b The second integer, which must not be zero if the operator is division.
     * @param operator The operator to apply, which must be one of the valid mathematical operators: '+', '-', '*', or '/'.
     * @return The result of the operation.
     */
    private int calculateResult(int a, int b, char operator) {
        // Assert that the operator is valid before calculation.
        assert "+-*/".indexOf(operator) != -1 : "Operator must be one of the valid operators.";
        int result = switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> b != 0 ? a / b : 0; // Prevent division by zero
            default -> throw new IllegalArgumentException("Invalid operator");
        };
        // Assert that the result is a valid integer.
        assert result == (int)result : "Result must be a valid integer.";
        return result;
    }

    /**
     * @requires equation != null && equation.length() > 0; // The equation must be a non-null and non-empty string.
     * @ensures \result == true <==> // The result is true if and only if the equation is valid.
     *           (equation.matches("[0-9+-/*()]+") && checkEquationFormat(equation)); // The equation must match a valid mathematical expression format.
     * @param equation The equation to validate.
     * @return True if the equation is valid, false otherwise.
     */
    private boolean isValidEquation(String equation) {
        // Assert that the equation is not null or empty.
        assert equation != null && !equation.isEmpty() : "Equation must not be null or empty.";
        int length = equation.length();
        if (length == 7) {
            return true;
        } else if (length == 6) {
            return isValidModifiedEquation(equation); // Needs to be regenerated
        } else if (length == 5) {
            // Take a random 0 or 1
            if (new Random().nextInt(2) == 0) {
                // The result is decomposed so that the formula forms an equation with a symbol b=c symbol d
                return isValidDecomposedEquation(equation, -1, '0');
            } else {
                // Look for two numbers that satisfy the form of a symbol b symbol c=d
                return isValidAdditionEquation(equation, -1, '0');
            }
        } else if (length > 7) {
            // Replace random symbols with + or -
            return isValidModifiedEquation(equation);
        }
        return false; // If still not valid, needs to be regenerated
    }

    /**
     * @requires equation != null && equation.length() > 0; // The equation must be a non-null and non-empty string.
     * @ensures \result == true <==> // The result is true if and only if a valid decomposed equation is found.
     *           (\exists String validEquation; validEquation.length() >= 0 && validEquation.length() <= equation.length();
     *           validEquation.matches("[0-9+-/*()]+") && checkDecomposedEquation(validEquation)); // There exists a string that is a valid decomposed equation of appropriate length.
     * @param equation The equation to be decomposed.
     * @param num The number to be used as an operand in the decomposition, if known.
     * @param operator The operator to be used in the decomposition, if known.
     * @return True if a valid decomposed equation is found, false otherwise.
     */
    private boolean isValidDecomposedEquation(String equation, int num, char operator) {
        // Assert that the input equation is not null or empty.
        assert equation != null && !equation.isEmpty() : "Input equation must not be null or empty.";
        // Assert that the input number is within the valid range if provided.
        assert num >= -1 && num <= 9 : "Input number must be between -1 and 9.";
        // Assert that the input operator is one of the valid operators.
        assert "+-*/".indexOf(operator) != -1 : "Input operator must be one of '+', '-', '*', '/'.";

        // Split the equation into left and right sides.
        String[] parts = equation.split("=");
        // Assert that the equation splits into exactly two parts.
        assert parts.length == 2 : "Equation must split into exactly two parts.";
        if (parts.length != 2) {
            return false; // Incorrect equation format.
        }
        String leftSide = parts[0].trim();
        String rightSide = parts[1].trim();

        // Initialize operands and operator.
        int a = num;
        char operator2 = operator;
        // If the operand is not known, generate a random number and operator.
        if (a < 0) {
            a = random.nextInt(10);
            operator2 = randomOperator();
        }
        int result = Integer.parseInt(rightSide);

        // Ensure valid operands for multiplication and division.
        while (a == 0 && (operator2 == '/' || operator2 == '*')) {
            a = random.nextInt(10);
            operator2 = randomOperator();
        }
        // Calculate the result of the operation.
        int result2 = calculateResult(result, a, operator2);

        // Check if the operation is valid and create a new equation.
        if (operator2 != '/' || result % a == 0) {
            String newEquation = leftSide + "=" + result2 + "" + getOppositeOperator(operator2) + "" + a;
            // If the new equation is too long, attempt to decompose further.
            if (newEquation.length() > 7)
                return isValidDecomposedEquation(equation, a-1, operator2);
            // Set the best equation found.
            bestEquation = newEquation;
            // Assert that the best equation is not null or empty.
            assert bestEquation != null && !bestEquation.isEmpty() : "Best equation must not be null or empty.";
            return true;
        } else {
            // If the result is not an integer, try with a different operand.
            if (a < 9) {
                return isValidDecomposedEquation(equation, a+1, operator2);
            }
            // If no valid decomposition is found, indicate the need to regenerate the equation.
            return false;
        }
    }

    /**
     * @requires equation != null && equation.length() > 0 && num < 9; // The equation must be a non-null, non-empty string and num must be less than 9.
     * @ensures \result == true <==> // The result is true if and only if a valid addition equation is found.
     *           (\exists String validEquation; validEquation.length() <= equation.length();
     *           validEquation.contains("+") && validEquation.matches("[0-9+]+") &&
     *           validEquation.length() == 7); // There exists a string that is a valid addition equation of length 7.
     * @param equation The equation to be modified.
     * @param num The number to be used as an operand in the modification, if known.
     * @param operator The operator to be used in the modification, if known.
     * @return True if a valid addition equation is found, false otherwise.
     */
    private boolean isValidAdditionEquation(String equation, int num, char operator) {
        // Assert that the input equation is not null or empty.
        assert equation != null && !equation.isEmpty() : "Input equation must not be null or empty.";
        // Assert that the input number is within the valid range if provided.
        assert num >= -1 && num <= 9 : "Input number must be between -1 and 9 inclusive.";
        // Assert that the input operator is one of the valid operators.
        assert "+-".indexOf(operator) != -1 : "Input operator must be one of '+', '-'.";

        // Split the equation into left and right sides.
        String[] parts = equation.split("=");
        // Assert that the equation splits into exactly two parts.
        assert parts.length == 2 : "Equation must split into exactly two parts.";

        if (parts.length != 2) {
            return false; // Incorrect equation format.
        }
        if (num >= 9)
            return false; // Prevent infinite recursion.
        String leftSide = parts[0].trim();
        String rightSide = parts[1].trim();

        // Initialize operands and operator.
        int a = num;
        char operator2 = operator;
        int result2;
        String newEquation;
        int result = Integer.parseInt(rightSide);
        // Calculate the result of the operation.
        if (new Random().nextInt(2) == 0) {
            if (a < 0) {
                a = random.nextInt(10);
                operator2 = randomOperator2();
            }
            result2 = calculateResult(result, a, operator2);
            newEquation = leftSide + "" + operator2 + "" + a + "=" + result2;
        } else {
            if (a < 0) {
                a = random.nextInt(10);
                operator2 = '+';
            }
            result2 = calculateResult(a, result, operator2);
            newEquation = a + "" + operator2 + "" + leftSide + "=" + result2;
        }
        // If the new equation is too long, attempt to modify further.
        if (newEquation.length() > 7)
            return isValidAdditionEquation(equation, a-1, '+');
        else {
            // Set the best equation found.
            bestEquation = newEquation;
            // Assert that the best equation is not null or empty.
            assert bestEquation != null && !bestEquation.isEmpty() : "Best equation must not be null or empty.";
            return true;
        }
    }

    /**
     * @requires equation != null && equation.length() > 0; // The equation must be a non-null and non-empty string.
     * @ensures \result == true <==> (\exists int i; 0 <= i && i < equation.length();
     *           (\old(equation.charAt(i)) == '+' || \old(equation.charAt(i)) == '-') &&
     *           equation.length() == 7); // The result is true if and only if the equation contains '+' or '-' and its length is 7 after modification.
     * @param equation The equation to be modified.
     * @return True if the equation is successfully modified to a valid form, false otherwise.
     */
    private boolean isValidModifiedEquation(String equation) {
        // Assert that the input equation is not null or empty.
        assert equation != null && !equation.isEmpty() : "Input equation must not be null or empty.";

        // Use regex to parse the equation and extract operands and the operator.
        Pattern pattern = Pattern.compile("(\\d+)([+\\-*/])(\\d+)=(-?\\d+)");
        Matcher matcher = pattern.matcher(equation);
        int a, b, result;
        char operator2;
        // Attempt to find a match for the equation pattern.
        if (matcher.find()) {
            a = Integer.parseInt(matcher.group(1)); // Extract number a
            b = Integer.parseInt(matcher.group(3)); // Extract number b
        } else {
            // If the equation does not match the pattern, return false.
            return false;
        }
        // Randomly choose an operator from '+' or '-'.
        operator2 = randomOperator2();
        // Calculate the result with the new operator.
        result = calculateResult(a, b, operator2);
        // Form a new equation with the calculated result.
        String newEquation = a + "" + operator2 + "" + b + "=" + result;

        // Check if the new equation has a valid length.
        if (newEquation.length() == 7) {
            bestEquation = newEquation;
            return true;
        } else {
            // Try the opposite operator if the length is not valid.
            result = calculateResult(a, b, getOppositeOperator(operator2));
            newEquation = a + "" + getOppositeOperator(operator2) + "" + b + "=" + result;
            if (newEquation.length() == 7) {
                bestEquation = newEquation;
                return true;
            } else {
                // Try swapping the operands and re-calculating.
                result = calculateResult(b, a, operator2);
                newEquation = b + "" + operator2 + "" + a + "=" + result;
                if (newEquation.length() == 7) {
                    bestEquation = newEquation;
                    return true;
                } else {
                    // Try with the opposite operator after swapping.
                    result = calculateResult(b, a, getOppositeOperator(operator2));
                    newEquation = b + "" + getOppositeOperator(operator2) + "" + a + "=" + result;
                    if (newEquation.length() == 7) {
                        bestEquation = newEquation;
                        return true;
                    } else {
                        // If the length is 5, validate the equation.
                        if (newEquation.length() == 5)
                            return isValidEquation(newEquation);
                        else {
                            // If no valid modification is found, return false.
                            return false;
                        }
                    }
                }
            }
        }
    }
}
