import javax.swing.*;
import java.awt.*;

/**
 * @description Implement chatting room
 * @version 1.0 2013-12-3
 * @author  zikang yao
 */
public class ClientView {
	// default user name, port number and host name
	private static String HOSTNAME = "localhost";
	private static String USERNAME = "Client";
	private static int PORTNUM = 9999;

	public static void main(String[] args) throws ClassNotFoundException {
		
		
		// argument process
		argsProcess(args);

		GuiThread guiThread = new GuiThread();
		guiThread.setUserName(USERNAME);
		guiThread.setServerFrame(false);
		// put guiThread in EventQueue
		EventQueue.invokeLater(guiThread);
		
		ChatFrame clientView = null;
		while(true){
			// can not get GUI reference, until it is assigned in the EvnetQueue
			if((clientView = guiThread.getFrame()) != null)
				break;
		}

		// start client
		Client client = new Client(clientView,PORTNUM,HOSTNAME,USERNAME);
		
	}

	/**
	* process command line arguments
	* @param args  arguments from command line
	*/
	public static void argsProcess(String[] args){
		if (args.length == 0){
			return;
		}else if (args.length == 1){
			if (isInteger(args[0])){
				PORTNUM = Integer.parseInt(args[0]);
			}else{
				USERNAME = args[0];
			}
		}else if (args.length == 2){
			if (isInteger(args[0])){
				PORTNUM = Integer.parseInt(args[0]);
				USERNAME = args[1];
			}else{
				USERNAME = args[1];
				PORTNUM = Integer.parseInt(args[0]);
			}
		}else if (args.length == 3){
			if (isInteger(args[0])){
				PORTNUM = Integer.parseInt(args[0]);
				USERNAME = args[1];
				HOSTNAME = args[2];
			}else{
				System.out.println("Wrong format: Please input <Port Number>, <User Name>, <Host Name>");
				System.exit(-1);
			}
		}
	}

	/**
	* judge whether an argument is integer
	* @return false if <code>arg</code> is not integer
	* @return true  if <code>arg</code> is integer
	*/
	public static boolean isInteger(String arg){
		try { 
        	Integer.parseInt(arg); 
    	} catch(NumberFormatException e) { 
        	return false; 
    	}
    
    	return true;
	}
}