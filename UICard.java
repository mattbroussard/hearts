/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.awt.*;
import javax.swing.*;

//UI element representing a single Card
public class UICard extends JComponent {

	Card card;
	boolean raised = false;
	
	//These fields are set in UIWindow then accessed by Animator to animate the cards into place.
	public int animateX = -1;
	public int animateY = -1;
	
	public UICard(Card c) {
	
		this(c, false);
	
	}
	
	public UICard(Card c, boolean r) {
	
		super();
		card = c;
		raised = r;
		//this is actually one pixel narrower than the images are... they have an extra shadow included that I didn't want.
		setSize(71, 106);
		repaint();
	
	}
	
	//returns the Card object this UICard represents
	public Card getCard() {
	
		return card;
	
	}
	
	//change the Card object this UICard represents and repaint.
	public void setCard(Card c) {
	
		card = c;
		repaint();
	
	}
	
	//Sets the raised state of the card. Cards are raised to denote being legal choices.
	public void setRaised(boolean r) {
	
		raised = r;
		repaint();
	
	}
	
	//Returns true if raised.
	public boolean isRaised() { return raised; }
	
	//Toggles raised state.
	public void toggleRaised() {
	
		if (raised) setRaised(false);
		else setRaised(true);
	
	}
	
	//Renders card image in correct location.
	public void paintComponent(Graphics g) {
	
		int y = 0;
		if (!raised) y = 10;
		
		g.drawImage(ImageResources.img(card.imageNumber()), 0, y, null);
	
	}

}