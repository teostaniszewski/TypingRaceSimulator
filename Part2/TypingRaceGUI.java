package Part2;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical user interface for the Typing Race Simulator.
 * This class creates the main GUI window for Part II of the project.
 *
 * @author Teo Staniszewski
 * @version 1.0
 */
public class TypingRaceGUI
{
    private JFrame frame;

    private JComboBox<String> passageComboBox;
    private JTextArea customPassageArea;
    private JLabel passageLengthLabel;

    private JSpinner seatCountSpinner;
    private JCheckBox autocorrectCheckBox;
    private JCheckBox caffeineModeCheckBox;
    private JCheckBox nightShiftCheckBox;
    private JComboBox<String> typingStyleComboBox;
    private JLabel typingStyleImpactLabel;
    private JComboBox<String> keyboardTypeComboBox;
    private JLabel keyboardTypeImpactLabel;
    private JTextField symbolField;
    private JComboBox<String> colourComboBox;
    private JLabel symbolColourImpactLabel;
    private JCheckBox wristSupportCheckBox;
    private JCheckBox energyDrinkCheckBox;
    private JCheckBox noiseCancellingCheckBox;
    private JLabel accessoryImpactLabel;

    private String selectedPassage;

    /**
     * Constructor for TypingRaceGUI.
     * Initialises the GUI and sets up the window.
     */
    public TypingRaceGUI()
    {
        selectedPassage = "";
        createWindow();
    }

    /**
     * Creates the main application window and layout.
     * Adds the tabbed GUI structure.
     */
    private void createWindow()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Typing Race Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Race Configuration", createRaceConfigurationPanel());
        tabbedPane.addTab("Customise Typists", createCustomiseTypistsPanel());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Creates the main race configuration panel.
     * This contains passage selection, seat count, and difficulty modifiers.
     *
     * @return the race configuration panel
     */
    private JPanel createRaceConfigurationPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel passageBox = createPassageSection();
        passageBox.setBorder(BorderFactory.createTitledBorder("Passage Selection"));

        JPanel seatBox = createSeatCountSection();
        seatBox.setBorder(BorderFactory.createTitledBorder("Seat Count"));

        JPanel difficultyBox = createDifficultySection();
        difficultyBox.setBorder(BorderFactory.createTitledBorder("Difficulty Modifiers"));

        panel.add(passageBox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(seatBox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(difficultyBox);

        return panel;
    }

    /**
     * Creates the passage selection section.
     *
     * @return the passage selection section
     */
    private JPanel createPassageSection()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] passages = {
            "Short: The quick brown fox jumps over the lazy dog.",
            "Medium: Java Swing allows developers to build interactive graphical applications.",
            "Long: Object oriented programming helps structure larger programs by separating data and behaviour into classes and objects.",
            "Custom Passage"
        };

        passageComboBox = new JComboBox<>(passages);

        customPassageArea = new JTextArea(4, 40);
        customPassageArea.setEnabled(false);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(customPassageArea);
        scrollPane.setPreferredSize(new Dimension(0, 80));

        passageLengthLabel = new JLabel("Passage length: 44 characters");
        passageLengthLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        passageComboBox.addActionListener(e -> updateSelectedPassage());

        customPassageArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e)
            {
                updateSelectedPassage();
            }
        });

        panel.add(passageComboBox, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(passageLengthLabel, BorderLayout.SOUTH);

        updateSelectedPassage();

        return panel;
    }

    /**
     * Creates the seat count section.
     *
     * @return the seat count section
     */
    private JPanel createSeatCountSection()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JLabel label = new JLabel("Number of racers:");

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, 6, 1);
        seatCountSpinner = new JSpinner(spinnerModel);
        seatCountSpinner.setPreferredSize(new Dimension(60, 28));

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) seatCountSpinner.getEditor();
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);

        panel.add(label);
        panel.add(seatCountSpinner);

        return panel;
    }

    /**
     * Creates the difficulty modifier section.
     *
     * @return the difficulty modifier section
     */
    private JPanel createDifficultySection()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        autocorrectCheckBox = new JCheckBox("Autocorrect On - slide back amount is reduced");
        caffeineModeCheckBox = new JCheckBox("Caffeine Mode - early speed boost, higher burnout risk");
        nightShiftCheckBox = new JCheckBox("Night Shift - accuracy reduced");

        panel.add(autocorrectCheckBox);
        panel.add(caffeineModeCheckBox);
        panel.add(nightShiftCheckBox);

        return panel;
    }

    /**
     * Creates the customisation panel for typist options.
     * This will later include typing style, keyboard type, symbol, colour, and accessories.
     *
     * @return the customise typists panel
     */
    /**
     * Creates the customisation panel for typist options.
     * This includes typing style choices that affect the typist's behaviour.
     *
     * @return the customise typists panel
     */
    private JPanel createCustomiseTypistsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel stylePanel = new JPanel(new GridLayout(3, 1, 5, 5));
        stylePanel.setBorder(BorderFactory.createTitledBorder("Typing Style"));

        String[] typingStyles = {
            "Touch Typist",
            "Hunt & Peck",
            "Phone Thumbs",
            "Voice-to-Text"
        };

        typingStyleComboBox = new JComboBox<>(typingStyles);
        typingStyleImpactLabel = new JLabel("Impact: Balanced accuracy and speed.");

        typingStyleComboBox.addActionListener(e -> updateTypingStyleImpact());

        stylePanel.add(new JLabel("Choose typing style:"));
        stylePanel.add(typingStyleComboBox);
        stylePanel.add(typingStyleImpactLabel);

        panel.add(stylePanel, BorderLayout.NORTH);
        JPanel customisationOptionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        customisationOptionsPanel.add(stylePanel);
        customisationOptionsPanel.add(createKeyboardTypePanel());
        customisationOptionsPanel.add(createSymbolColourPanel());
        customisationOptionsPanel.add(createAccessoriesPanel());

        panel.add(customisationOptionsPanel, BorderLayout.NORTH);

        return panel;
    }

    /**
 * Creates the symbol and colour customisation section.
 *
 * @return the symbol and colour panel
 */
private JPanel createSymbolColourPanel()
{
    JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
    panel.setBorder(BorderFactory.createTitledBorder("Symbol and Colour"));

    symbolField = new JTextField("1", 3);

    String[] colours = {
        "Red",
        "Blue",
        "Green",
        "Purple",
        "Orange",
        "Black"
    };

    colourComboBox = new JComboBox<>(colours);
    symbolColourImpactLabel = new JLabel("Impact: Visual customisation only.");

    panel.add(new JLabel("Typist symbol:"));
    panel.add(symbolField);
    panel.add(new JLabel("Progress colour:"));
    panel.add(colourComboBox);
    panel.add(new JLabel("Effect:"));
    panel.add(symbolColourImpactLabel);

    return panel;
}

    /**
     * Creates the keyboard type customisation section.
     *
     * @return the keyboard type panel
     */
    private JPanel createKeyboardTypePanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Keyboard Type"));

        String[] keyboardTypes = {
            "Mechanical",
            "Membrane",
            "Touchscreen",
            "Stenography"
        };

        keyboardTypeComboBox = new JComboBox<>(keyboardTypes);
        keyboardTypeImpactLabel = new JLabel("Impact: Mechanical keyboard gives steady typing performance.");

        keyboardTypeComboBox.addActionListener(e -> updateKeyboardTypeImpact());

        panel.add(new JLabel("Choose keyboard type:"));
        panel.add(keyboardTypeComboBox);
        panel.add(keyboardTypeImpactLabel);

        return panel;
    }

    /**
     * Creates the accessories section based on coursework specification.
     *
     * @return the accessories panel
     */
    private JPanel createAccessoriesPanel()
    {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        wristSupportCheckBox = new JCheckBox("Wrist Support - reduces burnout duration");
        energyDrinkCheckBox = new JCheckBox("Energy Drink - boosts accuracy early, reduces later");
        noiseCancellingCheckBox = new JCheckBox("Noise-Cancelling Headphones - reduces mistype chance");

        accessoryImpactLabel = new JLabel("Impact: Accessories affect typing performance.");

        wristSupportCheckBox.addActionListener(e -> updateAccessoryImpact());
        energyDrinkCheckBox.addActionListener(e -> updateAccessoryImpact());
        noiseCancellingCheckBox.addActionListener(e -> updateAccessoryImpact());

        panel.add(wristSupportCheckBox);
        panel.add(energyDrinkCheckBox);
        panel.add(noiseCancellingCheckBox);
        panel.add(accessoryImpactLabel);

        return panel;
    }

    /**
     * Updates accessory impact description based on selected options.
     */
    private void updateAccessoryImpact()
    {
        String impact = "Impact: ";

        if (wristSupportCheckBox.isSelected())
        {
            impact += "Lower burnout duration. ";
        }

        if (energyDrinkCheckBox.isSelected())
        {
            impact += "Higher early accuracy, lower later accuracy. ";
        }

        if (noiseCancellingCheckBox.isSelected())
        {
            impact += "Reduced mistype chance. ";
        }

        if (!wristSupportCheckBox.isSelected() &&
            !energyDrinkCheckBox.isSelected() &&
            !noiseCancellingCheckBox.isSelected())
        {
            impact += "No accessories selected.";
        }

        accessoryImpactLabel.setText(impact);
    }

    /**
     * Updates the description of how the selected keyboard type affects performance.
     */
    private void updateKeyboardTypeImpact()
    {
        String selectedKeyboard = (String) keyboardTypeComboBox.getSelectedItem();

        if ("Mechanical".equals(selectedKeyboard))
        {
            keyboardTypeImpactLabel.setText("Impact: Steady typing performance.");
        }
        else if ("Membrane".equals(selectedKeyboard))
        {
            keyboardTypeImpactLabel.setText("Impact: Slightly lower speed, but stable accuracy.");
        }
        else if ("Touchscreen".equals(selectedKeyboard))
        {
            keyboardTypeImpactLabel.setText("Impact: Higher mistype chance.");
        }
        else if ("Stenography".equals(selectedKeyboard))
        {
            keyboardTypeImpactLabel.setText("Impact: Very fast typing, but harder to control.");
        }
    }

    /**
     * Updates the description of how the selected typing style affects performance.
     */
    private void updateTypingStyleImpact()
    {
        String selectedStyle = (String) typingStyleComboBox.getSelectedItem();

        if ("Touch Typist".equals(selectedStyle))
        {
            typingStyleImpactLabel.setText("Impact: Balanced accuracy and speed.");
        }
        else if ("Hunt & Peck".equals(selectedStyle))
        {
            typingStyleImpactLabel.setText("Impact: Lower speed, slightly lower burnout risk.");
        }
        else if ("Phone Thumbs".equals(selectedStyle))
        {
            typingStyleImpactLabel.setText("Impact: Moderate speed, higher mistype chance.");
        }
        else if ("Voice-to-Text".equals(selectedStyle))
        {
            typingStyleImpactLabel.setText("Impact: Fast input, but accuracy may vary.");
        }
    }

    /**
     * Updates the currently selected passage.
     * Enables custom input when selected and updates the passage length label.
     */
    private void updateSelectedPassage()
    {
        String selectedOption = (String) passageComboBox.getSelectedItem();

        if ("Custom Passage".equals(selectedOption))
        {
            customPassageArea.setEnabled(true);
            selectedPassage = customPassageArea.getText();
        }
        else
        {
            customPassageArea.setEnabled(false);
            selectedPassage = selectedOption.substring(selectedOption.indexOf(":") + 2);
        }

        passageLengthLabel.setText("Passage length: " + selectedPassage.length() + " characters");
    }

    /**
     * Starts the GUI.
     */
    public void startRaceGUI()
    {
        frame.setVisible(true);
    }

    /**
     * Main method for testing the GUI independently.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        new TypingRaceGUI();
    }
}