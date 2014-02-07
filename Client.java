import java.net.*;
import java.io.*;

/**
* server as a Server
*/
class Client {

	/**
	* constructor of <code>Client</code>
	*/ 
	public Client(ChatFrame clientView, int portNum, String hostName, String userName) throws ClassNotFoundException{

		ObjectOutputStream outputStream = null;
		ObjectInputStream inputStream = null;
		Message message = null;
		Socket socket = null;
		try{

			socket = new Socket(hostName,portNum);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			

			// receive verification message 
			message=(Message)inputStream.readObject();
			int numOfOnlinePeople = message.getNumOfOnlinePeople();
			clientView.setChatBox("Connected to: "+message.getContent()+"\n\n"+"You are now joining in Chat Room \""+message.getSender()+"\"\n\n");
			clientView.setTotalNumLabel(numOfOnlinePeople);

			// send notification message to notify other clients
			message = new Message(MessageType.NOTIFICATION, "\""+userName+"\""+" joins in this chat room",userName);
			message.setNumOfOnlinePeople(numOfOnlinePeople);
			outputStream.writeObject(message);

			// once client connected with server, then it can set outputStream 
			// and activate send button 
			clientView.setOutputStream(outputStream);
			clientView.setSendButtonStatus(true);

			// receive message
			while(true) {
				message = (Message)inputStream.readObject();
				// receiving notification
				if(message.getType() == MessageType.NOTIFICATION){
					clientView.setChatBox(message.getContent()+"\n\n");
					clientView.setTotalNumLabel(message.getNumOfOnlinePeople());

				// receiving regular text
				}else if (message.getType() == MessageType.TEXT){
					clientView.setChatBox(message.getSender()+": \n"+message.getContent()+"\n\n");
				}
			}
			

		}catch (UnknownHostException e){
			clientView.setChatBox("Unknown chat room.\nPlease try another one!");

		}catch (EOFException e){
			clientView.setChatBox("You are disconnected from chat room.\nPlease restart and try again!");

		}catch (ConnectException e){
			clientView.setChatBox("Can not connect to chat room.\nPlease restart and try again!");

		}catch (IOException e){
			e.printStackTrace();

		}finally {

			try{
				if (socket != null){
					socket.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
			
		}
	}

}