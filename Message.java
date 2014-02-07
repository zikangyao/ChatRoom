
/**
* VERIFICATION is for shake hand message when connecting with server and clinet
* NOTIFICATION is for someone joining or leaving chat room
* TEXT is for regular chat message
* type of <code>Message</code>
*/
enum MessageType{
	VERIFICATION, 
	NOTIFICATION, 
	TEXT 
}

/**
* capsulate chatting message
*/ 
class Message implements java.io.Serializable{

	private MessageType type;
	private String text;
	private String sender;
	private int threadId;
	private int numOfOnlinePeople;
	/**
	* Constructor of <code>Message</code>
	* @param type  <code>Message</code> type
	* @param text  <code>Message</code> text
	* @param sender  <code>Message</code> sender user name
	*/
	public Message(MessageType type,String text, String sender){
		this.type = type;
		this.text = text;
		this.sender = sender;
	}
	
	/**
	* @return type  <code>Message</code> type
	*/
	public MessageType getType() {
		return type;
	}

	/**
	* @return text  <code>Message</code> text
	*/
	public String getContent() {
		return text;
	}
	
	/**
	* @return sender  <code>Message</code> sender user
	*/
	public String getSender() {
		return sender;
	}

	/**
	* @param threadId  hashCode of a specifc thread
	*/
	public void setThreadId(int threadId){
		this.threadId = threadId;
	}

	/**
	* @return threadId  hashCode of a specifc thread
	*/
	public int getThreadId(){
		return this.threadId;
	}

	/**
	* @param numOfOnlinePeople  set number of online people
	*/
	public void setNumOfOnlinePeople(int num){
		this.numOfOnlinePeople = num;
	}

	/**
	* @return numOfOnlinePeople  get number of online people
	*/
	public int getNumOfOnlinePeople(){
		return this.numOfOnlinePeople;
	}
	
	
	
}
