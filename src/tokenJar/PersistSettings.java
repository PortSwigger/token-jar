/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenJar;

import burp.IBurpExtenderCallbacks;
import static burp.BurpExtender.*;
import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author DanNegrea
 */
public class PersistSettings {
    public static final String DEFAULT_LINE = "[[false,\"csrf\",false,false,true,false,false,\"\",\"grp[1]\",\"csrf\\u003d([a-zA-Z0-9]*)\",\"*\"]]";
    private EvictingQueue<String> evalQueue;
    private EvictingQueue<String> regexQueue;
    private int queueMaxSize;
    
    private final IBurpExtenderCallbacks callbacks;
    
    public PersistSettings(IBurpExtenderCallbacks callbacks){
            this(callbacks, 50);
            queueMaxSize = 50;
    }
    
    public PersistSettings(IBurpExtenderCallbacks callbacks, int expMaxSize){
        this.queueMaxSize = expMaxSize;
        this.evalQueue = EvictingQueue.create(queueMaxSize);        
        this.regexQueue = EvictingQueue.create(queueMaxSize);        
        this.callbacks = callbacks;
    }
       
    public void save(Vector dataInTable){
        try{            
            Gson gson = new Gson();
            String dataToStore = gson.toJson(dataInTable);
            //Save version fist (5 chars)
            dataToStore = VERSION + dataToStore;
            callbacks.saveExtensionSetting(NAME+".dataInTable", dataToStore);
            //Signal that old format up to TokenJar 2.0 is no longer in use
            callbacks.saveExtensionSetting("dataInTable", "");            
        } catch (Exception ex) {
            PrintWriter stderr = new PrintWriter(callbacks.getStderr());
            ex.printStackTrace(stderr);
        }
        
        save(evalQueue, "evalQueue");
        save(regexQueue, "regexQueue");
    }
    private void save(EvictingQueue<String> queue, String queueName){
        try{            
            Gson gson = new Gson();
            String dataToStore = gson.toJson(queue);            
            callbacks.saveExtensionSetting(NAME+"."+queueName, dataToStore);          
        } catch (Exception ex) {
            PrintWriter stderr = new PrintWriter(callbacks.getStderr());
            ex.printStackTrace(stderr);
        }
    }
    public void saveLastConfigPath(String path){
        callbacks.saveExtensionSetting(NAME+".lastConfigPath", path);          
    }
 
    public Vector restore(){              
        Vector restoredDataInTable = null;
        Vector dataInTable = new Vector();
        
        /*Attempt to restore data from version TokenJar 2.0*/
        String tableData= callbacks.loadExtensionSetting("dataInTable");
        //if old setting still in settings store
        if (!Strings.isNullOrEmpty(tableData)){
            //*DEBUG*/callbacks.printOutput("!Strings.isNullOrEmpty(tableData)");
            try (
                ByteArrayInputStream byteArrIn = new ByteArrayInputStream(tableData.getBytes());
                ObjectInputStream objectIn = new ObjectInputStream(byteArrIn);
            ){  
                //get data in table from the serialized object
                dataInTable = (Vector) objectIn.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                ex.printStackTrace(stderr);
            }            
        }
        
        /*Attempt to restore data from newer version*/
        if (dataInTable.size()==0) {
            //*DEBUG*/callbacks.printOutput("dataInTable.size()==0");
            try 
            {
                String strObj = callbacks.loadExtensionSetting(NAME+".dataInTable");                
                
                if (!Strings.isNullOrEmpty(strObj) && strObj.length()>5){
                    //TODO To be used in future version to check the settings format version number
                    strObj = strObj.substring(5); //Skip version information (5 chars)
                } else {
                    /*Demo line if empty*/                
                    strObj = DEFAULT_LINE;
                }
                
                Gson gson = new Gson();
                restoredDataInTable = (Vector) gson.fromJson(strObj, Vector.class);

                //The respored data is a Vector of ArrayLists, the result should be a Vector of Vectors.
                for (int i=0; i<restoredDataInTable.size(); i++){
                    Vector row = new Vector( (ArrayList) restoredDataInTable.elementAt(i));
                    dataInTable.add(row);
                }
            } catch (Exception ex) {
                PrintWriter stderr = new PrintWriter(callbacks.getStderr());
                ex.printStackTrace(stderr);
               
                callbacks.printError("Failed to load settings. Restoring default line");
                String dataToStore = VERSION + DEFAULT_LINE;
                callbacks.saveExtensionSetting(NAME+".dataInTable", dataToStore);
            }
        }        
        //second objective, attempt to restore the evalQueue and regexQueue
        evalQueue = restore(evalQueue, "evalQueue");
        regexQueue = restore(regexQueue, "regexQueue");
        
        return dataInTable;
    }
    
    private EvictingQueue<String> restore(EvictingQueue<String> queue, String queueName){   
        EvictingQueue<String> newQueue= EvictingQueue.create(queueMaxSize);
        try{
            String storedStr= callbacks.loadExtensionSetting(NAME+"."+queueName);            
            if (Strings.isNullOrEmpty(storedStr))  return queue;

            Gson gson = new Gson();
            String[] storedQueue = gson.fromJson(storedStr, String[].class);
            
            for (int i=0; i<storedQueue.length; i++){
                if( !Strings.isNullOrEmpty(storedQueue[i]) ){
                    //if expression is contained, remove it and add it fresh
                    if (newQueue.contains(storedQueue[i])) 
                        newQueue.remove(storedQueue[i]);     
                    newQueue.add(storedQueue[i]);            
                }
                //*DEBUG*/callbacks.printOutput("newQueue["+i+"]= "+ storedQueue[i]);
            }
        } catch (Exception ex) {
            PrintWriter stderr = new PrintWriter(callbacks.getStderr());
            ex.printStackTrace(stderr);
        }
        if (newQueue.isEmpty()) return queue; 
        else return newQueue;
    } 
    
    public String restoreLastConfigPath(){
        return callbacks.loadExtensionSetting(NAME+".lastConfigPath");
    }
    
    public void pushEval(String expression){
        if( !Strings.isNullOrEmpty(expression) ){
            if (evalQueue.contains(expression)){ 
                evalQueue.remove(expression);
            }
            evalQueue.add(expression);            
        }
    }
    public Object[] getEval(){        
        if (evalQueue.size()>0)
           return evalQueue.toArray();
        else
            return new Object[0];
    }
    public void pushRegex(String expression){
        if( !Strings.isNullOrEmpty(expression) ){
            //if expression is contained, remove it and add it fresh
            if (regexQueue.contains(expression)) 
               regexQueue.remove(expression);     
            regexQueue.add(expression);            
        }
    }
    public Object[] getRegex(){
        if (regexQueue.size()>0)
           return regexQueue.toArray();
        else
            return new Object[0];
    }  
}