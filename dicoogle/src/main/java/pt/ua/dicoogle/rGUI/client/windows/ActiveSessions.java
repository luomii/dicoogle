/**
 * Copyright (C) 2014  Universidade de Aveiro, DETI/IEETA, Bioinformatics Group - http://bioinformatics.ua.pt/
 *
 * This file is part of Dicoogle/dicoogle.
 *
 * Dicoogle/dicoogle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dicoogle/dicoogle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * ActiveSessions.java
 *
 * Created on 26/Abr/2010, 14:56:44
 */

package pt.ua.dicoogle.rGUI.client.windows;

import java.awt.Image;
import java.awt.Toolkit;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.IActiveSessions;
import pt.ua.dicoogle.server.users.UserON;

/**
 *
 * @author samuelcampos
 */
@Deprecated
public class ActiveSessions extends javax.swing.JFrame {
    private HashMap<Integer, UserON> usersTable;
    private IActiveSessions activeSessions;

    private int adminID = -1;

    private Timer timer;
    private static int timeoutTime = 10000;  //10 seconds
    private TimerTask task;

    private static ActiveSessions instance = null;

    public static synchronized ActiveSessions getInstance() {
        if (instance == null) {
            instance = new ActiveSessions();
        }
        return instance;
    }

    /** Creates new form ActiveSessions */
    private ActiveSessions() {
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("trayicon.gif"));
        this.setIconImage(image);

        this.setTitle("Active Users");
        timer = new Timer();

        activeSessions = AdminRefs.getInstance().getActiveSessions();

        try {
            adminID = activeSessions.getAdminID();
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ActiveSessions.class).error(ex.getMessage(), ex);
        }
    }

    private void RefreshUsers(){
        try {
            usersTable = activeSessions.getUsersTable();

            DefaultListModel model = (DefaultListModel) jListUsers.getModel();
            model.clear();

            Iterator<Integer> en = usersTable.keySet().iterator();

            while(en.hasNext())
                model.addElement(usersTable.get(en.next()));

            jListUsers.setModel(model);
        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ActiveSessions.class).error(ex.getMessage(), ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        DefaultListModel model = new DefaultListModel();
        jListUsers = new javax.swing.JList(model);
        jButtonLogout = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(446, 291));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jScrollPane1.setViewportView(jListUsers);

        jButtonLogout.setText("Logout User");
        jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogoutActionPerformed(evt);
            }
        });

        jLabel1.setText("Active users in this moment:");

        jLabel2.setText("List Refresh Time: 10 seconds");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(41, 41, 41)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButtonLogout))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel1)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 367, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonLogout)
                    .add(jLabel2))
                .add(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        try {
            UserON user = (UserON) jListUsers.getSelectedValue();

            if(user != null){
                if(user.getUserID() != adminID){
                    if(activeSessions.adminLogoutUser(user.getUserID()))
                        RefreshUsers();
                }
                else
                    JOptionPane.showMessageDialog(this, "You can't logout yourself.",
                    "User Selection", JOptionPane.INFORMATION_MESSAGE);
            }
            else
                JOptionPane.showMessageDialog(this, "Please select one user.",
                "User Selection", JOptionPane.INFORMATION_MESSAGE);

        } catch (RemoteException ex) {
            LoggerFactory.getLogger(ActiveSessions.class).error(ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        task = new UsersRegfresh();
        timer.schedule(task, 100, timeoutTime);
    }//GEN-LAST:event_formComponentShown

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       task.cancel();
       timer.purge();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jListUsers;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private class UsersRegfresh extends TimerTask {

        @Override
        public void run() {
            RefreshUsers();
        }
    }
}
