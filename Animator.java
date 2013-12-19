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

//Handles animation of UICards in a separate thread
public class Animator extends Thread {

	public static JComponent parent = null;
	
	UICard card;
	int animateX;
	int animateY;
	int time;
	
	//Runnable that (if provided) will be run after animation is complete.
	public Runnable r = null;

	//Shortcut to construct an Animator and start it all at once.
	public static void animateCard(UICard c, int t) {
	
		new Animator(c, t).start();
	
	}
	
	public Animator(UICard c, int x, int y, int t) {
	
		super();
		card = c;
		animateX = x;
		animateY = y;
		time = t;
	
	}

	public Animator(UICard c, int t) {
	
		this(c, c.animateX, c.animateY, t);
	
	}
	
	//Handles animation. Loops infinitely, calculating every 10ms the new location of the UICard.
	public void run() {
	
		long startTime = System.currentTimeMillis();
		
		Point startLoc = card.getLocation();
		
		while (true) {
		
			try { Thread.sleep(10); } catch (Exception e) {}
			
			long timeElapsed = System.currentTimeMillis() - startTime;
			if (timeElapsed>=time) break;
			
			double percent = (double)timeElapsed / (double)time;
			
			double x = (percent * (animateX - startLoc.x)) + startLoc.x;
			double y = (percent * (animateY - startLoc.y)) + startLoc.y;
			
			card.setLocation((int)Math.round(x), (int)Math.round(y));
			if (Animator.parent!=null) Animator.parent.repaint();
		
		}
		
		card.setLocation(animateX, animateY);
		if (r!=null) r.run();
		return;
	
	}

}