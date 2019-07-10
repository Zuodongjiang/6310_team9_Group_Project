package Controller;
import Model.InputFile;
import Model.SimulationMonitor;
import GUI.MainPanel;

public class run {
    public static void main(String[] args){
        // 1. read the file to initialize the monitor;
        final InputFile input = new InputFile();
        final SimulationMonitor monitorSim = new SimulationMonitor();

        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found.");
            return;
        }

        input.loadSetting(args[0]);
        monitorSim.initialize(input);

//        while (monitorSim.issimulationOn()) {
//            monitorSim.nextMove();
//        }

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainPanel(monitorSim, input).setVisible(true);
            }
        });
    }
}
