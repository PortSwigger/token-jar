/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenJar;

import burp.IBurpExtenderCallbacks;
import static burp.BurpExtender.*;
import burp.ITab;
import com.google.common.primitives.Bytes;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *
 * @author DanNegrea
 */
public class Tab extends javax.swing.JPanel implements ITab, TableModelListener{    
    private final DefaultTableModel tableModel;
    private final DataModel dataModel;
    final IBurpExtenderCallbacks callbacks;
    private final PersistSettings persistSettings;
    private Timer timerNewHere;
    
    /**
     * Creates new form Panel
     */
    public Tab(IBurpExtenderCallbacks callbacks) {
        initComponents();
        
        tokenTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        
        //On window resize resize also the columns
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeColumns();
            }
        });
        
        this.callbacks = callbacks;
        
        persistSettings = new PersistSettings(callbacks, 50);
        
        tableModel = (DefaultTableModel) tokenTable.getModel();        
        dataModel = new DataModel(tableModel, callbacks);
       
        //Restore table or put demo data
        this.restoreTableData(persistSettings.restore()); 
        this.resizeColumns();        
        this.setStatusColor();
        
        //tokenTable.getColumnModel().removeColumn(null);  //in case I want to hide some columns
        
        //(re)Initialize dataModel
        dataModel.init();

        //load Last Config Path from burp settings
        this.lastPathConfig.setText(persistSettings.restoreLastConfigPath());
        
        tokenTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        
        tableModel.addTableModelListener(this);
        
        /*Blink the 'New Here' meesage*/
        timerNewHere = new Timer(1000, new BlinkLabel(jLabelNewHere));
        if ( "true".equals(callbacks.loadExtensionSetting("NewHere:hide"))){
            jLabelNewHere.setText("");
        }
        else{
            timerNewHere.start();
        }
        
    }
    
    public DataModel getDataModel() {
        return dataModel;
    }
    public DefaultTableModel getTableModel(){
        return tableModel;
    }
    
    public PersistSettings getPersistSettings(){
        return persistSettings;
    }
    
    @Override
    public String getTabCaption() {
        return "Token Jar";
    }

    @Override
    public Component getUiComponent() {
        return this;
    }
  
    @Override
    public void tableChanged(TableModelEvent e) {
        //*DEBUG*/callbacks.printOutput("TableChanged() e.getType="+e.getType()+"  getFirstRow=: "+e.getFirstRow()+" getLastRow="+e.getLastRow()+"");
       
        int type = e.getType(); 
        int rowId = e.getFirstRow();
        //int column = e.getColumn();
        
        //No line was updated or the table was dumpped
        if (rowId<0)
            return;
        
        /*New "empty" row do just init */
        if (type==TableModelEvent.INSERT || type == TableModelEvent.DELETE){
            /* Reinit the table*/
            /* Save settings in Burp storage*/
            dataModel.init();
            Vector dataInTable = tableModel.getDataVector();
            persistSettings.save(dataInTable);
            return;
        }        
        /*Value already updated in Datamodel*/
        if (dataModel.isValueUpdated(rowId))
            return;
        /*else => value provided by user*/
         
        Object enable = tableModel.getValueAt(rowId, 0);
         
        //If UPDATE and not valid row then uncheck 'Enable'
        //w/o checking 'enable!=null && (boolean)enable' next time will run the function body again and again
        if (enable!=null && (boolean)enable && type==TableModelEvent.UPDATE && !dataModel.checkRow(rowId, true)){
            //*DEBUG*/callbacks.printOutput("row updated, but not valid");
            tableModel.setValueAt(false, rowId, 0);
            return;
        }
        
        /* Reinit the table*/
        /* Save settings in Burp storage*/
        dataModel.init();
        Vector dataInTable = tableModel.getDataVector();
        persistSettings.save(dataInTable);
    }
    /*
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tokenTable = new javax.swing.JTable();
        addToken = new javax.swing.JButton();
        removeToken = new javax.swing.JButton();
        openRegexWindow = new javax.swing.JButton();
        masterEnable = new javax.swing.JCheckBox();
        masterDebug = new javax.swing.JCheckBox();
        importConf = new javax.swing.JButton();
        exportConf = new javax.swing.JButton();
        statusColor = new javax.swing.JTextField();
        goToSite1 = new javax.swing.JLabel();
        masterRepeater = new javax.swing.JCheckBox();
        masterIntruder = new javax.swing.JCheckBox();
        masterProxy = new javax.swing.JCheckBox();
        jLabelNewHere = new javax.swing.JLabel();
        lastPathConfig = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        tokenTable.setAutoCreateRowSorter(true);
        tokenTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Enable", "Name", "header", "url", "body", "cookie", "other", "Value", "Eval (js code)", "Regex", "Path"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tokenTable.setColumnSelectionAllowed(true);
        tokenTable.setDragEnabled(true);
        jScrollPane2.setViewportView(tokenTable);
        tokenTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        addToken.setText("Add");
        addToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTokenActionPerformed(evt);
            }
        });

        removeToken.setText("Remove");
        removeToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTokenActionPerformed(evt);
            }
        });

        openRegexWindow.setText("Regex");
        openRegexWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRegexWindowActionPerformed(evt);
            }
        });

        masterEnable.setText("Enable");
        masterEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masterEnableActionPerformed(evt);
            }
        });

        masterDebug.setText("Debug");
        masterDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masterDebugActionPerformed(evt);
            }
        });

        importConf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/import-25px.png"))); // NOI18N
        importConf.setToolTipText("Import configuration");
        importConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importConfActionPerformed(evt);
            }
        });

        exportConf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/export-25px.png"))); // NOI18N
        exportConf.setToolTipText("Export configuration");
        exportConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportConfActionPerformed(evt);
            }
        });

        statusColor.setEditable(false);
        statusColor.setBorder(null);

        goToSite1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        goToSite1.setForeground(new java.awt.Color(51, 0, 255));
        goToSite1.setText("Getting Started");
        goToSite1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                goToSite1MouseClicked(evt);
            }
        });

        masterRepeater.setSelected(true);
        masterRepeater.setText("Repeater");
        masterRepeater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masterRepeaterActionPerformed(evt);
            }
        });

        masterIntruder.setSelected(true);
        masterIntruder.setText("Intruder");
        masterIntruder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masterIntruderActionPerformed(evt);
            }
        });

        masterProxy.setSelected(true);
        masterProxy.setText("Proxy");
        masterProxy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                masterProxyActionPerformed(evt);
            }
        });

        jLabelNewHere.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelNewHere.setForeground(java.awt.Color.magenta);
        jLabelNewHere.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNewHere.setText("First time here >>");
        jLabelNewHere.setToolTipText("");

        lastPathConfig.setText("optionaly config path");
        lastPathConfig.setToolTipText("Use it to quickly specify the path for load/save");

        jLabel1.setText("Last config.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(masterEnable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusColor, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(masterProxy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(masterIntruder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(masterRepeater)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(masterDebug)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelNewHere, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addComponent(jScrollPane2))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(importConf, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(21, 21, 21)
                            .addComponent(exportConf, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(18, 18, 18)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(removeToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addToken, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(openRegexWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(goToSite1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(lastPathConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addToken, openRegexWindow, removeToken});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(masterEnable)
                        .addComponent(statusColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(masterDebug)
                        .addComponent(goToSite1)
                        .addComponent(masterRepeater)
                        .addComponent(masterIntruder)
                        .addComponent(masterProxy)
                        .addComponent(jLabelNewHere)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(addToken)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeToken)
                        .addGap(49, 49, 49)
                        .addComponent(openRegexWindow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastPathConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportConf, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(importConf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE))
                .addContainerGap())
        );

        goToSite1.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 920, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTokenActionPerformed
        //tableModel.addRow(new Object[]{ enable, name, header, url, body, cookie, other, value, eval, regex, path });
        tableModel.addRow(new Object[]{ false, "csrf", false, false, true, false, false, "", "grp[1]", "csrf=([a-zA-Z0-9]*)", "*" });
    }//GEN-LAST:event_addTokenActionPerformed

    private void removeTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTokenActionPerformed
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove the selected line(s)?", "Warning", dialogButton);
        if(dialogResult == 0) { /*0 > Yes   1 > No */  
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try{
                            int[] selectedRows = tokenTable.getSelectedRows();
                            for (int i = 0; i < selectedRows.length; i++){
                                // -i adjusts the index, it counts for already deleted rows, the rest of the rows "move" up
                                int selectedRow = tokenTable.convertRowIndexToModel(selectedRows[i]-i);
                                tableModel.removeRow(selectedRow);
                            }
                        }catch(Exception ex){
                            PrintStream burpErr = new PrintStream(callbacks.getStderr());
                            ex.printStackTrace(burpErr);
                        }
                }
            });
        }
    }//GEN-LAST:event_removeTokenActionPerformed

    private void openRegexWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRegexWindowActionPerformed
        int selectedRow = tokenTable.getSelectedRow();                        
        selectedRow = tokenTable.convertRowIndexToModel(selectedRow);
        RegexWindow window = new RegexWindow(this, selectedRow, callbacks);                
        window.setVisible(true);        
    }//GEN-LAST:event_openRegexWindowActionPerformed

    Object getCell(int row, int column){
        return tableModel.getValueAt(row, column);
    }
    boolean setCell(int row, int column, Object value){
        if (value==null) return false;        
        tableModel.setValueAt(value, row, column);
        return true;
    }
    
    private void masterEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masterEnableActionPerformed
        //*DEBUG*/callbacks.printOutput("masterEnable..() | "+dataModel.getMasterEnable());         
        if(masterEnable.isSelected()){            
            dataModel.setMasterEnable(true);
        }else{
            dataModel.setMasterEnable(false);
        }
        this.setStatusColor();   
         //*DEBUG*/callbacks.printOutput("end masterEnable..() | "+dataModel.getMasterEnable());
    }//GEN-LAST:event_masterEnableActionPerformed

    private void masterDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masterDebugActionPerformed
        dataModel.setMasterDebug(masterDebug.isSelected());        
        this.setStatusColor();
    }//GEN-LAST:event_masterDebugActionPerformed
    
    private void setStatusColor(){
        if(masterEnable.isSelected()){
            if(masterDebug.isSelected())
                statusColor.setBackground(Color.yellow);
            else
                statusColor.setBackground(Color.green);
        }else{
            statusColor.setBackground(Color.red);
        }    
        
    }
    
    private void exportConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportConfActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        //use the path from Last Config
        //in case of invalid path, it fails safe silently
        fileChooser.setSelectedFile(new File(this.lastPathConfig.getText()));
        int result = fileChooser.showSaveDialog(this);

        switch (result) {
        case JFileChooser.APPROVE_OPTION:
            File file = fileChooser.getSelectedFile();            
            try (
                FileWriter fileOut = new FileWriter(file);
            ){
                Gson gson = new Gson();
                Vector dataInTable = tableModel.getDataVector();
                String dataToStore = gson.toJson(dataInTable);
                dataToStore = NAME + VERSION + dataToStore;                
                fileOut.write(dataToStore);
                //set Last Config Path to the last loaded file
                this.lastPathConfig.setText(file.getAbsolutePath());
                //save Last Config Path in burp settings
                persistSettings.saveLastConfigPath(file.getAbsolutePath());
                
            } catch (Exception ex) {
                PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                ex.printStackTrace(stderr);
            }
            break;
        case JFileChooser.CANCEL_OPTION:
          break;
        case JFileChooser.ERROR_OPTION:
          callbacks.printError("Error export error");
          break;
        }
    }//GEN-LAST:event_exportConfActionPerformed

    private void importConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importConfActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        //use the path from Last Config
        //in case of invalid path, it fails safe silently
        fileChooser.setSelectedFile(new File(this.lastPathConfig.getText()));
        int result = fileChooser.showOpenDialog(this);
        switch (result) {
        case JFileChooser.APPROVE_OPTION:
            File file = fileChooser.getSelectedFile();
            
            //Magic Bytes
            final byte mbSerializedObj[] = {(byte)0xAC, (byte)0xED, (byte)0x00, (byte)0x05};
            byte mbFile[] = new byte[8];
            
            try (                
                RandomAccessFile fileIn = new RandomAccessFile(file,"r");
            ){  
                //Read magic bytes
                fileIn.read(mbFile, 0 , NAME.length());
                
                /*Attempt to restore data from version TokenJar 2.0*/
                if (Bytes.indexOf(mbFile, mbSerializedObj)==0){
                    fileIn.seek(0);                    
                    try (
                        InputStream is = Channels.newInputStream(fileIn.getChannel());
                        ObjectInputStream objectIn = new ObjectInputStream(is); 
                    ){
                        Vector dataInTable = (Vector) objectIn.readObject();
                        restoreTableData(dataInTable);
                        persistSettings.save(dataInTable);
                        //set Last Config Path to the last loaded file
                        this.lastPathConfig.setText(file.getAbsolutePath());
                        //save Last Config Path in burp settings
                        persistSettings.saveLastConfigPath(file.getAbsolutePath());
                    } catch (IOException | ClassNotFoundException ex) {
                        callbacks.printOutput("! Error loading settings when opening the file of type serialized object");
                        PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                        ex.printStackTrace(stderr);
                    }
                /*Attempt to restore data from newer version*/
                } else 
                    if (Bytes.indexOf(mbFile, NAME.getBytes())==0){                        
                        try 
                        {   
                            //TODO Magic Bytes to be used in future version to check the settings format version number
                            
                            //Skip Magic Bytes and Version
                            int fileStart = NAME.length()+VERSION.length();
                            int fileLen = (int) fileIn.length()-NAME.length()-VERSION.length();                            
                            
                            byte[] fileContent = new byte[fileLen];
                            
                            fileIn.seek(fileStart);
                            fileIn.read(fileContent, 0, fileLen); // wrong results with fileStart as offset 

                            InputStreamReader is = new InputStreamReader(new ByteArrayInputStream(fileContent));
                            Gson gson = new Gson();                            
                            Vector restoredDataInTable = (Vector) gson.fromJson(is, Vector.class);

                            //The respored data is a Vector of ArrayLists, the result should be a Vector of Vectors.
                            Vector dataInTable = new Vector();
                            for (int i=0; i<restoredDataInTable.size(); i++){
                                Vector row = new Vector( (ArrayList) restoredDataInTable.elementAt(i));
                                dataInTable.add(row);
                            }
                            restoreTableData(dataInTable);
                            persistSettings.save(dataInTable);
                            //set Last Config Path to the last loaded file
                            this.lastPathConfig.setText(file.getAbsolutePath());
                            //save Last Config Path in burp settings
                            persistSettings.saveLastConfigPath(file.getAbsolutePath());
                            
                        } catch (Exception ex) {
                            callbacks.printOutput("! Error loading settings when opening the file of type json");
                            callbacks.printOutput(ex.toString());               
                            PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                            ex.printStackTrace(stderr);                           
                        }
                    } else
                        callbacks.printOutput("! Error - unknown format for the file");
             
            } catch (IOException ex) {
                callbacks.printOutput("! Error when opening the file to restore");
                PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                ex.printStackTrace(stderr);
            }
            break;
        case JFileChooser.CANCEL_OPTION:
          break;
        case JFileChooser.ERROR_OPTION:
          callbacks.printError("Error import error");
          break;
        }
        //*DEBUG*/callbacks.printOutput("end.");
    }//GEN-LAST:event_importConfActionPerformed

    private void goToSite1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goToSite1MouseClicked
        showGettingStartedDialog();
    }//GEN-LAST:event_goToSite1MouseClicked

    private void masterProxyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masterProxyActionPerformed
        dataModel.setMasterProxy(masterProxy.isSelected());   
    }//GEN-LAST:event_masterProxyActionPerformed

    private void masterIntruderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masterIntruderActionPerformed
        dataModel.setMasterIntruder(masterIntruder.isSelected());
    }//GEN-LAST:event_masterIntruderActionPerformed

    private void masterRepeaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_masterRepeaterActionPerformed
        dataModel.setMasterRepeater(masterRepeater.isSelected());
    }//GEN-LAST:event_masterRepeaterActionPerformed

    private void restoreTableData(Vector dataInTable) {
        if (dataInTable==null) return;
         
         //Get the column names
        Vector<String> columnsInTable = new Vector<>(tableModel.getColumnCount());
        for (int i=0; i<tableModel.getColumnCount(); i++){
            columnsInTable.add(tableModel.getColumnName(i));
        }
        
        Vector invalidRows = new Vector(); // rows that don't have 11 elements 

        for (int i=0; i<dataInTable.size(); i++){
            //*DEBUG*/callbacks.printOutput("dataInTable["+i+"]="+dataInTable.elementAt(i));
            Vector row = (Vector) dataInTable.elementAt(i);           
            
            if (row.size() == 11){ 
                //Check if previous format (indicated by the "debug" as last field)
                String debug = String.valueOf(row.elementAt(10));
                if (debug.equals("true") || debug.equals("false")){
                    //transform TokenJar v1 format to the v2 format
                    for (int j=10; j>2; j--){  //move last 8 elements to the right
                        row.setElementAt(row.elementAt(j-1), j);
                    }
                    row.setElementAt(false, 2); // "header" is set to false
                    //*DEBUG*/callbacks.printOutput("restored dataInTable["+i+"]="+dataInTable.elementAt(i));
                }
            }
            else {  // Does not correspond to any format
                callbacks.printOutput("! Error when importing line"+row);
                callbacks.printOutput("!  skipping this line");
                invalidRows.add(row);
            }
        }

        
        //restore the DataVector
        if (invalidRows.size()>0){
            dataInTable.removeAll(invalidRows);
        }
        tableModel.setDataVector(dataInTable, columnsInTable);
        
        dataModel.init();
        this.resizeColumns();
    }
    
    private void resizeColumns() {
        tokenTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
       
        int tableWidth = this.getWidth() - 120;
        float[] columnWidthPercentage = {0.03f, 0.09f, 0.03f, 0.03f, 0.03f, 0.03f, 0.03f, 0.1825f, 0.1825f, 0.1825f, 0.1825f};
        
        for (int i=0; i<tokenTable.getColumnModel().getColumnCount(); i++){
            tokenTable.getColumnModel().getColumn(i).setPreferredWidth(Math.round(tableWidth * columnWidthPercentage[i]));
        }
            
        //*DEBUG*/callbacks.printOutput("tokenTable.getWidth()  "+tokenTable.getWidth());
    }
    
    
    /*Display 'Getting Started' modal*/
    private void showGettingStartedDialog(){
        GettingStartedDialog gettingStartedDialog = new GettingStartedDialog(this, true);
        gettingStartedDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor((Component) this));        
        gettingStartedDialog.setVisible(true);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToken;
    private javax.swing.JButton exportConf;
    private javax.swing.JLabel goToSite1;
    private javax.swing.JButton importConf;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelNewHere;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField lastPathConfig;
    private javax.swing.JCheckBox masterDebug;
    private javax.swing.JCheckBox masterEnable;
    private javax.swing.JCheckBox masterIntruder;
    private javax.swing.JCheckBox masterProxy;
    private javax.swing.JCheckBox masterRepeater;
    private javax.swing.JButton openRegexWindow;
    private javax.swing.JButton removeToken;
    private javax.swing.JTextField statusColor;
    private javax.swing.JTable tokenTable;
    // End of variables declaration//GEN-END:variables

    public void eraseNewHereLabel(){
        timerNewHere.stop();
        this.jLabelNewHere.setText(""); 
        callbacks.saveExtensionSetting("NewHere:hide", "true");
    }
    
}
class BlinkLabel implements ActionListener {  
        private javax.swing.JLabel label;
        private int count;

        public BlinkLabel(javax.swing.JLabel label){
            this.label = label;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(count % 2 == 0){
                label.setOpaque(false);
                label.setForeground(java.awt.Color.RED);
            }
            else{
                label.setOpaque(true);
                label.setForeground(java.awt.Color.MAGENTA);
                label.setBackground(java.awt.Color.YELLOW);
            }
            count++;
        }  
}
