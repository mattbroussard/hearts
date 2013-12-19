/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.util.*;
import java.io.*;
import java.net.*;

//Graphical player client. Essentially a copy of TextClient with all console-based controls stripped out and replaced with a GUI. Done this way to separate networking, etc. from the GUI code.
public class GraphicalClient {

	public static IO io = new IO();
	public static UIWindow win = null;
	
	public static NetThread server = null;
	public static ArrayList<Message> al = new ArrayList<Message>();
	
	public static String name = "Anonymous player";
	
	public static Deck hand = new Deck();
	public static Deck trick = new Deck();
	public static String[] trickPlayers = new String[4];
	
	//Send received status update to UIWindow to be displayed.
	public static void handleStatusUpdate(Message m) {
	
		win.statusUpdate(m.info);
	
	}
	
	//Called from UIWindow; sends chat message back to the server.
	public static void sendChatMessage(String text) {
	
		server.sendMessage(new Message(Message.CHAT, text, null));
	
	}
	
	//Send received chat message to UIWindow to be displayed.
	public static void handleChat(Message m) {
	
		win.receiveChatMessage(m.info);
	
	}
	
	//Receives hand and sends it to UIWindow to be displayed.
	public static void handleSendHand(Message m) {
	
		hand = m.getDeck();
		win.handUpdate(hand);
	
	}
	
	//Receives trick update and sends it to UIWindow to be displayed.
	public static void handleTrickUpdate(Message m) {
	
		trick = m.getDeck();
		trickPlayers = m.info.split("\n");
		
		win.trickUpdate(trick, trickPlayers);
	
	}
	
	//Receives passed cards and sends them to UIWindow to be displayed.
	public static void handleSendPassed(Message m) {
	
		Deck d = m.getDeck();
		for (int i = 0; i < d.length(); i++) hand.add(d.cardAt(i));
		hand.sort();
		win.handUpdate(hand);
	
	}
	
	//called by UIWindow to send passed cards to server.
	public static void sendPassedCards(Deck toPass) {
	
		for (int i = 0; i < toPass.length(); i++) {
			hand.remove(toPass.cardAt(i));
		}
		
		Message m2 = new Message(Message.PASS, null, toPass);
		server.sendMessage(m2);
	
	}
	
	//Tell UIWindow it should ask the user to pass three cards.
	public static void handleRequestPass(Message m) {

		win.startPassing();
	
	}
	
	//Called from UIWindow to send played card to server.
	public static void play(Card c) {
	
		Message m = new Message(Message.PLAY, null, c);
		hand.remove(c);
		trick.add(c);
		server.sendMessage(m);
	
	}
	
	//Tells UIWindow it should ask the user to play a card.
	public static void handleRequestPlay(Message m) {
	
		boolean heartsBroken = false;
		if (m.info.equals("true")) heartsBroken = true;
		
		Deck legal = Rules.extractLegalCards(hand, trick, heartsBroken);
		
		win.play(legal);
	
	}
	
	//Chooses a function to call based on the call the server made.
	public static void handleMessage(Message m) {
	
		switch (m.call) {
			case Message.CHAT: handleChat(m); break;
			case Message.STATUS_UPDATE: handleStatusUpdate(m); break;
			case Message.SEND_HAND: handleSendHand(m); break;
			case Message.TRICK_UPDATE: handleTrickUpdate(m); break;
			case Message.SEND_PASSED: handleSendPassed(m); break;
			case Message.REQUEST_PASS: handleRequestPass(m); break;
			case Message.REQUEST_PLAY: handleRequestPlay(m); break;
			default: io.pl("Received unimplemented call #" + m.call + " (" + Message.CALL_NAMES[m.call] + ") from server.");
		}
	
	}
	
	//Returns true if the message buffer is not empty.
	public static boolean haveMessages() {
	
		int len = -1;
		len = al.size();
		return len > 0;
	
	}
	
	//Connects to server, starts a NetThread, then waits for messages.
	public static void main(String[] args) {
		
		int port = -1;
		String host = null;
		
		if (args.length>=3) {
		
			name = args[0];
			host = args[1];
			try {
				port = Integer.parseInt(args[2]);
			} catch (Exception e) { port = -1; }
		
		}
		
		if (port==(-1)) {
		
			io.p("What is your name? ");
			name = io.readLine();
			
			io.p("What server would you like to connect to? ");
			host = io.readLine();
			
			io.p("What port would you like to connect on? ");
			port = io.readInt();
			
			if ((port<1)||(port>65535)) {
				io.pl("Invalid port number.");
				System.exit(1);
			}
		
		}
		
		io.pl("Connecting to " + host + ":" + port + " as \"" + name + "\"...");
		
		Socket s = null;
		
		try {
			s = new Socket(host, port);
		} catch (Exception e) {
			io.pl("Couldn't connect to that server.");
			UIWindow.alert(null, "Couldn't connect to that server.");
			System.exit(1);
		}
		
		server = new NetThread(s, al, -1);
		
		io.pl("Connected to server. Game will begin shortly...\n\n\n");
		
		Message nameMessage = new Message(Message.SET_NAME, name, null);
		server.sendMessage(nameMessage);
		
		win = new UIWindow(host, port, name);
		
		if (args.length>=5) {
			int x = -1;
			int y = -1;
			try {
				x = Integer.parseInt(args[3]);
				y = Integer.parseInt(args[4]);
			} catch (Exception e) {}
			if ((x>=0)&&(y>=0)) win.setLocation(x, y);
		}
		
		while (true) {
		
			synchronized (al) {
			
				if (haveMessages()) {
				
					Message m = null;
					
					m = al.get(0);
					al.remove(0);
					
					handleMessage(m);
				
				} else {
				
					try {
						al.wait();
					} catch (Exception e) {
						io.pl("An eception occurred while trying to wait for messages from the server.");
						UIWindow.alert(null, "Client error occurred.");
						System.exit(1);
					}
				
				}
			
			}
		
		}
	
	}

}