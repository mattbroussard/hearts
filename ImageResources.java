/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

//Handles preloading of image resources used in the graphical client
public class ImageResources {
	
	static String[] extras = {
		"b1fv.png",
		"b2fv.png",
		"felt.png"
	};
	
	static boolean inited = false;
	static Image[] images = new Image[55 + extras.length];
	
	//preloads an image resource from either the JAR or the local directory
	static void load(String name, int index) {
	
		images[index] = new ImageIcon(ImageResources.class.getResource("images/" + name)).getImage();
	
	}
	
	//preloads all image resources
	public static void init() {
	
		if (inited) return;
		
		images[0] = null;
		for (int i = 1; i <= 54; i++) load(i + ".png", i);
		for (int i = 0; i < extras.length; i++) load(extras[i], i + 55);
		
		inited = true;
	
	}
	
	//returns the preloaded Image at index.
	public static Image img(int index) {
	
		if (!inited) init();
		if ((index<0)||(index>=images.length)) return null;
		return images[index];
	
	}
	
	//returns a preloaded non-card image by name
	public static Image img(String name) {
	
		int found = -1;
		for (int i = 0; i < extras.length; i++) if (extras[i].equals(name)) found = i;
		
		if (found==(-1)) return null;
		return img(55 + found);
	
	}

}