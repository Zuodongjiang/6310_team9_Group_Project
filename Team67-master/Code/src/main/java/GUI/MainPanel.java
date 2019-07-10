package GUI;


import Model.InputFile;
import Model.LawnMower;
import Model.SimulationMonitor;
import Viewer.MowerStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class MainPanel extends JFrame implements Runnable{

    final InputFile cachedInput;

    Thread animatorThread;
    int delay = 1000 / 60;

    public MainPanel(SimulationMonitor simulationMonitor, InputFile input) {
        this.simulationMonitor = simulationMonitor;
        this.cachedInput = input;
        this.mowerCount = simulationMonitor.getMowerList().length;
        this.mowerTableData = new String[mowerCount][5];
        this.mowerList = simulationMonitor.getMowerList();
        initComponents();
    }

    private void initComponents() {

        btnPanel = new JPanel();
        nextStepBtn = new JButton();
        stopBtn = new JButton();
//        nextTurnBtn = new JButton();
        forwardBtn = new JButton();
        txtPanel1 = new JPanel();
        currentState = new Label();
        txtPanel2 = new JPanel();
        txtPanel3 = new JPanel();
        cutGrassState = new Label();
        remainGrassState = new Label();
        mowerStatusPanel = new JScrollPane();
        currentLawn = new JTable();
        realTimeLawnPanel = new JScrollPane();
        currentLawnPanel = new LawnPanel(500, 500, simulationMonitor.getLawn().getWidth(), simulationMonitor.getLawn().getHeight(), simulationMonitor.getLawn(), 2, 20, 12);
        jLabel1 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        nextStepBtn.setText("Next Step");
        nextStepBtn.setActionCommand("");
        nextStepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextStepBtnActionPerformed(evt);
            }
        });

        stopBtn.setText("Stop & Restart");
        stopBtn.setActionCommand("");
        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });

        forwardBtn.setText("Fast-Forward");
        forwardBtn.setActionCommand("");
        forwardBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                forwardBtnActionPerformed(evt);
            }
        });

//        nextTurnBtn.setText("Next Turn");
//        nextTurnBtn.setActionCommand("");
//        nextTurnBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                nextTurnBtnActionPerformed(evt);
//            }
//        });


        GroupLayout btnPanelLayout = new GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
                btnPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(btnPanelLayout.createSequentialGroup()
                                .addGap(20, 25, 40)
                                .addComponent(nextStepBtn)
                                .addGap(20, 25, 40)
                                .addComponent(stopBtn)
                                .addGap(20, 25, 40)
                                .addComponent(forwardBtn))
//                                .addGap(20, 25, 40)
//                                .addComponent(nextTurnBtn))
        );
        btnPanelLayout.setVerticalGroup(
                btnPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(btnPanelLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(btnPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(nextStepBtn)
                                        .addComponent(stopBtn)
//                                        .addComponent(nextTurnBtn)
                                        .addComponent(forwardBtn)))
        );

        currentState.setText("Turns Done:   " + (simulationMonitor.getInitialTotalTurn() - simulationMonitor.getTotalTurn()));

        generateTxtPanelLayout(txtPanel1, currentState);

        cutGrassState.setText("Grass Cut:      " + simulationMonitor.getCutGrass());

        generateTxtPanelLayout(txtPanel2, cutGrassState);

        remainGrassState.setText("Grass Left: " + (simulationMonitor.getTotalGrass() - simulationMonitor.getCutGrass() + simulationMonitor.getTotalCrater()));

        generateTxtPanelLayout(txtPanel3, remainGrassState);


        String[] columnNames = new String[]{
                "Mower ID", "Status", "Direction", "Next Action", "Turns to stall"
        };


        //Mower Status table
        //initialize the mower status table
        jLabel1.setText("Mower State");
        for (int i = 0; i < mowerCount; i++) {
            mowerTableData[i][0] = "Mower " + (i + 1);
        }
        for (int i = 0; i < mowerCount; i++) {
            mowerTableData[i][1] = "N/A";
            mowerTableData[i][2] = "North";
            mowerTableData[i][3] = "Turned Off";
            mowerTableData[i][4] = Integer.toString(mowerList[i].getStallTurn());
        }
        DefaultTableModel model = new DefaultTableModel(mowerTableData, columnNames) {
            public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

        JTable mowerStatusTable = new JTable(model);
        mowerStatusPanel.setViewportView(mowerStatusTable);


        // real time lawn map
        currentLawnPanel.setAutoscrolls(false);
        realTimeLawnPanel.setViewportView(currentLawnPanel);


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(txtPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(jLabel1))
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(mowerStatusPanel, GroupLayout.PREFERRED_SIZE, 450, GroupLayout.PREFERRED_SIZE))
                        .addGroup(GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                                .addComponent(realTimeLawnPanel, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel1)
                                .addGap(30, 30, 30)
                                .addComponent(mowerStatusPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(txtPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addComponent(realTimeLawnPanel, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void generateTxtPanelLayout(JPanel txtPanel, Label state) {
        GroupLayout txtPanelLayout = new GroupLayout(txtPanel);
        txtPanel.setLayout(txtPanelLayout);
        txtPanelLayout.setHorizontalGroup(
                txtPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(txtPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(state, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        txtPanelLayout.setVerticalGroup(
                txtPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(txtPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(state, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {
        stopSimulation();
    }

    private void forwardBtnActionPerformed(java.awt.event.ActionEvent evt) {
/*        while (simulationMonitor.issimulationOn()) {
            simulationMonitor.nextMove();
            updateGUI();
        }*/
        if (animatorThread == null){
            animatorThread = new Thread(this);
        }
        animatorThread.start();
    }

    private void nextStepBtnActionPerformed(java.awt.event.ActionEvent evt) {
        if (simulationMonitor.issimulationOn()){
            simulationMonitor.nextMove();
            updateGUI();
        } else {
            stopSimulation();
        }
    }

    public void run(){
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (Thread.currentThread() == animatorThread && simulationMonitor.issimulationOn()){
            simulationMonitor.nextMove();
            updateGUI();
            try{
                Thread.sleep(delay);
            } catch (InterruptedException ex){
                break;
            }
        }
        stopSimulation();
    }

//    private void nextTurnBtnActionPerformed(java.awt.event.ActionEvent evt) {
////        simulationMonitor.reset();
////        currentLawnPanel.update(simulationMonitor.getLawn().getWidth(), simulationMonitor.getLawn().getHeight(), simulationMonitor.getInitialLawn());
//    }

    private void updateGUI() {
        currentLawnPanel.update(simulationMonitor.getLawn().getWidth(), simulationMonitor.getLawn().getHeight(), simulationMonitor.getLawn());
        cutGrassState.setText("Grass Cut: " + (simulationMonitor.getCutGrass()));
        remainGrassState.setText("Grass Left: " + (simulationMonitor.getTotalGrass() - simulationMonitor.getCutGrass()));
        currentState.setText("Turns Done: " + (simulationMonitor.getInitialTotalTurn() - simulationMonitor.getTotalTurn()));
        for (int i = 0; i < mowerCount; i++) {
            mowerTableData[i][1] = mowerList[i].getCurrentStatus().toString();
            mowerTableData[i][2] = mowerList[i].getCurrentDirection().toString();
            if (mowerList[i].getCurrentStatus().toString().equals("turnedOff")){
                mowerTableData[i][3] = "turn off";
            } else if (mowerList[i].getCurrentStatus().toString().equals("stalled") ||
                    simulationMonitor.getLawn().getSquareState(mowerList[i].getCurrentLoc()).toString().equals("puppy_mower")){
                mowerTableData[i][3] = "stalled";
            } else if ( mowerList[i].getCachedNextAction() == null){
                mowerTableData[i][3] = "scan";
            } else {
                mowerTableData[i][3] = mowerList[i].getCachedNextAction().getName();
            }

            mowerTableData[i][4] = Integer.toString(mowerList[i].getStallTurn());
        }
        String[] columnNames = new String[]{
                "Mower ID", "Status", "Direction", "Next Action", "Turns to stall"
        };

        DefaultTableModel model = new DefaultTableModel(mowerTableData, columnNames);

        JTable mowerStatusTable = new JTable(model);


        mowerStatusTable = new JTable(model) {

            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int currentRowIndx = simulationMonitor.getCurrentMowerIdx();
//                System.out.println(currentRowIndx);
                if (row == currentRowIndx) {
                    component.setBackground(Color.yellow);
                } else {
                    component.setBackground(Color.white);
                }

                return component;
            }
        };
        mowerStatusPanel.setViewportView(mowerStatusTable);
    }

    private void stopSimulation(){
        LawnMower[] mowerList = simulationMonitor.getMowerList();
        for (int i = 0; i < mowerList.length; i++) {
            mowerList[i].setCurrentStatus(MowerStatus.turnedOff);
        }
        updateGUI();
        JOptionPane.showMessageDialog(null, "Simulation Stopped, Reset Now", "InfoBox: " + "Stopped", JOptionPane.INFORMATION_MESSAGE);
        this.getContentPane().removeAll();
        this.revalidate();
        this.repaint();
        SimulationMonitor simulationMonitor1 = new SimulationMonitor();
        simulationMonitor1.initialize(cachedInput);
        MainPanel mainPanel = new MainPanel(simulationMonitor1, cachedInput);
        mainPanel.setVisible(true);
    }

    // Variables declaration - do not modify
    private JPanel btnPanel;
    private JTable currentLawn;
    private LawnPanel currentLawnPanel;
    private java.awt.Label currentState;
    private JButton forwardBtn;
    private java.awt.Label cutGrassState;
    private java.awt.Label remainGrassState;
    private JLabel jLabel1;
    private JScrollPane mowerStatusPanel;
    private JScrollPane realTimeLawnPanel;
    private JButton nextStepBtn;
    private JButton stopBtn;
    private JButton nextTurnBtn;
    private JPanel txtPanel1;
    private JPanel txtPanel2;
    private JPanel txtPanel3;
    // End of variables declaration
    private SimulationMonitor simulationMonitor;
    private String[][] mowerTableData;
    private int mowerCount;
    private LawnMower[] mowerList;
}
