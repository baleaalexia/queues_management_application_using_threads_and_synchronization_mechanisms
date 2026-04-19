package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private JTextField clientsField = new JTextField("50");
    private JTextField queuesField = new JTextField("5");
    private JTextField simTimeField = new JTextField("60");
    private JTextField minArrivalField = new JTextField("2");
    private JTextField maxArrivalField = new JTextField("30");
    private JTextField minServiceField = new JTextField("2");
    private JTextField maxServiceField = new JTextField("4");
    private JComboBox<SelectionPolicy> policyBox = new JComboBox<>(SelectionPolicy.values());
    private JTextArea logArea = new JTextArea();

    private JLabel waitLabel = new JLabel("Avg Wait: -");
    private JLabel serviceLabel = new JLabel("Avg Service: -");
    private JLabel peakLabel = new JLabel("Peak Hour: -");

    public SimulationFrame() {
        setTitle("Queue Management System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createInputPanel(), BorderLayout.WEST);

        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(createStatsPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createInputPanel(){
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        panel.setBackground(new Color(116, 155, 99));

        panel.add(new JLabel("Nr of clients (N): "));
        panel.add(clientsField);
        panel.add(new JLabel("Nr of queues (Q): "));
        panel.add(queuesField);
        panel.add(new JLabel("Simulation time: "));
        panel.add(simTimeField);
        panel.add(new JLabel("Min arrival time"));
        panel.add(minArrivalField);
        panel.add(new JLabel("Max arrival time: "));
        panel.add(maxArrivalField);
        panel.add(new JLabel("Min service time:"));
        panel.add(minServiceField);
        panel.add(new JLabel("Max service time: "));
        panel.add(maxServiceField);
        panel.add(new JLabel("Selection Policy:"));
        panel.add(policyBox);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startSimulation());
        startButton.setBackground(new Color(37, 55, 29));
        startButton.setForeground(Color.white);

        panel.add(new JLabel(""));
        panel.add(startButton);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(116, 155, 99));
        wrapper.add(panel, BorderLayout.NORTH);
        wrapper.setPreferredSize(new Dimension(200, 0));

        return wrapper;
    }

    private JPanel createStatsPanel(){
        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Final Statistics"));
        statsPanel.setPreferredSize(new Dimension(0, 60));
        statsPanel.setBackground(new Color(116, 155, 99));

        waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        serviceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        peakLabel.setHorizontalAlignment(SwingConstants.CENTER);

        waitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        peakLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statsPanel.add(waitLabel);
        statsPanel.add(serviceLabel);
        statsPanel.add(peakLabel);

        return statsPanel;
    }

    private void startSimulation(){
        logArea.setText("");

        waitLabel.setText("Avg Wait: computing...");
        serviceLabel.setText("Avg Service: computing...");
        peakLabel.setText("Peak Hour: computing...");

        int clients = Integer.parseInt(clientsField.getText());
        int queues = Integer.parseInt(queuesField.getText());
        int time = Integer.parseInt(simTimeField.getText());
        int minArr = Integer.parseInt(minArrivalField.getText());
        int maxArr = Integer.parseInt(maxArrivalField.getText());
        int minSer = Integer.parseInt(minServiceField.getText());
        int maxSer = Integer.parseInt(maxServiceField.getText());
        SelectionPolicy policy = (SelectionPolicy) policyBox.getSelectedItem();

        SimulationManager manager = new SimulationManager(time, maxSer, minSer, minArr, maxArr, queues, clients, this, policy);
        Thread t = new Thread(manager);
        t.start();
    }

    public void appendLog(String text){
        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void updateStats(double wait, double service, int peak){
        waitLabel.setText(String.format("Avg Wait: %.2f", wait));
        serviceLabel.setText(String.format("Avg Service: %.2f", service));
        peakLabel.setText(String.format("Peak Hour: %d", peak));
    }

    static void main(String[] args) {
        SwingUtilities.invokeLater(SimulationFrame::new);
    }
}
