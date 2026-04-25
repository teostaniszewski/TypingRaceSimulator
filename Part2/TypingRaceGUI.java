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

        return panel;
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