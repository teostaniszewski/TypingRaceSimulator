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

    /**
     * Constructor for TypingRaceGUI.
     * Sets up the main GUI window.
     */
    public TypingRaceGUI()
    {
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

        JLabel subtitleLabel = new JLabel("Part II - GUI Module Development", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        frame.add(mainPanel);
        frame.setVisible(true);
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