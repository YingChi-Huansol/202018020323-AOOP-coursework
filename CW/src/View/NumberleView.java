package View;// View.NumberleView.java
import Controller.NumberleController;
import CustomClass.RoundedButton;
import CustomClass.SpacedJTextField;
import Model.Interface.INumberleModel;
import Model.NumberleModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

/**
 * The View.NumberleView class represents the view component in the MVC pattern.
 * It is responsible for displaying the user interface of the Numberle game,
 * including input fields, buttons, and labels to interact with the game.
 */
public class NumberleView implements Observer {
    // Reference to the model component of the MVC pattern.
    private final INumberleModel model;
    // Reference to the controller component of the MVC pattern.
    private final NumberleController controller;
    // Main window frame of the Numberle game.
    private final JFrame frame = new JFrame("Numberle");
    // Text field for user input.
    private  final SpacedJTextField inputTextField = new SpacedJTextField(40);
    //private final JTextField inputTextField = new JTextField(8);
    // Label to display the remaining attempts.
    private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");
    // Label to display the target equation or number.
    private final JLabel targetLabel = new JLabel("The target is ");
    // Panel to display the guesses.
    private JPanel guessPanel;
    // Button to start a new game.
    private RoundedButton newGameButton;
    // Map to store the association between characters and buttons.
    private HashMap<Character, RoundedButton> buttonMap;
    // Checkboxes to toggle game settings.
    private JCheckBox verifyEquationCheckBox;
    private JCheckBox displayEquationCheckBox;
    private JCheckBox randomEqualityCheckBox;
    // Labels for the checkboxes.
    private final JLabel verifySign = new JLabel("Verify");
    private final JLabel displaySign = new JLabel("Display");
    private final JLabel randomSign = new JLabel("Random");

    /**
     * Constructor for View.NumberleView. Initializes the view by setting up the model and controller,
     * starting a new game, and updating the view to reflect the current state of the model.
     *
     * @param model      The model component of the MVC pattern.
     * @param controller The controller component of the MVC pattern.
     */
    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        this.controller.setView(this);
        initializeFrame();
        update((NumberleModel)this.model, null);
        showGameSettingsDialog();
    }

    /**
     * Initializes the main frame of the application, setting up the layout, panels, and components.
     * Precondition: None
     * Postcondition: The main frame and its components are initialized and displayed to the user.
     */
    private void initializeFrame() {
        // Assert that the controller and model are properly initialized before setting up the frame.
        assert controller != null : "Controller must not be null.";
        assert model != null : "Model must not be null.";

        // Map to store the association between characters and buttons.
        buttonMap = new HashMap<>();

        // Set up the main frame properties.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 775);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // Center panel for the main content.
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(new JPanel());

        // Header panel with the game logo.
        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.X_AXIS));
        JLabel  headText = new JLabel ();
        BufferedImage originalImage;
        try {
            // Load and scale the game logo.
            originalImage = ImageIO.read(new File("logo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double scale = 0.15;
        Image resizedImage = originalImage.getScaledInstance((int)(originalImage.getWidth() * scale), (int)(originalImage.getHeight() * scale), Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage((int)(originalImage.getWidth() * scale), (int)(originalImage.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        // Assert that the frame and its components are properly initialized.
        assert frame != null : "Frame must not be null.";
        assert center != null : "Center panel must not be null.";
        assert head != null : "Head panel must not be null.";
        assert headText != null : "Head text label must not be null.";
        assert originalImage != null : "Original image must not be null.";
        assert resizedImage != null : "Resized image must not be null.";
        assert bufferedResizedImage != null : "Buffered resized image must not be null.";
        assert g2d != null : "Graphics2D object must not be null.";

        //Sets the icon for the header text label and defines its border.
        headText.setIcon(new ImageIcon(bufferedResizedImage));
        headText.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        head.add(headText);

        //Sets the font and border for the verification, display, and random labels.
        verifySign.setFont(new Font("Montserrat", Font.BOLD, 18)); // Set the text size to 18 point font
        verifySign.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 10));

        displaySign.setFont(new Font("Montserrat", Font.BOLD, 18)); // Set the text size to 18 point font
        displaySign.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        randomSign.setFont(new Font("Montserrat", Font.BOLD, 18)); // Set the text size to 18 point font
        randomSign.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Calls a method to update the flags based on the current game settings.
        updateFlags();

        // Adds the labels to the head panel and the head panel to the frame.
        head.add(verifySign);
        head.add(displaySign);
        head.add(randomSign);
        frame.add(head, BorderLayout.NORTH);

        // Initializes the guess panel which displays the history of user inputs.
        guessPanel = new JPanel();
        guessPanel.setLayout(new GridLayout(6, 7, 5, 5)); // 6 rows, 7 columns, 5px spacing
        //guessPanel.setPreferredSize(new Dimension(380, 325));
        int leftAndRight = 100;
        guessPanel.setBorder(BorderFactory.createEmptyBorder(10, leftAndRight, 10, leftAndRight));
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                // 创建并添加自定义 JLabel
                CustomClass.RoundedBorderLabel label = new CustomClass.RoundedBorderLabel(10); // Set the radius of the rounded corners to 10
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Montserrat", Font.BOLD, 28)); // Set the text size to 28 point font
                label.setBackground(new Color(251,252,255));
                label.setForeground(Color.WHITE); // Set the text color to black
                label.setPreferredSize(new Dimension(50, 50));
                guessPanel.add(label);
            }
        }
        center.add(guessPanel); // Adds the guessPanel to the center panel.

        assert frame != null : "Frame must not be null.";
        assert headText != null : "Head text label must not be null.";
        assert bufferedResizedImage != null : "Buffered resized image must not be null.";
        assert verifySign != null : "Verify sign label must not be null.";
        assert displaySign != null : "Display sign label must not be null.";
        assert randomSign != null : "Random sign label must not be null.";
        assert guessPanel != null : "Guess panel must not be null.";
        assert inputTextField != null : "Input text field must not be null.";

        // Initializes the input panel which contains the text field for user input.
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, leftAndRight, 0, leftAndRight));
        inputTextField.setFont(new Font("Montserrat", Font.BOLD, 28));
        inputTextField.setHorizontalAlignment(JTextField.CENTER);
        inputTextField.setBackground(new Color(251,252,255));
        inputPanel.add(inputTextField); // Adds the inputTextField to the inputPanel.

        // Assert that the inputPanel is not null after initialization.
        assert inputPanel != null : "Input panel must not be null.";

        // Set the text for attemptsLabel and add it to the inputPanel.
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        attemptsLabel.setFont(new Font("Montserrat", Font.BOLD, 18)); // Set the text size to 18 point font
        inputPanel.add(attemptsLabel);
        // Assert that attemptsLabel is added to the inputPanel.
        assert inputPanel.getComponent(inputPanel.getComponentCount() - 1).equals(attemptsLabel) : "attemptsLabel must be added to the inputPanel.";

        // Initialize newGameButton, set its properties, and add it to the inputPanel.
        newGameButton = new RoundedButton("New Game");
        //newGameButton = new JButton("New Game");
        newGameButton.setEnabled(false); // 初始状态为禁用
        newGameButton.setFont(new Font("Montserrat", Font.PLAIN, 20)); // Set the text size to 20 point font
        newGameButton.addActionListener(e -> showGameSettingsDialog());
        inputPanel.add(newGameButton);
        // Assert that newGameButton is added to the inputPanel.
        assert inputPanel.getComponent(inputPanel.getComponentCount() - 1).equals(newGameButton) : "newGameButton must be added to the inputPanel.";

        // Set the text for targetLabel and add it to the inputPanel.
        targetLabel.setText("The target is " + controller.getTargetWord().replaceAll("/","÷").replaceAll("\\*","×"));
        targetLabel.setFont(new Font("Montserrat", Font.BOLD, 18)); // Set the text size to 20 point font

        inputPanel.add(targetLabel);

        // Assert that targetLabel is added to the inputPanel.
        assert inputPanel.getComponent(inputPanel.getComponentCount() - 1).equals(targetLabel) : "targetLabel must be added to the inputPanel.";

        // Add inputPanel to the center of the frame.
        center.add(inputPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.CENTER);

        // Initialize keyboardPanel and add it to the frame.
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.add(new JPanel());
        // Assert that keyboardPanel is not null.
        assert keyboardPanel != null : "Keyboard panel must not be null.";

        // Initialize numberPanel and add number buttons to it.
        JPanel numberPanel = new JPanel();
        //numberPanel.setLayout(new GridLayout(1, 10));
        for (int i = 0; i < 10; i++) {
            RoundedButton button = new RoundedButton(Integer.toString(i));
//            JButton button = new JButton(Integer.toString(i));
            button.setEnabled(true);
            button.addActionListener(e -> {
                String text = inputTextField.getText();
                if (text.length() >= controller.getTargetWord().length())
                    inputTextField.setText(text.substring(0, text.length() - 1));
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            button.setBackground(new Color(220,225,237));
            button.setFont(new Font("Montserrat", Font.PLAIN, 16)); // Set font size to 16.
            button.setPreferredSize(new Dimension(50, 50));
            numberPanel.add(button);
            buttonMap.put((char) ('0' + i), (RoundedButton) numberPanel.getComponent(i));
        }
        // Assert that numberPanel contains 10 buttons.
        assert numberPanel.getComponentCount() == 10 : "Number panel must contain 10 buttons.";

        // Initialize operatorPanel and add backspaceButton to it.
        JPanel operatorPanel = new JPanel();
        RoundedButton backspaceButton = new RoundedButton("Backspace");
        backspaceButton.setBackground(new Color(220,225,237));
        backspaceButton.setFont(new Font("Montserrat", Font.PLAIN, 14)); // Set font size to 14.
        backspaceButton.setPreferredSize(new Dimension(107, 50));
        backspaceButton.addActionListener(e -> {
            String text = inputTextField.getText();
            if (text.length() > 0)
                inputTextField.setText(text.substring(0, text.length() - 1));
        });
        operatorPanel.add(backspaceButton);
        // Assert that operatorPanel contains the backspaceButton.
        assert operatorPanel.getComponent(operatorPanel.getComponentCount() - 1).equals(backspaceButton) : "Backspace button must be added to the operator panel.";

        // Assert that the operatorPanel is not null after initialization.
        assert operatorPanel != null : "Operator panel must not be null.";

        for (String s: new String[]{"+", "-", "×", "÷", "="}){
            //Initialize and set properties for all the operate buttons, then add it to the operatorPanel.
            RoundedButton button = new RoundedButton(s);
            button.setBackground(new Color(220, 225, 237));
            button.setFont(new Font("Montserrat", Font.PLAIN, 24)); // Set the text size to 24 point font
            button.setPreferredSize(new Dimension(60, 50));
            button.addActionListener(e -> {
                String text = inputTextField.getText();
                if (text.length() >= controller.getTargetWord().length())
                    inputTextField.setText(text.substring(0, text.length() - 1));
                inputTextField.setText(inputTextField.getText() + button.getText());
            });
            operatorPanel.add(button);
            // Assert that plusButton is added to the operatorPanel.
            assert operatorPanel.getComponent(operatorPanel.getComponentCount() - 1).equals(button) : "plusButton must be added to the operator panel.";

            // Assert that the buttonMap is not null after initialization.
            assert buttonMap != null : "Button map must not be null.";
            // Map the operator buttons to their respective symbols in the buttonMap.
            buttonMap.put(s.charAt(0), button);
            if (s.equals("×"))
                buttonMap.put('*', button); // For systems that use '*' as the multiplication symbol.
            else if (s.equals("÷"))
                buttonMap.put('/', button); // For systems that use '/' as the division symbol.
        }

        // Assert that all the necessary buttons are added to the buttonMap.
        assert buttonMap.containsKey('+') : "Plus button must be mapped.";
        assert buttonMap.containsKey('-') : "Minus button must be mapped.";
        assert buttonMap.containsKey('×') : "Multiply button must be mapped.";
        assert buttonMap.containsKey('*') : "Asterisk for multiplication must be mapped.";
        assert buttonMap.containsKey('÷') : "Divide button must be mapped.";
        assert buttonMap.containsKey('/') : "Slash for division must be mapped.";
        assert buttonMap.containsKey('=') : "Equal button must be mapped.";

        RoundedButton submitButton = new RoundedButton("Submit");
        submitButton.setBackground(new Color(220,225,237));
        submitButton.setFont(new Font("Montserrat", Font.PLAIN, 14)); // Set the text size to 14 point font
        submitButton.setPreferredSize(new Dimension(107, 50));
        submitButton.addActionListener(e -> {
            controller.processInput(inputTextField.getText());
            inputTextField.setText("");
        });
        operatorPanel.add(submitButton);
        // Assert that submitButton is added to the operatorPanel.
        assert operatorPanel.getComponent(operatorPanel.getComponentCount() - 1).equals(submitButton) : "submitButton must be added to the operator panel.";

        // Assert that keyboardPanel is not null and contains all necessary subpanels.
        assert keyboardPanel != null : "Keyboard panel must not be null.";
        assert keyboardPanel.getComponentCount() >= 7 : "Keyboard panel must contain all the components.";

        // Add numberPanel and operatorPanel to the keyboardPanel.
        keyboardPanel.add(numberPanel);
        keyboardPanel.add(operatorPanel);

        // Assert that numberPanel and operatorPanel are added to the keyboardPanel.
        assert keyboardPanel.getComponent(keyboardPanel.getComponentCount() - 3).equals(numberPanel) : "Number panel must be added to the keyboard panel.";
        assert keyboardPanel.getComponent(keyboardPanel.getComponentCount() - 2).equals(operatorPanel) : "Operator panel must be added to the keyboard panel.";

        // Add an empty JPanel as a spacer.
        keyboardPanel.add(new JPanel());

        // Assert that the spacer panel is added to the keyboardPanel.
        assert keyboardPanel.getComponent(keyboardPanel.getComponentCount() - 1) instanceof JPanel : "Spacer panel must be added to the keyboard panel.";

        // Add the keyboardPanel to the frame and make the frame visible.
        frame.add(keyboardPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        // Assert that the frame is set to be visible.
        assert frame.isVisible() : "Frame should be visible.";
    }

    /**
     * Updates the view to reflect the current state of the model.
     * This method is called whenever the observed object is changed.
     *
     * @param o   The observable object.
     * @param arg An argument passed to the notifyObservers method.
     */
    @Override
    public void update(java.util.Observable o, Object arg) {
        // Assert that the controller and guessPanel are not null.
        assert controller != null : "Controller must not be null.";
        assert guessPanel != null : "Guess panel must not be null.";

        // Update the attempts label with the remaining attempts from the controller.
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
        // Assert that the attemptsLabel is not null and has been updated.
        assert attemptsLabel != null : "Attempts label must not be null.";
        assert attemptsLabel.getText().contains(String.valueOf(controller.getRemainingAttempts())) : "Attempts label text must be updated with remaining attempts.";

        // Update the target label with the target word from the controller, formatting division and multiplication symbols for display.
        targetLabel.setText("The target is " + controller.getTargetWord().replaceAll("/","÷").replaceAll("\\*","×"));
        // Assert that the targetLabel is not null and has been updated.
        assert targetLabel != null : "Target label must not be null.";
        assert targetLabel.getText().contains(controller.getTargetWord().replaceAll("/","÷").replaceAll("\\*","×")) : "Target label text must be updated with the target word.";

        // Retrieve the list of guesses and comparison results from the controller.
        ArrayList<String> guessList = controller.getGuessList(); // List of guesses made by the user.
        ArrayList<int[]> compareList = controller.getCompareList(); // List of comparison results for each guess.
        // Assert that the guessList and compareList are not null and are synchronized in size.
        assert guessList != null : "Guess list must not be null.";
        assert compareList != null : "Compare list must not be null.";
        assert guessList.size() == compareList.size() : "Guess list and compare list must be of the same size.";

        // Update the state of the new game button.
        newGameButton.setEnabled((!controller.getGuessList().isEmpty()));

        // Update the flags for verifying, displaying, and randomizing equations.
        updateFlags();

        // Update the guess panel with the guesses and comparison results.
        for (int i = 0; i < guessList.size(); i++) {
            String guess = guessList.get(i);
            int[] compare = compareList.get(i);
            for (int j = 0; j < compare.length; j++) {
                JLabel label = (JLabel) guessPanel.getComponent(i * 7 + j);
                // Assert that each label is not null.
                assert label != null : "Label at index " + (i * 7 + j) + " must not be null.";
                if (j < guess.length()) {
                    // Set the text of the label to the current character of the guess.
                    label.setText(String.valueOf(guess.charAt(j)));
                    // Assert that the label text is set correctly.
                    assert label.getText().equals(String.valueOf(guess.charAt(j))) : "Label text must be set to the character at index " + j + " of the guess.";
                    // Change the background color of the label based on the comparison result.
                    switch (compare[j]) {
                        case 0 -> label.setBackground(new Color(164, 174, 196));
                        case 1 -> label.setBackground(new Color(47, 193, 165));
                        case 2 -> label.setBackground(new Color(247, 154, 111));
                    }
                    // Assert that the label background is set correctly.
                    assert label.getBackground() != null : "Label background must be set based on the comparison result.";
                } else {
                    // If there is no character at this position, set the label to blank.
                    label.setText(" ");
                    label.setBackground(Color.WHITE);
                    // Assert that the label is reset correctly for empty guess positions.
                    assert label.getText().equals(" ") : "Label text must be set to a space for empty guess positions.";
                    assert label.getBackground().equals(Color.WHITE) : "Label background must be set to white for empty guess positions.";
                }
            }
        }
        updateButtonColors();

        // Check if the game is over
        if (controller.isGameOver()) {
            showGameOverDialog();
        }
    }

    /**
     * Updates the visual flags for the verify, display, and random settings based on the current game state.
     * Precondition: None
     * Postcondition: The color of the flags is updated to reflect the current settings.
     */
    private void updateFlags() {
        // Assert that the controller is not null.
        assert controller != null : "Controller must not be null.";
        // Update the verifySign color based on the controller's state.
        if (controller.getVerifyEquation())
            verifySign.setForeground(new Color(47,193,165));
        else
            verifySign.setForeground(Color.BLACK);
        // Assert that verifySign is not null.
        assert verifySign != null : "Verify sign must not be null.";

        // Update the displaySign and targetLabel visibility based on the controller's state.
        if (controller.getDisplayEquation()) {
            displaySign.setForeground(new Color(47,193,165));
            targetLabel.setVisible(true);
        }
        else {
            displaySign.setForeground(Color.BLACK);
            targetLabel.setVisible(false);
        }
        // Assert that displaySign and targetLabel are not null.
        assert displaySign != null : "Display sign must not be null.";
        assert targetLabel != null : "Target label must not be null.";

        // Update the randomSign color based on the controller's state.
        if (controller.getRandomEquality())
            randomSign.setForeground(new Color(47,193,165));
        else
            randomSign.setForeground(Color.BLACK);
        // Assert that randomSign is not null.
        assert randomSign != null : "Random sign must not be null.";
    }

    /**
     * Updates the colors of the buttons based on the classification of characters in guesses.
     * Precondition: The model must provide valid classification lists.
     * Postcondition: The buttons' colors are updated to reflect the classification of characters.
     */
    private void updateButtonColors() {
        // Assert that model and buttonMap are not null.
        assert model != null : "Model must not be null.";
        assert buttonMap != null : "Button map must not be null.";

        // Classification lists used for tracking character matches.
        ArrayList<Character>[] classList = controller.getClassList();
        // Assert that classList is not null and has the expected number of lists.
        assert classList != null : "Class list must not be null.";
        assert classList.length == 4 : "Class list must contain four lists for character classification.";

        // Reset all button colors.
        buttonMap.values().forEach(button -> {
            button.setBackground(new Color(220, 225, 237));
            button.setHoverBackgroundColor(new Color(200, 200, 200));
            button.setPressedBackgroundColor(new Color(150, 150, 150));
        });
        // Set colors for characters not present in the target.
        for (char c : classList[0]) {
            buttonMap.get(c).setBackground(new Color(164,174,196));
            buttonMap.get(c).setHoverBackgroundColor(new Color(144, 154, 176));
            buttonMap.get(c).setPressedBackgroundColor(new Color(124, 134, 156));
        }
        // Set colors for characters present but in the wrong position.
        for (char c : classList[2]) {
            buttonMap.get(c).setBackground(new Color(247,154,111));
            buttonMap.get(c).setHoverBackgroundColor(new Color(227, 134, 81));
            buttonMap.get(c).setPressedBackgroundColor(new Color(207, 114, 61));
        }
        // Set colors for correctly positioned characters.
        for (char c : classList[1]) {
            buttonMap.get(c).setBackground(new Color(47,193,165));
            buttonMap.get(c).setHoverBackgroundColor(new Color(67, 213, 185));
            buttonMap.get(c).setPressedBackgroundColor(new Color(27, 173, 145));
        }
        // Assert that each character in classList has a corresponding button in buttonMap.
        for (ArrayList<Character> classGroup : classList) {
            for (char c : classGroup) {
                assert buttonMap.containsKey(c) : "Button map must contain a button for character: " + c;
            }
        }
    }

    /**
     * Displays a dialog for game settings before starting a new game.
     * Precondition: The controller must provide the current settings for verification, display, and random equality.
     * Postcondition: The game settings are updated based on user input, and a new game is started or the application is exited.
     */
    private void showGameSettingsDialog() {
        // Assert that the controller is not null.
        assert controller != null : "Controller must not be null.";

        // Initialize checkboxes with current settings from the controller.
        verifyEquationCheckBox = new JCheckBox("Verify Equation", controller.getVerifyEquation());
        displayEquationCheckBox = new JCheckBox("Display Equation", controller.getDisplayEquation());
        randomEqualityCheckBox = new JCheckBox("Random Equality", controller.getRandomEquality());

        // Create and configure the start game button.
        RoundedButton startButton = new RoundedButton("Start Game");
        startButton.addActionListener(e -> {
            // Update model flags based on checkbox selections.
            controller.setVerifyEquation(verifyEquationCheckBox.isSelected());
            controller.setDisplayEquation(displayEquationCheckBox.isSelected());
            controller.setRandomEquality(randomEqualityCheckBox.isSelected());
            // Dispose of the dialog and start a new game.
            Window dialog = SwingUtilities.getWindowAncestor(startButton);
            if (dialog != null) {
                dialog.dispose();
            }
            controller.startNewGame();
            clearGameView();
            initializeFrame();
            updateFlags();
        });

        // Create and configure the quit button.
        RoundedButton exitButton = new RoundedButton("Quit");
        exitButton.addActionListener(e -> System.exit(0));

        // Display the dialog with the game settings options.
        Object[] options = {startButton, exitButton};
        JOptionPane.showOptionDialog(frame,
                new Object[]{verifyEquationCheckBox, displayEquationCheckBox, randomEqualityCheckBox},
                "Starting Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
    }

    /**
     * Displays a dialog indicating the end of the game with a message based on the outcome.
     * Precondition: The controller must provide the game outcome and target word if the game is lost.
     * Postcondition: The dialog is displayed with the game outcome message and options to start a new game or quit.
     */
    private void showGameOverDialog() {
        // Assert that the controller is not null.
        assert controller != null : "Controller must not be null.";

        // Determine the message and title based on the game outcome.
        String message = controller.isGameWon() ? "<html>Congratulations!<br>Equation correct. You win.</html>" : "<html>Run out of opportunity.<br>Target is " + controller.getTargetWord().replaceAll("/","÷").replaceAll("\\*","×") + ". You lose. </html>";
        String title = controller.isGameWon() ? "WIN" : "LOSE";

        // Create and configure the message label.
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Montserrat", Font.PLAIN, 20));
        if (controller.isGameWon())
            messageLabel.setForeground(new Color(0,240,0));
        else
            messageLabel.setForeground(Color.GRAY);

        // Create and configure the start game button.
        RoundedButton startButton = new RoundedButton("Start Game");
        startButton.addActionListener(e -> {
            // Update model flags based on checkbox selections.
            controller.setVerifyEquation(verifyEquationCheckBox.isSelected());
            controller.setDisplayEquation(displayEquationCheckBox.isSelected());
            controller.setRandomEquality(randomEqualityCheckBox.isSelected());
            // Dispose of the dialog and start a new game.
            Window dialog = SwingUtilities.getWindowAncestor(startButton);
            if (dialog != null) {
                dialog.dispose();
            }
            clearGameView();
            controller.startNewGame();
            initializeFrame();
        });

        // Create and configure the quit button.
        RoundedButton exitButton = new RoundedButton("Quit");
        exitButton.addActionListener(e -> System.exit(0));

        // Display the dialog with the game outcome message and options.
        Object[] options = {startButton, exitButton};
        Object[] dialogContent = {messageLabel, verifyEquationCheckBox, displayEquationCheckBox, randomEqualityCheckBox};
        JOptionPane optionPane = new JOptionPane(
                dialogContent,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        // Create a dialog and set the JOptionPane as its content pane.
        JDialog dialog = optionPane.createDialog(frame, title);
        // Set the default close operation to DO_NOTHING_ON_CLOSE.
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // Display the custom dialog.
        dialog.setVisible(true);
    }

    /**
     * Displays a message dialog to inform the user of invalid input based on the status code.
     * Precondition: statusCode must be an integer representing a specific type of input validation error.
     * Postcondition: A message dialog is displayed with information about the input error.
     *
     * @param statusCode The code representing the type of input error.
     */
    public void showInvalidInputMessage(int statusCode) {
        // Assert that the statusCode is within the expected range.
        assert statusCode > 0 && statusCode <= 4 : "Status code must be between 1 and 4.";

        // Determine the message based on the status code.
        String message = switch (statusCode) {
            case 2 -> "The length of the equation is not 7.";
            case 3 -> "The input string does not follow the form of an ordinary equation.";
            case 4 -> "The two sides of the equation don't agree. The equation doesn't hold.";
            default -> "The input is valid.";
        };

        // Display the message dialog with the determined message.
        JOptionPane.showMessageDialog(null, message, "Tips", JOptionPane.INFORMATION_MESSAGE);
        // Assert that the message is not null or empty.
        assert message != null && !message.isEmpty() : "Message must not be null or empty.";
    }

    /**
     * Clears the game view by removing all components from the frame.
     * Precondition: None
     * Postcondition: The frame is cleared of all components and repainted.
     */
    private void clearGameView() {
        // Assert that the frame is not null before attempting to clear it.
        assert frame != null : "Frame must not be null.";
        // Remove all components from the frame.
        frame.getContentPane().removeAll();
        // Repaint the frame to update the display.
        frame.repaint();
        // Assert that the frame's content pane is empty after clearing.
        assert frame.getContentPane().getComponentCount() == 0 : "Frame's content pane should be empty after clearing.";
    }
}