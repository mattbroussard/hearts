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
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.*;

//Graphical interface that launches by default when the JAR is double clicked. Allows user to choose between hosting a game (server), or joining as either an AI or a Human.
//Note: The UI for this is not extremely user-friendly, and likely not constructed using best practices. It was a quick-and-dirty last minute addition to keep from making the user use the command line to launch the game.
public class Launcher extends JFrame implements ActionListener {
	
	String version = "Hearts by Matt Broussard - v2.0b";
	
	public static Launcher win = null;
	Container ca;
	CardLayout lay;
	
	JPanel c1 = new JPanel();
	JComponent[] c1e = {
		new JButton("Join Game as Human"),
		new JButton("Join Game as AI"),
		new JButton("Host a Game")
	};
	
	JPanel c2 = new JPanel();
	JComponent[] c2e = {
		new LabeledField("Name", ""),
		new LabeledField("Server", ""),
		new LabeledField("Port", "54400"),
		new JButton("Connect")
	};
	
	JPanel c3 = new JPanel();
	JComponent[] c3e = {
		new LabeledField("End Score", "0"),
		new JLabel("(Enter 0 to play endlessly.)"),
		new LabeledField("Port", "54400"),
		new JButton("Start Server")
	};

	JPanel c4 = new JPanel();
	JComponent[] c4e = {
		new JLabel("Initializing..."),
		new JLabel("")
	};

	int mode = -1; //0=human; 1=ai; 2=server
	Thread worker;
	
	//Updates display on status page. Index specifies which label should be updated (one is used for status; other shows IP if running as server)
	public void stat(String update, int index) {
	
		((JLabel)c4e[index]).setText(update);
	
	}
	
	//Shortcut for above.
	public void stat(String update) { stat(update, 0); }

	//Constructs UI for one page of the interface.
	public void initPanel(JPanel p, JComponent[] pe, String name) {
	
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		for (int i = 0; i < pe.length; i++) {
			if ((!(pe[i] instanceof JLabel))||(pe==c4e)) p.add(Box.createGlue());
			p.add(pe[i]);
			pe[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			if (pe[i] instanceof JButton) ((JButton)pe[i]).addActionListener(this);
		}
		p.add(Box.createGlue());
		ca.add(p, name);
	
	}

	//Initializes all UI stuff.
	public Launcher() {
	
		setTitle(version);
		setLocation(250, 250);
		setSize(350, 350);
		//setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ca = getContentPane();
		lay = new CardLayout();
		ca.setLayout(lay);
		
		initPanel(c1, c1e, "choose mode");
		initPanel(c2, c2e, "client details");
		initPanel(c3, c3e, "server details");
		initPanel(c4, c4e, "status");
		
		setVisible(true);
	
	}
	
	//Creates a new Launcher window.
	public static void main(String[] args) {
	
		win = new Launcher();
	
	}
	
	//After all configuration information is filled out, this is called to actually start the game in the desired mode.
	public void doStart() {
	
		lay.show(ca, "status");
		
		if (mode<=1) { //client
		
			String name = ((LabeledField)c2e[0]).getText();
			String server = ((LabeledField)c2e[1]).getText();
			String port = ((LabeledField)c2e[2]).getText();
			final String[] args = { name, server, port }; //necessary to be final in order to be accessed from anonymous inner class
			
			System.out.println("Mode=" + mode);
			
			if (mode==0) { //human client
			
				stat("Connecting...");
				
				worker = new Thread() {
					public void run() {
						GraphicalClient.main(args);
					}
				};
				worker.start();
				
				stat("Connected... Closing this window.");
				setVisible(false);
			
			} else if (mode==1) { //ai client
			
				stat("Connecting...");
				
				worker = new Thread() {
					public void run() {
						StupidAI.main(args);
					}
				};
				worker.start();
				
				stat("Connected as AI...");
			
			}
		
		} else if (mode==2) { //server
		
			String endCond = ((LabeledField)c3e[0]).getText();
			String port = ((LabeledField)c3e[2]).getText();
			final String[] args = { port, endCond }; //necessary to be final in order to be accessed from anonymous inner class
			
			stat("Starting server...");
			
			worker = new Thread() {
				public void run() {
					HeartsServer.main(args);
				}
			};
			worker.start();
			
			String host = "localhost";
			try {
				InetAddress addr = InetAddress.getLocalHost();
				host = addr.toString();
			} catch (Exception e) {}
			
			stat("Server started.");
			stat(host + ":" + port, 1);
		
		}
	
	}
	
	//Handles all button clicks, validates text input, and performs the appropriate action.
	public void actionPerformed(ActionEvent event) {
	
		String src = ((JButton)event.getSource()).getText();
		
		if (src.indexOf("Join Game as")>=0) {
		
			if (src.indexOf("Human")>=0) mode = 0;
			else mode = 1;
			
			lay.show(ca, "client details");
			return;
		
		}
		
		if (src.indexOf("Host")>=0) {
		
			mode = 2;
			lay.show(ca, "server details");
			return;
		
		}
		
		if (mode <= 1) { //client
		
			boolean invalid = false;
			
			for (int i = 0; i < c2e.length; i++) {
			
				if (!(c2e[i] instanceof LabeledField)) continue;
				
				String val = ((LabeledField)c2e[i]).getText();
				if (val==null) { invalid = true; break; }
				if (val.equals("")) { invalid = true; break; }
				
				if (i==2) {
					int port = -1;
					try {
						port = Integer.parseInt(val);
					} catch (Exception e) { invalid = true; break; }
					if ((port<1)||(port>65535)) { invalid = true; break; }
				}
				
			}
			
			if (invalid) {
			
				alert("One or more fields has an invalid value. Please correct.");
				return;
			
			}
		
		} else if (mode == 2) { //server
		
			boolean invalid = false;
			
			for (int i = 0; i < c3e.length; i++) {
			
				if (!(c3e[i] instanceof LabeledField)) continue;
				
				String val = ((LabeledField)c3e[i]).getText();
				
				int valInt = -1;
				try {
					valInt = Integer.parseInt(val);
				} catch (Exception e) { invalid = true; break; }
				
				if (valInt<0) { invalid = true; break; }
				
				if ((i==2)&&((valInt<1)||(valInt>65535))) { invalid = true; break; }
			
			}
			
			if (invalid) {
			
				alert("One or more fields has an invalid value. Please correct.");
				return;
			
			}
		
		}
		
		doStart();
	
	}
	
	//Displays an alert box
	public void alert(String msg) {
	
		UIWindow.alert(this, msg);
	
	}

}