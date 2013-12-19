/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.io.*;
import java.net.*;
import java.util.*;

//Thread used for network communication. Used on both server and client.
public class NetThread extends Thread {

	Socket s;
	
	InputStream is;
	ObjectInputStream ois;
	
	OutputStream os;
	ObjectOutputStream oos;
	
	ArrayList<Message> buffer = null;
	int id = -1;
	
	//Construct object; open all necessary streams and start the thread.
	public NetThread(Socket ps, ArrayList<Message> buf, int identity) {
	
		s = ps;
		buffer = buf;
		id = identity;
		
		try {
			os = s.getOutputStream();
			oos = new ObjectOutputStream(os);
			is = s.getInputStream();
			ois = new ObjectInputStream(is);
		} catch (Exception e) {
			e.printStackTrace();
			UIWindow.alert(null, "Unknown socket error occurred. Exiting now.");
			System.exit(1);
		}
		
		start();
	
	}
	
	//Sends a message object to the client or server on the other end.
	public void sendMessage(Message m) {
	
		try {
			oos.writeObject(m);
			oos.reset();
		} catch (Exception e) {
			e.printStackTrace();
			UIWindow.alert(null, "Unknown socket error occurred while sending message.");
		}
	
	}
	
	//Listens for messages from the other end and notifies the main thread when they come in.
	public void run() {
	
		while (true) {
		
			Object o = null;
			
			try {
				o = ois.readObject();
			} catch (Exception e) {
				System.out.println("Other side disconnected; Game over.");
				UIWindow.alert(null, "Other end disconnected; Game over.");
				System.exit(1);
			}
			
			Message m = (Message)o;
			m.origin = id;
			
			synchronized (buffer) {
				buffer.add(m);
				buffer.notify();
			}
		
		}
	
	}
	
	//Closes all streams and sockets.
	public void close() {
	
		try {
			ois.close();
			is.close();
			oos.close();
			os.close();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			//do something more useful here later
		}
	
	}

}