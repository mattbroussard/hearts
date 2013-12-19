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

//An unintelligent AI that picks cards at random to play. Will eventually be superseded by a smarter AI that actually tries to choose the best play.
public class StupidAI {

	public static IO io = new IO();
	
	public static NetThread server = null;
	public static ArrayList<Message> al = new ArrayList<Message>();
	
	public static String name = "Computer INDEX";
	
	public static Deck hand = new Deck();
	public static Deck trick = new Deck();
	public static String[] trickPlayers = new String[4];
	
	//Chooses n random cards from haystack
	public static Deck randomCards(Deck haystack, int n) {
	
		Deck tbr = new Deck();
		
		while (tbr.length() < n) {
		
			int random = Randomness.random(0, haystack.length() - 1);
			Card c = haystack.cardAt(random);
			if (tbr.hasCard(c)) continue;
			tbr.add(c);
		
		}
		
		return tbr;
	
	}
	
	//Receives hand.
	public static void handleSendHand(Message m) {
	
		hand = m.getDeck();
	
	}
	
	//Receives trick update.
	public static void handleTrickUpdate(Message m) {
	
		trick = m.getDeck();
		trickPlayers = m.info.split("\n");
	
	}
	
	//Receives passed cards.
	public static void handleSendPassed(Message m) {
	
		Deck d = m.getDeck();
		for (int i = 0; i < d.length(); i++) hand.add(d.cardAt(i));
		hand.sort();
	
	}
	
	//Chooses 3 cards to pass.
	public static void handleRequestPass(Message m) {
	
		Deck toPass = randomCards(hand, 3);
		
		for (int i = 0; i < toPass.length(); i++) hand.remove(toPass.cardAt(i));
		
		Message m2 = new Message(Message.PASS, null, toPass);
		server.sendMessage(m2);
	
	}
	
	//Plays a card.
	public static void handleRequestPlay(Message m) {
	
		
		boolean heartsBroken = false;
		if (m.info.equals("true")) heartsBroken = true;
		
		Deck legal = Rules.extractLegalCards(hand, trick, heartsBroken);
		
		Card c = randomCards(legal, 1).cardAt(0);
		hand.remove(c);
		
		Message m2 = new Message(Message.PLAY, null, c);
		server.sendMessage(m2);
	
	}
	
	//Chooses a function to call based on the call the server made.
	public static void handleMessage(Message m) {
	
		switch (m.call) {
			case Message.CHAT: break;
			case Message.STATUS_UPDATE: break;
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
		
			io.p("What server would you like to connect to? ");
			host = io.readLine();
			
			io.p("What port would you like to connect on? ");
			port = io.readInt();
			
			if ((port<1)||(port>65535)) {
				io.pl("Invalid port number.");
				System.exit(1);
			}
		
		}
		
		io.pl("Connecting to " + host + ":" + port + "...");
		
		Socket s = null;
		
		try {
			s = new Socket(host, port);
		} catch (Exception e) {
			io.pl("Couldn't connect to that server.");
			UIWindow.alert(null, "Couldn't connect to that server.");
			System.exit(1);
		}
		
		server = new NetThread(s, al, -1);
		
		io.pl("Connected to server.");
		
		Message nameMessage = new Message(Message.SET_NAME, name, null);
		server.sendMessage(nameMessage);
		
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
						io.pl("An exception occurred while trying to wait for messages from the server.");
						UIWindow.alert(null, "AI Client error occurred.");
						System.exit(1);
					}
					
				
				}
			
			}
		
		}
	
	}

}