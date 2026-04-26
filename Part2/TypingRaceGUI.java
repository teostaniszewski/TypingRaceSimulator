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

    private JSpinner seatCountSpinner;
    private JCheckBox autocorrectCheckBox;
    private JCheckBox caffeineModeCheckBox;
    private JCheckBox nightShiftCheckBox;
    private JCheckBox[] wristSupportBoxes;
    private JCheckBox[] energyDrinkBoxes;
    private JCheckBox[] noiseCancellingBoxes;

    private JTabbedPane typistTabbedPane;

    private JComboBox<String>[] typingStyleBoxes;
    private JComboBox<String>[] keyboardTypeBoxes;
    private JComboBox<String>[] symbolBoxes;
    private JComboBox<String>[] colourBoxes;

    private JLabel[] impactLabels;
    private JLabel passageLengthLabel;

    private JButton startRaceButton;

    private boolean updatingSymbols;
    private String selectedPassage;

    private final String[] typistNames = {
        "TURBOFINGERS",
        "QWERTY_QUEEN",
        "HUNT_N_PECK",
        "KEYBOARD_NINJA",
        "SPACEBAR_KING",
        "CTRL_ALT_ELITE"
    };

    private final String[] availableSymbols = {
        "■", "▲", "◆", "★", "⬤", "✖"
    };

    /**
     * Constructor for TypingRaceGUI.
     * Initialises the GUI and sets up the window.
     */
    public TypingRaceGUI()
    {
        selectedPassage = "";
        updatingSymbols = false;
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
        frame.setSize(1250, 750);
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

        panel.add(Box.createVerticalStrut(10));
        panel.add(createStartButtonSection());

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
        editor.getTextField().setEditable(false);
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);

        seatCountSpinner.addChangeListener(e -> updateTypistTabs());

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
     * Creates the start race button section.
     *
     * @return the start button panel
     */
    private JPanel createStartButtonSection()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        startRaceButton = new JButton("Start Race");
        startRaceButton.addActionListener(e -> showRaceScreen());

        panel.add(startRaceButton);

        return panel;
    }

    /**
     * Shows the basic race screen.
     * The race lanes are divided equally based on the number of typists.
     */
    private void showRaceScreen()
    {
        Typist[] typists = createTypistsFromGUI();

        JPanel racePanel = new JPanel(new BorderLayout(10, 10));
        racePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Typing Race", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel lanesPanel = new JPanel(new GridLayout(typists.length, 1, 5, 5));

        for (int i = 0; i < typists.length; i++)
        {
            lanesPanel.add(createRaceLanePanel(typists[i]));
        }

        racePanel.add(titleLabel, BorderLayout.NORTH);
        racePanel.add(lanesPanel, BorderLayout.CENTER);

        frame.setContentPane(racePanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates one visual lane for a typist in the race screen.
     *
     * @param typist the typist displayed in the lane
     * @return the race lane panel
     */
    private JPanel createRaceLanePanel(Typist typist)
    {
        JPanel lanePanel = new JPanel(new BorderLayout(10, 10));
        lanePanel.setBorder(BorderFactory.createTitledBorder(
            typist.getSymbol() + " " + typist.getName()
        ));

        JTextArea passageArea = new JTextArea(selectedPassage);
        passageArea.setLineWrap(true);
        passageArea.setWrapStyleWord(true);
        passageArea.setEditable(false);

        lanePanel.add(new JScrollPane(passageArea), BorderLayout.CENTER);

        return lanePanel;
    }

    /**
     * Previews the selected race configuration before starting the race.
     */
    private void previewRaceConfiguration()
    {
        int seatCount = (Integer) seatCountSpinner.getValue();
        Typist[] typists = createTypistsFromGUI();
        
        String message = "Race configuration:\n\n"
            + "Passage length: " + selectedPassage.length() + " characters\n"
            + "Number of racers: " + seatCount + "\n"
            + "Autocorrect: " + autocorrectCheckBox.isSelected() + "\n"
            + "Caffeine Mode: " + caffeineModeCheckBox.isSelected() + "\n"
            + "Night Shift: " + nightShiftCheckBox.isSelected() + "\n\n"
            + "Typists:\n";

        for (int i = 0; i < seatCount; i++)
        {
            message += typistNames[i]
                + " | Symbol: " + symbolBoxes[i].getSelectedItem()
                + " | Style: " + typingStyleBoxes[i].getSelectedItem()
                + " | Keyboard: " + keyboardTypeBoxes[i].getSelectedItem()
                + " | Accuracy: " + String.format("%.2f", typists[i].getAccuracy())
                + "\n";
        }

        JOptionPane.showMessageDialog(frame, message, "Race Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates the customisation panel for typist options.
     * Each typist has their own tab.
     *
     * @return the customise typists panel
     */
    private JPanel createCustomiseTypistsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        typistTabbedPane = new JTabbedPane();
        panel.add(typistTabbedPane, BorderLayout.CENTER);

        updateTypistTabs();

        return panel;
    }

    /**
     * Updates the typist tabs based on the selected number of racers.
     */
    @SuppressWarnings("unchecked")
    private void updateTypistTabs()
    {
        if (typistTabbedPane == null || seatCountSpinner == null)
        {
            return;
        }

        int count = (Integer) seatCountSpinner.getValue();

        typingStyleBoxes = new JComboBox[count];
        keyboardTypeBoxes = new JComboBox[count];
        symbolBoxes = new JComboBox[count];
        colourBoxes = new JComboBox[count];
        wristSupportBoxes = new JCheckBox[count];
        energyDrinkBoxes = new JCheckBox[count];
        noiseCancellingBoxes = new JCheckBox[count];
        impactLabels = new JLabel[count];

        typistTabbedPane.removeAll();

        for (int i = 0; i < count; i++)
        {
            typistTabbedPane.addTab(availableSymbols[i] + " " + typistNames[i], createSingleTypistPanel(i));
        }

        updateSymbolAvailability();

        typistTabbedPane.revalidate();
        typistTabbedPane.repaint();
    }

    /**
     * Creates a customisation panel for one typist.
     *
     * @param index the index of the typist
     * @return the typist customisation panel
     */
    private JPanel createSingleTypistPanel(int index)
    {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createTypingStylePanel(index));
        panel.add(createKeyboardTypePanel(index));
        panel.add(createSymbolColourPanel(index));
        panel.add(createAccessoriesPanel(index));

        return panel;
    }

    /**
     * Creates the typing style customisation section for one typist.
     *
     * @param index the index of the typist
     * @return the typing style panel
     */
    private JPanel createTypingStylePanel(int index)
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Typing Style"));

        String[] typingStyles = {
            "Touch Typist",
            "Hunt & Peck",
            "Phone Thumbs",
            "Voice-to-Text"
        };

        typingStyleBoxes[index] = new JComboBox<>(typingStyles);
        JLabel impactLabel = new JLabel("Impact: Balanced accuracy and speed.");

        typingStyleBoxes[index].addActionListener(e -> updateTypistImpact(index));

        panel.add(new JLabel("Choose typing style:"));
        panel.add(typingStyleBoxes[index]);
        panel.add(impactLabel);

        return panel;
    }

    /**
     * Builds Typist objects from the current GUI customisation choices.
     *
     * @return an array of Typist objects
     */
    private Typist[] createTypistsFromGUI()
    {
        int seatCount = (Integer) seatCountSpinner.getValue();
        Typist[] typists = new Typist[seatCount];

        for (int i = 0; i < seatCount; i++)
        {
            String name = typistNames[i];
            String selectedSymbol = (String) symbolBoxes[i].getSelectedItem();
            char symbol = selectedSymbol.charAt(0);

            double accuracy = 0.70;

            if ("Touch Typist".equals(typingStyleBoxes[i].getSelectedItem()))
            {
                accuracy = accuracy + 0.10;
            }
            else if ("Hunt & Peck".equals(typingStyleBoxes[i].getSelectedItem()))
            {
                accuracy = accuracy - 0.10;
            }
            else if ("Voice-to-Text".equals(typingStyleBoxes[i].getSelectedItem()))
            {
                accuracy = accuracy + 0.05;
            }

            if ("Mechanical".equals(keyboardTypeBoxes[i].getSelectedItem()))
            {
                accuracy = accuracy + 0.05;
            }
            else if ("Touchscreen".equals(keyboardTypeBoxes[i].getSelectedItem()))
            {
                accuracy = accuracy - 0.05;
            }

            if (nightShiftCheckBox.isSelected())
            {
                accuracy = accuracy - 0.05;
            }

            if (energyDrinkBoxes[i].isSelected())
            {
                accuracy = accuracy + 0.05;
            }

            typists[i] = new Typist(symbol, name, accuracy);
        }

        return typists;
    }

    /**
     * Creates the keyboard type customisation section for one typist.
     *
     * @param index the index of the typist
     * @return the keyboard type panel
     */
    private JPanel createKeyboardTypePanel(int index)
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Keyboard Type"));

        String[] keyboardTypes = {
            "Mechanical",
            "Membrane",
            "Touchscreen",
            "Stenography"
        };

        keyboardTypeBoxes[index] = new JComboBox<>(keyboardTypes);
        JLabel impactLabel = new JLabel("Impact: Steady typing performance.");

        keyboardTypeBoxes[index].addActionListener(e -> updateTypistImpact(index));

        panel.add(new JLabel("Choose keyboard type:"));
        panel.add(keyboardTypeBoxes[index]);
        panel.add(impactLabel);

        return panel;
    }

    /**
     * Creates the symbol and colour customisation section for one typist.
     *
     * @param index the index of the typist
     * @return the symbol and colour panel
     */
    private JPanel createSymbolColourPanel(int index)
    {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Symbol and Colour"));

        String[] colours = {
            "Red",
            "Blue",
            "Green",
            "Purple",
            "Orange",
            "Black"
        };

        symbolBoxes[index] = new JComboBox<>(availableSymbols);
        symbolBoxes[index].setSelectedItem(availableSymbols[index]);
        symbolBoxes[index].addActionListener(e -> updateSymbolAvailability());

        colourBoxes[index] = new JComboBox<>(colours);

        panel.add(new JLabel("Typist symbol:"));
        panel.add(symbolBoxes[index]);
        panel.add(new JLabel("Progress colour:"));
        panel.add(colourBoxes[index]);

        return panel;
    }

    /**
     * Updates all symbol dropdowns so a symbol already used by one typist
     * cannot be selected by another typist.
     */
    private void updateSymbolAvailability()
    {
        if (updatingSymbols || symbolBoxes == null)
        {
            return;
        }

        updatingSymbols = true;

        int count = (Integer) seatCountSpinner.getValue();
        String[] selectedSymbols = new String[count];

        for (int i = 0; i < count; i++)
        {
            if (symbolBoxes[i] != null)
            {
                selectedSymbols[i] = (String) symbolBoxes[i].getSelectedItem();
            }
        }

        for (int i = 0; i < count; i++)
        {
            if (symbolBoxes[i] != null)
            {
                String currentSymbol = selectedSymbols[i];

                symbolBoxes[i].removeAllItems();

                for (String symbol : availableSymbols)
                {
                    if (symbol.equals(currentSymbol) || !symbolIsUsedByAnotherTypist(symbol, selectedSymbols, i))
                    {
                        symbolBoxes[i].addItem(symbol);
                    }
                }

                symbolBoxes[i].setSelectedItem(currentSymbol);
                typistTabbedPane.setTitleAt(i, currentSymbol + " " + typistNames[i]);
            }
        }

        updatingSymbols = false;
    }

    /**
     * Checks whether a symbol is already selected by another typist.
     *
     * @param symbol the symbol to check
     * @param selectedSymbols the currently selected symbols
     * @param currentIndex the typist currently being checked
     * @return true if another typist already uses the symbol
     */
    private boolean symbolIsUsedByAnotherTypist(String symbol, String[] selectedSymbols, int currentIndex)
    {
        for (int i = 0; i < selectedSymbols.length; i++)
        {
            if (i != currentIndex && symbol.equals(selectedSymbols[i]))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates the accessories section for one typist.
     *
     * @param index the index of the typist
     * @return the accessories panel
     */
    private JPanel createAccessoriesPanel(int index)
    {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        wristSupportBoxes[index] = new JCheckBox("Wrist Support - reduces burnout duration");
        energyDrinkBoxes[index] = new JCheckBox("Energy Drink - boosts accuracy early, reduces later");
        noiseCancellingBoxes[index] = new JCheckBox("Noise-Cancelling Headphones - reduces mistype chance");

        impactLabels[index] = new JLabel("Impact: No accessories selected.");

        wristSupportBoxes[index].addActionListener(e -> updateTypistImpact(index));
        energyDrinkBoxes[index].addActionListener(e -> updateTypistImpact(index));
        noiseCancellingBoxes[index].addActionListener(e -> updateTypistImpact(index));

        JButton applyButton = new JButton("Apply / Preview Effects");
        applyButton.addActionListener(e -> applyAccessoryEffects(index));

        panel.add(wristSupportBoxes[index]);
        panel.add(energyDrinkBoxes[index]);
        panel.add(noiseCancellingBoxes[index]);
        panel.add(impactLabels[index]);
        panel.add(applyButton);

        return panel;
    }

    /**
     * Updates the impact label for one typist based on selected customisation options.
     *
     * @param index the index of the typist
     */
    private void updateTypistImpact(int index)
    {
        String impact = "Impact: ";

        if (wristSupportBoxes[index].isSelected())
        {
            impact += "Lower burnout duration. ";
        }

        if (energyDrinkBoxes[index].isSelected())
        {
            impact += "Higher early accuracy, lower later accuracy. ";
        }

        if (noiseCancellingBoxes[index].isSelected())
        {
            impact += "Reduced mistype chance. ";
        }

        if (!wristSupportBoxes[index].isSelected() &&
            !energyDrinkBoxes[index].isSelected() &&
            !noiseCancellingBoxes[index].isSelected())
        {
            impact += "No accessories selected.";
        }

        impactLabels[index].setText(impact);
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
     * Calculates and displays the selected accessory effects for one typist.
     *
     * @param index the index of the typist
     */
    private void applyAccessoryEffects(int index)
    {
        int burnoutDurationChange = 0;
        double accuracyChange = 0.0;
        double mistypeChanceChange = 0.0;

        if (wristSupportBoxes[index].isSelected())
        {
            burnoutDurationChange = burnoutDurationChange - 1;
        }

        if (energyDrinkBoxes[index].isSelected())
        {
            accuracyChange = accuracyChange + 0.05;
        }

        if (noiseCancellingBoxes[index].isSelected())
        {
            mistypeChanceChange = mistypeChanceChange - 0.05;
        }

        JOptionPane.showMessageDialog(frame,
            "Effects for " + typistNames[index] + ":\n"
            + "Burnout duration change: " + burnoutDurationChange + "\n"
            + "Accuracy change: " + accuracyChange + "\n"
            + "Mistype chance change: " + mistypeChanceChange,
            "Typist Effects",
            JOptionPane.INFORMATION_MESSAGE);
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