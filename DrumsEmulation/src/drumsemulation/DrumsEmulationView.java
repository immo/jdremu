/*
 *  DrumsEmulation - drum emulator & sythesizer
 *  Copyright (C) 2011 C.D.Immanuel Albrecht
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

/*
 * DrumsEmulationView.java
 */
package drumsemulation;

import drumsemulation.abstraction.abstractData;
import drumsemulation.abstraction.instrumentMode;
import drumsemulation.abstraction.joist;
import drumsemulation.abstraction.scaffolding;
import drumsemulation.snd.hitGenerator;
import java.awt.event.MouseEvent;
import javax.swing.event.TableModelEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * The application's main frame.
 */
public class DrumsEmulationView extends FrameView implements TableModelListener {

    public int bank=0;

    public DrumsEmulationView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;

            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");


    }

    public void tableChanged(TableModelEvent tme) {

        if (tme.getSource() == instrumentTable.getModel()) {
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            if (tme.getType() == TableModelEvent.UPDATE) {
                if (tme.getColumn() == 0) {
                    for (int row = tme.getFirstRow(); row <= tme.getLastRow(); ++row) {

                        app.setGeneratorName(row, (String) instrumentTable.getValueAt(row, 0));

                    }
                } else if (tme.getColumn() == 2) {
                    for (int row = tme.getFirstRow(); row <= tme.getLastRow(); ++row) {
                        hitGenerator new_generator = hitGenerator.getGeneratorByDesc((String) instrumentTable.getValueAt(row, 2));

                        app.setGenerator(row, new_generator);
                    }
                }
            }
        } else if (tme.getSource() == playModeTable.getModel()) {
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            if (tme.getType() == TableModelEvent.UPDATE) {
                if (tme.getColumn() == 0) {
                    for (int row = tme.getFirstRow(); row <= tme.getLastRow(); ++row) {

                        app.setModeName(row, (String) playModeTable.getValueAt(row, 0));

                    }
                } else if (tme.getColumn() == 2) {
                    for (int row = tme.getFirstRow(); row <= tme.getLastRow(); ++row) {
                        instrumentMode new_generator = new instrumentMode((String) playModeTable.getValueAt(row, 2));

                        app.setMode(row, new_generator);
                    }
                }
            }
        } else {

            System.out.println("Wrong source!" + tme.getSource());
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DrumsEmulationApp.getApplication().getMainFrame();
            aboutBox = new DrumsEmulationAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DrumsEmulationApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        instrumentTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextBuffersize = new javax.swing.JTextPane();
        jSlider2 = new javax.swing.JSlider();
        jLabel14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        playModeTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jDotCodePane = new javax.swing.JEditorPane();
        jLabel4 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jBindingsText = new javax.swing.JEditorPane();
        jLabel5 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPlayToggle = new javax.swing.JToggleButton();
        jSlider1 = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPlaybackTerm = new javax.swing.JEditorPane();
        jButton10 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextInputArea = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextBankUp = new javax.swing.JTextPane();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextBankDown = new javax.swing.JTextPane();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextBank = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(drumsemulation.DrumsEmulationApp.class).getContext().getResourceMap(DrumsEmulationView.class);
        jToggleButton1.setText(resourceMap.getString("jToggleButton1.text")); // NOI18N
        jToggleButton1.setName("jToggleButton1"); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        instrumentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Hit", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        instrumentTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        instrumentTable.setName("instrumentTable"); // NOI18N
        instrumentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                instrumentTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(instrumentTable);
        {javax.swing.table.DefaultTableModel mdl = (javax.swing.table.DefaultTableModel)instrumentTable.getModel();
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            for (int i=0;i<app.getGeneratorsCount();++i) {
                mdl.addRow(new Object[]{app.getGeneratorName(i),"(click)",app.getGenerator(i).getDescription()});
            }
            instrumentTable.getColumnModel().getColumn(2).setPreferredWidth(400);
            instrumentTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            instrumentTable.getColumnModel().getColumn(1).setPreferredWidth(50);

            mdl.addTableModelListener(this);}

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setIcon(resourceMap.getIcon("jLabel2.icon")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel3.setIcon(resourceMap.getIcon("jLabel3.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        jTextBuffersize.setText(resourceMap.getString("jTextBuffersize.text")); // NOI18N
        jTextBuffersize.setName("jTextBuffersize"); // NOI18N
        jScrollPane10.setViewportView(jTextBuffersize);

        jSlider2.setMaximum(0);
        jSlider2.setMinimum(-400);
        jSlider2.setValue(-280);
        jSlider2.setName("jSlider2"); // NOI18N
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jToggleButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)))
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3))
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        playModeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Hit", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        playModeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        playModeTable.setName("playModeTable"); // NOI18N
        playModeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playModeTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(playModeTable);
        {javax.swing.table.DefaultTableModel mdl = (javax.swing.table.DefaultTableModel)playModeTable.getModel();
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            for (int i=0;i<app.getModesCount();++i) {
                mdl.addRow(new Object[]{app.getModeName(i),"(click)",app.getMode(i).getDescription()});
            }
            playModeTable.getColumnModel().getColumn(2).setPreferredWidth(400);
            playModeTable.getColumnModel().getColumn(0).setPreferredWidth(80);
            playModeTable.getColumnModel().getColumn(1).setPreferredWidth(50);

            mdl.addTableModelListener(this);}

        jLabel7.setIcon(resourceMap.getIcon("jLabel7.icon")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jLabel8.setIcon(resourceMap.getIcon("jLabel8.icon")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 177, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton5)
                                .addComponent(jButton4))
                            .addComponent(jLabel6)))
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jDotCodePane.setFont(resourceMap.getFont("jDotCodePane.font")); // NOI18N
        jDotCodePane.setName("jDotCodePane"); // NOI18N
        jScrollPane3.setViewportView(jDotCodePane);
        jDotCodePane.setText(DrumsEmulationApp.getStringFromConfFile("scaffoldings"));

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 292, Short.MAX_VALUE)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton6)
                        .addComponent(jButton7)
                        .addComponent(jButton11)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jBindingsText.setFont(resourceMap.getFont("jBindingsText.font")); // NOI18N
        jBindingsText.setName("jBindingsText"); // NOI18N
        jScrollPane4.setViewportView(jBindingsText);
        jBindingsText.setText(DrumsEmulationApp.getStringFromConfFile("scaffoldings-bindings"));

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8)
                            .addComponent(jButton9)
                            .addComponent(jButton12))
                        .addGap(9, 9, 9))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jPlayToggle.setText(resourceMap.getString("jPlayToggle.text")); // NOI18N
        jPlayToggle.setName("jPlayToggle"); // NOI18N
        jPlayToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlayToggleActionPerformed(evt);
            }
        });

        jSlider1.setMajorTickSpacing(50);
        jSlider1.setMaximum(200);
        jSlider1.setMinorTickSpacing(2);
        jSlider1.setPaintTicks(true);
        jSlider1.setValue(100);
        jSlider1.setName("jSlider1"); // NOI18N
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jPlaybackTerm.setFont(resourceMap.getFont("jPlaybackTerm.font")); // NOI18N
        jPlaybackTerm.setName("jPlaybackTerm"); // NOI18N
        jScrollPane5.setViewportView(jPlaybackTerm);

        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPlayToggle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(5, 5, 5)
                .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(349, 349, 349)
                .addComponent(jButton10)
                .addContainerGap())
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPlayToggle)
                    .addComponent(jLabel9)
                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextInputArea.setColumns(20);
        jTextInputArea.setFont(resourceMap.getFont("jTextInputArea.font")); // NOI18N
        jTextInputArea.setLineWrap(true);
        jTextInputArea.setRows(5);
        jTextInputArea.setFocusCycleRoot(true);
        jTextInputArea.setName("jTextInputArea"); // NOI18N
        jTextInputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextInputAreaKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTextInputArea);

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTextBankUp.setFont(new java.awt.Font("Arial Black", 0, 48));
        jTextBankUp.setText(resourceMap.getString("jTextBankUp.text")); // NOI18N
        jTextBankUp.setFocusCycleRoot(false);
        jTextBankUp.setName("jTextBankUp"); // NOI18N
        jScrollPane7.setViewportView(jTextBankUp);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        jTextBankDown.setFont(new java.awt.Font("Arial Black", 0, 48));
        jTextBankDown.setText(resourceMap.getString("jTextBankDown.text")); // NOI18N
        jTextBankDown.setFocusCycleRoot(false);
        jTextBankDown.setName("jTextBankDown"); // NOI18N
        jScrollPane8.setViewportView(jTextBankDown);

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        jTextBank.setEditable(false);
        jTextBank.setFont(resourceMap.getFont("jTextBank.font")); // NOI18N
        jTextBank.setText(resourceMap.getString("jTextBank.text")); // NOI18N
        jTextBank.setFocusCycleRoot(false);
        jTextBank.setName("jTextBank"); // NOI18N
        jScrollPane9.setViewportView(jTextBank);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel12)
                        .addGap(48, 48, 48)
                        .addComponent(jLabel13)
                        .addGap(10, 10, 10)))
                .addContainerGap(354, Short.MAX_VALUE))
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jScrollPane8)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(drumsemulation.DrumsEmulationApp.class).getContext().getActionMap(DrumsEmulationView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void instrumentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_instrumentTableMouseClicked
        int c = instrumentTable.getSelectedColumn();

        int i = instrumentTable.getSelectedRow();
        if (c == 1) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                DrumsEmulationApp.getApplication().instrument_hit_button(i);
            } else {
                DrumsEmulationApp.getApplication().instrument_hit_button2(i);
            }
        }
    }//GEN-LAST:event_instrumentTableMouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        float p1 = ((float) evt.getPoint().x) / 49.f;
        float p2 = ((float) evt.getPoint().y) / 49.f;
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.p2dHitGenerator(instrumentTable.getSelectedRow(), p1, p2);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        int l = evt.getPoint().x;
        if (l < 4) {
            l = 4;
        } else if (l > 104) {
            l = 104;
        }
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.levelHitGenerator(instrumentTable.getSelectedRow(), ((float) l) / 99.f);
}//GEN-LAST:event_jLabel2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int i = instrumentTable.getSelectedRow();
        DefaultTableModel mdl = (DefaultTableModel) instrumentTable.getModel();
        mdl.removeRow(i);
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.delGenerator(i);
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int i = instrumentTable.getSelectedRow();
        if (i == -1) {
            DefaultTableModel mdl = (DefaultTableModel) instrumentTable.getModel();
            mdl.addRow(new Object[]{"Beep", "(click)", "Beep()"});
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            app.addNamedGenerator("Beep", "Beep()");
        } else {
            DefaultTableModel mdl = (DefaultTableModel) instrumentTable.getModel();
            mdl.addRow(new Object[]{instrumentTable.getValueAt(i, 0) + "'", "(click)", instrumentTable.getValueAt(i, 2)});
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            app.addNamedGenerator((String) instrumentTable.getValueAt(i, 0) + "'", (String) instrumentTable.getValueAt(i, 2));
        }
}//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DrumsEmulationApp.getApplication().beep(1.f);
}//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        DrumsEmulationApp.getApplication().setRequestBufferSize(Integer.parseInt(jTextBuffersize.getText()));

        boolean on_air = DrumsEmulationApp.getApplication().setOn_air(jToggleButton1.isSelected());
        jToggleButton1.setSelected(on_air);
}//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       int i = instrumentTable.getSelectedRow();
        DefaultTableModel mdl = (DefaultTableModel) playModeTable.getModel();
        mdl.removeRow(i);
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.delMode(i);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int i = playModeTable.getSelectedRow();
        if (i == -1) {
            DefaultTableModel mdl = (DefaultTableModel) playModeTable.getModel();
            mdl.addRow(new Object[]{"default", "(click)", ""});
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            app.addNamedMode("default", "");
        } else {
            DefaultTableModel mdl = (DefaultTableModel) playModeTable.getModel();
            mdl.addRow(new Object[]{playModeTable.getValueAt(i, 0) + "'", "(click)", playModeTable.getValueAt(i, 2)});
            DrumsEmulationApp app = DrumsEmulationApp.getApplication();
            app.addNamedMode((String) playModeTable.getValueAt(i, 0) + "'", (String) playModeTable.getValueAt(i, 2));
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void playModeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playModeTableMouseClicked
        int c = playModeTable.getSelectedColumn();

        int i = playModeTable.getSelectedRow();
        if (c == 1) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                DrumsEmulationApp.getApplication().mode_hit_button(i);
            } else {
                DrumsEmulationApp.getApplication().mode_hit_button2(i);
            }
        }
    }//GEN-LAST:event_playModeTableMouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        int l = evt.getPoint().x;
        if (l < 4) {
            l = 4;
        } else if (l > 104) {
            l = 104;
        }
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.levelHitMode(playModeTable.getSelectedRow(), ((float) l) / 99.f);
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        float p1 = ((float) evt.getPoint().x) / 49.f;
        float p2 = ((float) evt.getPoint().y) / 49.f;
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();

        int row = playModeTable.getSelectedRow();

        String desc = app.getMode(row).getDescription();

        String updated_desc = "";

        boolean found_p1=false;
        boolean found_p2=false;

        Scanner scan = new Scanner(desc);
        scan.useDelimiter(",");
        while (scan.hasNext()) {
            String parm = scan.next();
            if (parm.contains("=")) {
                int idx = parm.indexOf("=");
                String pname = parm.substring(0, idx).trim();
                String pval = parm.substring(idx + 1).trim();

                if (pname.equals("p1")) {
                    pval = Float.toString(p1);
                    found_p1 = true;
                } else if (pname.equals("p2")) {
                    pval = Float.toString(p2);
                    found_p2 = true;
                }
                updated_desc +=","+pname + "="+pval;
            }
        }

        if (!found_p1) {
            updated_desc +=",p1="+Float.toString(p1);
        }

        if (!found_p2) {
            updated_desc += ",p2="+Float.toString(p2);
        }
        updated_desc = updated_desc.substring(1);

        app.setMode(row, new instrumentMode(updated_desc));

        playModeTable.getModel().setValueAt(updated_desc, row, 2);

        app.mode_hit_button(row);
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.getData().build(jDotCodePane.getText());
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        app.getData().buildVars(jBindingsText.getText());
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        System.out.println(app.getData().joistsContent());
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       DrumsEmulationApp app = DrumsEmulationApp.getApplication();
        System.out.println(app.getData().scaffoldingsContent());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jPlayToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlayToggleActionPerformed
        boolean on = jPlayToggle.isSelected();

        DrumsEmulationApp.getApplication().setPlayback(on);
    }//GEN-LAST:event_jPlayToggleActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        abstractData dta = DrumsEmulationApp.getApplication().getData();
        joist eval = dta.evaluateTerm(jPlaybackTerm.getText());
        System.out.println(eval);
        if (eval instanceof scaffolding) {
            ((scaffolding)eval).disprepare();
        }
        eval.prepareLayout();
        System.out.println(eval);
        DrumsEmulationApp.getApplication().reSetMaster(eval);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
       DrumsEmulationApp.setConfFile("scaffoldings-bindings", jBindingsText.getText());
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        DrumsEmulationApp.setConfFile("scaffoldings", jDotCodePane.getText());
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        float pos = jSlider1.getValue();
        if (pos == 100) {
            DrumsEmulationApp.getApplication().setPlaybackSpeed(1.f);
        } else if (pos < 100) {
            pos /= 100.f;
            DrumsEmulationApp.getApplication().setPlaybackSpeed(pos*0.7f + 0.3f);
        } else {
            pos /= 100.f;
            DrumsEmulationApp.getApplication().setPlaybackSpeed(pos*0.7f + 0.3f);
        }
    }//GEN-LAST:event_jSlider1StateChanged

    private void jTextInputAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextInputAreaKeyPressed
        char keyname=Character.toLowerCase(evt.getKeyChar());
        String kname = "";
        kname += keyname;
        if (kname.equals(jTextBankUp.getText())) {
            System.out.println("BANK-UP");
            bank ++;
            if (bank >= 8) {
                bank = 0;
            }
            jTextBank.setText(""+bank);
            return;
        }
        if (kname.equals(jTextBankDown.getText())) {
            System.out.println("BANK-DOWN");
            bank --;
            if (bank < 0) {
                bank = 7;
            }
            jTextBank.setText(""+bank);
            return;
        }
        String bind_name = "key-"+keyname+"-"+bank;
        joist j = DrumsEmulationApp.getApplication().getData().getNamedJoist(bind_name);
        System.out.println("key-down="+bind_name);
        if (j!=null) {
            System.out.println("key-down!!");
            joist eval = DrumsEmulationApp.getApplication().getData().evaluateTerm(bind_name);
            
            if (eval instanceof scaffolding) {
                ((scaffolding)eval).disprepare();
            }

            eval.prepareLayout();
            String eval_content=eval.toString();
            System.out.println(eval_content);
            float duration = eval.duration();
            DrumsEmulationApp.getApplication().reSetMaster(eval);
            jTextInputArea.setText("\n"+bind_name+"\t"+duration+"\n"+eval_content);
            jTextInputArea.setCaretPosition(0);
            }
    }//GEN-LAST:event_jTextInputAreaKeyPressed

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        float dB = jSlider2.getValue()*0.005f;
        System.out.println("dB="+dB);
        System.out.println("exp="+Math.exp(dB));
        DrumsEmulationApp.getApplication().set_total_lvl(dB);
    }//GEN-LAST:event_jSlider2StateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable instrumentTable;
    private javax.swing.JEditorPane jBindingsText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JEditorPane jDotCodePane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JToggleButton jPlayToggle;
    private javax.swing.JEditorPane jPlaybackTerm;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextBank;
    private javax.swing.JTextPane jTextBankDown;
    private javax.swing.JTextPane jTextBankUp;
    private javax.swing.JTextPane jTextBuffersize;
    private javax.swing.JTextArea jTextInputArea;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTable playModeTable;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
