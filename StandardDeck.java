/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

//A variety of Deck that can be constructed to contain the standard 52 cards in sequential order.
public class StandardDeck extends Deck {

	public StandardDeck() {
	
		super();
		
		for (int suit = 1; suit <= 4; suit++) {
		
			for (int value = 2; value <= 14; value++) {
			
				add(new Card(suit, value));
			
			}
		
		}
	
	}

}