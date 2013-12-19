/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.io.*;

//Serializable object used for communication over the network.
public class Message implements Serializable {

	public Serializable payload;
	public int call;
	public String info;
	
	public transient int origin = -1; //set on the server when a message arrives to indicate which client it came from
	
	public static final String[] CALL_NAMES = { null, "STATUS_UPDATE", "SEND_HAND", "REQUEST_PASS", "SEND_PASSED", "TRICK_UPDATE", "REQUEST_PLAY", "CHAT", "SET_NAME", "PLAY", "PASS" };
	
	//Server-to-client calls.
	public static final int STATUS_UPDATE = 1; //Update score display with names, round/total scores, and what round it is.
	public static final int SEND_HAND = 2; //Send hand.
	public static final int REQUEST_PASS = 3; //Request for client to pass 3 cards.
	public static final int SEND_PASSED = 4; //Send client the cards passed to it, including who sent them.
	public static final int TRICK_UPDATE = 5; //Update trick display, including a list of who played what.
	public static final int REQUEST_PLAY = 6; //Request client to play card, given whether hearts have been broken (should have trick cached locally).
	
	//Bidirectional calls.
	public static final int CHAT = 7; //Chat or system message.
	
	//Client-to-server calls.
	public static final int SET_NAME = 8; //Set displayed player name. Required at the beginning of the game; server will not start game until everyone has named themselves.
	public static final int PLAY = 9; //Play card as requested.
	public static final int PASS = 10; //Pass cards as requested.
	
	public Message(int c, String i, Serializable o) {
	
		call = c;
		info = i;
		payload = o;
	
	}
	
	//Handles the casting of the payload object to Card to simplify code that uses this
	public Card getCard() {
	
		if (payload instanceof Card) return (Card)payload;
		return null;
	
	}
	
	//Handles the casting of the payload object to Deck to simplify code that uses this
	public Deck getDeck() {
	
		if (payload instanceof Deck) return (Deck)payload;
		return null;
	
	}
	
	public String toString() {
	
		String tbr = "";
		
		tbr += "call=" + call + " ";
		
		tbr += "info=";
		if (info==null) tbr += "null";
		else tbr += info;
		tbr += " ";
		
		tbr += "payload=";
		if (payload==null) tbr += "null";
		else tbr += payload.toString();
		tbr += " ";
		
		tbr += "origin=" + origin;
		
		return tbr;
	
	}

}