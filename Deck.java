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

//Represents any set of Cards, be it a hand, a trick, or the whole deck.
public class Deck implements Serializable {

	ArrayList<Card> al = new ArrayList<Card>();
	
	public Deck() {}
	
	public Deck(Card c) {
	
		add(c);
	
	}
	
	public Deck(Card[] c) {
	
		for (int i = 0; i < c.length; i++) add(c[i]);
	
	}
	
	//Returns the number of cards in the Deck.
	public int length() {
	
		return al.size();
	
	}
	
	//Appends c to the end of the Deck if the Deck does not already include c
	public void add(Card c) {
	
		if (!hasCard(c)) al.add(c);
	
	}
	
	//Removes the card at index
	public void remove(int index) {
	
		if ((index<0) || (index>=al.size())) return;
		al.remove(index);
	
	}
	
	//Finds c in the Deck and removes it.
	public void remove(Card c) {
	
		int index = findCard(c);
		if (index<0) return;
		remove(index);
	
	}
	
	//Finds the location of c in the Deck. Returns -1 if c is not present.
	public int findCard(Card c) {
	
		for (int i = 0; i < al.size(); i++) {
		
			Card c2 = al.get(i);
			if (c.equals(c2)) return i;
		
		}
		
		return -1;
	
	}
	
	//Returns the Card at index
	public Card cardAt(int index) {
	
		if ((index<0) || (index>=al.size())) return null;
		return al.get(index);
	
	}
	
	//Returns true if c is in the Deck
	public boolean hasCard(Card c) {
	
		return findCard(c) != (-1);
	
	}
	
	//Sorts the Deck. (Sort function copied from the one I wrote for Project: Address Book)
	public void sort() {
	
		for (int i = 0; i < al.size(); i++) {
		
			int min = i;
			
			for (int j = i + 1; j < al.size(); j++) {
			
				Card jE = al.get(j);
				Card minE = al.get(min);
				if (jE.compareTo(minE) < 0) min = j;
			
			}
			
			swap(i, min);
		
		}
	
	}
	
	//Swaps the cards at i and j (used for sorting and shuffling)
	public void swap(int i, int j) {
	
		if (i==j) return;
		
		Card temp = al.get(i);
		al.set(i, al.get(j));
		al.set(j, temp);
	
	}
	
	//Randomizes the order of the cards in the Deck.
	public void shuffle() {
	
		for (int i = 0; i < al.size(); i++) {
		
			int that = Randomness.random(0, al.size() - 1);
			swap(i, that);
		
		}
	
	}
	
	//Removes and returns a Deck of n cards off the end of this Deck. Assumes Deck is already shuffled.
	public Deck deal(int n) {
	
		Deck tbr = new Deck();
		
		while (tbr.length() < n) {
		
			Card c = al.get(al.size() - 1);
			tbr.add(c);
			al.remove(al.size() - 1);
		
		}
		
		return tbr;
	
	}
	
	//Returns sum of pointValue()s of each Card
	public int pointValue() {
	
		int sum = 0;
		for (int i = 0; i < length(); i++) sum += cardAt(i).pointValue();
		return sum;
	
	}
	
	public String toString() {
	
		String tbr = "[ ";
		
		for (int i = 0; i < length(); i++) {
			if (i!=0) tbr += ", ";
			tbr += cardAt(i).toString();
		}
		
		tbr += " ]";
		
		return tbr;
	
	}

}