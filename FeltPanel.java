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

//JPanel subclass used for the card area in the UI. Drawn with a felt image in the background.
public class FeltPanel extends JPanel {

	public void paintComponent(Graphics g) {
	
		super.paintComponent(g);
		g.drawImage(ImageResources.img("felt.png"), 0, 0, null);
		//Is there a better way of doing this than subclassing JPanel?
	
	}

}