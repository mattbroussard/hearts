/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.util.*;

//handles generation of random numbers. Designed so that a set seed can be used while debugging to ensure the game proceeds the same way each time.
public class Randomness {

	public static Random r = null;

	//generates a random integer between min and max.
	public static int random(int min, int max) {
	
		if (max<=min) return min;
		
		if (r==null) {
		
			//long seed = 42;
			long seed = System.currentTimeMillis();
			r = new Random(seed);
		
		}
		
		return min + r.nextInt(max - min);
	
	}

}