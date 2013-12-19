/***
 ***   Matt Broussard
 ***   4th Period Computer Science I
 ***   Assignment: Project: Hearts
 ***   Assignment URL: http://cs.leanderisd.org/current/cs2/progs/project-hearts.html
 ***   Date: February 26, 2011
 ***   Revision: 2.0 beta
 ***/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

//UI Code for graphical client.
public class UIWindow extends JFrame implements MouseListener, KeyListener {

	JPanel chatPanel = new JPanel();
	JTextField chatField = new JTextField("");
	JEditorPane chatConvo = new JEditorPane("text/html", "");
	JScrollPane chatScroller = new JScrollPane(chatConvo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	FeltPanel table = new FeltPanel();
	JLabel instructionsLabel = new JLabel("Please choose 3 cards to pass.", JLabel.CENTER);
	JButton actionButton = new JButton("Pass");

	//CSS that controls the styling of text in the chat box.
	String[] initialCSS = {
		".update { border: 1px solid red; background: green; }",
		".sender { font-weight: bold; }",
		".message, .system { display: block; border-bottom: 1px dotted black; padding-bottom: 3px; }",
		".system .sender { color: #ff0000; }",
		".system { border-bottom: 1px dotted red; }",
		"#not_started { background-color: #ffaaaa; border: 1px solid red; padding: 5px; color: black; font-style: italic; }"
	};
	
	int[] trickX = { 90, 180, 270, 360 }; //huh? How did that happen? Strange coincidence.
	int trickY = 50;
	
	ArrayList<UICard> cards = new ArrayList<UICard>();
	UICard[] trick = new UICard[4];
	
	boolean isPlaying = false;
	boolean isPassing = false;
	boolean chatOperational = false;
	
	JLabel[] trickLabels = {
		new JLabel("", JLabel.CENTER),
		new JLabel("", JLabel.CENTER),
		new JLabel("", JLabel.CENTER),
		new JLabel("", JLabel.CENTER)
	};
	
	Container ca;

	//Like String.split() but splits the String only once, at the first instance of the split character. Used for formatting chat messages from the server.
	public String[] splitAtFirst(String str, String split) {
	
		String[] tbr = { str, str };
		int index = str.indexOf(split);
		if (index<0) return tbr;
		tbr[0] = str.substring(0, index);
		tbr[1] = str.substring(index + split.length());
		return tbr;
	
	}
	
	//Adds a CSS rule to the chat box.
	public void addChatCSS(String rule) {
	
		((HTMLEditorKit)chatConvo.getEditorKit()).getStyleSheet().addRule(rule);
	
	}
	
	//Appends a chat message or status update to the chat box and autoscrolls to the bottom.
	public void appendChat(String html) {
	
		if (!chatOperational) {
			chatOperational = true;
			chatConvo.setText("<span></span>"); //a hack to fix an inexplicable behavior
		}
		
		String[] oldText = splitAtFirst(chatConvo.getText(), "</body>");
		chatConvo.setText(oldText[0] + html + oldText[1]);
		chatConvo.setCaretPosition(chatConvo.getDocument().getLength() - 1);
	
	}
	
	//Appends a status update to the chat box.
	public void statusUpdate(String update) {
	
		appendChat("<pre class='update'>" + update + "</pre>");
	
	};
	
	//Updates UI so user can choose a card to play.
	public void play(Deck legal) {
	
		for (int i = 0; i < cards.size(); i++) {
		
			Card c = cards.get(i).getCard();
			if (legal.findCard(c)>=0) cards.get(i).setRaised(true);
		
		}
		
		isPlaying = true;
	
	}
	
	//Animates hand cards into their correct position after a card is played.
	public void handUpdate() {
	
		for (int i = 0; i < cards.size(); i++) {
			
			UICard c = cards.get(i);
			
			int[] loc = getCardLocation(i, cards.size());
			c.animateX = loc[0];
			c.animateY = loc[1];
			
			Animator.animateCard(c, 50);
		
		}
	
	}
	
	//Updates hand display with new cards from server.
	public void handUpdate(Deck hand) {
	
		hand.sort();
		
		for (int i = cards.size() - 1; i >= 0; i--) {
		
			UICard c = cards.get(i);
			c.setVisible(false);
			table.remove(c);
			cards.remove(i);
		
		}
		
		for (int i = hand.length() - 1; i >= 0; i--) {
		
			UICard c = new UICard(hand.cardAt(i));
			cards.add(0, c);
			table.add(c);
			int[] loc = getCardLocation(i, hand.length());
			c.setLocation(loc[0], loc[1]);
			c.addMouseListener(this);
		
		}
	
	}
	
	//Updates UI so user can choose cards to pass.
	public void startPassing() {
	
		isPassing = true;
		instructionsLabel.setVisible(true);
		actionButton.setVisible(true);
	
	}
	
	//Updates the cards displayed in the trick area.
	public void trickUpdate(Deck newTrick, String[] names) {
	
		if (newTrick.length()<=0) {
		
			try { Thread.sleep(750); } catch (Exception e) {}
			
			for (int i = 0; i < trick.length; i++) {
				trickLabels[i].setText(names[i]);
				if (trick[i]==null) continue;
				trick[i].setVisible(false);
				table.remove(trick[i]);
				trick[i] = null;
			}
			
			return;
		
		}
		
		for (int i = 0; i < 4; i++) {
		
			trickLabels[i].setText(names[i]);
			
			if (trick[i]!=null) {
			
				if (trick[i].getCard().equals(newTrick.cardAt(i))) continue;
				
				trick[i].setVisible(false);
				table.remove(trick[i]);
				trick[i] = null;
			}
			
			if (newTrick.length()>i) {
				trick[i] = new UICard(newTrick.cardAt(i));
				table.add(trick[i]);
				trick[i].setLocation(trickX[i], trickY);
			}
		
		}
	
	}
	
	//Formats and displays incoming chat messages.
	public void receiveChatMessage(String chat) {
	
		String newText = "";
		String[] split = splitAtFirst(chat, ": ");
		newText += "<div class='";
		if (chat.startsWith("[system]:")) newText += "system";
		else newText += "message";
		newText += "'><span class='sender'>" + split[0] + ":</span> ";
		newText += split[1];
		newText += "</div>";
		appendChat(newText);
		System.out.println(newText + "\n\n\norig: \"" + chat + "\"");
	
	}
	
	//Initialize UI
	public UIWindow(String host, int port, String name) {
	
		setTitle("Hearts - \"" + name + "\"@" + host + ":" + port);
		setLocation(250, 250);
		setSize(720, 480);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ca = getContentPane();
		ca.setLayout(new BorderLayout());
		
		//setup chat panel
		ca.add(chatPanel, BorderLayout.EAST);
		chatPanel.setPreferredSize(new Dimension(200, 480));
		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(chatScroller, BorderLayout.CENTER);
		chatPanel.add(chatField, BorderLayout.SOUTH);
		chatConvo.setEditable(false);
		for (int i = 0; i < initialCSS.length; i++) addChatCSS(initialCSS[i]);
		chatConvo.setText("<div id='not_started'>No chat messages may be sent or received until all players are connected. If you try to send a message, it will not be sent until all players are connected.</div>");
		chatField.addKeyListener(this);
		
		//setup table area
		table.setLayout(null);
		table.setBackground(new Color(24, 148, 63));
		table.setPreferredSize(new Dimension(513/*520*/, 480));
		ca.add(table, BorderLayout.WEST);
		Animator.parent = table;
		
		for (int i = 0; i < 4; i++) {
			JLabel l = trickLabels[i];
			table.add(l);
			l.setSize(71, 20);
			l.setLocation(trickX[i], trickY + 112);
			l.setForeground(Color.WHITE);
			//l.setBorder(BorderFactory.createLineBorder(Color.RED));
		}
		
		table.add(instructionsLabel);
		instructionsLabel.setSize(500, 20);
		instructionsLabel.setLocation(10, 240);
		instructionsLabel.setForeground(Color.WHITE);
		instructionsLabel.setVisible(false);
		
		table.add(actionButton);
		actionButton.setSize(70, 25);
		actionButton.setLocation(225, 270);
		actionButton.setVisible(false);
		actionButton.addMouseListener(this);
		
		ca.setComponentZOrder(chatPanel, 0);
		ca.setComponentZOrder(table, 1);
		
		setVisible(true);
	
	}
	
	//Gets the (x,y) coordinate of where a hand card should be placed, given its index and the total number of cards (so cards are centered).
	public static int[] getCardLocation(int index, int number) {
	
		int x = 0;
		int y = 300;
		
		int totalWidth = 71 + ((number - 1) * 20);
		int startX = (520 - totalWidth) / 2;
		
		x = (index * 20) + startX;
		
		int[] tbr = {x, y};
		return tbr;
	
	}
	
	//Animates card from hand to position in trick after being played.
	public void pushCard(UICard c, int trickIndex, Runnable r) {
	
		c.animateX = trickX[trickIndex];
		c.animateY = trickY;
		
		Animator anim = new Animator(c, 250);
		anim.r = r;
		anim.start();
		
		trick[trickIndex] = c;
		cards.remove(c);
		
		handUpdate();
	
	}
	
	//Display an alert box.
	public static void alert(JFrame parent, String msg) {
	
		JOptionPane.showMessageDialog(parent, msg);
	
	}
	
	//Shortcut for above.
	public void alert(String msg) {
	
		UIWindow.alert(this, msg);
	
	}
	
	//Verify 3 cards are selected, then pass them.
	public void finishPass() {
	
		Deck toPass = new Deck();
		
		for (int i = 0; i < cards.size(); i++) {
		
			UICard c = cards.get(i);
			if (!c.isRaised()) continue;
			toPass.add(c.getCard());
		
		}
		
		if (toPass.length()!=3) {
		
			alert("Please select exactly 3 cards to pass.");
			return;
		
		}
		
		outer: for (int i = cards.size() - 1; i >= 0; i--) {
		
			UICard c = cards.get(i);
			
			for (int j = 0; j < 3; j++) {
				if (c.getCard().equals(toPass.cardAt(j))) {
               cards.remove(i);
					c.setVisible(false);
					table.remove(c);
					continue outer;
				}
			}
			
		}
		
		handUpdate();
		
		instructionsLabel.setVisible(false);
		actionButton.setVisible(false);
		isPassing = false;
		GraphicalClient.sendPassedCards(toPass);
	
	}
	
	//Animate a card into place in the trick, then notify the server the card was played.
	public void playCard(UICard c) {
	
		isPlaying = false;
		for (int i = 0; i < cards.size(); i++) cards.get(i).setRaised(false);
		
		int trickIndex = -1;
		for (int i = 0; i < trick.length; i++) {
			if (trick[i]==null) {
				trickIndex = i;
				break;
			}
		}
		
		final Card c2 = c.getCard();
		Runnable r = new Runnable() {
			public void run() {
				GraphicalClient.play(c2);
			}
		};
		
		pushCard(c, trickIndex, r);
		handUpdate();
	
	}
	
	//Handles mouse click events on UICards and buttons.
	public void mouseClicked(MouseEvent event) {
	
		Object source = event.getSource();
		
		if (source instanceof UICard) {
		
			UICard c = (UICard)source;
			
			if (isPassing) {
			
				c.toggleRaised();
			
			} else if (isPlaying) {
			
				if (!c.isRaised()) return;
				playCard(c);
			
			}
		
		} else { //click was from "Pass" button.
		
			finishPass();
		
		}
	
	}
	
	//Handles key pressed in chat field; if enter is pressed, sends the chat.
	public void keyPressed(KeyEvent event) {
	
		if (event.getKeyCode()!=KeyEvent.VK_ENTER) return;
		
		String chat = chatField.getText();
		chatField.setText("");
		if (chat.length()==0) return;
		
		GraphicalClient.sendChatMessage(chat);
	
	}
	
	//Empty methods required to be present in order to implement KeyListener and MouseListener
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	public void mousePressed(MouseEvent event) {}
	public void mouseReleased(MouseEvent event) {}
	public void keyTyped(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}

}