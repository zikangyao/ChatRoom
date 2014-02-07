import javax.swing.*;
import java.awt.*;

/**
 * @description Implement chatting room 
 * @version 1.0 2013-12-3
 * @author  zikang yao
 */
public class ServerView {
	// default user name, port number 
	private static String SERVERNAME = "NYU";
	private static int PORTNUM = 9999;
	
	public static void main(String[] args) throws InterruptedException{
		//arguments process
		argsProcess(args);

		GuiThread guiThread = new GuiThread();
		guiThread.setUserName(SERVERNAME);
		guiThread.setServerFrame(true);
		// put guiThread in EventQueue, start GUI frame
		EventQueue.invokeLater(guiThread);
		
		ChatFrame serverView = null;
		while(true){
			// can not get GUI reference, until it is assigned in the EvnetQueue
			if((serverView = guiThread.getFrame()) != null)
				break;
		}

		// start server
		Server server = new Server(serverView,PORTNUM,SERVERNAME);

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
				SERVERNAME = args[0];
			}
		}else if (args.length == 2){
			if (isInteger(args[0])){
				PORTNUM = Integer.parseInt(args[0]);
				SERVERNAME = args[1];
			}else{
				System.out.println("Wrong format: Please input <Port Number>, <User Name>");
				System.exit(1);
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