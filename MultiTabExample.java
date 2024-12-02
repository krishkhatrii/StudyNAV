import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("StudyNAV");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        MainAppSwing tb = new MainAppSwing();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", new Dashboard());

        AttendanceTrackerGUI attendanceTab = new AttendanceTrackerGUI();
        tabbedPane.addTab("Timetable", tb );
        tabbedPane.addTab("Goals", new GoalTrackingApp());
        tabbedPane.addTab("Attendance", attendanceTab);
        tabbedPane.addTab("Energy", new EnergyHealthTracker());
        tabbedPane.addTab("Profile", new StudentPanel());

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = sourceTabbedPane.getSelectedIndex();
                String selectedTab = sourceTabbedPane.getTitleAt(selectedIndex);
                if ("Attendance".equals(selectedTab)) {
                    attendanceTab.refreshData();
                }
        
                if ("Timetable".equals(selectedTab)) {
                    tb.refreshData();
                }
            }
        });
        

        frame.add(tabbedPane);


        frame.setVisible(true);
    }
}
