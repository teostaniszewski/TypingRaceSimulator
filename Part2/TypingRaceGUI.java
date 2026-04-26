package Part2;

import javax.swing.*;
import javax.swing.text.*;
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
    private Timer raceTimer;

    private Typist[] currentTypists;
    private JTextPane[] raceTextPanes;
    private JLabel[] raceStatusLabels;
    private boolean[] justMistyped;

    private JTextArea customPassageArea;
    private JSpinner seatCountSpinner;

    private JCheckBox autocorrectCheckBox;
    private JCheckBox caffeineModeCheckBox;
    private JCheckBox nightShiftCheckBox;
    private JCheckBox[] wristSupportBoxes;
    private JCheckBox[] energyDrinkBoxes;
    private JCheckBox[] noiseCancellingBoxes;

    private JTabbedPane typistTabbedPane;

    private JComboBox<String> passageComboBox;
    private JComboBox<String>[] typingStyleBoxes;
    private JComboBox<String>[] keyboardTypeBoxes;
    private JComboBox<String>[] symbolBoxes;
    private JComboBox<String>[] colourBoxes;

    private JLabel[] impactLabels;
    private JLabel passageLengthLabel;

    private JButton startRaceButton;
    private JButton viewStatsButton;

    private boolean updatingSymbols;
    private String selectedPassage;

    private int[] mistypeCounts;
    private int[] burnoutCounts;

    private double[] startingAccuracies;
    private double[] finalAccuracies;
    private double[] bestWpmRecords;

    private long raceStartTime;
    private String lastRaceStats;

    private static final double MISTYPE_BASE_CHANCE = 0.30;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;

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
        lastRaceStats = "No race has been run yet.\n\nStart a race to see statistics here.";
        bestWpmRecords = new double[typistNames.length];
        updatingSymbols = false;
        createWindow();
    }

    /**
     * Creates the main application window.
     */
    private void createWindow()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1250, 750);
        frame.setLocationRelativeTo(null);

        frame.add(createMainPanel());
        frame.setVisible(true);
    }

    /**
     * Creates the main setup panel containing configuration, customisation, and stats tabs.
     *
     * @return the main setup panel
     */
    private JPanel createMainPanel()
    {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Typing Race Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Race Configuration", createRaceConfigurationPanel());
        tabbedPane.addTab("Customise Typists", createCustomiseTypistsPanel());
        tabbedPane.addTab("Stats", createStatsPanel());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates the main race configuration panel.
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
     * Shows the race screen and starts the animated race.
     */
    private void showRaceScreen()
    {
        if (selectedPassage.trim().isEmpty())
        {
            JOptionPane.showMessageDialog(frame,
                "Please select or enter a passage before starting the race.",
                "Missing Passage",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentTypists = createTypistsFromGUI();
        raceTextPanes = new JTextPane[currentTypists.length];
        raceStatusLabels = new JLabel[currentTypists.length];
        justMistyped = new boolean[currentTypists.length];

        mistypeCounts = new int[currentTypists.length];
        burnoutCounts = new int[currentTypists.length];
        startingAccuracies = new double[currentTypists.length];
        finalAccuracies = new double[currentTypists.length];
        raceStartTime = System.currentTimeMillis();

        for (int i = 0; i < currentTypists.length; i++)
        {
            startingAccuracies[i] = currentTypists[i].getAccuracy();
        }

        JPanel racePanel = new JPanel(new BorderLayout(10, 10));
        racePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Typing Race", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel lanesPanel = new JPanel(new GridLayout(currentTypists.length, 1, 5, 5));

        for (int i = 0; i < currentTypists.length; i++)
        {
            lanesPanel.add(createRaceLanePanel(currentTypists[i], i));
        }

        racePanel.add(titleLabel, BorderLayout.NORTH);
        racePanel.add(lanesPanel, BorderLayout.CENTER);
        racePanel.add(createRaceButtonPanel(), BorderLayout.SOUTH);

        frame.setContentPane(racePanel);
        frame.revalidate();
        frame.repaint();

        startRaceAnimation();
    }

    /**
     * Creates buttons shown on the race screen.
     *
     * @return the race button panel
     */
    private JPanel createRaceButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        viewStatsButton = new JButton("View Stats");
        viewStatsButton.setEnabled(false);
        viewStatsButton.addActionListener(e -> showStatsWindow());

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> showSetupScreen());

        panel.add(viewStatsButton);
        panel.add(backButton);

        return panel;
    }

    /**
     * Returns the user back to the configuration and customisation menu.
     */
    private void showSetupScreen()
    {
        if (raceTimer != null)
        {
            raceTimer.stop();
        }

        frame.setContentPane(createMainPanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates one visual race lane for a typist.
     *
     * @param typist the typist displayed in the lane
     * @param index the index of the typist
     * @return the race lane panel
     */
    private JPanel createRaceLanePanel(Typist typist, int index)
    {
        JPanel lanePanel = new JPanel(new BorderLayout(10, 10));
        String hex = toHex(getSelectedColourSafe(index));

        lanePanel.setBorder(BorderFactory.createTitledBorder(
            "<html><font color='" + hex + "'>" + typist.getSymbol() + "</font> " + typist.getName() + "</html>"
        ));

        JTextPane passagePane = new JTextPane();
        passagePane.setEditable(false);
        passagePane.setText(selectedPassage);
        raceTextPanes[index] = passagePane;

        raceStatusLabels[index] = new JLabel("Ready");

        lanePanel.add(new JScrollPane(passagePane), BorderLayout.CENTER);
        lanePanel.add(raceStatusLabels[index], BorderLayout.SOUTH);

        return lanePanel;
    }

    /**
     * Starts the GUI race animation using a Swing timer.
     */
    private void startRaceAnimation()
    {
        raceTimer = new Timer(450, e -> runRaceTurn());
        raceTimer.start();
    }

    /**
     * Runs one turn of the race animation.
     * Uses Typist methods for progress, slide-back and burnout state.
     */
    private void runRaceTurn()
    {
        for (int i = 0; i < currentTypists.length; i++)
        {
            Typist typist = currentTypists[i];
            justMistyped[i] = false;

            if (typist.isBurntOut())
            {
                typist.recoverFromBurnout();
                updateRaceText(i);
                updateRaceStatus(i);
                continue;
            }

            if (Math.random() < typist.getAccuracy())
            {
                typist.typeCharacter();
            }

            double mistypeChance = (1.0 - typist.getAccuracy()) * MISTYPE_BASE_CHANCE;

            if (noiseCancellingBoxes[i].isSelected())
            {
                mistypeChance = mistypeChance - 0.05;
            }

            if (mistypeChance < 0.0)
            {
                mistypeChance = 0.0;
            }

            if (Math.random() < mistypeChance)
            {
                int slideBackAmount = SLIDE_BACK_AMOUNT;

                if (autocorrectCheckBox.isSelected())
                {
                    slideBackAmount = 1;
                }

                typist.slideBack(slideBackAmount);
                justMistyped[i] = true;
                mistypeCounts[i]++;
            }

            if (Math.random() < 0.05 * typist.getAccuracy() * typist.getAccuracy())
            {
                int burnoutDuration = BURNOUT_DURATION;

                if (wristSupportBoxes[i].isSelected())
                {
                    burnoutDuration = burnoutDuration - 1;
                }

                typist.burnOut(burnoutDuration);
                burnoutCounts[i]++;
                typist.setAccuracy(typist.getAccuracy() - 0.01);
            }

            updateRaceText(i);
            updateRaceStatus(i);

            if (typist.getProgress() >= selectedPassage.length())
            {
                finishRace(typist);
                return;
            }
        }
    }

    /**
     * Finishes the race, stores final statistics, and enables the stats button.
     *
     * @param winner the winning typist
     */
    private void finishRace(Typist winner)
    {
        raceTimer.stop();

        double oldAccuracy = winner.getAccuracy();
        winner.setAccuracy(oldAccuracy + 0.02);

        for (int i = 0; i < currentTypists.length; i++)
        {
            finalAccuracies[i] = currentTypists[i].getAccuracy();
        }

        lastRaceStats = buildStatsText();

        if (viewStatsButton != null)
        {
            viewStatsButton.setEnabled(true);
        }

        JOptionPane.showMessageDialog(frame,
            "And the winner is... " + winner.getName() + "!\n"
            + "Final accuracy: " + String.format("%.2f", winner.getAccuracy())
            + " (improved from " + String.format("%.2f", oldAccuracy) + ")",
            "Race Finished",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates the visible passage text for one typist.
     * Typed text is highlighted using the typist's selected colour.
     * The current character is highlighted in yellow.
     *
     * @param index the index of the typist
     */
    private void updateRaceText(int index)
    {
        int progress = currentTypists[index].getProgress();

        if (progress > selectedPassage.length())
        {
            progress = selectedPassage.length();
        }

        JTextPane pane = raceTextPanes[index];
        StyledDocument doc = pane.getStyledDocument();

        try
        {
            doc.remove(0, doc.getLength());

            SimpleAttributeSet typedStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(typedStyle, getSelectedColour(index));
            StyleConstants.setBold(typedStyle, true);

            SimpleAttributeSet currentStyle = new SimpleAttributeSet();
            StyleConstants.setBackground(currentStyle, Color.YELLOW);
            StyleConstants.setForeground(currentStyle, Color.BLACK);
            StyleConstants.setBold(currentStyle, true);

            SimpleAttributeSet remainingStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(remainingStyle, Color.BLACK);

            doc.insertString(doc.getLength(), selectedPassage.substring(0, progress), typedStyle);

            if (progress < selectedPassage.length())
            {
                doc.insertString(doc.getLength(), selectedPassage.substring(progress, progress + 1), currentStyle);
                doc.insertString(doc.getLength(), selectedPassage.substring(progress + 1), remainingStyle);
            }
        }
        catch (BadLocationException e)
        {
            pane.setText(selectedPassage);
        }
    }

    /**
     * Updates the status label for one typist.
     *
     * @param index the index of the typist
     */
    private void updateRaceStatus(int index)
    {
        Typist typist = currentTypists[index];

        if (typist.isBurntOut())
        {
            raceStatusLabels[index].setText("BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)");
        }
        else if (justMistyped[index])
        {
            raceStatusLabels[index].setText("Just mistyped [<]");
        }
        else
        {
            raceStatusLabels[index].setText("Progress: " + typist.getProgress()
                + " / " + selectedPassage.length()
                + " | Accuracy: " + String.format("%.2f", typist.getAccuracy()));
        }
    }

    /**
     * Displays race statistics for all typists.
     */
    private void showStatsWindow()
    {
        JOptionPane.showMessageDialog(frame, lastRaceStats, "Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Builds the race statistics text.
     *
     * @return formatted race statistics
     */
    private String buildStatsText()
    {
        double elapsedMinutes = (System.currentTimeMillis() - raceStartTime) / 60000.0;
        String stats = "Race Statistics:\n\n";

        for (int i = 0; i < currentTypists.length; i++)
        {
            double wordsTyped = currentTypists[i].getProgress() / 5.0;
            double wpm = 0.0;

            if (elapsedMinutes > 0)
            {
                wpm = wordsTyped / elapsedMinutes;
            }

            if (wpm > bestWpmRecords[i])
            {
                bestWpmRecords[i] = wpm;
            }

            int totalAttempts = currentTypists[i].getProgress() + mistypeCounts[i];
            double accuracyPercent = 100.0;

            if (totalAttempts > 0)
            {
                accuracyPercent = ((double) currentTypists[i].getProgress() / totalAttempts) * 100.0;
            }

            stats += currentTypists[i].getName()
                + "\nWPM: " + String.format("%.2f", wpm)
                + "\nAccuracy percentage: " + String.format("%.2f", accuracyPercent) + "%"
                + "\nMistypes: " + mistypeCounts[i]
                + "\nBurnouts: " + burnoutCounts[i]
                + "\nStarting accuracy: " + String.format("%.2f", startingAccuracies[i])
                + "\nFinal accuracy: " + String.format("%.2f", finalAccuracies[i])
                + "\nAccuracy change: " + String.format("%.2f", finalAccuracies[i] - startingAccuracies[i])
                + "\n\n";
        }

        return stats;
    }

    /**
     * Creates the statistics tab.
     * This contains separate tabs for summary, personal bests, history, comparison, and charts.
     *
     * @return the stats panel
     */
    private JPanel createStatsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane statsTabs = new JTabbedPane();

        statsTabs.addTab("Last Race", createStatsTextPanel(lastRaceStats));
        statsTabs.addTab("Personal Bests", createStatsTextPanel(buildPersonalBestsText()));
        statsTabs.addTab("Race History", createStatsTextPanel("Full race history will appear here."));
        statsTabs.addTab("Comparison", createStatsTextPanel("Comparison view will be added here."));
        statsTabs.addTab("Charts", createStatsTextPanel("Graphical display will be added here."));

        panel.add(statsTabs, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the personal best WPM statistics text.
     *
     * @return formatted personal best statistics
     */
    private String buildPersonalBestsText()
    {
        String text = "Personal Bests:\n\n";

        for (int i = 0; i < typistNames.length; i++)
        {
            text += typistNames[i]
                + " - Best WPM: "
                + String.format("%.2f", bestWpmRecords[i])
                + "\n";
        }

        return text;
    }

    /**
     * Creates a simple scrollable text panel for a statistics section.
     *
     * @param text the text to display
     * @return the text statistics panel
     */
    private JPanel createStatsTextPanel(String text)
    {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Gets the selected colour for a typist.
     *
     * @param index the index of the typist
     * @return the selected colour
     */
    private Color getSelectedColour(int index)
    {
        String colour = (String) colourBoxes[index].getSelectedItem();

        if ("Red".equals(colour))
        {
            return Color.RED;
        }
        else if ("Blue".equals(colour))
        {
            return Color.BLUE;
        }
        else if ("Green".equals(colour))
        {
            return new Color(0, 128, 0);
        }
        else if ("Purple".equals(colour))
        {
            return new Color(128, 0, 128);
        }
        else if ("Orange".equals(colour))
        {
            return Color.ORANGE;
        }

        return Color.BLACK;
    }

    /**
     * Safely gets the selected colour for a typist.
     * This prevents errors if the colour dropdown has not been created yet.
     *
     * @param index the index of the typist
     * @return the selected colour, or black as a fallback
     */
    private Color getSelectedColourSafe(int index)
    {
        if (colourBoxes == null || colourBoxes[index] == null)
        {
            return Color.BLACK;
        }

        return getSelectedColour(index);
    }

    /**
     * Converts a Color object into a hex colour string for HTML text.
     *
     * @param color the colour to convert
     * @return the colour as a hex string
     */
    private String toHex(Color color)
    {
        return String.format("#%02x%02x%02x",
            color.getRed(),
            color.getGreen(),
            color.getBlue());
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
            String hex = toHex(getSelectedColourSafe(i));

            typistTabbedPane.addTab(
                "<html><font color='" + hex + "'>" + availableSymbols[i] + "</font> " + typistNames[i] + "</html>",
                createSingleTypistPanel(i)
            );
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
        colourBoxes[index].addActionListener(e -> updateSymbolAvailability());

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

                String hex = toHex(getSelectedColourSafe(i));

                typistTabbedPane.setTitleAt(i,
                    "<html><font color='" + hex + "'>" + currentSymbol + "</font> " + typistNames[i] + "</html>"
                );
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
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        wristSupportBoxes[index] = new JCheckBox("Wrist Support - reduces burnout duration");
        energyDrinkBoxes[index] = new JCheckBox("Energy Drink - boosts accuracy early, reduces later");
        noiseCancellingBoxes[index] = new JCheckBox("Noise-Cancelling Headphones - reduces mistype chance");

        impactLabels[index] = new JLabel("Impact: No accessories selected.");

        wristSupportBoxes[index].addActionListener(e -> updateTypistImpact(index));
        energyDrinkBoxes[index].addActionListener(e -> updateTypistImpact(index));
        noiseCancellingBoxes[index].addActionListener(e -> updateTypistImpact(index));

        panel.add(wristSupportBoxes[index]);
        panel.add(energyDrinkBoxes[index]);
        panel.add(noiseCancellingBoxes[index]);
        panel.add(impactLabels[index]);

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

            if (caffeineModeCheckBox.isSelected())
            {
                accuracy = accuracy + 0.03;
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