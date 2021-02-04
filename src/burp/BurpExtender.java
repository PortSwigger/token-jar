package burp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import tokenJar.*;

public class BurpExtender implements IBurpExtender, IHttpListener, IProxyListener, IExtensionStateListener
{
    public static final String NAME="TokenJar";
    public static final String VERSION=" 2.1 "; //always 5 chars
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private Tab tab;
    private DataModel dataModel;
    
    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks){
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        
        // Set extension name
        callbacks.setExtensionName(NAME+" "+VERSION);
        
        tab = new Tab(callbacks);
        dataModel = tab.getDataModel();
              
        callbacks.customizeUiComponent(tab);
        callbacks.addSuiteTab(tab);
        
        // Register as listener
        callbacks.registerHttpListener(this);
        callbacks.registerProxyListener(this);
        callbacks.registerExtensionStateListener(this);  
    }
    
    /*
    // IExtensionStateListener implementation 
    */    
    @Override
    public void extensionUnloaded(){
        ;//dataModel is no longer accessible at this point
    }
    
    /* 
    // IProxyListener implementation
    */
    @Override
    public void processProxyMessage( boolean isRequest, IInterceptedProxyMessage message){
        //EXIT if Master Enable button is disabled
        if (dataModel.getMasterEnable()==false)
            return;
        if (dataModel.getMasterProxy()==false)
            return;
        
    	IHttpRequestResponse OLD_message = message.getMessageInfo();
    	if (isRequest) {
    		processRequestMessage(OLD_message);
    	} else {
    		processResponseMessage(OLD_message);    		
    	}
    	message.setInterceptAction(IInterceptedProxyMessage.ACTION_FOLLOW_RULES);
    }
    
    /*
    // IHttpListener implementation
    */
    @Override
    public void processHttpMessage(int toolFlag, boolean isRequest, IHttpRequestResponse message){
        //EXIT, it was already proccessed by PROXY
        if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY)
           return;
        
        //EXIT if Master Enable button is disabled
        if (dataModel.getMasterEnable()==false)
            return;
        if (dataModel.getMasterIntruder()==false && toolFlag == IBurpExtenderCallbacks.TOOL_INTRUDER)
            return;
        if (dataModel.getMasterRepeater()==false && toolFlag == IBurpExtenderCallbacks.TOOL_REPEATER)
            return;
       
    	if (isRequest){
    		processRequestMessage(message);
    	} else {
    		processResponseMessage(message);    		
    	}
    }
   
    /* 
    // Response message
    */
    private void processResponseMessage(IHttpRequestResponse HTTP_message) 
    {        
        try{
        
        //Obtaining path
    	IRequestInfo requestInfo = helpers.analyzeRequest(HTTP_message);
    	String path = requestInfo.getUrl().getPath();
        
        //Debug enabled
        if (dataModel.getMasterDebug()){
            callbacks.printOutput("");
            callbacks.printOutput("<<< Processing Response Message");
            callbacks.printOutput(". Path=" + path);
            if (HTTP_message.getComment()==null)
                HTTP_message.setComment(NAME+":");
        }
        
        String HTTP_response = null;
        //get all ids for token that 'listen' for this response
        Set<Integer> ids = dataModel.getByPath( path );
                
        for(Integer id: ids){                     
            //Get only the first time the response
            if (HTTP_response==null) HTTP_response = new String(HTTP_message.getResponse());
            String value = null;
            
            Matcher matcher = dataModel.getPattern(id).matcher( HTTP_response );
            int grpCount = matcher.groupCount()+1;   
            
            if (matcher.find()) { //do I need to test for && grpCount >=0 ?                
                   /*Debug enabled*/
                    if (dataModel.getMasterDebug()) {
                        HTTP_message.setComment( HTTP_message.getComment() + " match:"+dataModel.getName(id));                        
                        callbacks.printOutput(". Match for "+dataModel.getName(id)+" (rule "+id+") for Regex=" + dataModel.getRegex(id));                   
                    }
                    String[] grpValues = new String[grpCount];
                    
                    for (int i=0; i<grpCount; i++){
                        grpValues[i]= matcher.group(i);                        
                        /*Debug enabled*/
                        if (dataModel.getMasterDebug()){ 
                            callbacks.printOutput("+ grp["+i+"]="+ grpValues[i]);
                        }
                    }                    
                    dataModel.setValue(id, grpValues);
            } 
            else 
                /*Debug enabled*/
                if (dataModel.getMasterDebug()){ 
                    callbacks.printOutput("< No match for "+dataModel.getName(id)+"("+id+"), regex=" + dataModel.getRegex(id));
                }
        }
        }catch (Exception ex){
            callbacks.printOutput("! Exception while proccising the response");
            PrintStream burpErr = new PrintStream(callbacks.getStderr());
            ex.printStackTrace(burpErr);
        }
    }
    /* 
    // Request message
    */
    private void processRequestMessage(IHttpRequestResponse HTTP_message){        
    	IRequestInfo requestInfo = helpers.analyzeRequest(HTTP_message);
    	byte[] oRequest = HTTP_message.getRequest();
        List<IParameter> oParameters = requestInfo.getParameters();
        List<enhancedParameter> nParameters = new ArrayList<>();
        
        Integer id;
        int deltaRequest=0;       
        int deltaContentLen=0; 
        int delta; //work variable
        
        //Debug enabled
        if (dataModel.getMasterDebug()){
                callbacks.printOutput("");
                callbacks.printOutput(">>> Processing Request Message");
                callbacks.printOutput(". Path=" + requestInfo.getUrl().getPath());
                if (HTTP_message.getComment()==null)
                    HTTP_message.setComment(NAME+":");
        }
        
        //1. Identify all params that are also in the table
        //2. Calculate Content-Length delta length 
        for (IParameter parameter : oParameters) {
            // the parameter type must be between 0 and 4 (0-> header, 1->url, 2->body, 3->cookie, 4->other)
            byte parameterType = parameter.getType(); 
            parameterType++; // increment with one to make room for header (0)
            
            if (dataModel.getMasterDebug()) {callbacks.printOutput(". Parameter["+parameter.getName()+"]="+parameter.getValue()+" of Type="+enhancedParameter.Type.get(parameterType));} /*Debug enabled*/
     
            if (parameterType>4)
                parameterType = 4;
                                    
            if ( (id = dataModel.getByNameType(parameter.getName(), parameterType))!=null ) {
                String newValue = dataModel.getValue(id);
                            
                nParameters.add(new enhancedParameter(parameter, newValue));
                delta = newValue.length() - (parameter.getValueEnd() - parameter.getValueStart());
                deltaRequest += delta;
                                              
                //Update Content-Length only for body parameters
                if ( parameterType == 2 || parameterType == 4 ){ // 2->body, 4->other
                    deltaContentLen += delta;
                }
                //*DEBUG*/callbacks.printOutput("deltaContentLen="+deltaContentLen);
            }
        }      
        
        //Content-Length preparation
        List<String> HTTP_headers = requestInfo.getHeaders();        
        int oContLenStart=0, oContLenLength=0, oContLenValue=0;
        boolean oContLenProccess = false;
        
        /*Processing headers*/
        int oCursor=0; //Assumption: headers are proccessed in top down order
        for(int i=0; i<HTTP_headers.size(); i++){
            
            if (dataModel.getMasterDebug()) {callbacks.printOutput(". Header["+i+"]="+HTTP_headers.get(i));} /*Debug enabled*/
            
            String HTTP_headerName = HTTP_headers.get(i).split(":")[0];
            
            /*Identify if any parameter is a header parameter*/
            if ( (id = dataModel.getByNameType(HTTP_headerName, (byte) 0))!=null ) {
                String HTTP_header = HTTP_headers.get(i);
                
                int headerStart = indexOf(oRequest, oCursor, HTTP_header.getBytes());
                int valueStart = headerStart + HTTP_headerName.length() + 2; //2 accounts for ": " 
                int valueEnd = headerStart + HTTP_header.length();
                String newValue = dataModel.getValue(id);
                //create a nwe enhanced param
                nParameters.add(new enhancedParameter(HTTP_headerName, (byte) 0, valueStart, valueEnd, newValue));
                //*DEBUG*/callbacks.printOutput("nParameters.add(new enhancedParameter("+HTTP_headerName+", (byte) 0 ,"+ valueStart+", "+valueEnd+", "+newValue+"))");                
                                              
                delta = newValue.length() - (valueEnd - valueStart);
                deltaRequest += delta;
                oCursor = valueEnd; //next time search from here
            }
            
            if ( HTTP_headerName.equals("Content-Length")) {
                String Content_Length = HTTP_headers.get(i);                
                try {                    
                    oContLenStart = indexOf(oRequest, oCursor, Content_Length.getBytes());
                    oContLenLength = Content_Length.length();
                    oContLenValue = Integer.parseInt(Content_Length.substring("Content-Length:".length()).trim());                            
                     
                    oContLenProccess = true;
                    oCursor = oContLenStart+oContLenLength;
                }
                catch (NumberFormatException e){
                    // do nothing, let the for search for another "Content-Length" header
                    // if none was found the oContLenProccess shold be false
                    if (dataModel.getMasterDebug()){  /*Debug enabled*/
                        callbacks.printOutput("! NumberFormatException when converting "+Content_Length );
                        callbacks.printOutput("! skipped updating 'Content-Length' header ");
                    }
                }
            }
        }
        //end Content-Length preparation
        
        //EXIT if no parameter was found
        if (nParameters.isEmpty()){
            if (dataModel.getMasterDebug()){callbacks.printOutput("= No parameters to update");}; //Debug enabled
            return;
        }
        //need to sort because headers where introduced at the end
        nParameters.sort((p1, p2) -> p1.valueStart - p2.valueStart);
        
        byte[] nRequest = new byte[oRequest.length + deltaRequest];
        
        int oStart = 0;
        int oEnd = oRequest.length;
        int nStart = 0;
        int oParamStart, oParamEnd;
             
        //1. Update all parameters identified above
        //2. Update the Content-Length
        for (enhancedParameter parameter : nParameters) {             
            oParamStart  = parameter.valueStart;
            oParamEnd = parameter.valueEnd;
           
            //Content-Length update
            if (oContLenProccess && oParamStart > oContLenStart){ //found the parameter just after Content-Length?
                //copy everything before the Content-Length
                delta = oContLenStart-oStart; 
                System.arraycopy(oRequest, oStart, nRequest, nStart, delta);
                oStart+= delta;
                nStart+= delta;
                
                //Compute and append the new Content-Length
                String nContLen = "Content-Length: "+ ((int) oContLenValue + (int) deltaContentLen);
                
                if (dataModel.getMasterDebug()) {callbacks.printOutput("+ Content-Length="+ ((int) oContLenValue + (int) deltaContentLen));} /*Debug enabled*/
                
                int nContLenLength = nContLen.length();
                System.arraycopy(nContLen.getBytes(), 0, nRequest, nStart, nContLenLength);
                //*DEBUG*/callbacks.printOutput("###nStart="+nStart+", nContLenLength="+nContLenLength);       
                oStart+= oContLenLength;
                nStart+= nContLenLength;
                //Content-Lenght updated
                oContLenProccess = false;
            }
            //end Content-Length update

            //copy everithing before the parameter
            delta = oParamStart-oStart;
            //*DEBUG*/callbacks.printOutput("###oStart="+oStart+", nStart="+nStart+", delta="+delta); 
            System.arraycopy(oRequest, oStart, nRequest, nStart, delta);
            oStart+= delta;
            nStart+= delta;
            
            //DEBUG enabled
            if (dataModel.getMasterDebug()){                        
                callbacks.printOutput("+ Parameter["+parameter.name+"]="+parameter.newvalue+" of Type="+ enhancedParameter.Type.get(parameter.type));
                HTTP_message.setComment( HTTP_message.getComment() + " new:" + parameter.name + " ");                
            }
            //end DEBUG

            int nParamLenght = parameter.newvalue.length();
            
            //Copying new value (might be empty, smaller or larger)
            System.arraycopy(parameter.newvalue.getBytes(), 0, nRequest, nStart, nParamLenght);
            //A better alternative?
            //Bytes.concat(byte[]... arrays);

            oStart+= oParamEnd-oParamStart;
            nStart+= nParamLenght;
        }
        
        //Copying till the end
        if (oStart>0 && oStart<oEnd){
            try{
                System.arraycopy(oRequest, oStart, nRequest, nStart, oEnd-oStart); 

            }catch (Exception ex){
                callbacks.printOutput("! arraycopy exception on path=" + requestInfo.getUrl().getPath() + ", oStart=" + oStart + ", nStart=" + nStart );                
                PrintStream burpErr = new PrintStream(callbacks.getStderr());
                ex.printStackTrace(burpErr);
            }            
        }
        //Send the new request 
        if (dataModel.getMasterDebug()){ 
            callbacks.printOutput(".......Final Request......."); 
            callbacks.printOutput(callbacks.getHelpers().bytesToString(nRequest)); 
            callbacks.printOutput("..........................."); 
        }            
        HTTP_message.setRequest(nRequest);
    }
    private int indexOf(byte[] source, int startSource, byte[] target) {
        for(int i = startSource; i < source.length - target.length+1; i++) {
            boolean found = true;
            for(int j = 0; j < target.length; j++) {
               if (source[i+j] != target[j]) {
                   found = false;
                   break;
               }
            }
            if (found) return i;
         }
       return -1;  
   } 
}

class enhancedParameter{
    public static List<String> Type;
    static {        
        Type = new ArrayList(8);
        Type.add(0,                           "header");
        //increment with one to make room for header (0)
        Type.add(IParameter.PARAM_URL+1,      "url");
        Type.add(IParameter.PARAM_BODY+1,     "body");      
        Type.add(IParameter.PARAM_COOKIE+1,   "cookie");       
        Type.add(IParameter.PARAM_XML+1,      "xml"); /*other - all below types are reporesented by this type*/
        Type.add(IParameter.PARAM_XML_ATTR+1, "xml attr");
        Type.add(IParameter.PARAM_MULTIPART_ATTR+1, "multipart attr");
        Type.add(IParameter.PARAM_JSON+1,     "json");
    }

    /*Construct new enhancedParam from IParam (used for Burp listed params)*/
    public enhancedParameter(IParameter IParam, String newValue){
        this.name = IParam.getName();
        this.type = IParam.getType();
        this.type++; //increment with one to make room for header (0)
        this.valueStart = IParam.getValueStart();
        this.valueEnd = IParam.getValueEnd();
        this.newvalue = newValue;
    }
    /*Construct new enhancedParam from it's components (used for Header)*/
    public enhancedParameter(String name, byte type, int valueStart, int valueEnd, String newValue){
        this.name = name;
        this.type = type;
        this.valueStart = valueStart;
        this.valueEnd = valueEnd;
        this.newvalue = newValue;
    }
    public String name;
    public byte type;
    public int valueStart;
    public int valueEnd;   
    public String newvalue;
}
