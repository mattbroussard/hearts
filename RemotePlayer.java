/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.util.*;
import java.net.*;

//A variety of NetThread with some extra variables that the server needs to keep track of for each client.
public class RemotePlayer extends NetThread {

	public int roundScore = 0;
	public int totalScore = 0;
	public Deck hand = null;
	public Deck pass = null;
	public String name = "Anonymous player";
	
	public RemotePlayer(Socket ps, ArrayList<Message> buf, int identity) {
		super(ps, buf, identity);
	}

}