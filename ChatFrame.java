import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

class ChatFrame extends JFrame{

	private String userName;
	private JTextField inputBox;
	private JScrollPane scrollPanel;
	private JPanel southPanel;
	private JPanel northPanel;
	private JButton sendButton;
	private JLabel totalNumLabel;
	private JTextArea chatBox;
	private ObjectOutputStream outputStream;
	private Object lock1 = new Object();
	private Object lock2 = new Object();

	/**
	* Constructor of <code>ChatFrame</code>
	* initiate components of frame
	* @param userName user name of current chat window
	*/
	public ChatFrame(String userName, boolean isServerFrame){

		// initiate fields
		initiateButtons();
		this.userName = userName;
		this.chatBox=new JTextArea();
		this.chatBox.setEditable(false);
		this.scrollPanel=new JScrollPane(chatBox);
		this.inputBox=new JTextField(10);
		this.totalNumLabel = new JLabel("0");
		this.southPanel=new JPanel();
		this.northPanel=new JPanel();

		// adding components
		this.southPanel.add(inputBox);
		this.southPanel.add(sendButton);
		this.northPanel.add(new JLabel("Online People: "));
		this.northPanel.add(totalNumLabel);
		this.add(scrollPanel);
		this.add(northPanel,"North");

		// different frame components for server and clinet
		if (isServerFrame)
			this.setTitle("Server");
		else
			this.add(southPanel,"South");
			

		// JFrame setting
		this.setSize(400, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}


	/**
	* initiate Send Button 
	*/
	private void initiateButtons(){

		sendButton=new JButton("Send");
		sendButton.setEnabled(false);
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//get text from inputBox
				String text = inputBox.getText();
				Message message = new Message(MessageType.TEXT, text, userName);
				chatBox.append("You"+": \n"+message.getContent()+"\n\n");
				inputBox.setText("");
				try{
					// send message to server 
					outputStream.writeObject(message);
				}catch (IOException e){
					e.printStackTrace();
					System.exit(-1);
				}
				
			}
		});
	
	}

	/**
	* when client is already connect to server, 
	* it will set an outputStream in View that used to trasfer message
	* @param outputStream  use to transfer message with server
	*/
	public void setOutputStream(ObjectOutputStream outputStream){
		this.outputStream = outputStream;
	}

	/**
	* set <code>true</code> only after client is connected with server
	* @param status  flag to determine whether button is inactive or not
	*/
	public void setSendButtonStatus(boolean status){
		this.sendButton.setEnabled(status);
	}

	/**
	* use a lock on this method, since on server side multiple threads would use this method
	* @param text  text to display on <code>JTextArea</code>
	*/
	public void setChatBox(String text){
		synchronized(lock1){
			this.chatBox.append(text);
		}
	}

	/**
	* use a lock on this method, since on server side multiple threads would use this method
	* @param num  num of online people 
	*/
	public void setTotalNumLabel(int num){
		synchronized(lock2){
			this.totalNumLabel.setText(num+"");
		}	
	}

}


/**
* Thread class that use to run chatFrame
*/
class GuiThread implements Runnable {

	private volatile ChatFrame frame; // should use keywork volatile, since two threads try to visit this field
	private String userName;
	private boolean isServerFrame;

	/**
	* instance a chatFrame 
	*/
	@Override
	public void run(){
		this.frame = new ChatFrame(this.userName,this.isServerFrame);
	}

	/**
	* @return frame  GUI reference
	*/
	public ChatFrame getFrame(){
		return this.frame;
	}

	/**
	* @param userName  user name of Server
	*/ 
	public void setUserName(String userName){
		this.userName = userName;
	}

	/**
	* @param isServerFrame  flag that determine whether is Server Frame or not
	*/
	public void setServerFrame(boolean isServerFrame){
		this.isServerFrame = isServerFrame;
	}
}



