import javax.swing.*;
import java.awt.*;

/**
 * Application Entry Point and main window (JFrame) configuration.
 * Sets up a clean dark-themed dashboard control interface.
 */
public class Main extends JFrame {

    private final MazePanel mazePanel;
    private final JButton btnGenerate;
    private final JButton btnSolve;
    private final JComboBox<String> comboAlgorithm;
    private final JSlider sliderSpeed;

    public Main() {
        super("Algorithmic Maze Solver & Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Core visual component
        mazePanel = new MazePanel();

        // Control Panel with Dark Background
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(21, 21, 21));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        controlPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 40)));

        // Fonts
        Font uiFontBold = new Font("Segoe UI", Font.BOLD, 12);
        Font uiFontPlain = new Font("Segoe UI", Font.PLAIN, 12);

        // Styled "Generate Maze" Button
        btnGenerate = new JButton("Generate Maze");
        btnGenerate.setFont(uiFontBold);
        btnGenerate.setBackground(new Color(33, 150, 243)); // Modern blue
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnGenerate.addActionListener(e -> mazePanel.startGeneration());

        // Styled "Algorithm" Selector Combo Box
        JLabel lblAlgorithm = new JLabel("Algorithm:");
        lblAlgorithm.setFont(uiFontBold);
        lblAlgorithm.setForeground(Color.WHITE);

        comboAlgorithm = new JComboBox<>(new String[]{
                "Breadth-First Search",
                "Depth-First Search"
        });
        comboAlgorithm.setFont(uiFontPlain);
        comboAlgorithm.setBackground(new Color(45, 45, 45));
        comboAlgorithm.setForeground(Color.WHITE);
        comboAlgorithm.setFocusable(false);
        comboAlgorithm.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        // Styled "Solve Maze" Button
        btnSolve = new JButton("Solve Maze");
        btnSolve.setFont(uiFontBold);
        btnSolve.setBackground(new Color(76, 175, 80)); // Modern green
        btnSolve.setForeground(Color.WHITE);
        btnSolve.setFocusPainted(false);
        btnSolve.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSolve.addActionListener(e -> {
            String algo = (String) comboAlgorithm.getSelectedItem();
            mazePanel.startSolving(algo);
        });

        // Speed Control Slider (sliding right = faster)
        JLabel lblSpeed = new JLabel("Speed:");
        lblSpeed.setFont(uiFontBold);
        lblSpeed.setForeground(Color.WHITE);

        // Slider value 1 (slow, 200ms delay) to 200 (fast, 1ms delay)
        sliderSpeed = new JSlider(1, 200, 180);
        sliderSpeed.setBackground(new Color(21, 21, 21));
        sliderSpeed.setFocusable(false);
        sliderSpeed.addChangeListener(e -> {
            int val = sliderSpeed.getValue();
            // Map 1..200 to 200..1 ms delay (higher slider value = lower delay = faster speed)
            int delay = 201 - val;
            mazePanel.setSpeed(delay);
        });

        // Apply initial speed mapped from slider
        mazePanel.setSpeed(201 - sliderSpeed.getValue());

        // Assemble control panel
        controlPanel.add(btnGenerate);
        controlPanel.add(lblAlgorithm);
        controlPanel.add(comboAlgorithm);
        controlPanel.add(btnSolve);
        controlPanel.add(lblSpeed);
        controlPanel.add(sliderSpeed);

        // State change listener updates active/disabled UI states dynamically
        mazePanel.setOnStateChanged(this::updateControlStates);

        // Main Layout configuration
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mazePanel, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center on screen
        updateControlStates();
    }

    /**
     * Updates button and dropdown interactive states based on whether
     * algorithms are currently animating.
     */
    private void updateControlStates() {
        boolean isBusy = mazePanel.isBusy();
        btnGenerate.setEnabled(!isBusy);
        comboAlgorithm.setEnabled(!isBusy);
        btnSolve.setEnabled(!isBusy);

        // Visual cues for disabled/enabled states
        if (isBusy) {
            btnGenerate.setBackground(new Color(66, 66, 66));
            btnSolve.setBackground(new Color(66, 66, 66));
        } else {
            btnGenerate.setBackground(new Color(33, 150, 243));
            btnSolve.setBackground(new Color(76, 175, 80));
        }
    }

    public static void main(String[] args) {
        // Run on the Event Dispatch Thread (EDT) for thread-safe Swing operations
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
