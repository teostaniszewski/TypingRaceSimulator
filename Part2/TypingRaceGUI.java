package Part2;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
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
    private JLabel[] typingStyleImpactLabels;
    private JLabel[] keyboardTypeImpactLabels;
    private JLabel difficultyImpactLabel;
    private JLabel passageLengthLabel;
    private JLabel activeRacersLabel;

    private JButton startRaceButton;
    private JButton viewStatsButton;
    private JLabel raceWinnerLabel;

    private boolean updatingSymbols;
    private boolean showingCustomPassagePlaceholder;
    private String selectedPassage;

    private int[] mistypeCounts;
    private int[] burnoutCounts;

    private double[] startingAccuracies;
    private double[] finalAccuracies;
    private double[] bestWpmRecords;
    private double[] speedModifiers;
    private double[] mistypeRateModifiers;
    private double[] burnoutChanceModifiers;
    private int[] burnoutDurationModifiers;
    private boolean[] energyDrinkPenaltyApplied;

    private long raceStartTime;
    private String lastRaceStats;

    private String raceHistoryText;
    private int raceNumber;

    private static final Color BG_DARK = new Color(11, 17, 32);
    private static final Color PANEL_DARK = new Color(17, 24, 39);
    private static final Color PURPLE = new Color(139, 92, 246);
    private static final Color PURPLE_DARK = new Color(49, 46, 129);
    private static final Color TEXT_LIGHT = new Color(245, 245, 255);
    private static final Color TEXT_MUTED = new Color(145, 155, 174);
    private static final Color CHECKBOX_TEXT = new Color(125, 211, 252);
    private static final String CUSTOM_PASSAGE_PLACEHOLDER = "Type your custom passage here...";
    private static final String UI_FONT_NAME = "Segoe UI Symbol";

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
        "\u25A0 Block",
        "\u25B2 Triangle",
        "\u25C6 Diamond",
        "\u2605 Star",
        "\u25CF Circle",
        "\u2715 Cross"
    };

    private final String[] availableColours = {
        "Cyan",
        "Green",
        "Pink",
        "Purple",
        "Orange",
        "Red",
        "Yellow"
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
        showingCustomPassagePlaceholder = false;
        raceHistoryText = "No race history available yet.";
        raceNumber = 0;
        createWindow();
    }

    /**
     * Creates the main application window.
     */
    private void createWindow()
    {
        configureThemeDefaults();

        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 850);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_DARK);

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
        JPanel mainPanel = new JPanel(new BorderLayout(12, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 30, 26, 30));

        JLabel titleLabel = new JLabel("< Typing Race Simulator />", JLabel.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(PURPLE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Race Configuration", createRaceConfigurationPanel());
        tabbedPane.addTab("Customise Typists", createCustomiseTypistsPanel());
        tabbedPane.addTab("Stats", createStatsPanel());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        styleTabbedPane(tabbedPane);
        stylePanel(mainPanel);

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
        panel.setBorder(BorderFactory.createEmptyBorder(42, 24, 22, 24));

        JPanel passageBox = createPassageSection();
        passageBox.setBorder(createThemedBorder("Passage Selection"));
        passageBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        passageBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel seatBox = createSeatCountSection();
        seatBox.setBorder(createThemedBorder("Seat Count"));
        seatBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        seatBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel difficultyBox = createDifficultySection();
        difficultyBox.setBorder(createThemedBorder("Difficulty Modifiers"));
        difficultyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 178));
        difficultyBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(passageBox);
        panel.add(Box.createVerticalStrut(18));
        panel.add(seatBox);
        panel.add(Box.createVerticalStrut(18));
        panel.add(difficultyBox);
        panel.add(Box.createVerticalStrut(34));
        JPanel startButtonSection = createStartButtonSection();
        startButtonSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        startButtonSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(startButtonSection);
        panel.add(Box.createVerticalGlue());
        stylePanel(panel);

        return panel;
    }

    /**
     * Creates the passage selection section.
     *
     * @return the passage selection section
     */
    private JPanel createPassageSection()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

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
        scrollPane.setPreferredSize(new Dimension(0, 92));

        passageLengthLabel = new JLabel("Passage length: 44 characters");
        passageLengthLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        passageComboBox.addActionListener(e -> updateSelectedPassage());

        customPassageArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e)
            {
                updateSelectedPassage();
            }
        });

        customPassageArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e)
            {
                if ("Custom Passage".equals(passageComboBox.getSelectedItem()))
                {
                    clearCustomPassagePlaceholder();
                }
            }

            public void focusLost(java.awt.event.FocusEvent e)
            {
                if ("Custom Passage".equals(passageComboBox.getSelectedItem()) &&
                    customPassageArea.getText().trim().isEmpty())
                {
                    showCustomPassagePlaceholder();
                }

                updateSelectedPassage();
            }
        });

        panel.add(passageComboBox, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(passageLengthLabel, BorderLayout.SOUTH);

        updateSelectedPassage();
        styleSectionPanel(panel);
        styleImpactLabel(passageLengthLabel);
        styleComboBox(passageComboBox);
        styleTextArea(customPassageArea);
        styleScrollPane(scrollPane);
        showCustomPassagePlaceholder();

        return panel;
    }

    /**
     * Creates the seat count section.
     *
     * @return the seat count section
     */
    private JPanel createSeatCountSection()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel label = new JLabel("Number of racers:");

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, 6, 1);
        seatCountSpinner = new JSpinner(spinnerModel);
        seatCountSpinner.setPreferredSize(new Dimension(70, 34));
        activeRacersLabel = new JLabel();

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) seatCountSpinner.getEditor();
        editor.getTextField().setEditable(false);
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);

        seatCountSpinner.addChangeListener(e -> {
            updateTypistTabs();
            updateActiveRacersLabel();
        });

        panel.add(label);
        panel.add(seatCountSpinner);
        panel.add(Box.createHorizontalStrut(16));
        panel.add(activeRacersLabel);

        styleSectionPanel(panel);
        styleLabel(label);
        styleImpactLabel(activeRacersLabel);
        styleSpinner(seatCountSpinner);
        updateActiveRacersLabel();
        return panel;
    }

    /**
     * Updates the list of active racers shown beside the seat count spinner.
     */
    private void updateActiveRacersLabel()
    {
        if (activeRacersLabel == null || seatCountSpinner == null)
        {
            return;
        }

        int count = (Integer) seatCountSpinner.getValue();
        String text = "<html><span style=\"color:" + toHex(TEXT_MUTED) + ";\">Racers: </span>";

        for (int i = 0; i < count; i++)
        {
            if (i > 0)
            {
                text += "<span style=\"color:" + toHex(TEXT_MUTED) + ";\"> &nbsp; </span>";
            }

            text += "<span style=\"font-family:'Segoe UI Symbol'; color:"
                + toHex(getTypistDisplayColour(i)) + ";\">"
                + getTypistDisplaySymbol(i) + "</span> "
                + "<span style=\"color:" + toHex(getTypistDisplayColour(i)) + ";\">"
                + typistNames[i] + "</span>";
        }

        activeRacersLabel.setText(text + "</html>");
    }

    /**
     * Creates the difficulty modifier section.
     *
     * @return the difficulty modifier section
     */
    private JPanel createDifficultySection()
    {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        autocorrectCheckBox = new JCheckBox("Autocorrect On");
        caffeineModeCheckBox = new JCheckBox("Caffeine Mode");
        nightShiftCheckBox = new JCheckBox("Night Shift");
        difficultyImpactLabel = new JLabel("Impact: No difficulty modifiers selected.");

        optionsPanel.add(autocorrectCheckBox);
        optionsPanel.add(Box.createVerticalStrut(4));
        optionsPanel.add(caffeineModeCheckBox);
        optionsPanel.add(Box.createVerticalStrut(4));
        optionsPanel.add(nightShiftCheckBox);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(difficultyImpactLabel);
        panel.add(optionsPanel, BorderLayout.WEST);

        autocorrectCheckBox.addActionListener(e -> updateDifficultyImpact());
        caffeineModeCheckBox.addActionListener(e -> updateDifficultyImpact());
        nightShiftCheckBox.addActionListener(e -> updateDifficultyImpact());

        styleSectionPanel(panel);
        styleSectionPanel(optionsPanel);
        styleCheckBox(autocorrectCheckBox);
        styleCheckBox(caffeineModeCheckBox);
        styleCheckBox(nightShiftCheckBox);
        styleImpactLabel(difficultyImpactLabel);
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
        styleButton(startRaceButton);

        stylePanel(panel);

        return panel;
    }

    /**
     * Updates the impact label for selected difficulty modifiers.
     */
    private void updateDifficultyImpact()
    {
        String impact = "Impact: ";

        if (autocorrectCheckBox.isSelected())
        {
            impact += "Slide back amount is reduced. ";
        }

        if (caffeineModeCheckBox.isSelected())
        {
            impact += "Higher speed and accuracy, higher burnout risk. ";
        }

        if (nightShiftCheckBox.isSelected())
        {
            impact += "Accuracy reduced, mistype rate increased. ";
        }

        if (!autocorrectCheckBox.isSelected() &&
            !caffeineModeCheckBox.isSelected() &&
            !nightShiftCheckBox.isSelected())
        {
            impact += "No difficulty modifiers selected.";
        }

        difficultyImpactLabel.setText(impact);
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

        int seatCount = (Integer) seatCountSpinner.getValue();
        speedModifiers = new double[seatCount];
        mistypeRateModifiers = new double[seatCount];
        burnoutChanceModifiers = new double[seatCount];
        burnoutDurationModifiers = new int[seatCount];
        energyDrinkPenaltyApplied = new boolean[seatCount];

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

        JPanel racePanel = new JPanel(new BorderLayout(12, 18));
        racePanel.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 34));

        JLabel titleLabel = new JLabel("< Typing Race Simulator />", JLabel.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(PURPLE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel lanesPanel = new ScrollableContentPanel();
        lanesPanel.setLayout(new BoxLayout(lanesPanel, BoxLayout.Y_AXIS));
        lanesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        stylePanel(lanesPanel);

        for (int i = 0; i < currentTypists.length; i++)
        {
            lanesPanel.add(createRaceLanePanel(currentTypists[i], i));
            lanesPanel.add(Box.createVerticalStrut(14));
        }

        JScrollPane lanesScrollPane = new JScrollPane(lanesPanel);
        lanesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleBorderlessScrollPane(lanesScrollPane);

        racePanel.add(titleLabel, BorderLayout.NORTH);
        racePanel.add(lanesScrollPane, BorderLayout.CENTER);
        racePanel.add(createRaceButtonPanel(), BorderLayout.SOUTH);

        frame.setContentPane(racePanel);
        frame.revalidate();
        frame.repaint();
        stylePanel(racePanel);

        startRaceAnimation();
    }

    /**
     * Creates buttons shown on the race screen.
     *
     * @return the race button panel
     */
    private JPanel createRaceButtonPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(14, 0));

        raceWinnerLabel = new JLabel("Race in progress...");
        raceWinnerLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

        viewStatsButton = new JButton("View Stats");
        viewStatsButton.setEnabled(false);
        viewStatsButton.addActionListener(e -> showStatsWindow());

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> showSetupScreen());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 5));
        buttonsPanel.add(viewStatsButton);
        buttonsPanel.add(backButton);

        panel.add(raceWinnerLabel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.EAST);
        stylePanel(panel);
        stylePanel(buttonsPanel);
        styleLabel(raceWinnerLabel);
        styleButton(viewStatsButton);
        styleButton(backButton);

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
        JPanel lanePanel = new JPanel(new BorderLayout(12, 12));
        Dimension laneSize = new Dimension(0, 165);

        lanePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lanePanel.setPreferredSize(laneSize);
        lanePanel.setMinimumSize(new Dimension(0, 150));
        lanePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));

        javax.swing.border.TitledBorder laneBorder = BorderFactory.createTitledBorder(
            new RoundedLineBorder(PURPLE, 12),
            typist.getSymbol() + " " + typist.getName()
        );
        laneBorder.setTitleColor(getSelectedColourSafe(index));
        laneBorder.setTitleFont(new Font(UI_FONT_NAME, Font.BOLD, 13));
        lanePanel.setBorder(BorderFactory.createCompoundBorder(
            laneBorder,
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JTextPane passagePane = new JTextPane();
        passagePane.setEditable(false);
        passagePane.setText(selectedPassage);
        passagePane.setFont(new Font(UI_FONT_NAME, Font.BOLD, 15));
        passagePane.setPreferredSize(new Dimension(0, 76));
        raceTextPanes[index] = passagePane;

        raceStatusLabels[index] = new JLabel("Ready");
        raceStatusLabels[index].setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));

        JScrollPane scrollPane = new JScrollPane(passagePane);

        lanePanel.add(scrollPane, BorderLayout.CENTER);
        lanePanel.add(raceStatusLabels[index], BorderLayout.SOUTH);
        styleSectionPanel(lanePanel);
        styleTextPane(passagePane);
        styleLabel(raceStatusLabels[index]);
        styleScrollPane(scrollPane);

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
            applyMidRaceEnergyDrinkPenalty(i);

            if (typist.isBurntOut())
            {
                typist.recoverFromBurnout();
                updateRaceText(i);
                updateRaceStatus(i);
                continue;
            }

            typeCharactersForTurn(typist, speedModifiers[i]);

            double mistypeChance = (1.0 - typist.getAccuracy())
                * MISTYPE_BASE_CHANCE
                * mistypeRateModifiers[i];

            if (noiseCancellingBoxes[i].isSelected())
            {
                mistypeChance = mistypeChance * 0.75;
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

            double burnoutChance = 0.04 * burnoutChanceModifiers[i];

            if (caffeineModeCheckBox.isSelected())
            {
                burnoutChance = burnoutChance * 1.35;
            }

            if (Math.random() < burnoutChance)
            {
                int burnoutDuration = BURNOUT_DURATION + burnoutDurationModifiers[i];

                if (wristSupportBoxes[i].isSelected())
                {
                    burnoutDuration = burnoutDuration - 1;
                }

                if (burnoutDuration < 1)
                {
                    burnoutDuration = 1;
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
     * Gives a typist one or more typing attempts based on their speed profile.
     *
     * @param typist the typist taking this turn
     * @param speedModifier the typing speed multiplier for this typist
     */
    private void typeCharactersForTurn(Typist typist, double speedModifier)
    {
        int attempts = 0;
        double remainingSpeed = speedModifier;

        while (remainingSpeed >= 1.0)
        {
            attempts++;
            remainingSpeed = remainingSpeed - 1.0;
        }

        if (Math.random() < remainingSpeed)
        {
            attempts++;
        }

        for (int attempt = 0; attempt < attempts; attempt++)
        {
            if (Math.random() < typist.getAccuracy())
            {
                typist.typeCharacter();
            }
        }
    }

    /**
     * Applies the late-race drawback for the Energy Drink accessory once.
     *
     * @param index the index of the typist
     */
    private void applyMidRaceEnergyDrinkPenalty(int index)
    {
        if (!energyDrinkBoxes[index].isSelected() ||
            energyDrinkPenaltyApplied[index] ||
            selectedPassage.length() == 0)
        {
            return;
        }

        if (currentTypists[index].getProgress() >= selectedPassage.length() / 2)
        {
            currentTypists[index].setAccuracy(currentTypists[index].getAccuracy() - 0.10);
            speedModifiers[index] = speedModifiers[index] * 0.90;
            energyDrinkPenaltyApplied[index] = true;
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
        addRaceToHistory(winner);

        if (viewStatsButton != null)
        {
            viewStatsButton.setEnabled(true);
        }

        if (raceWinnerLabel != null)
        {
            raceWinnerLabel.setText("<html>Winner: " + winner.getName()
                + "<br>Accuracy: " + String.format("%.2f", oldAccuracy)
                + " -> " + String.format("%.2f", winner.getAccuracy()) + "</html>");
        }
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
            StyleConstants.setForeground(remainingStyle, TEXT_LIGHT);

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
     * Adds the completed race to the race history record.
     *
     * @param winner the winning typist
     */
    private void addRaceToHistory(Typist winner)
    {
        raceNumber++;

        if (raceNumber == 1)
        {
            raceHistoryText = "";
        }

        raceHistoryText += "Race " + raceNumber + "\n";
        raceHistoryText += "Winner: " + winner.getName() + "\n";
        raceHistoryText += lastRaceStats;
        raceHistoryText += "------------------------------\n\n";
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
     * Displays statistics for the current race only.
     */
    private void showStatsWindow()
    {
        JFrame statsFrame = new JFrame("Race Statistics");
        statsFrame.setSize(820, 560);
        statsFrame.setLocationRelativeTo(frame);
        statsFrame.getContentPane().setBackground(BG_DARK);
        statsFrame.add(createStatsTextPanel(lastRaceStats));
        statsFrame.setVisible(true);
    }

    /**
     * Builds a comparison view for two selected typists.
     *
     * @param firstIndex the first typist index
     * @param secondIndex the second typist index
     * @return formatted comparison statistics
     */
    private String buildComparisonText(int firstIndex, int secondIndex)
    {
        if (currentTypists == null)
        {
            return "No comparison data available yet.\n\nRun a race first.";
        }

        if (firstIndex >= currentTypists.length || secondIndex >= currentTypists.length)
        {
            return "One or both selected typists did not take part in the last race.";
        }

        if (firstIndex == secondIndex)
        {
            return "Please choose two different typists to compare.";
        }

        return "Comparison View:\n\n"
            + buildSingleTypistComparison(firstIndex)
            + "\n------------------------------\n\n"
            + buildSingleTypistComparison(secondIndex);
    }

    /**
     * Builds comparison text for one typist.
     *
     * @param index the typist index
     * @return formatted typist comparison text
     */
    private String buildSingleTypistComparison(int index)
    {
        return currentTypists[index].getName()
            + "\nProgress: " + currentTypists[index].getProgress() + " / " + selectedPassage.length()
            + "\nMistypes: " + mistypeCounts[index]
            + "\nBurnouts: " + burnoutCounts[index]
            + "\nStarting accuracy: " + String.format("%.2f", startingAccuracies[index])
            + "\nFinal accuracy: " + String.format("%.2f", finalAccuracies[index])
            + "\nAccuracy change: " + String.format("%.2f", finalAccuracies[index] - startingAccuracies[index]);
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
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JTabbedPane statsTabs = new JTabbedPane();

        statsTabs.addTab("Last Race", createStatsTextPanel(lastRaceStats));
        statsTabs.addTab("Personal Bests", createStatsTextPanel(buildPersonalBestsText()));
        statsTabs.addTab("Race History", createStatsTextPanel(raceHistoryText));
        statsTabs.addTab("Comparison", createComparisonPanel());
        statsTabs.addTab("Charts", createStatsTextPanel("Graphical display will be added here."));

        panel.add(statsTabs, BorderLayout.CENTER);
        stylePanel(panel);
        styleTabbedPane(statsTabs);

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
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        stylePanel(panel);
        styleTextArea(textArea);
        styleScrollPane(scrollPane);
        return panel;
    }

    /**
     * Creates a comparison panel where two typists can be selected and compared.
     *
     * @return the comparison panel
     */
    private JPanel createComparisonPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JComboBox<String> firstTypistBox = new JComboBox<>(typistNames);
        JComboBox<String> secondTypistBox = new JComboBox<>(typistNames);

        firstTypistBox.setSelectedIndex(0);
        secondTypistBox.setSelectedIndex(1);

        JTextArea comparisonArea = new JTextArea();
        comparisonArea.setEditable(false);

        JButton compareButton = new JButton("Compare");
        compareButton.addActionListener(e -> {
            int firstIndex = firstTypistBox.getSelectedIndex();
            int secondIndex = secondTypistBox.getSelectedIndex();

            if (firstIndex == secondIndex)
            {
                JOptionPane.showMessageDialog(panel,
                    "Please select two different typists.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            comparisonArea.setText(buildComparisonText(firstIndex, secondIndex));
        });

        JLabel firstLabel = new JLabel("Typist 1:");
        JLabel secondLabel = new JLabel("Typist 2:");

        selectionPanel.add(firstLabel);
        selectionPanel.add(firstTypistBox);
        selectionPanel.add(secondLabel);
        selectionPanel.add(secondTypistBox);
        selectionPanel.add(compareButton);

        firstTypistBox.addActionListener(e ->
            updateComparisonDropdown(firstTypistBox, secondTypistBox)
        );

        secondTypistBox.addActionListener(e ->
            updateComparisonDropdown(secondTypistBox, firstTypistBox)
        );

        updateComparisonDropdown(firstTypistBox, secondTypistBox);

        comparisonArea.setText(buildComparisonText(0, 1));

        panel.add(selectionPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(comparisonArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        stylePanel(panel);
        stylePanel(selectionPanel);
        styleLabel(firstLabel);
        styleLabel(secondLabel);
        styleComboBox(firstTypistBox);
        styleComboBox(secondTypistBox);
        styleTextArea(comparisonArea);
        styleScrollPane(scrollPane);
        styleButton(compareButton);

        return panel;
    }

    /**
     * Updates a dropdown so it excludes the selected value of the other dropdown.
     */
    private void updateComparisonDropdown(JComboBox<String> source, JComboBox<String> target)
    {
        String selected = (String) source.getSelectedItem();
        String currentTargetSelection = (String) target.getSelectedItem();

        target.removeAllItems();

        for (String name : typistNames)
        {
            if (!name.equals(selected))
            {
                target.addItem(name);
            }
        }

        // restore selection if still valid
        if (currentTargetSelection != null && !currentTargetSelection.equals(selected))
        {
            target.setSelectedItem(currentTargetSelection);
        }
        else
        {
            target.setSelectedIndex(0);
        }
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
        return getColourFromName(colour);
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
            return getTypistDisplayColour(index);
        }

        return getSelectedColour(index);
    }

    /**
     * Gets a typist display colour, falling back to the default colour list.
     *
     * @param index the index of the typist
     * @return the display colour for the typist
     */
    private Color getTypistDisplayColour(int index)
    {
        if (colourBoxes != null &&
            index >= 0 &&
            index < colourBoxes.length &&
            colourBoxes[index] != null)
        {
            return getSelectedColour(index);
        }

        if (index >= 0 && index < availableColours.length)
        {
            return getColourFromName(availableColours[index]);
        }

        return TEXT_LIGHT;
    }

    /**
     * Gets a typist display symbol, falling back to the default symbol list.
     *
     * @param index the index of the typist
     * @return the display symbol for the typist
     */
    private String getTypistDisplaySymbol(int index)
    {
        if (symbolBoxes != null &&
            index >= 0 &&
            index < symbolBoxes.length &&
            symbolBoxes[index] != null)
        {
            return getSymbolGlyph((String) symbolBoxes[index].getSelectedItem());
        }

        if (index >= 0 && index < availableSymbols.length)
        {
            return getSymbolGlyph(availableSymbols[index]);
        }

        return "";
    }

    /**
     * Converts a colour name into a Color object.
     *
     * @param colour the colour name
     * @return the matching Color object
     */
    private Color getColourFromName(String colour)
    {
        if ("Cyan".equals(colour))
        {
            return new Color(103, 232, 249);
        }
        else if ("Green".equals(colour))
        {
            return new Color(134, 239, 172);
        }
        else if ("Pink".equals(colour))
        {
            return new Color(244, 114, 182);
        }
        else if ("Purple".equals(colour))
        {
            return new Color(196, 181, 253);
        }
        else if ("Orange".equals(colour))
        {
            return new Color(251, 146, 60);
        }
        else if ("Red".equals(colour))
        {
            return new Color(248, 113, 113);
        }
        else if ("Yellow".equals(colour))
        {
            return new Color(250, 204, 21);
        }

        return Color.BLACK;
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
     * Gets only the display glyph from a symbol dropdown option.
     *
     * @param symbolOption the full symbol option text
     * @return the symbol glyph
     */
    private String getSymbolGlyph(String symbolOption)
    {
        if (symbolOption == null || symbolOption.isEmpty())
        {
            return "";
        }

        int spaceIndex = symbolOption.indexOf(" ");
        if (spaceIndex > 0)
        {
            return symbolOption.substring(0, spaceIndex);
        }

        return symbolOption.substring(0, 1);
    }

    /**
     * Creates the customisation panel for typist options.
     * Each typist has their own tab.
     *
     * @return the customise typists panel
     */
    private JPanel createCustomiseTypistsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        typistTabbedPane = new JTabbedPane();
        panel.add(typistTabbedPane, BorderLayout.CENTER);

        updateTypistTabs();
        stylePanel(panel);
        styleTabbedPane(typistTabbedPane);

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
        typingStyleImpactLabels = new JLabel[count];
        keyboardTypeImpactLabels = new JLabel[count];

        typistTabbedPane.removeAll();

        for (int i = 0; i < count; i++)
        {
            String hex = toHex(getSelectedColourSafe(i));
            String symbolGlyph = getSymbolGlyph(availableSymbols[i]);

            typistTabbedPane.addTab(
                typistNames[i],
                createTypistScrollPane(i)
            );
            typistTabbedPane.setTabComponentAt(i,
                createTypistTabLabel(symbolGlyph, typistNames[i], hex)
            );
        }

        updateSymbolAvailability();
        updateColourAvailability();
        updateActiveRacersLabel();

        typistTabbedPane.revalidate();
        typistTabbedPane.repaint();
    }

    /**
     * Creates a scrollable customisation page for one typist.
     *
     * @param index the index of the typist
     * @return the scrollable typist customisation page
     */
    private JScrollPane createTypistScrollPane(int index)
    {
        JScrollPane scrollPane = new JScrollPane(createSingleTypistPanel(index));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        styleBorderlessScrollPane(scrollPane);
        return scrollPane;
    }

    /**
     * Creates a customisation panel for one typist.
     *
     * @param index the index of the typist
     * @return the typist customisation panel
     */
    private JPanel createSingleTypistPanel(int index)
    {
        JPanel panel = new ScrollableContentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel typingStylePanel = createTypingStylePanel(index);
        JPanel keyboardTypePanel = createKeyboardTypePanel(index);
        JPanel symbolColourPanel = createSymbolColourPanel(index);
        JPanel accessoriesPanel = createAccessoriesPanel(index);

        typingStylePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 126));
        keyboardTypePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 126));
        symbolColourPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 138));
        accessoriesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 178));

        panel.add(typingStylePanel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(keyboardTypePanel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(symbolColourPanel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(accessoriesPanel);
        panel.add(Box.createVerticalGlue());
        stylePanel(panel);

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
        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));
        panel.setBorder(createThemedBorder("Typing Style"));

        String[] typingStyles = {
            "Touch Typist",
            "Hunt & Peck",
            "Phone Thumbs",
            "Voice-to-Text"
        };

        typingStyleBoxes[index] = new JComboBox<>(typingStyles);
        JLabel chooseLabel = new JLabel("Choose typing style:");
        typingStyleImpactLabels[index] = new JLabel();
        updateTypingStyleImpact(index);

        typingStyleBoxes[index].addActionListener(e -> updateTypingStyleImpact(index));

        panel.add(chooseLabel);
        panel.add(typingStyleBoxes[index]);
        panel.add(typingStyleImpactLabels[index]);
        styleSectionPanel(panel);
        styleLabel(chooseLabel);
        styleImpactLabel(typingStyleImpactLabels[index]);
        styleComboBox(typingStyleBoxes[index]);

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
        JPanel panel = new JPanel(new GridLayout(3, 1, 8, 8));
        panel.setBorder(createThemedBorder("Keyboard Type"));

        String[] keyboardTypes = {
            "Mechanical",
            "Membrane",
            "Touchscreen",
            "Stenography"
        };

        keyboardTypeBoxes[index] = new JComboBox<>(keyboardTypes);
        JLabel chooseLabel = new JLabel("Choose keyboard type:");
        keyboardTypeImpactLabels[index] = new JLabel();
        updateKeyboardTypeImpact(index);

        keyboardTypeBoxes[index].addActionListener(e -> updateKeyboardTypeImpact(index));

        panel.add(chooseLabel);
        panel.add(keyboardTypeBoxes[index]);
        panel.add(keyboardTypeImpactLabels[index]);
        styleSectionPanel(panel);
        styleLabel(chooseLabel);
        styleImpactLabel(keyboardTypeImpactLabels[index]);
        styleComboBox(keyboardTypeBoxes[index]);

        return panel;
    }

    /**
     * Updates the visible typing style impact text.
     *
     * @param index the index of the typist
     */
    private void updateTypingStyleImpact(int index)
    {
        if (typingStyleImpactLabels == null || typingStyleImpactLabels[index] == null)
        {
            return;
        }

        typingStyleImpactLabels[index].setText(
            "Impact: " + getTypingStyleImpact((String) typingStyleBoxes[index].getSelectedItem())
        );
    }

    /**
     * Updates the visible keyboard type impact text.
     *
     * @param index the index of the typist
     */
    private void updateKeyboardTypeImpact(int index)
    {
        if (keyboardTypeImpactLabels == null || keyboardTypeImpactLabels[index] == null)
        {
            return;
        }

        keyboardTypeImpactLabels[index].setText(
            "Impact: " + getKeyboardTypeImpact((String) keyboardTypeBoxes[index].getSelectedItem())
        );
    }

    /**
     * Gets the impact text for a typing style.
     *
     * @param typingStyle the selected typing style
     * @return the impact description
     */
    private String getTypingStyleImpact(String typingStyle)
    {
        if ("Touch Typist".equals(typingStyle))
        {
            return "Higher accuracy, slightly lower burnout risk.";
        }
        else if ("Hunt & Peck".equals(typingStyle))
        {
            return "Lower accuracy, lower burnout risk.";
        }
        else if ("Phone Thumbs".equals(typingStyle))
        {
            return "Lower accuracy, higher and longer burnout risk.";
        }
        else if ("Voice-to-Text".equals(typingStyle))
        {
            return "Higher accuracy, slightly higher burnout risk.";
        }

        return "Balanced accuracy and burnout profile.";
    }

    /**
     * Gets the impact text for a keyboard type.
     *
     * @param keyboardType the selected keyboard type
     * @return the impact description
     */
    private String getKeyboardTypeImpact(String keyboardType)
    {
        if ("Mechanical".equals(keyboardType))
        {
            return "Faster typing speed, lower mistype rate.";
        }
        else if ("Membrane".equals(keyboardType))
        {
            return "Slightly slower speed, steady mistype rate.";
        }
        else if ("Touchscreen".equals(keyboardType))
        {
            return "Slower speed, higher mistype rate.";
        }
        else if ("Stenography".equals(keyboardType))
        {
            return "Much faster speed, higher mistype rate.";
        }

        return "Steady typing speed and mistype rate.";
    }

    /**
     * Creates the symbol and colour customisation section for one typist.
     *
     * @param index the index of the typist
     * @return the symbol and colour panel
     */
    private JPanel createSymbolColourPanel(int index)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createThemedBorder("Symbol and Colour"));

        symbolBoxes[index] = new JComboBox<>(availableSymbols);
        symbolBoxes[index].setSelectedItem(availableSymbols[index]);
        symbolBoxes[index].setRenderer(new SymbolComboBoxRenderer(index));
        symbolBoxes[index].setForeground(getSelectedColourSafe(index));
        symbolBoxes[index].addActionListener(e -> updateSymbolAvailability());

        colourBoxes[index] = new JComboBox<>(availableColours);
        colourBoxes[index].setSelectedItem(availableColours[index]);
        colourBoxes[index].setRenderer(new ColourComboBoxRenderer());
        colourBoxes[index].setForeground(getSelectedColourSafe(index));
        colourBoxes[index].addActionListener(e -> updateColourAvailability());

        JLabel symbolLabel = new JLabel("Typist symbol:");
        JLabel colourLabel = new JLabel("Progress colour:");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 0, 8, 14);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(symbolLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 0, 8, 0);
        panel.add(symbolBoxes[index], constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(4, 0, 4, 14);
        panel.add(colourLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 0, 4, 0);
        panel.add(colourBoxes[index], constraints);
        styleSectionPanel(panel);
        styleLabel(symbolLabel);
        styleLabel(colourLabel);
        styleComboBox(symbolBoxes[index]);
        styleComboBox(colourBoxes[index]);

        return panel;
    }

    /**
     * Updates all colour dropdowns so a colour already used by one typist
     * cannot be selected by another typist.
     */
    private void updateColourAvailability()
    {
        if (updatingSymbols || colourBoxes == null)
        {
            return;
        }

        updatingSymbols = true;

        int count = (Integer) seatCountSpinner.getValue();
        String[] selectedColours = new String[count];

        for (int i = 0; i < count; i++)
        {
            if (colourBoxes[i] != null)
            {
                selectedColours[i] = (String) colourBoxes[i].getSelectedItem();
            }
        }

        for (int i = 0; i < count; i++)
        {
            if (colourBoxes[i] != null)
            {
                String currentColour = selectedColours[i];

                colourBoxes[i].removeAllItems();

                for (String colour : availableColours)
                {
                    if (colour.equals(currentColour) || !colourIsUsedByAnotherTypist(colour, selectedColours, i))
                    {
                        colourBoxes[i].addItem(colour);
                    }
                }

                colourBoxes[i].setSelectedItem(currentColour);
                colourBoxes[i].setRenderer(new ColourComboBoxRenderer());
                colourBoxes[i].setForeground(getSelectedColourSafe(i));
                symbolBoxes[i].setRenderer(new SymbolComboBoxRenderer(i));
                symbolBoxes[i].setForeground(getSelectedColourSafe(i));

                String currentSymbol = (String) symbolBoxes[i].getSelectedItem();
                String hex = toHex(getSelectedColourSafe(i));

                typistTabbedPane.setTabComponentAt(i,
                    createTypistTabLabel(getSymbolGlyph(currentSymbol), typistNames[i], hex)
                );
            }
        }

        updatingSymbols = false;
        updateActiveRacersLabel();
    }

    /**
     * Checks whether a colour is already selected by another typist.
     *
     * @param colour the colour to check
     * @param selectedColours the currently selected colours
     * @param currentIndex the typist currently being checked
     * @return true if another typist already uses the colour
     */
    private boolean colourIsUsedByAnotherTypist(String colour, String[] selectedColours, int currentIndex)
    {
        for (int i = 0; i < selectedColours.length; i++)
        {
            if (i != currentIndex && colour.equals(selectedColours[i]))
            {
                return true;
            }
        }

        return false;
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
                symbolBoxes[i].setRenderer(new SymbolComboBoxRenderer(i));
                symbolBoxes[i].setForeground(getSelectedColourSafe(i));

                String hex = toHex(getSelectedColourSafe(i));

                typistTabbedPane.setTabComponentAt(i,
                    createTypistTabLabel(getSymbolGlyph(currentSymbol), typistNames[i], hex)
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
        JPanel panel = new JPanel(new GridLayout(4, 1, 8, 8));
        panel.setBorder(createThemedBorder("Accessories"));

        wristSupportBoxes[index] = new JCheckBox("Wrist Support");
        energyDrinkBoxes[index] = new JCheckBox("Energy Drink");
        noiseCancellingBoxes[index] = new JCheckBox("Noise-Cancelling Headphones");

        impactLabels[index] = new JLabel("Impact: No accessories selected.");

        wristSupportBoxes[index].addActionListener(e -> updateTypistImpact(index));
        energyDrinkBoxes[index].addActionListener(e -> updateTypistImpact(index));
        noiseCancellingBoxes[index].addActionListener(e -> updateTypistImpact(index));

        panel.add(wristSupportBoxes[index]);
        panel.add(energyDrinkBoxes[index]);
        panel.add(noiseCancellingBoxes[index]);
        panel.add(impactLabels[index]);
        styleSectionPanel(panel);
        styleCheckBox(wristSupportBoxes[index]);
        styleCheckBox(energyDrinkBoxes[index]);
        styleCheckBox(noiseCancellingBoxes[index]);
        styleImpactLabel(impactLabels[index]);

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
            impact += "Higher early accuracy and speed, lower later accuracy and speed. ";
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
            char symbol = getSymbolGlyph(selectedSymbol).charAt(0);

            double accuracy = 0.70;
            speedModifiers[i] = 1.00;
            mistypeRateModifiers[i] = 1.00;
            burnoutChanceModifiers[i] = 1.00;
            burnoutDurationModifiers[i] = 0;

            String typingStyle = (String) typingStyleBoxes[i].getSelectedItem();
            String keyboardType = (String) keyboardTypeBoxes[i].getSelectedItem();

            if ("Touch Typist".equals(typingStyle))
            {
                accuracy = accuracy + 0.10;
                burnoutChanceModifiers[i] = burnoutChanceModifiers[i] * 0.90;
            }
            else if ("Hunt & Peck".equals(typingStyle))
            {
                accuracy = accuracy - 0.10;
                burnoutChanceModifiers[i] = burnoutChanceModifiers[i] * 0.80;
            }
            else if ("Phone Thumbs".equals(typingStyle))
            {
                accuracy = accuracy - 0.05;
                burnoutChanceModifiers[i] = burnoutChanceModifiers[i] * 1.25;
                burnoutDurationModifiers[i] = burnoutDurationModifiers[i] + 1;
            }
            else if ("Voice-to-Text".equals(typingStyle))
            {
                accuracy = accuracy + 0.05;
                burnoutChanceModifiers[i] = burnoutChanceModifiers[i] * 1.10;
            }

            if ("Mechanical".equals(keyboardType))
            {
                speedModifiers[i] = speedModifiers[i] * 1.15;
                mistypeRateModifiers[i] = mistypeRateModifiers[i] * 0.90;
            }
            else if ("Membrane".equals(keyboardType))
            {
                speedModifiers[i] = speedModifiers[i] * 0.95;
                mistypeRateModifiers[i] = mistypeRateModifiers[i] * 1.00;
            }
            else if ("Touchscreen".equals(keyboardType))
            {
                speedModifiers[i] = speedModifiers[i] * 0.90;
                mistypeRateModifiers[i] = mistypeRateModifiers[i] * 1.30;
            }
            else if ("Stenography".equals(keyboardType))
            {
                speedModifiers[i] = speedModifiers[i] * 1.35;
                mistypeRateModifiers[i] = mistypeRateModifiers[i] * 1.20;
            }

            if (nightShiftCheckBox.isSelected())
            {
                accuracy = accuracy - 0.05;
                mistypeRateModifiers[i] = mistypeRateModifiers[i] * 1.15;
            }

            if (caffeineModeCheckBox.isSelected())
            {
                accuracy = accuracy + 0.03;
                speedModifiers[i] = speedModifiers[i] * 1.10;
            }

            if (energyDrinkBoxes[i].isSelected())
            {
                accuracy = accuracy + 0.05;
                speedModifiers[i] = speedModifiers[i] * 1.08;
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
            selectedPassage = getCustomPassageText();

            if (selectedPassage.isEmpty() && !customPassageArea.hasFocus())
            {
                showCustomPassagePlaceholder();
            }
        }
        else
        {
            customPassageArea.setEnabled(false);
            selectedPassage = selectedOption.substring(selectedOption.indexOf(":") + 2);
            if (customPassageArea.getText().trim().isEmpty())
            {
                showCustomPassagePlaceholder();
            }
        }

        passageLengthLabel.setText("Passage length: " + selectedPassage.length() + " characters");
    }

    /**
     * Gets the custom passage text while ignoring placeholder text.
     *
     * @return the custom passage typed by the user
     */
    private String getCustomPassageText()
    {
        if (showingCustomPassagePlaceholder)
        {
            return "";
        }

        return customPassageArea.getText();
    }

    /**
     * Displays muted placeholder text in the custom passage area.
     */
    private void showCustomPassagePlaceholder()
    {
        showingCustomPassagePlaceholder = true;
        customPassageArea.setForeground(TEXT_MUTED);
        customPassageArea.setText(CUSTOM_PASSAGE_PLACEHOLDER);
    }

    /**
     * Clears placeholder text when the user starts typing a custom passage.
     */
    private void clearCustomPassagePlaceholder()
    {
        if (showingCustomPassagePlaceholder)
        {
            showingCustomPassagePlaceholder = false;
            customPassageArea.setText("");
            customPassageArea.setForeground(TEXT_LIGHT);
        }
    }

    /**
     * Applies the dark purple theme to a panel.
     *
     * @param panel the panel to style
     */
    private void stylePanel(JPanel panel)
    {
        panel.setBackground(BG_DARK);
        panel.setForeground(TEXT_LIGHT);
    }

    /**
     * Applies the slightly lifted panel colour used for grouped sections.
     *
     * @param panel the section panel to style
     */
    private void styleSectionPanel(JPanel panel)
    {
        panel.setBackground(PANEL_DARK);
        panel.setForeground(TEXT_LIGHT);
    }

    /**
     * Applies the dark purple theme to labels.
     *
     * @param label the label to style
     */
    private void styleLabel(JLabel label)
    {
        label.setForeground(TEXT_LIGHT);
        if (label.getFont() != null && label.getFont().getSize() < 13)
        {
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        }
    }

    /**
     * Applies the dark purple theme to buttons.
     *
     * @param button the button to style
     */
    private void styleButton(JButton button)
    {
        button.setBackground(PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(11, 26, 11, 26));
        button.setUI(new RoundedButtonUI());
    }

    /**
     * Applies the dark purple theme to text areas.
     *
     * @param textArea the text area to style
     */
    private void styleTextArea(JTextArea textArea)
    {
        textArea.setBackground(PANEL_DARK);
        textArea.setForeground(TEXT_LIGHT);
        textArea.setFont(new Font(UI_FONT_NAME, Font.BOLD, 14));
        textArea.setCaretColor(TEXT_LIGHT);
        textArea.setSelectionColor(PURPLE_DARK);
        textArea.setSelectedTextColor(Color.WHITE);
        textArea.setDisabledTextColor(TEXT_MUTED);
        textArea.setBorder(BorderFactory.createLineBorder(PURPLE_DARK));
        textArea.setMargin(new Insets(10, 12, 10, 12));
    }

    /**
     * Applies the dark purple theme to text panes.
     *
     * @param textPane the text pane to style
     */
    private void styleTextPane(JTextPane textPane)
    {
        textPane.setBackground(PANEL_DARK);
        textPane.setForeground(TEXT_LIGHT);
        textPane.setFont(new Font(UI_FONT_NAME, Font.BOLD, 15));
        textPane.setCaretColor(TEXT_LIGHT);
        textPane.setSelectionColor(PURPLE_DARK);
        textPane.setSelectedTextColor(Color.WHITE);
        textPane.setBorder(BorderFactory.createLineBorder(PURPLE_DARK));
        textPane.setMargin(new Insets(10, 12, 10, 12));
    }

    /**
     * Applies the dark purple theme to combo boxes.
     *
     * @param comboBox the combo box to style
     */
    private void styleComboBox(JComboBox<?> comboBox)
    {
        comboBox.setBackground(PANEL_DARK);
        comboBox.setForeground(TEXT_LIGHT);
        comboBox.setFont(new Font(UI_FONT_NAME, Font.BOLD, 13));
        comboBox.setBorder(new RoundedLineBorder(PURPLE_DARK, 10));
        comboBox.setFocusable(false);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 38));
        comboBox.setUI(new ThemedComboBoxUI());
    }

    /**
     * Applies the dark purple theme to check boxes.
     *
     * @param checkBox the check box to style
     */
    private void styleCheckBox(JCheckBox checkBox)
    {
        checkBox.setBackground(PANEL_DARK);
        checkBox.setForeground(CHECKBOX_TEXT);
        checkBox.setFont(new Font(UI_FONT_NAME, Font.BOLD, 13));
        checkBox.setFocusPainted(false);
        checkBox.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Applies muted styling to secondary impact and metadata labels.
     *
     * @param label the label to style
     */
    private void styleImpactLabel(JLabel label)
    {
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font(UI_FONT_NAME, Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Applies app-wide Swing colours so popups, tabs, and option panes match the theme.
     */
    private void configureThemeDefaults()
    {
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_LIGHT);
        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.foreground", TEXT_LIGHT);
        UIManager.put("TabbedPane.selected", PANEL_DARK);
        UIManager.put("ComboBox.background", PANEL_DARK);
        UIManager.put("ComboBox.foreground", TEXT_LIGHT);
        UIManager.put("TextArea.background", PANEL_DARK);
        UIManager.put("TextArea.foreground", TEXT_LIGHT);
        UIManager.put("TextPane.background", PANEL_DARK);
        UIManager.put("TextPane.foreground", TEXT_LIGHT);
        UIManager.put("Button.background", PURPLE);
        UIManager.put("Button.foreground", Color.WHITE);
    }

    /**
     * Applies the theme to tabbed panes.
     *
     * @param tabbedPane the tabbed pane to style
     */
    private void styleTabbedPane(JTabbedPane tabbedPane)
    {
        tabbedPane.setUI(new ThemedTabbedPaneUI());
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_LIGHT);
        tabbedPane.setOpaque(true);
        tabbedPane.setFont(new Font(UI_FONT_NAME, Font.BOLD, 12));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
    }

    /**
     * Creates a tab label for a typist, using a Unicode-friendly font and the selected colour.
     *
     * @param symbolGlyph the symbol glyph to show
     * @param name the typist's name
     * @param hexColour the colour hex value
     * @return a label to use as the tab component
     */
    private JLabel createTypistTabLabel(String symbolGlyph, String name, String hexColour)
    {
        JLabel label = new JLabel(
            "<html><span style=\"font-family:'Segoe UI Symbol'; color:" + hexColour + ";\">"
            + symbolGlyph + "</span> <span style=\"color:" + hexColour + ";\">" + name + "</span></html>"
        );
        label.setFont(new Font(UI_FONT_NAME, Font.BOLD, 12));
        label.setOpaque(false);
        return label;
    }

    /**
     * Applies the theme to scroll panes and their viewport.
     *
     * @param scrollPane the scroll pane to style
     */
    private void styleScrollPane(JScrollPane scrollPane)
    {
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(PANEL_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(PURPLE_DARK));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        styleScrollBar(scrollPane.getHorizontalScrollBar());
    }

    /**
     * Removes the border from scroll panes that sit inside already-bordered tab pages.
     *
     * @param scrollPane the scroll pane to style
     */
    private void styleBorderlessScrollPane(JScrollPane scrollPane)
    {
        styleScrollPane(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BG_DARK);
    }

    /**
     * Applies the purple dark theme to scroll bars.
     *
     * @param scrollBar the scroll bar to style
     */
    private void styleScrollBar(JScrollBar scrollBar)
    {
        if (scrollBar == null)
        {
            return;
        }

        scrollBar.setUI(new ThemedScrollBarUI());
        scrollBar.setBackground(BG_DARK);
        scrollBar.setPreferredSize(new Dimension(12, 12));
    }

    /**
     * Applies the theme to numeric spinners.
     *
     * @param spinner the spinner to style
     */
    private void styleSpinner(JSpinner spinner)
    {
        spinner.setBackground(PANEL_DARK);
        spinner.setForeground(TEXT_LIGHT);
        spinner.setBorder(new RoundedLineBorder(PURPLE_DARK, 10));
        spinner.setPreferredSize(new Dimension(70, 34));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor)
        {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setBackground(PANEL_DARK);
            textField.setForeground(TEXT_LIGHT);
            textField.setCaretColor(TEXT_LIGHT);
            textField.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        }

        styleSpinnerButtons(spinner);
    }

    /**
     * Finds and themes the arrow buttons inside a spinner.
     *
     * @param container the spinner or nested spinner component
     */
    private void styleSpinnerButtons(Container container)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton)
            {
                JButton button = (JButton) component;
                button.setBackground(PANEL_DARK);
                button.setForeground(TEXT_MUTED);
                button.setBorder(BorderFactory.createLineBorder(PURPLE_DARK));
                button.setFocusPainted(false);
            }

            if (component instanceof Container)
            {
                styleSpinnerButtons((Container) component);
            }
        }
    }

    /**
     * Creates a purple titled border for grouped GUI sections.
     *
     * @param title the title shown on the border
     * @return the styled titled border
     */
    private javax.swing.border.Border createThemedBorder(String title)
    {
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(
            new RoundedLineBorder(PURPLE, 12),
            title
        );

        border.setTitleColor(TEXT_LIGHT);
        border.setTitleFont(new Font(UI_FONT_NAME, Font.BOLD, 13));
        return BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    /**
     * Custom renderer for the colour dropdown.
     * Displays each colour option using its actual colour.
     */
    private class ColourComboBoxRenderer extends JPanel implements ListCellRenderer<String>
    {
        private JLabel colourSquare;
        private JLabel colourName;

        public ColourComboBoxRenderer()
        {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 2));
            setOpaque(true);

            colourSquare = new JLabel("■");
            colourSquare.setFont(new Font(UI_FONT_NAME, Font.BOLD, 16));

            colourName = new JLabel();
            colourName.setFont(new Font(UI_FONT_NAME, Font.BOLD, 12));

            add(colourSquare);
            add(colourName);
        }

        public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            Color colour = getColourFromName(value);

            colourSquare.setForeground(colour);
            colourName.setText(value);

            if (index == -1)
            {
                colourName.setForeground(colour);
            }
            else
            {
                colourName.setForeground(isSelected ? Color.WHITE : TEXT_LIGHT);
            }

            if (isSelected)
            {
                setBackground(PURPLE_DARK);
            }
            else
            {
                setBackground(PANEL_DARK);
            }

            return this;
        }
    }

    /**
     * Custom renderer for the symbol dropdown.
     * Displays symbols in the typist's selected progress colour.
     */
    private class SymbolComboBoxRenderer extends JLabel implements ListCellRenderer<String>
    {
        private int typistIndex;

        public SymbolComboBoxRenderer(int typistIndex)
        {
            this.typistIndex = typistIndex;
            setOpaque(true);
            setFont(new Font(UI_FONT_NAME, Font.BOLD, 15));
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        }

        public Component getListCellRendererComponent(
            JList<? extends String> list,
            String value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            setText(value == null ? "" : value);
            setForeground(getSelectedColourSafe(typistIndex));
            setBackground(isSelected ? PURPLE_DARK : PANEL_DARK);
            return this;
        }
    }

    /**
     * Rounded border used by inputs and grouped panels.
     */
    private class RoundedLineBorder extends AbstractBorder
    {
        private Color colour;
        private int arc;

        public RoundedLineBorder(Color colour, int arc)
        {
            this.colour = colour;
            this.arc = arc;
        }

        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height)
        {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(colour);
            graphics2D.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            graphics2D.dispose();
        }

        public Insets getBorderInsets(Component component)
        {
            return new Insets(4, 8, 4, 8);
        }

        public Insets getBorderInsets(Component component, Insets insets)
        {
            insets.left = 8;
            insets.right = 8;
            insets.top = 4;
            insets.bottom = 4;
            return insets;
        }
    }

    /**
     * Paints buttons as rounded purple controls.
     */
    private class RoundedButtonUI extends BasicButtonUI
    {
        public void paint(Graphics graphics, JComponent component)
        {
            AbstractButton button = (AbstractButton) component;
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = button.isEnabled() ? PURPLE : PURPLE_DARK;
            if (button.getModel().isRollover())
            {
                fill = new Color(157, 111, 255);
            }

            graphics2D.setColor(fill);
            graphics2D.fillRoundRect(0, 0, component.getWidth(), component.getHeight(), 14, 14);
            graphics2D.dispose();

            super.paint(graphics, component);
        }

        protected void paintText(Graphics graphics, AbstractButton button, Rectangle textRectangle, String text)
        {
            FontMetrics metrics = graphics.getFontMetrics();

            graphics.setColor(button.isEnabled() ? button.getForeground() : TEXT_MUTED);
            graphics.drawString(
                text,
                textRectangle.x,
                textRectangle.y + metrics.getAscent()
            );
        }
    }

    /**
     * Keeps combo box arrow buttons inside the dark theme.
     */
    private class ThemedComboBoxUI extends BasicComboBoxUI
    {
        protected JButton createArrowButton()
        {
            JButton button = new JButton("▼");
            button.setFont(new Font(UI_FONT_NAME, Font.BOLD, 9));
            button.setForeground(TEXT_MUTED);
            button.setBackground(PANEL_DARK);
            button.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, PURPLE_DARK));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            return button;
        }
    }

    /**
     * Paints scrollbars so scrollable tab content stays in the dark theme.
     */
    private class ThemedScrollBarUI extends BasicScrollBarUI
    {
        protected void configureScrollBarColors()
        {
            thumbColor = PURPLE;
            trackColor = BG_DARK;
        }

        protected JButton createDecreaseButton(int orientation)
        {
            return createScrollButton();
        }

        protected JButton createIncreaseButton(int orientation)
        {
            return createScrollButton();
        }

        private JButton createScrollButton()
        {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds)
        {
            graphics.setColor(BG_DARK);
            graphics.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds)
        {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
            {
                return;
            }

            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(PURPLE);
            graphics2D.fillRoundRect(
                thumbBounds.x + 2,
                thumbBounds.y + 2,
                thumbBounds.width - 4,
                thumbBounds.height - 4,
                10,
                10
            );
            graphics2D.dispose();
        }
    }

    /**
     * Removes the default Metal tab bars and paints compact dark tabs.
     */
    private class ThemedTabbedPaneUI extends BasicTabbedPaneUI
    {
        protected void installDefaults()
        {
            super.installDefaults();
            tabInsets = new Insets(7, 18, 7, 18);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)
        {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 8;
        }

        protected void paintTabBackground(
            Graphics graphics,
            int tabPlacement,
            int tabIndex,
            int x,
            int y,
            int width,
            int height,
            boolean isSelected)
        {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(isSelected ? PANEL_DARK : BG_DARK);
            graphics2D.fillRoundRect(x + 4, y + 2, width - 8, height - 2, 10, 10);
            graphics2D.dispose();
        }

        protected void paintTabBorder(
            Graphics graphics,
            int tabPlacement,
            int tabIndex,
            int x,
            int y,
            int width,
            int height,
            boolean isSelected)
        {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(isSelected ? PURPLE : PURPLE_DARK);
            graphics2D.drawRoundRect(x + 4, y + 2, width - 9, height - 3, 10, 10);
            graphics2D.dispose();
        }

        protected void paintContentBorder(Graphics graphics, int tabPlacement, int selectedIndex)
        {
        }

        protected void paintFocusIndicator(
            Graphics graphics,
            int tabPlacement,
            Rectangle[] rectangles,
            int tabIndex,
            Rectangle iconRect,
            Rectangle textRect,
            boolean isSelected)
        {
        }
    }

    /**
     * Panel used inside scroll panes so content follows the viewport width.
     */
    private class ScrollableContentPanel extends JPanel implements Scrollable
    {
        public Dimension getPreferredScrollableViewportSize()
        {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visibleRectangle, int orientation, int direction)
        {
            return 18;
        }

        public int getScrollableBlockIncrement(Rectangle visibleRectangle, int orientation, int direction)
        {
            return 90;
        }

        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }

        public boolean getScrollableTracksViewportHeight()
        {
            return false;
        }
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
