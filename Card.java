/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.io.*;

//Represents a single card.
public class Card implements Comparable, Serializable {

	public int suit;
	public int value;
	
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;
	public static final int ACE = 14;
	
	public static final int CLUBS = 1;
	public static final int DIAMONDS = 2;
	public static final int SPADES = 3;
	public static final int HEARTS = 4;
	
	public static final String[] SUITS = { null, "clubs", "diamonds", "spades", "hearts" };
	
	public Card(int pSuit, int pValue) {
	
		suit = pSuit;
		value = pValue;
	
	}
	
	//Parse a string representation of the card and construct the object.
	public Card(String strRep) {
	
		String[] parts = strRep.split(" of ");
		String valueStr = parts[0];
		String suitStr = parts[1];
		
		if (valueStr.equals("jack")) valueStr = "11";
		if (valueStr.equals("queen")) valueStr = "12";
		if (valueStr.equals("king")) valueStr = "13";
		if (valueStr.equals("ace")) valueStr = "14";
		
		try {
			value = Integer.parseInt(valueStr);
		} catch (Exception e) { value = 0; }
		
		suit = 0;
		
		for (int i = 0; i < Card.SUITS.length; i++) {
			if (suitStr.equals(Card.SUITS[i])) { suit = i; break; }
		}
	
	}
	
	public String toString() {
	
		String valueStr = "" + value;
		
		if (value==Card.ACE) valueStr = "ace";
		if (value==Card.JACK) valueStr = "jack";
		if (value==Card.QUEEN) valueStr = "queen";
		if (value==Card.KING) valueStr = "king";
		
		String suitStr = Card.SUITS[suit];
		
		return valueStr + " of " + suitStr;
	
	}
	
	public boolean equals(Object o) {
	
		return compareTo(o) == 0;
	
	}
	
	public int compareTo(Object o) {
	
		if (!(o instanceof Card)) return 0;
		return compareTo((Card)o);
	
	}
	
	public int compareTo(Card c) {
	
		if (suit < c.suit) return -100;
		if (suit > c.suit) return 100;
		
		return value - c.value;
	
	}
	
	//Returns the value of the card in points. 1 for hearts, 13 for the queen of spades, 0 otherwise.
	public int pointValue() {
	
		if (suit==Card.HEARTS) return 1;
		if ((suit==Card.SPADES)&&(value==Card.QUEEN)) return 13;
		return 0;
	
	}
	
	//Returns the filename of the image to display this card with
	public int imageNumber() {
	
		int val = 14 - value;
		int st = 0;
		
		switch (suit) {
			case CLUBS: st = 1; break;
			case SPADES: st = 2; break;
			case HEARTS: st = 3; break;
			case DIAMONDS: st = 4; break;
		}
		
		int tbr = (4 * val) + st;
		return tbr;
	
	}

}