package controller.serial;

import java.util.function.Consumer;

public interface CommChannel {
	
	/**
	 * Send a message represented by a string (without new line).
	 * 
	 * Asynchronous model.
	 * 
	 * @param msg
	 */
	void sendMsg(String msg);
	
	/**
	 * Method to use when we want to stop using the port.
	 */
	public  void close();
	
	/**
	 * To set the handler to call when receiving a message
	 * 
	 * @param handler the message handler.
	 */
	void setMessageHandler(Consumer<String> handler);

}
