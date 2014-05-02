package VirtualConf;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.util.*;
import java.text.*;

public class VirtualConf extends JFrame implements WindowListener {
   // The frame consists of a single container and a cardlayout.
    JPanel mainPanel;
    CardLayout card;

    // Individual experiments/simulators:
    ConfSimPanel standard; //grace

    //------------------------------------------------------------------
    // Constructor

    public VirtualConf ()
    {
	this.setTitle ("ConferenceSim");
	this.setBackground (Color.white);
	this.setResizable (true);
	this.setSize (700, 700);

	Container cPane = this.getContentPane();

	JMenuBar menuBar = new JMenuBar ();
	menuBar.add (makeModelMenu());
	this.setJMenuBar (menuBar);

	mainPanel = new JPanel ();
	card = new CardLayout ();
	mainPanel.setLayout (card);
	mainPanel.setOpaque (false);

    standard = new ConfSimPanel ();
	mainPanel.add (standard, "1");
	cPane.add (mainPanel);

	this.setVisible (true);
    }

    JMenu makeModelMenu()
    {
	JMenu modelMenu = new JMenu ("Model");

	JMenuItem m = new JMenuItem ("Conference Simulator: Standard");
	m.addActionListener (
          new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  card.show (mainPanel, "1");
	      }  
	  }
	  );
	modelMenu.add (m);
	
	m = new JMenuItem ("Exit");
	m.addActionListener (
          new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  System.exit (0);
	      }  
	  }
	  );
	modelMenu.add (m);

	return modelMenu;
    }

    // Implementation of WindowListener interface:

    public void windowClosing(WindowEvent e) 
    {
	System.exit(0);
    }

    public void windowClosed(WindowEvent e) 
    {
	System.exit(0);
    }

    // Empty implementations of methods we don't need.
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}


   public static void main (String[] argv)
   {
	   new VirtualConf ();
   }

}
