package Part2;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;

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
    private Point windowDragStart;

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

    private JButton viewStatsButton;
    private JLabel raceWinnerLabel;

    private boolean updatingSymbols;
    private boolean showingCustomPassagePlaceholder;
    private String selectedPassage;

    private int[] mistypeCounts;
    private int[] burnoutCounts;

    private double[] startingAccuracies;
    private double[] bestWpmRecords;
    private int[] leaderboardPoints;
    private int[] consecutiveWinCounts;
    private double[] speedModifiers;
    private double[] mistypeRateModifiers;
    private double[] burnoutChanceModifiers;
    private int[] burnoutDurationModifiers;
    private boolean[] energyDrinkPenaltyApplied;

    private long raceStartTime;
    private int raceNumber;
    private RacePerformance[] lastRacePerformances;
    private ArrayList<RacePerformance>[] typistRaceHistories;

    private static final Color BG_DARK = new Color(11, 17, 32);
    private static final Color PANEL_DARK = new Color(17, 24, 39);
    private static final Color PURPLE = new Color(139, 92, 246);
    private static final Color PURPLE_DARK = new Color(49, 46, 129);
    private static final Color TEXT_LIGHT = new Color(245, 245, 255);
    private static final Color TEXT_MUTED = new Color(145, 155, 174);
    private static final Color CHECKBOX_TEXT = new Color(125, 211, 252);
    private static final Color TABLE_ROW_ALT = new Color(21, 30, 48);
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
        bestWpmRecords = new double[typistNames.length];
        leaderboardPoints = new int[typistNames.length];
        consecutiveWinCounts = new int[typistNames.length];
        lastRacePerformances = new RacePerformance[0];
        initialiseRaceHistories();
        updatingSymbols = false;
        showingCustomPassagePlaceholder = false;
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
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 850);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_DARK);

        frame.setContentPane(createWindowContent(createMainPanel()));
        frame.setVisible(true);
    }

    /**
     * Initialises per-typist race history storage.
     */
    @SuppressWarnings("unchecked")
    private void initialiseRaceHistories()
    {
        typistRaceHistories = (ArrayList<RacePerformance>[]) new ArrayList<?>[typistNames.length];

        for (int i = 0; i < typistRaceHistories.length; i++)
        {
            typistRaceHistories[i] = new ArrayList<>();
        }
    }

    /**
     * Wraps app screens with the custom window controls.
     *
     * @param contentPanel the screen content to display
     * @return the complete window content panel
     */
    private JPanel createWindowContent(JPanel contentPanel)
    {
        JPanel windowPanel = new JPanel(new BorderLayout());

        windowPanel.add(createCustomWindowBar(), BorderLayout.NORTH);
        windowPanel.add(contentPanel, BorderLayout.CENTER);
        stylePanel(windowPanel);

        return windowPanel;
    }

    /**
     * Creates a custom title bar with minimise and close controls.
     *
     * @return the custom window bar
     */
    private JPanel createCustomWindowBar()
    {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 8));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton minimiseButton = new JButton("-");
        JButton closeButton = new JButton("X");

        minimiseButton.setToolTipText("Minimise");
        closeButton.setToolTipText("Close");
        minimiseButton.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        closeButton.addActionListener(e ->
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
        );

        controlsPanel.add(minimiseButton);
        controlsPanel.add(closeButton);
        titleBar.add(controlsPanel, BorderLayout.EAST);

        MouseAdapter dragListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e)
            {
                windowDragStart = e.getPoint();
            }

            public void mouseDragged(MouseEvent e)
            {
                frame.setLocation(
                    e.getXOnScreen() - windowDragStart.x,
                    e.getYOnScreen() - windowDragStart.y
                );
            }
        };

        titleBar.addMouseListener(dragListener);
        titleBar.addMouseMotionListener(dragListener);

        stylePanel(titleBar);
        stylePanel(controlsPanel);
        styleWindowControlButton(minimiseButton);
        styleWindowControlButton(closeButton);

        return titleBar;
    }

    /**
     * Creates the main setup panel containing configuration, customisation, and stats tabs.
     *
     * @return the main setup panel
     */
    private JPanel createMainPanel()
    {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 18));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 30, 26, 30));

        JLabel titleLabel = new JLabel("< Typing Race Simulator />", JLabel.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(PURPLE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Race Configuration", createRaceConfigurationPanel());
        tabbedPane.addTab("Customise Typists", createCustomiseTypistsPanel());
        tabbedPane.addTab("Statistics & Analytics", createStatsPanel());

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

        JButton startRaceButton = new JButton("Start Race");
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
        raceStartTime = System.currentTimeMillis();

        for (int i = 0; i < currentTypists.length; i++)
        {
            startingAccuracies[i] = currentTypists[i].getAccuracy();
        }

        JPanel racePanel = new JPanel(new BorderLayout(12, 25));
        racePanel.setBorder(BorderFactory.createEmptyBorder(14, 24, 22, 34));

        JLabel titleLabel = new JLabel("< Typing Race Simulator />", JLabel.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(PURPLE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

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

        frame.setContentPane(createWindowContent(racePanel));
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

        frame.setContentPane(createWindowContent(createMainPanel()));
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

        recordRaceStatistics();

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
        statsFrame.setUndecorated(true);
        statsFrame.setSize(1200, 355);
        statsFrame.setLocationRelativeTo(frame);
        statsFrame.getContentPane().setBackground(BG_DARK);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedLineBorder(PURPLE, 12),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel titleLabel = new JLabel("Race Statistics", JLabel.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> statsFrame.dispose());

        buttonPanel.add(closeButton);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(createLastRacePanel(), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        stylePanel(panel);
        stylePanel(buttonPanel);
        styleLabel(titleLabel);
        styleButton(closeButton);

        statsFrame.add(panel);
        statsFrame.setVisible(true);
    }

    /**
     * Records calculated statistics for the completed race.
     *
     */
    private void recordRaceStatistics()
    {
        raceNumber++;

        double elapsedMinutes = (System.currentTimeMillis() - raceStartTime) / 60000.0;
        lastRacePerformances = new RacePerformance[currentTypists.length];
        int[] positions = calculateRacePositions();
        updateConsecutiveWins(positions);

        for (int i = 0; i < currentTypists.length; i++)
        {
            double wordsTyped = currentTypists[i].getProgress() / 5.0;
            double wpm = 0.0;

            if (elapsedMinutes > 0)
            {
                wpm = wordsTyped / elapsedMinutes;
            }

            int totalAttempts = currentTypists[i].getProgress() + mistypeCounts[i];
            double accuracyPercent = 100.0;

            if (totalAttempts > 0)
            {
                accuracyPercent = ((double) currentTypists[i].getProgress() / totalAttempts) * 100.0;
            }

            int rewardPoints = calculateRewardPoints(positions[i], wpm, burnoutCounts[i]);
            leaderboardPoints[i] += rewardPoints;

            RacePerformance performance = new RacePerformance(
                raceNumber,
                currentTypists[i].getName(),
                positions[i],
                wpm,
                accuracyPercent,
                mistypeCounts[i],
                burnoutCounts[i],
                rewardPoints,
                startingAccuracies[i],
                currentTypists[i].getAccuracy()
            );

            lastRacePerformances[i] = performance;
            typistRaceHistories[i].add(performance);

            if (wpm > bestWpmRecords[i])
            {
                bestWpmRecords[i] = wpm;
            }
        }

    }

    /**
     * Calculates finishing positions from progress through the final race state.
     *
     * @return position for each typist index
     */
    private int[] calculateRacePositions()
    {
        int[] positions = new int[currentTypists.length];
        boolean[] assigned = new boolean[currentTypists.length];

        for (int position = 1; position <= currentTypists.length; position++)
        {
            int bestIndex = -1;

            for (int i = 0; i < currentTypists.length; i++)
            {
                if (!assigned[i] && (bestIndex == -1 || typistFinishedAheadOf(i, bestIndex)))
                {
                    bestIndex = i;
                }
            }

            assigned[bestIndex] = true;
            positions[bestIndex] = position;
        }

        return positions;
    }

    /**
     * Compares two typists when assigning race positions.
     *
     * @param firstIndex the first typist index
     * @param secondIndex the second typist index
     * @return true if the first typist should be placed ahead
     */
    private boolean typistFinishedAheadOf(int firstIndex, int secondIndex)
    {
        if (currentTypists[firstIndex].getProgress() != currentTypists[secondIndex].getProgress())
        {
            return currentTypists[firstIndex].getProgress() > currentTypists[secondIndex].getProgress();
        }

        if (mistypeCounts[firstIndex] != mistypeCounts[secondIndex])
        {
            return mistypeCounts[firstIndex] < mistypeCounts[secondIndex];
        }

        if (burnoutCounts[firstIndex] != burnoutCounts[secondIndex])
        {
            return burnoutCounts[firstIndex] < burnoutCounts[secondIndex];
        }

        if (currentTypists[firstIndex].getAccuracy() != currentTypists[secondIndex].getAccuracy())
        {
            return currentTypists[firstIndex].getAccuracy() > currentTypists[secondIndex].getAccuracy();
        }

        return firstIndex < secondIndex;
    }

    /**
     * Updates consecutive win counts after a race.
     *
     * @param positions finishing positions
     */
    private void updateConsecutiveWins(int[] positions)
    {
        for (int i = 0; i < positions.length; i++)
        {
            if (positions[i] == 1)
            {
                consecutiveWinCounts[i]++;
            }
            else
            {
                consecutiveWinCounts[i] = 0;
            }
        }
    }

    /**
     * Calculates leaderboard reward points for one typist in one race.
     *
     * @param position finishing position
     * @param wpm words per minute
     * @param burnouts burnout count
     * @return reward points earned
     */
    private int calculateRewardPoints(int position, double wpm, int burnouts)
    {
        int points = 0;

        if (position == 1)
        {
            points = 3;
        }
        else if (position == 2)
        {
            points = 2;
        }
        else if (position == 3)
        {
            points = 1;
        }

        if (wpm >= 25.0)
        {
            points++;
        }

        if (burnouts == 0)
        {
            points++;
        }

        return points;
    }

    /**
     * Creates the statistics tab.
     * This contains separate tabs for leaderboard, summary, history, comparison, and charts.
     *
     * @return the stats panel
     */
    private JPanel createStatsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JTabbedPane statsTabs = new JTabbedPane();

        statsTabs.addTab("Leaderboard", createLeaderboardPanel());
        statsTabs.addTab("Race History", createRaceHistoryPanel());
        statsTabs.addTab("Comparison", createComparisonPanel());
        statsTabs.addTab("Charts", createChartsPanel());

        panel.add(statsTabs, BorderLayout.CENTER);
        stylePanel(panel);
        styleTabbedPane(statsTabs);

        return panel;
    }

    /**
     * Creates the last race statistics table.
     *
     * @return the last race statistics panel
     */
    private JPanel createLastRacePanel()
    {
        String[] columns = {
            "Position",
            "Typist",
            "WPM",
            "Accuracy %",
            "Mistypes",
            "Burnouts",
            "Points Earned",
            "Accuracy Change"
        };

        if (lastRacePerformances == null || lastRacePerformances.length == 0)
        {
            return createPlaceholderPanel("No race data yet. Run a race to see performance metrics.");
        }

        Object[][] rows = new Object[lastRacePerformances.length][columns.length];
        RacePerformance[] orderedPerformances = getLastRacePerformancesByPosition();

        for (int i = 0; i < orderedPerformances.length; i++)
        {
            RacePerformance performance = orderedPerformances[i];
            rows[i] = createPerformanceRow(performance);
        }

        return createTablePanel(rows, columns);
    }

    /**
     * Creates the leaderboard title and badge explanation panel.
     *
     * @return the leaderboard rules panel
     */
    private JPanel createLeaderboardRulesPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 4, 4));
        panel.setBorder(createThemedBorder("How to Earn Titles and Badges"));

        JLabel pointsLabel = new JLabel("Points: 1st = 3, 2nd = 2, 3rd = 1, +1 for WPM >= 25, +1 for zero burnouts.");
        JLabel titlesLabel = new JLabel("Titles: Rookie under 5 points, Rising Racer at 5, Podium Regular at 10, Track Champion at 20.");
        JLabel badgesLabel = new JLabel("Badges: Speed Demon = 3 consecutive wins, Rapid Keys = best WPM >= 30, Iron Fingers = 5 burnout-free races.");

        panel.add(pointsLabel);
        panel.add(titlesLabel);
        panel.add(badgesLabel);

        styleSectionPanel(panel);
        styleImpactLabel(pointsLabel);
        styleImpactLabel(titlesLabel);
        styleImpactLabel(badgesLabel);

        return panel;
    }

    /**
     * Gets last race performances ordered by finishing position.
     *
     * @return ordered race performance data
     */
    private RacePerformance[] getLastRacePerformancesByPosition()
    {
        RacePerformance[] orderedPerformances = new RacePerformance[lastRacePerformances.length];

        for (int i = 0; i < lastRacePerformances.length; i++)
        {
            orderedPerformances[i] = lastRacePerformances[i];
        }

        for (int i = 0; i < orderedPerformances.length - 1; i++)
        {
            for (int j = i + 1; j < orderedPerformances.length; j++)
            {
                if (orderedPerformances[j].position < orderedPerformances[i].position)
                {
                    RacePerformance temp = orderedPerformances[i];
                    orderedPerformances[i] = orderedPerformances[j];
                    orderedPerformances[j] = temp;
                }
            }
        }

        return orderedPerformances;
    }

    /**
     * Creates the cumulative leaderboard table.
     *
     * @return the leaderboard panel
     */
    private JPanel createLeaderboardPanel()
    {
        if (countTypistsWithRaceData() == 0)
        {
            return createPlaceholderPanel("No leaderboard data yet. Run a race to award points.");
        }

        String[] columns = {
            "Rank",
            "Typist",
            "Points",
            "Best WPM",
            "Races",
            "Current Title",
            "Badges"
        };
        int[] orderedIndexes = getLeaderboardOrder();
        Object[][] rows = new Object[countTypistsWithRaceData()][columns.length];
        int row = 0;

        for (int index : orderedIndexes)
        {
            if (!typistRaceHistories[index].isEmpty())
            {
                rows[row][0] = row + 1;
                rows[row][1] = typistNames[index];
                rows[row][2] = leaderboardPoints[index];
                rows[row][3] = formatDouble(bestWpmRecords[index]);
                rows[row][4] = typistRaceHistories[index].size();
                rows[row][5] = getLeaderboardTitle(index);
                rows[row][6] = getLeaderboardBadges(index);
                row++;
            }
        }

        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel tablePanel = createShortTablePanel(rows, columns);
        JPanel rulesPanel = createLeaderboardRulesPanel();
        JPanel tableCenterPanel = new JPanel(new BorderLayout());
        tableCenterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tablePanel.setPreferredSize(new Dimension(0, tablePanel.getPreferredSize().height));
        tableCenterPanel.add(Box.createVerticalGlue(), BorderLayout.NORTH);
        tableCenterPanel.add(tablePanel, BorderLayout.CENTER);
        tableCenterPanel.add(Box.createVerticalGlue(), BorderLayout.SOUTH);

        panel.add(tableCenterPanel, BorderLayout.CENTER);
        panel.add(rulesPanel, BorderLayout.SOUTH);
        stylePanel(panel);
        stylePanel(tableCenterPanel);

        return panel;
    }

    /**
     * Gets typist indexes ordered by cumulative leaderboard score.
     *
     * @return ordered typist indexes
     */
    private int[] getLeaderboardOrder()
    {
        int[] orderedIndexes = new int[typistNames.length];

        for (int i = 0; i < orderedIndexes.length; i++)
        {
            orderedIndexes[i] = i;
        }

        for (int i = 0; i < orderedIndexes.length - 1; i++)
        {
            for (int j = i + 1; j < orderedIndexes.length; j++)
            {
                if (leaderboardRankedAheadOf(orderedIndexes[j], orderedIndexes[i]))
                {
                    int temp = orderedIndexes[i];
                    orderedIndexes[i] = orderedIndexes[j];
                    orderedIndexes[j] = temp;
                }
            }
        }

        return orderedIndexes;
    }

    /**
     * Compares typists for leaderboard ranking.
     *
     * @param firstIndex first typist index
     * @param secondIndex second typist index
     * @return true if the first typist ranks ahead
     */
    private boolean leaderboardRankedAheadOf(int firstIndex, int secondIndex)
    {
        if (leaderboardPoints[firstIndex] != leaderboardPoints[secondIndex])
        {
            return leaderboardPoints[firstIndex] > leaderboardPoints[secondIndex];
        }

        if (bestWpmRecords[firstIndex] != bestWpmRecords[secondIndex])
        {
            return bestWpmRecords[firstIndex] > bestWpmRecords[secondIndex];
        }

        return firstIndex < secondIndex;
    }

    /**
     * Gets a leaderboard title from cumulative point milestones.
     *
     * @param index typist index
     * @return current title
     */
    private String getLeaderboardTitle(int index)
    {
        if (leaderboardPoints[index] >= 20)
        {
            return "Track Champion";
        }
        else if (leaderboardPoints[index] >= 10)
        {
            return "Podium Regular";
        }
        else if (leaderboardPoints[index] >= 5)
        {
            return "Rising Racer";
        }

        return "Rookie";
    }

    /**
     * Gets all badges earned from race milestones.
     *
     * @param index typist index
     * @return milestone badges
     */
    private String getLeaderboardBadges(int index)
    {
        ArrayList<String> badges = new ArrayList<>();

        if (consecutiveWinCounts[index] >= 3)
        {
            badges.add("Speed Demon");
        }

        if (bestWpmRecords[index] >= 30.0)
        {
            badges.add("Rapid Keys");
        }

        if (typistHasBurnoutFreeStreak(index, 5))
        {
            badges.add("Iron Fingers");
        }

        if (badges.isEmpty())
        {
            return "-";
        }

        return String.join(", ", badges);
    }

    /**
     * Checks whether a typist has a burnout-free streak.
     *
     * @param index typist index
     * @param streakLength required streak length
     * @return true if the streak exists
     */
    private boolean typistHasBurnoutFreeStreak(int index, int streakLength)
    {
        ArrayList<RacePerformance> history = typistRaceHistories[index];

        if (history.size() < streakLength)
        {
            return false;
        }

        for (int i = history.size() - streakLength; i < history.size(); i++)
        {
            if (history.get(i).burnouts > 0)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates the full race history table.
     *
     * @return the race history panel
     */
    private JPanel createRaceHistoryPanel()
    {
        String[] columns = {
            "Race",
            "Position",
            "Typist",
            "WPM",
            "Accuracy %",
            "Mistypes",
            "Burnouts",
            "Reward Points",
            "Accuracy Change"
        };
        int rowCount = countRaceHistoryRows();

        if (rowCount == 0)
        {
            return createPlaceholderPanel("No race history yet. Run a race to start tracking trends.");
        }

        Object[][] rows = new Object[rowCount][columns.length];
        int row = 0;

        for (int i = 0; i < typistRaceHistories.length; i++)
        {
            for (RacePerformance performance : typistRaceHistories[i])
            {
                rows[row][0] = performance.raceNumber;
                rows[row][1] = performance.position;
                rows[row][2] = performance.typistName;
                rows[row][3] = formatDouble(performance.wpm);
                rows[row][4] = formatDouble(performance.accuracyPercent) + "%";
                rows[row][5] = performance.mistypes;
                rows[row][6] = performance.burnouts;
                rows[row][7] = performance.rewardPoints;
                rows[row][8] = formatSignedDouble(performance.accuracyChange);
                row++;
            }
        }

        return createTablePanel(rows, columns);
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
        JPanel tableHolder = new JPanel(new BorderLayout());

        int[] recordedTypistIndexes = getTypistsWithRaceData();

        if (recordedTypistIndexes.length < 2)
        {
            return createPlaceholderPanel("Run races with at least two typists before comparing performance.");
        }

        JPanel typistSelectionPanel = new JPanel(new GridLayout(0, 3, 10, 4));
        JCheckBox[] typistSelectionBoxes = new JCheckBox[recordedTypistIndexes.length];

        for (int i = 0; i < recordedTypistIndexes.length; i++)
        {
            typistSelectionBoxes[i] = new JCheckBox(typistNames[recordedTypistIndexes[i]]);
            typistSelectionBoxes[i].setSelected(i < 2);
            typistSelectionPanel.add(typistSelectionBoxes[i]);
        }

        String[] metrics = {"WPM", "Accuracy %", "Burnouts", "Mistypes"};
        JComboBox<String> metricBox = new JComboBox<>(metrics);

        JButton compareButton = new JButton("Compare");
        compareButton.addActionListener(e -> {
            int selectedCount = countSelectedBoxes(typistSelectionBoxes);

            if (selectedCount < 2)
            {
                JOptionPane.showMessageDialog(panel,
                    "Please select at least two typists.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int[] selectedTypistIndexes = new int[selectedCount];
            int selectedIndex = 0;

            for (int i = 0; i < typistSelectionBoxes.length; i++)
            {
                if (typistSelectionBoxes[i].isSelected())
                {
                    selectedTypistIndexes[selectedIndex] = recordedTypistIndexes[i];
                    selectedIndex++;
                }
            }

            replacePanelContent(tableHolder,
                createComparisonTablePanel(selectedTypistIndexes, (String) metricBox.getSelectedItem())
            );
            
        });

        JLabel typistsLabel = new JLabel("Typists:");
        JLabel metricLabel = new JLabel("Metric:");

        selectionPanel.add(typistsLabel);
        selectionPanel.add(typistSelectionPanel);
        selectionPanel.add(metricLabel);
        selectionPanel.add(metricBox);
        selectionPanel.add(compareButton);

        tableHolder.add(createComparisonTablePanel(
            new int[] {recordedTypistIndexes[0], recordedTypistIndexes[1]},
            (String) metricBox.getSelectedItem()
        ), BorderLayout.CENTER);

        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(tableHolder, BorderLayout.CENTER);
        stylePanel(panel);
        stylePanel(selectionPanel);
        stylePanel(typistSelectionPanel);
        styleLabel(typistsLabel);
        styleLabel(metricLabel);
        for (JCheckBox box : typistSelectionBoxes)
        {
            styleCheckBox(box);
        }
        styleComboBox(metricBox);
        styleButton(compareButton);

        for (int i = 0; i < typistSelectionBoxes.length; i++)
        {
            styleCheckBox(typistSelectionBoxes[i]);
            typistSelectionBoxes[i].setForeground(getTypistDisplayColour(recordedTypistIndexes[i]));
        }

        return panel;
    }

    /**
     * Creates the graphical chart panel for performance trends.
     *
     * @return the charts panel
     */
    private JPanel createChartsPanel()
    {
        int[] recordedTypistIndexes = getTypistsWithRaceData();

        if (recordedTypistIndexes.length == 0)
        {
            return createPlaceholderPanel("No chart data yet. Run a race to start drawing trends.");
        }

        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        JPanel typistSelectionPanel = new JPanel(new GridLayout(0, 3, 10, 4));
        JCheckBox[] typistSelectionBoxes = new JCheckBox[recordedTypistIndexes.length];

        for (int i = 0; i < recordedTypistIndexes.length; i++)
        {
            int typistIndex = recordedTypistIndexes[i];
            typistSelectionBoxes[i] = new JCheckBox(typistNames[typistIndex]);
            typistSelectionBoxes[i].setSelected(true);
            typistSelectionPanel.add(typistSelectionBoxes[i]);
        }

        String[] metrics = {"WPM", "Accuracy %", "Burnouts", "Mistypes"};
        JComboBox<String> metricBox = new JComboBox<>(metrics);
        PerformanceChartPanel chartPanel = new PerformanceChartPanel(
            recordedTypistIndexes,
            typistSelectionBoxes,
            metricBox
        );

        JLabel typistsLabel = new JLabel("Typists:");
        JLabel metricLabel = new JLabel("Metric:");

        controlsPanel.add(typistsLabel);
        controlsPanel.add(typistSelectionPanel);
        controlsPanel.add(metricLabel);
        controlsPanel.add(metricBox);

        metricBox.addActionListener(e -> chartPanel.repaint());

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        stylePanel(panel);
        stylePanel(controlsPanel);
        stylePanel(typistSelectionPanel);
        styleLabel(typistsLabel);
        styleLabel(metricLabel);
        styleComboBox(metricBox);

        for (int i = 0; i < typistSelectionBoxes.length; i++)
        {
            styleCheckBox(typistSelectionBoxes[i]);
            typistSelectionBoxes[i].setForeground(getTypistDisplayColour(recordedTypistIndexes[i]));
            typistSelectionBoxes[i].addActionListener(e -> chartPanel.repaint());
        }

        styleSectionPanel(chartPanel);

        return panel;
    }

    /**
     * Creates a comparison table for selected typists and a metric.
     *
     * @param typistIndexes selected typist indexes
     * @param metric selected metric
     * @return the comparison table panel
     */
    private JPanel createComparisonTablePanel(int[] typistIndexes, String metric)
    {
        String[] columns = new String[typistIndexes.length + 1];
        columns[0] = "Race";

        for (int i = 0; i < typistIndexes.length; i++)
        {
            columns[i + 1] = typistNames[typistIndexes[i]];
        }

        if (raceNumber == 0)
        {
            return createPlaceholderPanel("No comparison data available for the selected typists.");
        }

        Object[][] rows = new Object[raceNumber][columns.length];

        for (int raceIndex = 0; raceIndex < raceNumber; raceIndex++)
        {
            int currentRaceNumber = raceIndex + 1;
            rows[raceIndex][0] = currentRaceNumber;

            for (int typistIndex = 0; typistIndex < typistIndexes.length; typistIndex++)
            {
                RacePerformance performance = findPerformanceForRace(
                    typistIndexes[typistIndex],
                    currentRaceNumber
                );

                if (performance != null)
                {
                    rows[raceIndex][typistIndex + 1] = getMetricValue(performance, metric);
                }
                else
                {
                    rows[raceIndex][typistIndex + 1] = "No race data";
                }
            }
        }

        return createTablePanel(rows, columns);
    }

    /**
     * Finds one typist's performance for a specific race.
     *
     * @param typistIndex the typist index
     * @param targetRaceNumber the race number to find
     * @return the matching performance, or null if no data exists
     */
    private RacePerformance findPerformanceForRace(int typistIndex, int targetRaceNumber)
    {
        for (RacePerformance performance : typistRaceHistories[typistIndex])
        {
            if (performance.raceNumber == targetRaceNumber)
            {
                return performance;
            }
        }

        return null;
    }

    /**
     * Gets formatted metric data for one race performance.
     *
     * @param performance the performance record
     * @param metric the selected metric
     * @return formatted metric value
     */
    private Object getMetricValue(RacePerformance performance, String metric)
    {
        if ("Accuracy %".equals(metric))
        {
            return formatDouble(performance.accuracyPercent) + "%";
        }
        else if ("Burnouts".equals(metric))
        {
            return performance.burnouts;
        }
        else if ("Mistypes".equals(metric))
        {
            return performance.mistypes;
        }
        else if ("Position".equals(metric))
        {
            return performance.position;
        }
        else if ("Accuracy Change".equals(metric))
        {
            return formatSignedDouble(performance.accuracyChange);
        }

        return formatDouble(performance.wpm);
    }

    /**
     * Gets numeric metric data for charts.
     *
     * @param performance the performance record
     * @param metric the selected metric
     * @return numeric metric value
     */
    private double getChartMetricValue(RacePerformance performance, String metric)
    {
        if ("Accuracy %".equals(metric))
        {
            return performance.accuracyPercent;
        }
        else if ("Burnouts".equals(metric))
        {
            return performance.burnouts;
        }
        else if ("Mistypes".equals(metric))
        {
            return performance.mistypes;
        }
        else if ("Position".equals(metric))
        {
            return performance.position;
        }
        else if ("Accuracy Change".equals(metric))
        {
            return performance.accuracyChange;
        }

        return performance.wpm;
    }

    /**
     * Counts selected check boxes.
     *
     * @param boxes the check boxes to count
     * @return the selected count
     */
    private int countSelectedBoxes(JCheckBox[] boxes)
    {
        int count = 0;

        for (JCheckBox box : boxes)
        {
            if (box.isSelected())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts typists who have at least one race result.
     *
     * @return the number of typists with recorded race data
     */
    private int countTypistsWithRaceData()
    {
        int count = 0;

        for (ArrayList<RacePerformance> history : typistRaceHistories)
        {
            if (!history.isEmpty())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts all race history rows.
     *
     * @return the total number of stored performance rows
     */
    private int countRaceHistoryRows()
    {
        int count = 0;

        for (ArrayList<RacePerformance> history : typistRaceHistories)
        {
            count += history.size();
        }

        return count;
    }

    /**
     * Gets indexes for typists with recorded race data.
     *
     * @return typist indexes with race history
     */
    private int[] getTypistsWithRaceData()
    {
        int[] indexes = new int[countTypistsWithRaceData()];
        int row = 0;

        for (int i = 0; i < typistRaceHistories.length; i++)
        {
            if (!typistRaceHistories[i].isEmpty())
            {
                indexes[row] = i;
                row++;
            }
        }

        return indexes;
    }

    /**
     * Builds a row for a full performance table.
     *
     * @param performance the performance record
     * @return table row data
     */
    private Object[] createPerformanceRow(RacePerformance performance)
    {
        return new Object[] {
            performance.position,
            performance.typistName,
            formatDouble(performance.wpm),
            formatDouble(performance.accuracyPercent) + "%",
            performance.mistypes,
            performance.burnouts,
            performance.rewardPoints,
            formatSignedDouble(performance.accuracyChange)
        };
    }

    /**
     * Replaces the content inside a holder panel.
     *
     * @param holder the panel receiving new content
     * @param content the new content panel
     */
    private void replacePanelContent(JPanel holder, JPanel content)
    {
        holder.removeAll();
        holder.add(content, BorderLayout.CENTER);
        holder.revalidate();
        holder.repaint();
    }

    /**
     * Formats a double to two decimal places.
     *
     * @param value the value to format
     * @return formatted value
     */
    private String formatDouble(double value)
    {
        return String.format("%.2f", value);
    }

    /**
     * Formats a signed double to two decimal places.
     *
     * @param value the value to format
     * @return formatted signed value
     */
    private String formatSignedDouble(double value)
    {
        return String.format("%+.2f", value);
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

        typingStyleBoxes = (JComboBox<String>[]) new JComboBox<?>[count];
        keyboardTypeBoxes = (JComboBox<String>[]) new JComboBox<?>[count];
        symbolBoxes = (JComboBox<String>[]) new JComboBox<?>[count];
        colourBoxes = (JComboBox<String>[]) new JComboBox<?>[count];
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
     * Creates a themed table panel from row and column data.
     *
     * @param rows table rows
     * @param columns table columns
     * @return themed table panel
     */
    private JPanel createTablePanel(Object[][] rows, String[] columns)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JTable table = createThemedStatsTable(rows, columns);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
        stylePanel(panel);
        styleTable(table);
        styleScrollPane(scrollPane);
        return panel;
    }

    /**
     * Creates a themed table panel that only uses enough height for its rows.
     *
     * @param rows table rows
     * @param columns table columns
     * @return short themed table panel
     */
    private JPanel createShortTablePanel(Object[][] rows, String[] columns)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 0, 14));

        JTable table = createThemedStatsTable(rows, columns);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);

        stylePanel(panel);
        styleTable(table);
        styleScrollPane(scrollPane);

        int height = table.getTableHeader().getPreferredSize().height
            + (table.getRowHeight() * rows.length)
            + 6;
        scrollPane.setPreferredSize(new Dimension(0, height));
        panel.setPreferredSize(new Dimension(0, height + 14));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the shared themed stats table.
     *
     * @param rows table rows
     * @param columns table columns
     * @return stats table
     */
    private JTable createThemedStatsTable(Object[][] rows, String[] columns)
    {
        return new JTable(new DefaultTableModel(rows, columns) {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        }) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column)
            {
                Component component = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row))
                {
                    component.setBackground(row % 2 == 0 ? PANEL_DARK : TABLE_ROW_ALT);
                    component.setForeground(TEXT_LIGHT);
                }

                return component;
            }
        };
    }

    /**
     * Creates a themed placeholder panel for empty stats states.
     *
     * @param message the placeholder message
     * @return themed placeholder panel
     */
    private JPanel createPlaceholderPanel(String message)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel label = new JLabel(message);
        panel.add(label);

        stylePanel(panel);
        styleImpactLabel(label);
        return panel;
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
     * Applies the theme to custom window control buttons.
     *
     * @param button the window control button to style
     */
    private void styleWindowControlButton(JButton button)
    {
        button.setPreferredSize(new Dimension(38, 24));
        button.setBackground(PANEL_DARK);
        button.setForeground(TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(new RoundedLineBorder(PURPLE_DARK, 8));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setOpaque(true);
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
     * Applies the theme to data tables.
     *
     * @param table the table to style
     */
    private void styleTable(JTable table)
    {
        table.setBackground(PANEL_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(PURPLE_DARK);
        table.setSelectionBackground(PURPLE_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font(UI_FONT_NAME, Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        table.setDefaultRenderer(Object.class, cellRenderer);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_DARK);
        header.setForeground(CHECKBOX_TEXT);
        header.setFont(new Font(UI_FONT_NAME, Font.BOLD, 13));
        header.setBorder(BorderFactory.createLineBorder(PURPLE_DARK));
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
        private static final long serialVersionUID = 1L;
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
        private static final long serialVersionUID = 1L;
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
        private static final long serialVersionUID = 1L;
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
        private static final long serialVersionUID = 1L;

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
     * Draws performance trends as curved coloured line charts.
     */
    private class PerformanceChartPanel extends JPanel
    {
        private static final long serialVersionUID = 1L;
        private int[] typistIndexes;
        private JCheckBox[] typistSelectionBoxes;
        private JComboBox<String> metricBox;

        public PerformanceChartPanel(
            int[] typistIndexes,
            JCheckBox[] typistSelectionBoxes,
            JComboBox<String> metricBox)
        {
            this.typistIndexes = typistIndexes;
            this.typistSelectionBoxes = typistSelectionBoxes;
            this.metricBox = metricBox;
            setPreferredSize(new Dimension(0, 430));
            setBorder(new RoundedLineBorder(PURPLE_DARK, 12));
        }

        protected void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);

            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int left = 68;
            int right = 190;
            int top = 46;
            int bottom = 62;
            int chartWidth = getWidth() - left - right;
            int chartHeight = getHeight() - top - bottom;

            if (chartWidth <= 0 || chartHeight <= 0)
            {
                graphics2D.dispose();
                return;
            }

            String metric = (String) metricBox.getSelectedItem();
            double[] range = getChartRange(metric);

            drawChartGrid(graphics2D, left, top, chartWidth, chartHeight, range, metric);
            drawChartLines(graphics2D, left, top, chartWidth, chartHeight, range, metric);
            drawChartLegend(graphics2D, getWidth() - right + 28, top + 8);

            graphics2D.setColor(TEXT_LIGHT);
            graphics2D.setFont(new Font(UI_FONT_NAME, Font.BOLD, 15));
            graphics2D.drawString(metric + " Trend", left, 26);

            graphics2D.dispose();
        }

        /**
         * Draws the chart grid, axes, and scale labels.
         */
        private void drawChartGrid(
            Graphics2D graphics2D,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            double[] range,
            String metric)
        {
            graphics2D.setFont(new Font(UI_FONT_NAME, Font.PLAIN, 11));

            for (int i = 0; i <= 4; i++)
            {
                int y = top + (chartHeight * i / 4);
                double value = range[1] - ((range[1] - range[0]) * i / 4.0);
                String label = formatChartScaleValue(value, metric);

                graphics2D.setColor(PURPLE_DARK);
                graphics2D.drawLine(left, y, left + chartWidth, y);
                graphics2D.setColor(TEXT_MUTED);
                graphics2D.drawString(label, 12, y + 4);
            }

            int raceCount = Math.max(raceNumber, 1);

            for (int race = 1; race <= raceCount; race++)
            {
                int x = getChartX(race, left, chartWidth);

                graphics2D.setColor(new Color(34, 42, 66));
                graphics2D.drawLine(x, top, x, top + chartHeight);
                graphics2D.setColor(TEXT_MUTED);
                graphics2D.drawString(String.valueOf(race), x - 3, top + chartHeight + 22);
            }

            graphics2D.setColor(TEXT_MUTED);
            graphics2D.drawString("Race", left + chartWidth / 2 - 12, top + chartHeight + 45);
            graphics2D.setColor(PURPLE);
            graphics2D.drawRect(left, top, chartWidth, chartHeight);
        }

        /**
         * Draws selected typist lines.
         */
        private void drawChartLines(
            Graphics2D graphics2D,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            double[] range,
            String metric)
        {
            boolean anySelected = false;

            for (int i = 0; i < typistIndexes.length; i++)
            {
                if (typistSelectionBoxes[i].isSelected())
                {
                    anySelected = true;
                    drawSingleTypistLine(
                        graphics2D,
                        typistIndexes[i],
                        left,
                        top,
                        chartWidth,
                        chartHeight,
                        range,
                        metric
                    );
                }
            }

            if (!anySelected)
            {
                graphics2D.setColor(TEXT_MUTED);
                graphics2D.setFont(new Font(UI_FONT_NAME, Font.PLAIN, 13));
                graphics2D.drawString(
                    "Select at least one typist to draw a chart.",
                    left + 24,
                    top + chartHeight / 2
                );
            }
        }

        /**
         * Draws a single typist's curved line.
         */
        private void drawSingleTypistLine(
            Graphics2D graphics2D,
            int typistIndex,
            int left,
            int top,
            int chartWidth,
            int chartHeight,
            double[] range,
            String metric)
        {
            ArrayList<RacePerformance> history = typistRaceHistories[typistIndex];

            if (history.isEmpty())
            {
                return;
            }

            Color colour = getTypistDisplayColour(typistIndex);
            Path2D.Double path = new Path2D.Double();
            boolean started = false;
            int previousX = 0;
            int previousY = 0;

            graphics2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics2D.setColor(colour);

            for (RacePerformance performance : history)
            {
                int x = getChartX(performance.raceNumber, left, chartWidth);
                int y = getChartY(getChartMetricValue(performance, metric), top, chartHeight, range);

                if (!started)
                {
                    path.moveTo(x, y);
                    started = true;
                }
                else
                {
                    path.quadTo(previousX, previousY, x, y);
                }

                previousX = x;
                previousY = y;
            }

            graphics2D.draw(path);

            for (RacePerformance performance : history)
            {
                int x = getChartX(performance.raceNumber, left, chartWidth);
                int y = getChartY(getChartMetricValue(performance, metric), top, chartHeight, range);
                graphics2D.fillOval(x - 4, y - 4, 8, 8);
            }
        }

        /**
         * Draws the typist colour legend.
         */
        private void drawChartLegend(Graphics2D graphics2D, int x, int y)
        {
            graphics2D.setFont(new Font(UI_FONT_NAME, Font.BOLD, 12));

            for (int i = 0; i < typistIndexes.length; i++)
            {
                if (typistSelectionBoxes[i].isSelected())
                {
                    Color colour = getTypistDisplayColour(typistIndexes[i]);

                    graphics2D.setColor(colour);
                    graphics2D.fillRect(x, y - 9, 10, 10);
                    graphics2D.drawString(typistNames[typistIndexes[i]], x + 18, y);
                    y += 22;
                }
            }
        }

        /**
         * Finds the visible value range for the selected metric.
         */
        private double[] getChartRange(String metric)
        {
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;

            for (int i = 0; i < typistIndexes.length; i++)
            {
                if (typistSelectionBoxes[i].isSelected())
                {
                    for (RacePerformance performance : typistRaceHistories[typistIndexes[i]])
                    {
                        double value = getChartMetricValue(performance, metric);
                        min = Math.min(min, value);
                        max = Math.max(max, value);
                    }
                }
            }

            if ("Burnouts".equals(metric) || "Mistypes".equals(metric))
            {
                if (max < 1.0)
                {
                    max = 1.0;
                }

                return new double[] {0.0, Math.ceil(max)};
            }

            if ("Accuracy %".equals(metric))
            {
                if (min == Double.MAX_VALUE)
                {
                    min = 95.0;
                }

                return new double[] {Math.max(0.0, min), 100.0};
            }

            if (min == Double.MAX_VALUE)
            {
                min = 0.0;
                max = 1.0;
            }
            else if (min == max)
            {
                min = min - 1.0;
                max = max + 1.0;
            }
            else
            {
                double padding = (max - min) * 0.12;
                min = min - padding;
                max = max + padding;
            }

            return new double[] {min, max};
        }

        /**
         * Formats chart scale labels for the selected metric.
         */
        private String formatChartScaleValue(double value, String metric)
        {
            if ("Burnouts".equals(metric) || "Mistypes".equals(metric))
            {
                return String.valueOf((int) Math.round(value));
            }

            if ("Accuracy %".equals(metric))
            {
                return String.valueOf((int) Math.round(value)) + "%";
            }

            return formatDouble(value);
        }

        /**
         * Converts a race number into a chart x-coordinate.
         */
        private int getChartX(int currentRaceNumber, int left, int chartWidth)
        {
            if (raceNumber <= 1)
            {
                return left + chartWidth / 2;
            }

            return left + (int) (((double) (currentRaceNumber - 1) / (raceNumber - 1)) * chartWidth);
        }

        /**
         * Converts a metric value into a chart y-coordinate.
         */
        private int getChartY(double value, int top, int chartHeight, double[] range)
        {
            double normalised = (value - range[0]) / (range[1] - range[0]);
            return top + chartHeight - (int) (normalised * chartHeight);
        }
    }

    /**
     * Stores the calculated performance data for one typist in one race.
     */
    private class RacePerformance
    {
        private int raceNumber;
        private String typistName;
        private int position;
        private double wpm;
        private double accuracyPercent;
        private int mistypes;
        private int burnouts;
        private int rewardPoints;
        private double accuracyChange;

        public RacePerformance(
            int raceNumber,
            String typistName,
            int position,
            double wpm,
            double accuracyPercent,
            int mistypes,
            int burnouts,
            int rewardPoints,
            double startAccuracy,
            double finalAccuracy)
        {
            this.raceNumber = raceNumber;
            this.typistName = typistName;
            this.position = position;
            this.wpm = wpm;
            this.accuracyPercent = accuracyPercent;
            this.mistypes = mistypes;
            this.burnouts = burnouts;
            this.rewardPoints = rewardPoints; 
            this.accuracyChange = finalAccuracy - startAccuracy;
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
