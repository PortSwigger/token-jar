/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenJar;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import javax.swing.JDialog;

/**
 *
 * @author Dan
 */
public class GettingStartedDialog extends javax.swing.JDialog {

     private final Tab parent;
     
    /**
     * Creates new form GettingStartedDialog
     */
    public GettingStartedDialog(Tab parent, boolean modal) {
        //super(null, modal);
        this.setModal(true);
        
        this.parent = parent;
        initComponents();
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Getting Started");
        
        //stop 'New here message'
        parent.eraseNewHereLabel();
        
        initCloseButton();
    }
    
    private void initCloseButton(){
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                 dispose();
            }
        });        
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        closeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setText("<html>\n<h3>Useful for ...</h3>\nextracting tokens from responses and embedding them in requests. Further proccessing is posible through javascript code.\n<br/><br/>\n\n<h3>Configure: Start from right to left by ...</h3>\n\n<b>I. Extracting the token value</b><br/>\n<b>Path</b>-  a URL that limits the location from where the token is extracted (reponse). <br/>\n<small><b>Tip:</b> Can be <b>*</b> for most cases. </small><br/>\n<b>Regex</b>- expression that identifies the desired value. Should have at least one matching group.<br/>\n<small><b>Tip:</b> Use a common value such as \"200 OK\" if you just want a token that, for example, is unique per each request</small><br/>\n<b>Eval</b>-  javascript code for transforming the value. grp[1], grp[2], ... return the matched group 1, 2, ...<br/>\n<small><b>Tip:</b> Here you can use string operations (ex. concatenation), common js functions (ex. Math.random()) or your own code </small><br/>\n<br/>\n\n<b>II. Storing the token</b><br/>\n<b>Value</b> - an optional initial value for the first run<br/>\n<br/>\n\n<b>III. Apply the token</b><br/>\n<b>Name</b>- token name as it appears in request<br/>\n<b>header, url, body, ...</b>- token position in request. Multiple choises are possible.<br/>\n<i>header</i> - a custom header contains the token. Token name is without colon \":\" <br/>\n<i>url</i> - the URL query contains the token as  parameter <br/>\n<i>body</i> - the token is an usual POST parameter <br/>\n<i>cookie</i> - one of the cookies contains the token <br/>\n<i>other</i> - for tokens inside json, xml, xml attribute, multi-part attribute <br/>\n\n<h3>Debug: When further help is required ...</h3>\nenable the debugging. Run the request once again through TokenJar and consult the Output window (Extender >Extensions >select TokenJar >Output).\nInformation about the request / response is listed: <br/>\n<i><b>Request:</b></i><br/>\n<i>parameters</i> - name and type. Case sensitivity matters! <br/>\n<i>headers</i> - name and current value <br/>\n<i>tokens</i> - that have been processed <br/>\n<i>request</i> - full final request (after processing was done) <br/>\n<i><b>Response:</b></i><br/>\n<i>path</i> - the processed URL<br/>\n<i>regex</i> - the expression that matched<br/>\n<i>groups</i> - the values captured in each group <br/>\n\n<h3>Tip: For making your job easy ...</h3>\n- use the Regex button to test your reqular expression. <br/>\n- enable all token locations (header, url, body, ..) if not sure what to use and when this does not impact performance. <br/>\nIt matters only when multiple parameters  have the same name.<br/>\n\n<h3>Tip: Persist your configurations</h3>\n- use the right button below to export your configuration as JSON file <br/>\n- use the left button below to import an already saved configuration (old binary or JSON format) <br/>\n- optinaly you can can provide a path to quickly load a configuration <br/>\n</html>\n");
        jTextPane1.setCaretPosition(0);
        jScrollPane1.setViewportView(jTextPane1);

        closeButton.setText("Close");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 204));
        jLabel1.setText("Visit plugin page ");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(19, 653, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(closeButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        URI uri = URI.create("https://dannegrea.github.io/TokenJar");
        if (uri != null && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jLabel1MouseClicked



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
