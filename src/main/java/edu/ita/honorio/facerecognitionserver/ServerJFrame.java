package edu.ita.honorio.facerecognitionserver;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;
import java.util.concurrent.ExecutionException;

public class ServerJFrame extends javax.swing.JFrame {
    private DatabaseReference m_enrollRequest = null;
    private DatabaseReference m_enrollResponse = null;
    private DatabaseReference m_recognizeRequest = null;
    private DatabaseReference m_recognizeResponse = null;
    private ChildEventListener m_enrollListener = null;
    private ChildEventListener m_recognizeListener = null;
    private String m_jarPath = null;
    private Config m_config = null;
    private final ExecutorService m_executor;
    private static final int NTHREADS = 30;

    public ServerJFrame() {
        initComponents();
        
        m_executor = Executors.newFixedThreadPool(NTHREADS);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnStart = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        lblMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Servidor de Reconocimiento Facial");
        setMaximumSize(new java.awt.Dimension(400, 300));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setName("frmMain"); // NOI18N
        setPreferredSize(new java.awt.Dimension(400, 300));
        setResizable(false);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWidths = new int[] {0, 5, 0};
        layout.rowHeights = new int[] {0, 25, 0, 25, 0, 25, 0, 25, 0, 25, 0};
        getContentPane().setLayout(layout);

        btnStart.setText("Iniciar");
        btnStart.setPreferredSize(new java.awt.Dimension(62, 24));
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        getContentPane().add(btnStart, gridBagConstraints);

        btnExit.setText("Salir");
        btnExit.setPreferredSize(new java.awt.Dimension(62, 24));
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        getContentPane().add(btnExit, gridBagConstraints);

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMessage.setMaximumSize(new java.awt.Dimension(400, 14));
        lblMessage.setMinimumSize(new java.awt.Dimension(400, 14));
        lblMessage.setPreferredSize(new java.awt.Dimension(400, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        getContentPane().add(lblMessage, gridBagConstraints);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        btnStart.setEnabled(false);
        btnExit.setEnabled(false);
        
        SwingTask<Integer, Void> task = new SwingTask<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if(!checkResources()) {
                    return Enums.SRV_CHECK_RESOURCES;
                }
                
                if(!createDatabase()) {
                    return Enums.SRV_CREATE_DATABASE;
                }
                
                if (!initFirebase()) {
                    return Enums.SRV_INIT_FIREBASE;
                }
                
                Thread.sleep(1500);
                return Enums.SRV_OK;
            }
        };
        
        task.addDoneCallback((SwingTask<Integer, Void> t) -> {
            try {
                Integer result = t.get();
                if (result != Enums.SRV_OK) {
                    JOptionPane.showMessageDialog(ServerJFrame.this, getErrorMessage(result), Enums.SRV_ERROR_MSG_KEY, JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                    return;
                }
                
                runFirebase();
                btnExit.setEnabled(true);
                lblMessage.setText(Enums.SRV_LABEL_MSG_KEY);
                Logger.getLogger(ServerJFrame.class.getName()).log(Level.INFO, Enums.SRV_LABEL_MSG_KEY);
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(ServerJFrame.this, getErrorMessage(Enums.SRV_UNEXPECTED_ERROR, ex), Enums.SRV_ERROR_MSG_KEY, JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        
        SwingTaskExecutors.create(task)
                .setCancelable(false)
                .setModal(true)
                .setParentComponent(this)
                .setTitle(Enums.SRV_TITLE_INIT_MSG_KEY)
                .setMillisToPopup(100)
                .build()
                .execute();
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        btnExit.setEnabled(false);
        
        SwingTask<Integer, Void> task = new SwingTask<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if (m_enrollRequest != null && m_enrollListener != null) {
                    m_enrollRequest.removeEventListener(m_enrollListener);
                }
        
                if (m_recognizeRequest != null && m_recognizeListener != null) {
                    m_recognizeRequest.removeEventListener(m_recognizeListener);
                }
        
                m_executor.shutdown();
                try {
                    if (!m_executor.awaitTermination(Enums.SRV_TIMEOUT, TimeUnit.SECONDS)) {
                        m_executor.shutdownNow();
                        if (!m_executor.awaitTermination(Enums.SRV_TIMEOUT, TimeUnit.SECONDS)) {
                            Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, Enums.SRV_POOL_MSG_KEY);
                        }
                    }
                } catch (InterruptedException ex) {
                    m_executor.shutdownNow();
                    Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Thread.sleep(1500);
                return Enums.SRV_OK;
            }
        };
        
        task.addDoneCallback((SwingTask<Integer, Void> t) -> {
            Logger.getLogger(ServerJFrame.class.getName()).log(Level.INFO, Enums.SRV_EXIT_MSG_KEY);
            System.exit(1);
        });
        
        SwingTaskExecutors.create(task)
                .setCancelable(false)
                .setModal(true)
                .setParentComponent(this)
                .setTitle(Enums.SRV_TITLE_EXIT_MSG_KEY)
                .setMillisToPopup(100)
                .build()
                .execute();
    }//GEN-LAST:event_btnExitActionPerformed

    private boolean checkResources() {
        m_jarPath = Utils.getJarPath(ServerJFrame.class);
        if (m_jarPath == null) {
            return false;
        }
        
        boolean success = false;
        File resources = Paths.get(m_jarPath, Enums.SRV_RESOURCES_KEY).toFile();
        if (resources.exists() && resources.isDirectory()) {
            int count = 0;
            File[] list = resources.listFiles();
            for (File f : list) {
                if (f.getName().compareTo(Enums.SRV_HAARCASCADE_KEY) == 0 || 
                    f.getName().compareTo(Enums.SRV_FACENET_KEY) == 0 ||
                    f.getName().compareTo(Enums.SRV_SHAPE_PREDICTOR_KEY) == 0 || 
                    f.getName().compareTo(Enums.SRV_CONFIG_KEY) == 0 || 
                    f.getName().compareTo(Enums.SRV_SERVICE_ACCOUNT_KEY) == 0) {
                    count++;
                }
            }
            
            success = (count == 5);
        }
        
        if (!success) {
            return false;
        }
        
        m_config = Config.load(Paths.get(m_jarPath, Enums.SRV_RESOURCES_KEY, Enums.SRV_CONFIG_KEY).toString());
        if (m_config == null) {
            return false;
        }
        
        File dllPath = Paths.get(m_jarPath, Enums.SRV_LIBFACE_KEY).toFile();
        return dllPath.exists() && dllPath.isFile();
    }
    
    private boolean createDatabase() {
        try (Database db = new Database(m_jarPath)) {
            db.create();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private boolean initFirebase() {
        try {
            FileInputStream serviceAccount = new FileInputStream(Paths.get(m_jarPath, Enums.SRV_RESOURCES_KEY, Enums.SRV_SERVICE_ACCOUNT_KEY).toFile());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(m_config.databaseUrl)
                    .build();
            
            FirebaseApp app = FirebaseApp.initializeApp(options);
            if (app == null) {
                return false;
            }
            
            FirebaseDatabase db = FirebaseDatabase.getInstance(app);
            if (db == null) {
                return false;
            }
            
            DatabaseReference ref = db.getReference();
            return (ref != null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | IllegalStateException ex) {
            Logger.getLogger(ServerJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void runFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.setValueAsync(null);
        m_enrollRequest = ref.child(Enums.SRV_ENROLL_REQUEST_KEY);
        m_enrollResponse = ref.child(Enums.SRV_ENROLL_RESPONSE_KEY);
        m_recognizeRequest = ref.child(Enums.SRV_RECOGNIZE_REQUEST_KEY);
        m_recognizeResponse = ref.child(Enums.SRV_RECOGNIZE_RESPONSE_KEY);
        
        m_enrollListener = m_enrollRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                EnrollRequest request = ds.getValue(EnrollRequest.class);
                Runnable command = new EnrollRunnable(ds.getKey(), request, m_enrollResponse);
                m_executor.execute(command);
                ds.getRef().removeValueAsync();
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) { }

            @Override
            public void onChildRemoved(DataSnapshot ds) { }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) { }

            @Override
            public void onCancelled(DatabaseError de) { }
        });
        
        m_recognizeListener = m_recognizeRequest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                RecognizeRequest request = ds.getValue(RecognizeRequest.class);
                Runnable command = new RecognizeRunnable(ds.getKey(), request, m_recognizeResponse, m_config.threshold);
                m_executor.execute(command);
                ds.getRef().removeValueAsync();
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) { }

            @Override
            public void onChildRemoved(DataSnapshot ds) { }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) { }

            @Override
            public void onCancelled(DatabaseError de) { }
        });
    }
    
    private static class EnrollRunnable implements Runnable {
        private String m_key = null;
        private EnrollRequest m_request = null;
        private DatabaseReference m_enrollResponse = null;
        
        EnrollRunnable(String key, EnrollRequest request, DatabaseReference enrollResponse) {
            m_key = key;
            m_request = request;
            m_enrollResponse = enrollResponse;
        }
        
        @Override
        public void run() {
            EnrollResponse response = FaceRecognitionEngine.enroll(m_request);
            m_enrollResponse.child(m_key).setValueAsync(response);
        }
    }
    
    private static class RecognizeRunnable implements Runnable {
        private String m_key = null;
        private RecognizeRequest m_request = null;
        private DatabaseReference m_recognizeResponse = null;
        private float m_threshold = 0;
        
        RecognizeRunnable(String key, RecognizeRequest request, DatabaseReference recognizeResponse, float threshold) {
            m_key = key;
            m_request = request;
            m_recognizeResponse = recognizeResponse;
            m_threshold = threshold;
        }
        
        @Override
        public void run() {
            RecognizeResponse response = FaceRecognitionEngine.recognize(m_request, m_threshold);
            m_recognizeResponse.child(m_key).setValueAsync(response);
        }
    }    
    
    private static String getErrorMessage(int code) {
        return getErrorMessage(code, null);
    }
    
    private static String getErrorMessage(int code, Exception ex) {
        String message;
        switch (code) {
            case Enums.SRV_CHECK_RESOURCES:
                message = "Error al cargar los recursos del servidor";
                break;
                
            case Enums.SRV_CREATE_DATABASE:
                message = "Error al crear la base de datos";
                break;
                
            case Enums.SRV_INIT_FIREBASE:
                message = "Error al iniciar Firebase";
                break;
                
            default:
                message = "Error inesperado en el servidor: " + (ex != null ? ex.getMessage() : "null");
                break;
        }
        return message;
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
            new ServerJFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel lblMessage;
    // End of variables declaration//GEN-END:variables
}
