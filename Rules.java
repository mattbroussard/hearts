/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

//Implements some functions governing gameplay, specifically the legality of plays.
public class Rules {
	
	//Given a completed trick, returns the index of the card that should take the trick.
	public static int indexOfWinningCard(Deck trick) {
	
		Card leader = trick.cardAt(0);
		int index = 0;
		
		for (int i = 1; i < trick.length(); i++) {
			Card c = trick.cardAt(i);
			if (c.suit!=leader.suit) continue;
			if (c.value > trick.cardAt(index).value) index = i;
		}
		
		return index;
	
	}

	//Returns all Cards from d of suit, or if include==false returns all Cards from d of suits other than suit.
	public static Deck extract(Deck d, int suit, boolean include) {
	
		Deck tbr = new Deck();
		
		for (int i = 0; i < d.length(); i++) {
			Card c = d.cardAt(i);
			int s = c.suit;
			if (include&&(s==suit)) tbr.add(c);
			if ((!include)&&(s!=suit)) tbr.add(c);
		}
		
		return tbr;
	
	}
	
	//Returns true if a player should be allowed to play c, given their hand, the trick, and whether hearts have been broken yet.
	public static boolean cardIsLegal(Card c, Deck hand, Deck trick, boolean heartsBroken) {
	
		if (hand.findCard(c)==(-1)) return false;
		
		Deck legalCards = extractLegalCards(hand, trick, heartsBroken);
		return legalCards.findCard(c) != (-1);
	
	}

	//Returns the subset of cards in the hand that are legal to play for this trick.
	public static Deck extractLegalCards(Deck hand, Deck trick, boolean heartsBroken) {
	
		if (trick.length()==0) {
		
			if (heartsBroken) return hand;
			
			if (hand.length()==13) {
				Card c = new Card(Card.CLUBS, 2);
				Deck tbr = new Deck();
				tbr.add(c);
				return tbr;
			}
			
			Deck tbr = extract(hand, Card.HEARTS, false);
			if (tbr.length()==0) return hand;
			return tbr;
		
		}
		
		int suit = trick.cardAt(0).suit;
		Deck cardsOfSuit = extract(hand, suit, true);
		
		if (cardsOfSuit.length()>0) return cardsOfSuit;
		
		return hand;
	
	}

}