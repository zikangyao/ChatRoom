import java.net.*;
import java.io.*;
import java.util.*;

/**
* server as a Server
*/
class Server {

	private ManageCliThread mThread;
	private ManageMessage mMessage;
	private ChatFrame serverView;

	/**
	* constructor of <code>Server</code>
	*/ 
	public Server(ChatFrame serverView, int portNum, String userName){

		this.mThread = new ManageCliThread();
		this.mMessage = new ManageMessage();
		this.serverView = serverView;
		ServerSocket serverSocket = null;

		try{

			this.serverView.setChatBox("waiting connection on Port: "+portNum+"...\n\n");
			serverSocket = new ServerSocket(portNum);
			
			// one single thread 
			// Dequeue message and transfer to client
			new Thread() {
         		public void run() {
         			while(true){
         				try{
	         				Message message = mMessage.dequeue();
	            			mThread.notifyOtherClient(message);
	         			}catch(InterruptedException e){
	         				e.printStackTrace();
	         				System.exit(-1);

	         			}catch(IOException e){
	         				e.printStackTrace();
	         				System.exit(-1);
	         			}
         			}
         			
         		}
			}.start();
			
			// manage client connection
			// once a client is connected with server, assign a new thread for this connection 
			while(true){
				// wait to connect
				Socket socket = serverSocket.accept();

				// create a thread to maintain a socket
				SerConCliThread thread = new SerConCliThread(socket,userName,this.serverView);

				// run thread
				thread.start();


			}

		}catch (BindException e){
			// terminate the program when using same address
			System.out.println("Address already in use");
			System.exit(-1);

		}catch (IOException e){
			e.printStackTrace();
			System.exit(-1);

		}finally{
			try{
				if(serverSocket != null){
					serverSocket.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
			
		}
	
	}


	/**
	* this class is for connceting with client
	*/
	private class SerConCliThread extends Thread{

		private Socket socket;
		private ChatFrame serverView;
		private String serverName;
		private String conCliName;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;

		public SerConCliThread(Socket socket, String userName, ChatFrame serverView) {

			this.serverView = serverView;
			this.socket = socket;
			this.serverName = userName;

			// add thread in threadList
			mThread.addThread(this);
			// update total number of online people
			this.serverView.setTotalNumLabel(mThread.getNumOfThread());
			
		}	

		@Override
		public void run(){ 

			Message message = null;

			try{

				this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
				this.inputStream = new ObjectInputStream(this.socket.getInputStream());
			
				// sending verification message to client 
				message = new Message(MessageType.VERIFICATION, this.socket.getInetAddress()+":"+this.socket.getLocalPort(),this.serverName);
				message.setNumOfOnlinePeople(mThread.getNumOfThread());
				this.outputStream.writeObject(message);

				// receiving notification message, display on ServerView and retransfer to other clients
				message=(Message)inputStream.readObject();
				this.serverView.setChatBox(message.getContent()+"\n\n");
				this.conCliName = message.getSender();
				message.setThreadId(this.hashCode());
				// enqueue message
				mMessage.enqueue(message);
				

				// receiving messages and enqueue
				while(true){
					message = (Message) inputStream.readObject();
					message.setThreadId(this.hashCode());
					mMessage.enqueue(message);
				}

				
			
			}catch (ClassNotFoundException e) {
				e.printStackTrace();

			}catch (EOFException e){
				// client disconnect from server
				System.out.println("Client close");

			}catch (IOException e ){
				e.printStackTrace();
				
			}finally {
				// remove this thread from list
				mThread.removeThread(this);
				this.serverView.setTotalNumLabel(mThread.getNumOfThread());
				String quitText = "\""+this.conCliName+"\" quits this chat room.";
				Message quitMessage = new Message(MessageType.NOTIFICATION,quitText,this.serverName);
				this.serverView.setChatBox(quitText+"\n\n");	
				quitMessage.setNumOfOnlinePeople(mThread.getNumOfThread());
				// send quit message to other clients
				mMessage.enqueue(quitMessage);

				try{
					if(this.socket != null){
						this.socket.close();
					}
				}catch (IOException e){
					e.printStackTrace();
					System.exit(-1);
				}
				

			}
		
		}

		/**
		* @return outputStream  get outputStream to send message to clinet
		*/
		public ObjectOutputStream getOutputStream(){
			return this.outputStream;
		}

	}


	/**
	* this class is for managing thread 
	*/
	private class ManageCliThread {

		private ArrayList<Thread> threadList; 

		/**
		* constructor of <code>ManageCliThread</code>
		*/ 
		public ManageCliThread(){
			this.threadList = new ArrayList<Thread>();
		}

		/**
		* add thread to the list
		* @param thread  <code>thread</code> needs to be added
		*/ 
		public synchronized void addThread(Thread thread){
			threadList.add(thread);
		}

		/**
		* remove thread to the list
		* @param thread  <code>thread</code> needs to be removed
		*/ 
		public synchronized void removeThread(Thread thread){
			threadList.remove(thread);
		}

		/**
		* get current number of thread
		* @return number of thread 
		*/ 
		public synchronized int getNumOfThread(){
			return threadList.size();
		}

		/**
		* get current number of thread
		* @param message  <code>message</code> needs to be delivered to other client
		*/ 
		public synchronized void notifyOtherClient(Message message) throws IOException{
			int threadId = message.getThreadId();
			for (int i =0; i<threadList.size(); i++){
				SerConCliThread cliThread = (SerConCliThread)threadList.get(i);
				// skip sending message to itself
				if (cliThread.hashCode() == threadId)
					continue;
				//System.out.println(cliThread);
				cliThread.getOutputStream().writeObject(message);
				
			}
		}

	}


	/**
	* this class is for managing messages
	*/
	private class ManageMessage {
		private ArrayDeque<Message> messages;

		/**
		* constructor of <code>ManageMessage</code>
		*/ 
		public ManageMessage(){
			this.messages = new ArrayDeque<Message>();
		}

		/**
		* @param message  enqueue message
		*/ 	
		public synchronized void enqueue(Message message){
			messages.add(message);
			// when adding new message, notifiy others to retrive message
			notifyAll();
		}

		/**
		* @return message  dequeue message
		*/ 
		public synchronized Message dequeue() throws InterruptedException, IOException{	
			Message message = null;
			while((message = messages.poll()) == null){
				//block here until queue is not empty
				wait();
			}
			return message;
			
		}
	}


	



}