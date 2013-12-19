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

//A combined label and textfield used to simplify layout in Launcher
public class LabeledField extends JPanel {

	JLabel label;
	JTextField field;

	public String getText() { return field.getText(); }
	public void setText(String val) { field.setText(val); }

	public LabeledField(String labelText, String defaultText) {
	
		super();
		label = new JLabel(labelText + ":");
		field = new JTextField(defaultText, 15);
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(label);
		add(field);
	
	}

}