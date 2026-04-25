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
    private JPanel mainPanel;

    private JComboBox<String> passageComboBox;
    private JTextArea customPassageArea;
    private JLabel passageLengthLabel;
    
    private JComboBox<Integer> seatCountComboBox;
    private JPanel typistConfigPanel;

    private String selectedPassage;

    /**
     * Constructor for TypingRaceGUI.
     * Sets up the main GUI window.
     */
    public TypingRaceGUI()
    {
        selectedPassage = "";
        createWindow();
    }

    /**
     * Creates the main application window.
     */
    private void createWindow()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Typing Race Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel headerPanel = new JPanel(new GridLayout(1, 1));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(createPassagePanel(), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Creates the panel used for selecting or entering a passage.
     *
     * @return the passage selection panel
     */
    private JPanel createPassagePanel()
    {
        JPanel passagePanel = new JPanel(new BorderLayout(10, 10));
        passagePanel.setBorder(BorderFactory.createTitledBorder("Passage Selection"));

        String[] passages = {
            "Short: The quick brown fox jumps over the lazy dog.",
            "Medium: Java Swing allows developers to build interactive graphical applications.",
            "Long: Object oriented programming helps structure larger programs by separating data and behaviour into classes and objects.",
            "Custom Passage"
        };

        passageComboBox = new JComboBox<>(passages);

        customPassageArea = new JTextArea(5, 40);
        customPassageArea.setLineWrap(true);
        customPassageArea.setWrapStyleWord(true);
        customPassageArea.setEnabled(false);

        passageLengthLabel = new JLabel("Passage length: 44 characters");

        passageComboBox.addActionListener(e -> updateSelectedPassage());

        customPassageArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e)
            {
                updateSelectedPassage();
            }
        });

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.add(new JLabel("Choose a passage:"));
        topPanel.add(passageComboBox);

        seatCountComboBox = new JComboBox<>(new Integer[] {2, 3, 4, 5, 6});

        JPanel seatPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        seatPanel.add(new JLabel("Number of typists:"));
        seatPanel.add(seatCountComboBox);

        topPanel.add(seatPanel);

        passagePanel.add(topPanel, BorderLayout.NORTH);
        passagePanel.add(new JScrollPane(customPassageArea), BorderLayout.CENTER);
        passagePanel.add(passageLengthLabel, BorderLayout.SOUTH);

        updateSelectedPassage();

        return passagePanel;
    }

    /**
     * Updates the selected passage based on the dropdown or custom text area.
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
     * Starts the graphical typing race application.
     */
    public void startRaceGUI()
    {
        frame.setVisible(true);
    }

    /**
     * Main method used to test the GUI.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        new TypingRaceGUI();
    }
}