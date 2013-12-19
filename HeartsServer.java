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

//The main server class. Manages all aspects of gameplay and dictates to clients what they are to do.s
public class HeartsServer {

	public static ArrayList<Message> al = new ArrayList<Message>();
	public static RemotePlayer[] clients = new RemotePlayer[4];
	public static IO io = new IO();
	
	public static int roundNum = 1;
	public static int trickNum = 0;
	public static int named = 0;
	public static int passed = 0;
	public static int leader = -1;
	public static int whoseTurn = -1;
	public static boolean heartsBroken = false;
	public static Deck trick = new Deck();
	
	public static int passStyle = 0;
	public static final int[] PASS_STYLES = { -1, 1, 2, 0 }; //left, right, across, don't pass.
	
	public static int endCondition = 0; //Number of points to end the game after
	
	//Starts a new trick
	public static void startTrick() {
	
		trickNum++;
		trick = new Deck();
		
		if (trickNum==1) {
			Card c2 = new Card(Card.CLUBS, 2);
			for (int i = 0; i < clients.length; i++) {
				if (clients[i].hand.findCard(c2)>=0) { leader = i; break; }
			}
		}
		
		multicastMessage(new Message(Message.TRICK_UPDATE, playerNames(leader), trick));
		whoseTurn = leader;
		
		Message m = new Message(Message.REQUEST_PLAY, "false", new Deck());
		clients[leader].sendMessage(m);
	
	}
	
	//Sends each client an update with all players' scores; returns whether the game is going to continue (checks end condition)
	public static boolean scoreUpdate(boolean endOfRound) {
	
		String tbr = "Round " + roundNum;
		
		boolean gameOver = false;
		
		for (int i = 0; i < clients.length; i++) {
		
			tbr += "\n";
			RemotePlayer p = clients[i];
			tbr += p.name + " (" + p.roundScore + "/" + p.totalScore + ")";
			
			if ((endCondition!=0)&&(p.totalScore>=endCondition)) gameOver = true;
		
		}
		
		Message m = new Message(Message.STATUS_UPDATE, tbr, null);
		multicastMessage(m);
		
		if (endOfRound && gameOver) {
			
			multicastMessage(new Message(Message.TRICK_UPDATE, " \n \n \n ", new Deck()));
            multicastMessage(new Message(Message.CHAT, "[system]: Game is now over.", null));
            io.pl("Game is now over.");
            
			return false;
			
		} else return true;
	
	}
	
	//Called at the end of a trick. Counts points and gives them to the approriate player; Updates all clients on current status; Prepares for a new trick or ends the round.
	public static void endTrick() {
	
		if (trick.length() < 4) return;
		
		int winningCard = Rules.indexOfWinningCard(trick);
		
		int winner = leader + winningCard;
		if (winner>=4) winner -= 4;
		
		leader = winner;
		
		int points = trick.pointValue();
		clients[winner].roundScore += points;
		
		String pointString = "(" + points;
		pointString += " point";
		if (points!=1) pointString += "s";
		pointString += ")";
		
		scoreUpdate(false);
		multicastMessage(new Message(Message.CHAT, ("[system]: " + clients[winner].name + " took the trick " + pointString), null));
		
		if (trickNum==13) endRound();
		else startTrick();
	
	}
	
	//Returns the list of player names in order starting at a given index (used to label who played which card)
	public static String playerNames(int startingAt) {
	
		String tbr = "";
		
		for (int i = 0; i < 4; i++) {
			if (i!=0) tbr += "\n";
			int j = startingAt + i;
			if (j>=4) j -= 4;
			tbr += clients[j].name;
		}
		
		return tbr;
	
	}
	
	//Handles the call from the client when they play a card. Makes sure it's legal, then updates all the other clients that the play happened.
	public static void handlePlay(Message m) {
	
		Card c = m.getCard();
		
		if ((m.origin != whoseTurn)||(!Rules.cardIsLegal(c, clients[m.origin].hand, trick, heartsBroken))) {
			io.pl("Client #" + m.origin + " tried to play illegally (" + m.getCard().toString() + ").");
			return;
		}
		
		trick.add(c);
		clients[m.origin].hand.remove(c);
		if (c.suit==Card.HEARTS) heartsBroken = true;
		
		Message update = new Message(Message.TRICK_UPDATE, playerNames(leader), trick);
		multicastMessage(update);
		
		if (trick.length() < 4) {
		
			whoseTurn++;
			if (whoseTurn>=4) whoseTurn -= 4;
			
			Message m2 = new Message(Message.REQUEST_PLAY, "" + heartsBroken, null);
			clients[whoseTurn].sendMessage(m2);
		
		} else endTrick();
	
	}
	
	//Called at the end of the round. Updates scores, including logic to determine if anyone shot the moon. Prepares to start a new round.
	public static void endRound() {
	
		for (int i = 0; i < clients.length; i++) {
		
			if (clients[i].roundScore==26) {
			
				clients[i].roundScore = 0;
				for (int j = 0; j < clients.length; j++) if (j!=i) clients[j].totalScore += 26;
				multicastMessage(new Message(Message.CHAT, ("[system]: " + clients[i].name + " shot the moon! Sucks for the rest of y'all!"), null));
				break;
			
			}
			
			clients[i].totalScore += clients[i].roundScore;
			clients[i].roundScore = 0;
		
		}
		
		roundNum++;
		
		boolean continuePlaying = scoreUpdate(true);
		if (!continuePlaying) return;
		else startRound();
	
	}
	
	//Starts a new round. Shuffles a new Deck and deals cards out to each player. Tells players to pass, if applicable, or starts the first trick.
	public static void startRound() {
	
		trickNum = 0;
		passed = 0;
		leader = -1;
		whoseTurn = -1;
		heartsBroken = false;
		
		multicastMessage(new Message(Message.TRICK_UPDATE, " \n \n \n ", new Deck()));
		
		Deck wholeDeck = new StandardDeck();
		wholeDeck.shuffle();
		
		Message pass_req = new Message(Message.REQUEST_PASS, null, null);
		
		for (int i = 0; i < clients.length; i++) {
		
			Deck hand = wholeDeck.deal(13);
			hand.sort();
			clients[i].hand = hand;
			clients[i].pass = null;
			Message handMessage = new Message(Message.SEND_HAND, null, hand);
			clients[i].sendMessage(handMessage);
			
			if (PASS_STYLES[passStyle]!=0) {
				clients[i].sendMessage(pass_req);
			}
		
		}
		
		if (PASS_STYLES[passStyle]==0) startTrick();
	
	}
	
	//Takes cards passed from client and passes them to appropriate other client.
	public static void handlePass(Message m) {
	
		passed++;
		clients[m.origin].pass = m.getDeck();
		
		if (passed==4) {
		
			for (int i = 0; i < clients.length; i++) {
			
				int passRecipient = i + PASS_STYLES[passStyle];
				if (passRecipient>=4) passRecipient -= 4;
				if (passRecipient<0) passRecipient += 4;
				
				Message m2 = new Message(Message.SEND_PASSED, clients[i].name, clients[i].pass);
				
				for (int j = 0; j < clients[i].pass.length(); j++) {
					clients[i].hand.remove(clients[i].pass.cardAt(j));
					clients[passRecipient].hand.add(clients[i].pass.cardAt(j));
				}
				
				clients[passRecipient].hand.sort();
				clients[passRecipient].sendMessage(m2);
			
			}
			
			passStyle++;
			if (passStyle>=4) passStyle -= 4;
			startTrick();
		
		}
	
	}
	
	//Sends a Message object to all clients.
	public static void multicastMessage(Message m) {
	
		for (int i = 0; i < clients.length; i++) clients[i].sendMessage(m);
	
	}
	
	//Receives and rebroadcasts chat messages from clients.
	public static void handleChat(Message m) {
	
		String name = clients[m.origin].name;
		Message m2 = new Message(Message.CHAT, (name + ": " + m.info), null);
		multicastMessage(m2);
	
	}
	
	//Receives name from each client. When all names received, starts first round.
	public static void handleSetName(Message m) {
	
		if (named>=4) return;
		
		clients[m.origin].name = m.info.replace("INDEX", (m.origin + 1) + "");
		Message m2 = new Message(Message.CHAT, ("[system]: " + clients[m.origin].name + " has joined the game."), null);
		multicastMessage(m2);
		named++;
		
		if (named==4) {
		
			String gameStartMsg = "Game is starting. Will play ";
			if (endCondition==0) gameStartMsg += "endlessly.";
			else gameStartMsg += "until someone has " + endCondition + " or more points.";
			multicastMessage(new Message(Message.CHAT, "[system]: " + gameStartMsg, null));
			io.pl(gameStartMsg);
			
			startRound();
		}
	
	}
	
	//Chooses a function to call based on the call the client made.
	public static void handleMessage(Message m) {
	
		switch (m.call) {
			case Message.CHAT: handleChat(m); break;
			case Message.SET_NAME: handleSetName(m); break;
			case Message.PASS: handlePass(m); break;
			case Message.PLAY: handlePlay(m); break;
			default: io.pl("Received unimplemented call #" + m.call + " (" + Message.CALL_NAMES[m.call] + ") from client " + m.origin + ".");
		}
	
	}
	
	//Returns true if the message buffer is not empty.
	public static boolean haveMessages() {
	
		int len = -1;
		len = al.size();
		return len > 0;
	
	}
	
	//Opens a ServerSocket, listens for 4 connections, constructs NetThreads for each, then waits for messages.
	public static void main(String[] args) {
	
		int port = -1;
		
		if (args.length>=1) {
			try {
				port = Integer.parseInt(args[0]);
				if (args.length>=2) endCondition = Integer.parseInt(args[1]);
			} catch (Exception e) {}
			if ((port<1)||(port>65535)) port = -1;
		}
		
		if (port==(-1)) {
			io.p("Enter port number to run on: ");
			port = io.readInt();
		}
		
		if ((port<1)||(port>65535)) {
			io.pl("Invalid port number. Now exiting.");
			System.exit(1);
		}
		
		if (port==(-1)) {
			io.pl("\nEnter the score to end the game at.");
			io.pl("The game will end when someone exceeds this score.");
			io.pl("Alternatively, enter 0 to play endlessly.");
			io.p("End score: ");
			endCondition = io.readInt();
			if (endCondition<0) endCondition = 0;
		}
		
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (Exception e) {
			io.pl("Couldn't bind a ServerSocket on that port. It's probably taken by something more important.");
			UIWindow.alert(null, "Couldn't bind a ServerSocket on that port. It's probably taken by something more important.");
			System.exit(1);
		}
		
		io.pl("Listening for clients on port " + port + "...");
		
		for (int i = 0; i < 4; i++) {
		
			Socket s = null;
			
			try {
				s = ss.accept();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			clients[i] = new RemotePlayer(s, al, i);
			io.pl("Client " + (i + 1) + " of 4 connected. Waiting on " + (3-i) + " more.");
		
		}
		
		try {
			ss.close();
		} catch (Exception e) {
			io.pl("An unknown error occurred while trying to close the ServerSocket.");
			System.exit(1);
		}
		
		io.pl("All clients are now connected. Game will begin momentarily...");
		
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
						io.pl("An exception occurred while trying to wait for messages from clients.");
						UIWindow.alert(null, "Server error occurred.");
						System.exit(1);
					}
				
				}
			
			}
		
		}
	
	}

}