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

//A text-based player client. Superseded by GUI version.
public class TextClient {

	public static IO io = new IO();
	
	public static NetThread server = null;
	public static ArrayList<Message> al = new ArrayList<Message>();
	
	public static String name = "Anonymous player";
	
	public static Deck hand = new Deck();
	public static Deck trick = new Deck();
	public static String[] trickPlayers = new String[4];
	
	//Prints out a numbered list of cards, usually for the user to pick from. Optionally labels each one (asterisk if legal, or for a trick, who played the card).
	public static void printDeck(Deck d, String[] labels) {
	
		for (int i = 0; i < d.length(); i++) {
			String label = "";
			if ((i<labels.length)&&(!labels[i].equals(""))&&(labels[i]!=null)) label = " (" + labels[i] + ")";
			io.pl("\t#" + (i+1) + ": " + d.cardAt(i).toString() + label);
		}
	
	}
	
	public static void printDeck(Deck d) { printDeck(d, new String[0]); }
	
	public static void printHand() { printDeck(hand); }
	public static void printTrick() { printDeck(trick, trickPlayers); }
	
	//Prints chat messages and status updates on the screen.
	public static void handleChat(Message m) { io.pl("CHAT: " + m.info); }
	public static void handleStatusUpdate(Message m) { io.pl("Status Update:\n" + m.info); }
	
	//Receives hand and notifies the user.
	public static void handleSendHand(Message m) {
	
		hand = m.getDeck();
		io.pl("Received hand:\n\n");
		printHand();
	
	}
	
	//Receives trick update and prints it on the screen
	public static void handleTrickUpdate(Message m) {
	
		trick = m.getDeck();
		trickPlayers = m.info.split("\n");
		
		if (trick.length()!=0) {
			io.pl("Trick updated:\n\n");
			printTrick();
		}
	
	}
	
	//Receives passed cards and notifies the user
	public static void handleSendPassed(Message m) {
	
		Deck d = m.getDeck();
		io.pl(m.info + " passed you these cards:\n\n");
		printDeck(d);
		for (int i = 0; i < d.length(); i++) hand.add(d.cardAt(i));
		hand.sort();
	
	}
	
	//Asks the user to pass 3 cards.
	public static void handleRequestPass(Message m) {
	
		io.pl("Select 3 cards to pass:\n\n");
		printHand();
		io.pl("\n\n");
		
		int[] pass = { -1, -1, -1 };
		String[] labels = { "1st", "2nd", "3rd" };
		int i = 0;
		
		outer: while (i < 3) {
		
			io.p(labels[i] + " card: ");
			int c = io.readInt();
			
			for (int j = 0; j < pass.length; j++) {
				if ((pass[j] + 1)==c) {
					io.pl("Invalid choice. Try again.");
					continue outer;
				}
			}
			
			if ((c>0)&&(c<=hand.length())) {
				pass[i] = c - 1;
				i++;
				continue;
			}
			
			io.pl("Invalid choice. Try again.");
		
		}
		
		Deck toPass = new Deck();
		
		for (int j = 0; j < 3; j++) toPass.add(hand.cardAt(pass[j]));
		for (int j = 0; j < 3; j++) hand.remove(toPass.cardAt(j));
		
		Message m2 = new Message(Message.PASS, null, toPass);
		server.sendMessage(m2);
		
		io.pl("\nCards passed.");
	
	}
	
	//Asks the user to play a card.
	public static void handleRequestPlay(Message m) {
	
		boolean heartsBroken = false;
		if (m.info.equals("true")) heartsBroken = true;
		
		if (trick.length() != 0) {
			io.pl("Current Trick:\n\n");
			printTrick();
		} else io.pl("You are leading the trick.\n\n");
		
		io.pl("\n\nSelect a card to play:\n\n");
		
		String[] legalLabels = new String[hand.length()];
		Deck legal = Rules.extractLegalCards(hand, trick, heartsBroken);
		
		for (int i = 0; i < hand.length(); i++) {
			if (legal.findCard(hand.cardAt(i))!=(-1)) legalLabels[i] = "*";
			else legalLabels[i] = "";
		}
		
		printDeck(hand, legalLabels);
		
		io.pl("\n\n");
		
		int card = -1;
		
		while (true) {
		
			io.p("Card to play: ");
			card = io.readInt();
			
			if ((card<1)||(card>hand.length())) {
				io.pl("Invalid choice. Try again.");
				continue;
			}
			
			if (legal.findCard(hand.cardAt(card - 1))==(-1)) {
				io.pl("Illegal choice. Try again.");
				continue;
			}
			
			card--;
			break;
		
		}
		
		Card c = hand.cardAt(card);
		hand.remove(c);
		
		Message m2 = new Message(Message.PLAY, null, c);
		server.sendMessage(m2);
		
		io.pl("Card played.");
	
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
		
		if (args.length==3) {
		
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
			System.exit(1);
		}
		
		server = new NetThread(s, al, -1);
		
		io.pl("Connected to server. Game will begin shortly...\n\n\n");
		
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
						System.exit(1);
					}
				
				}
			
			}
		
		}
	
	}

}