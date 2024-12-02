import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class EnergyHealthTracker extends JPanel {
    private JTable inputTable;
    private JPanel graphPanel;
    private JTextArea tipsArea;

    public EnergyHealthTracker() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(900, 700));

        String[] columnNames = {"Day", "Hours of Sleep", "Energy Level (1-10)"};
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        Object[][] data = new Object[7][3];

        for (int i = 0; i < days.length; i++) {
            data[i][0] = days[i];
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; 
            }
        };

        inputTable = new JTable(model);
        inputTable.setPreferredScrollableViewportSize(new Dimension(500, 120));
        inputTable.setFillsViewportHeight(true);

        inputTable.setRowHeight(25);
        inputTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        inputTable.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane tableScrollPane = new JScrollPane(inputTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Weekly Health Data"));

        loadDataFromCSV();

        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(800, 300));
        graphPanel.setBorder(BorderFactory.createTitledBorder("Sleep Hours Trend"));

        tipsArea = new JTextArea(5, 20);
        tipsArea.setEditable(false);
        tipsArea.setWrapStyleWord(true);
        tipsArea.setLineWrap(true);
        tipsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane tipsScrollPane = new JScrollPane(tipsArea);
        tipsScrollPane.setBorder(BorderFactory.createTitledBorder("Health Insights"));

        JButton submitButton = new JButton("Analyze Data");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.addActionListener(e -> updateGraphAndProvideTips());

        JButton saveButton = new JButton("Save Data");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> saveDataToCSV());

        JButton newWeekButton = new JButton("New Week");
        newWeekButton.setFont(new Font("Arial", Font.BOLD, 14));
        newWeekButton.addActionListener(e -> clearDataForNewWeek());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(newWeekButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(tipsScrollPane, BorderLayout.SOUTH);
    }

    private void updateGraphAndProvideTips() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        StringBuilder tipsBuilder = new StringBuilder("Healthy Tips:\n");
        boolean validInput = true;

        for (int i = 0; i < days.length; i++) {
            try {
                Object sleepValue = inputTable.getValueAt(i, 1);
                Object energyValue = inputTable.getValueAt(i, 2);

                if (sleepValue == null || energyValue == null) {
                    throw new NumberFormatException("Empty fields");
                }

                double sleepHours = Double.parseDouble(sleepValue.toString());
                int energyLevel = Integer.parseInt(energyValue.toString());

                if (sleepHours < 7) {
                    tipsBuilder.append(days[i]).append(": Try to get more sleep. Aim for 7-9 hours.\n");
                }
                if (energyLevel < 5) {
                    tipsBuilder.append(days[i]).append(": Stay hydrated and include more fruits and vegetables in your diet.\n");
                }
            } catch (NumberFormatException ex) {
                validInput = false;
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }

        if (validInput) {
            tipsArea.setText(tipsBuilder.toString());
            graphPanel.repaint();
        }
    }

    private void saveDataToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("health_data.csv"))) {
            for (int i = 0; i < inputTable.getRowCount(); i++) {
                for (int j = 0; j < inputTable.getColumnCount(); j++) {
                    Object value = inputTable.getValueAt(i, j);
                    writer.print(value != null ? value.toString() : "");
                    if (j < inputTable.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            JOptionPane.showMessageDialog(this, "Data saved successfully to health_data.csv!", "Save Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data to CSV: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader("health_data.csv"))) {
            String line;
            DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
            int row = 0;

            while ((line = reader.readLine()) != null && row < inputTable.getRowCount()) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length && col < inputTable.getColumnCount(); col++) {
                    model.setValueAt(values[col], row, col);
                }
                row++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting with default values.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading data from CSV: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDataForNewWeek() {
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(null, row, 1);
            model.setValueAt(null, row, 2);
        }
        tipsArea.setText("");
        graphPanel.repaint();
    }

    private void drawGraph(Graphics g) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        int padding = 40;
        int graphWidth = width - 2 * padding;
        int graphHeight = height - 2 * padding;
        int dayCount = days.length;

        double[] sleepHours = new double[dayCount];
        boolean validInput = true;

        try {
            for (int i = 0; i < dayCount; i++) {
                Object sleepValue = inputTable.getValueAt(i, 1);
                if (sleepValue != null) {
                    sleepHours[i] = Double.parseDouble(sleepValue.toString());
                } else {
                    validInput = false;
                }
            }
        } catch (NumberFormatException ex) {
            validInput = false;
        }

        if (!validInput) {
            g.drawString("Please fill all sleep hours to generate the graph.", padding, height / 2);
            return;
        }

    
        g.setColor(Color.BLACK);
        g.drawLine(padding, height - padding, padding, padding);
        g.drawLine(padding, height - padding, width - padding, height - padding);

        g.drawString("Hours", padding - 30, padding - 10);
        g.drawString("Days", width / 2, height - 10);

        
        int maxHours = 12; 
        int[] xPoints = new int[dayCount];
        int[] yPoints = new int[dayCount];

        g.setColor(Color.BLUE);
        for (int i = 0; i < dayCount; i++) {
            xPoints[i] = padding + i * graphWidth / (dayCount - 1);
            yPoints[i] = height - padding - (int) (sleepHours[i] * graphHeight / maxHours);
            g.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
        }

        for (int i = 0; i < dayCount - 1; i++) {
            g.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Energy Health Tracker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            frame.add(new EnergyHealthTracker());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}