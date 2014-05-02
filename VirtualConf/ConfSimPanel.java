package VirtualConf;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.util.*;
import java.text.*;
import java.awt.geom.*;
import java.io.FileWriter;
import java.io.IOException;

public class ConfSimPanel extends JPanel {
	ConfSimulator simulator;

    JLabel statusLabel;                  // A bar on top for messages.
    JPanel drawPanel;

    JLabel  sessionLabel, paperLabel, areaLabel;
    JTextField sessionField, paperField, areaField;
    
    int numNodes, numEdges, avgDegree;                // Variables for molecule counts.
    JLabel ALabel, BLabel, CLabel;
    JTextField AField, BField, CField;   // Read in initial concentrations.

    // Parameters.
    JTextField pField; // probability that follow the rule
    ButtonGroup goButton, talkButton; //grace choose rule
    JRadioButton goRand, goSame, goDiff; //grace
    JRadioButton talkRand, talkSame, talkDiff; //grace

    // Time variables (start time = 0).
    int currentTime = 0;
    int endTime = 1;
    JTextField endTimeField;
    JLabel timeLabel;
 
    // How often to record statistics.
    int timeInterval = 1;            
    JTextField intervalField;

    // Animation.
    double speed = 1;                   // Higher number => faster animation.
    JTextField speedField;           // Read in from GUI.
    boolean isStopped = true;        // Use as flag to start/stop animation.
    int moveLength = 5;              // Amount to move each molecule.

    //plotting
    Vector edgePoints;

    // A reset is required for each new simulation.
    boolean resetOccurred = false;
    DecimalFormat df = new DecimalFormat ("###.###");

    //------------------------------------------------------------------
    // Constructor

    public ConfSimPanel ()
    {
	simulator = new ConfSimulator();
	this.setLayout (new BorderLayout());
	// Status bar:
	Border border = BorderFactory.createLineBorder (Color.black);
	statusLabel = new JLabel (" ");
	statusLabel.setBorder (border);
	this.add (statusLabel, BorderLayout.NORTH);

	border = BorderFactory.createLineBorder (Color.black);
	drawPanel = new JPanel ();
	drawPanel.setBorder (border);
	this.add (makeBottomPanel(), BorderLayout.SOUTH);
	this.add (drawPanel, BorderLayout.CENTER);
		simulator.init();
    }

    //------------------------------------------------------------------
    // Screen updates
    
    // Report status messages on screen.
    public void status (String msg)
    {
	statusLabel.setForeground (Color.black);
	statusLabel.setText (msg);
    }

    // Report error messages on screen.
    public void error (String str)
    {
	statusLabel.setForeground (Color.red);
	statusLabel.setText ("  " + str);
    }

    //------------------------------------------------------------------
    // GUI construction

    // The bottom panel is a collection of fields, labels and buttons.

    JPanel makeBottomPanel () 
    {
	JPanel panel = new JPanel ();
	panel.setLayout (new GridLayout (6, 1));

	panel.add (makeDisplayLabels());
	panel.add (makeConfControls());
	panel.add (makeConcControls());
	panel.add (makeParameterControls());
	panel.add (makeSingleRunControls());

	return panel;
    }

    // Display labels for displaying statistics during simulation

    JPanel makeDisplayLabels ()
    {
	JPanel panel = new JPanel ();

	Border border = BorderFactory.createTitledBorder ("  Stats  ");
	panel.setBorder (border);

	panel.setLayout (new GridLayout (1, 5));


	ALabel = new JLabel (" Nodes: ");
	border = BorderFactory.createLineBorder (Color.black);
	ALabel.setBorder (border);
	panel.add (ALabel);

	BLabel = new JLabel (" Edges: ");
	border = BorderFactory.createLineBorder (Color.black);
	BLabel.setBorder (border);
	panel.add (BLabel);

	CLabel = new JLabel (" AvgDegree: ");
	border = BorderFactory.createLineBorder (Color.black);
	CLabel.setBorder (border);
	panel.add (CLabel);

	timeLabel = new JLabel (" Time: ");
	border = BorderFactory.createLineBorder (Color.black);
	timeLabel.setBorder (border);
	panel.add (timeLabel);

	return panel;
    }

 // Read in people number.

    JPanel makeConfControls ()
    {
	JPanel panel = new JPanel ();

	Border border = BorderFactory.createTitledBorder ("  Initial Conference setting  ");
	panel.setBorder (border);
	
	JLabel label = new JLabel ("Session:");
	panel.add (label);
	sessionField = new JTextField (4);
	sessionField.setText ("" + simulator.numSession);
	panel.add (sessionField);

	panel.add (new JLabel ("         "));
	label = new JLabel ("Paper:");
	panel.add (label);
	paperField = new JTextField (4);
	paperField.setText ("" + simulator.numPaper);
	panel.add (paperField);

	panel.add (new JLabel ("          "));
	label = new JLabel ("Area:");
	panel.add (label);
	areaField = new JTextField (4);
	areaField.setText ("" + simulator.numArea);
	panel.add (areaField);

	panel.add (new JLabel ("         "));
	JButton cB = new JButton ("Change");
	cB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  changeInitConf ();
	      }
	  }
        );
	panel.add (cB);

	return panel;
    }
    
    // Read in people number.
    JPanel makeConcControls ()
    {
	JPanel panel = new JPanel ();

	Border border = BorderFactory.createTitledBorder ("  Number of People in different role  ");
	panel.setBorder (border);

	JLabel label = new JLabel ("Attendant:");
	panel.add (label);
	AField = new JTextField (4);
	AField.setText ("" + simulator.attendant);
	panel.add (AField);

	panel.add (new JLabel ("   "));
	label = new JLabel ("Presenter:");
	panel.add (label);
	BField = new JTextField (4);
	BField.setText ("" + simulator.presenter);
	panel.add (BField);

	panel.add (new JLabel ("   "));
	label = new JLabel ("Leader:");
	panel.add (label);
	CField = new JTextField (4);
	CField.setText ("" + simulator.leader);
	panel.add (CField);

	panel.add (new JLabel ("         "));
	JButton cB = new JButton ("Change");
	cB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  changeInitConc ();
	      }
	  }
        );
	panel.add (cB);

	return panel;
    }

    // Read in parameters
    JPanel makeParameterControls ()
    {
	JPanel panel = new JPanel ();

	Border border = BorderFactory.createTitledBorder ("  Rule  ");
	panel.setBorder (border);

	JLabel label = new JLabel ("p:");
	panel.add (label);
	pField = new JTextField (4);
	pField.setText ("" + simulator.prob);
	panel.add (pField);

	panel.add (new JLabel ("         "));
	
	label = new JLabel ("Go:");
	panel.add (label);
	goButton = new ButtonGroup();
	goRand = new JRadioButton("Rand");
	goSame = new JRadioButton("Same");
	goDiff = new JRadioButton("Diff");
	goButton.add(goRand);
	goButton.add(goSame);
	goButton.add(goDiff);
	panel.add (goRand);
	panel.add (goSame); 
	panel.add (goDiff);
	
	panel.add (new JLabel ("         "));
	
	label = new JLabel ("Talk:");
	panel.add (label);
	talkButton = new ButtonGroup();
	talkRand = new JRadioButton("Rand");
	talkSame = new JRadioButton("Same");
	talkDiff = new JRadioButton("Diff");
	talkButton.add(talkRand);
	talkButton.add(talkSame);
	talkButton.add(talkDiff);
	panel.add (talkRand);
	panel.add (talkSame); 
	panel.add (talkDiff);
	
	panel.add (new JLabel ("         "));

	JButton paramB = new JButton ("Change");
	paramB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  changeParams ();
	      }
	  }
        );
	panel.add (paramB);

	return panel;
    }

    // The panel for single-run controls
    JPanel makeSingleRunControls ()
    {
	JPanel panel = new JPanel ();

	Border border = BorderFactory.createTitledBorder ("  Single Run with animation ");
	panel.setBorder (border);

	JButton clearB = new JButton ("Reset");
	clearB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  reset ();
	      }
	  }
        );
	panel.add (clearB);

	JButton nextB = new JButton ("Step");
	nextB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  nextStep ();
	      }
	  }
        );
	panel.add (nextB);

	JButton goB = new JButton ("Go");
	goB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  animate ();
	      }
	  }
        );
	panel.add (goB);

	JButton stopB = new JButton ("Stop");
	stopB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  stop ();
	      }
	  }
        );
	panel.add (stopB);

    JButton viewB = new JButton ("View"); //grace
	viewB.addActionListener (
			  new ActionListener () {
			      public void actionPerformed (ActionEvent a)
			      {
				  viewNetwork (); //To implement
			      }
			  }
		        );
			panel.add (viewB);
    
	panel.add (new JLabel ("    "));
	JLabel label = new JLabel ("Speed:");
	panel.add (label);
	speedField = new JTextField (4);
	speedField.setText ("1.0");
	panel.add (speedField);
	JButton speedB = new JButton ("Change");
	speedB.addActionListener (
	  new ActionListener () {
	      public void actionPerformed (ActionEvent a)
	      {
		  changeSpeed ();
	      }
	  }
        );
	panel.add (speedB);

	return panel;
    }

    //------------------------------------------------------------------
    // GUI actions

    // Read in initial conf setting
    void changeInitConf ()
    {
	try {
		String str = sessionField.getText ();
		int newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0) {
	    	simulator.numSession = newVal;
	    }
	    else {
		error ("Session must be >= 0");
	    }
			
	    str = paperField.getText ();
	    newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0 && newVal >= simulator.paperPerSession * simulator.numSession) {
	    	simulator.numPaper = newVal;
	    }
	    else {
		error ("Paper must be >= paperPerSession * numSession");
	    }

	    str = areaField.getText ();
	    newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0 && newVal >= simulator.areaPerSession * simulator.numSession) {
	    	simulator.numArea = newVal;
	    }
	    else {
		error ("numArea must be >= areaPerSession * numSession");
	    }

	    resetOccurred = false;
	    status ("Changed numbers: Session=" + simulator.numSession + ", Paper=" + simulator.numPaper + ", Area=" + simulator.numArea);
	}
	catch (Exception e) {
	    error ("Improper number in concentration field");
	}
    }

    // Read in initial numbers of people
    void changeInitConc ()
    {
	try {
	    String str = AField.getText ();
	    int newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0) {
	    	simulator.attendant = newVal;
	    }
	    else {
		error ("Attendent must be >= 0");
	    }

	    str = BField.getText ();
	    newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0 && newVal == simulator.numPaper) {
	    	simulator.presenter = newVal;
	    }
	    else {
		error ("Presenter must be = Paper");
	    }

	    str = CField.getText ();
	    newVal = Integer.parseInt (str.trim());
	    if (newVal >= 0) {
	    	simulator.leader = newVal;
	    }
	    else {
		error ("Leader must be >= 0");
	    }
	    simulator.numNodes = simulator.attendant + simulator.presenter +simulator.leader;
	    resetOccurred = false;
	    status ("Changed numbers: Attendant=" + simulator.attendant + ", Presenter=" + simulator.presenter + ", Leader=" + simulator.leader);
	}
	catch (Exception e) {
	    error ("Improper number in initialConf field");
	}
    }

    // Read in parameter values.
    void changeParams ()
    {
	try {
	    String str = pField.getText ();
	    double newVal = Double.parseDouble (str.trim());
	    if ( (newVal >= 0) && (newVal <= 1) ) {
	    	simulator.prob = newVal;
	    }
	    else {
		error ("p must be between 0 and 1");
	    }

	  	if (goRand.isSelected())
	  		simulator.goRandVal = 1;
	  	else
	  		simulator.goRandVal = 0;
	  	if (goSame.isSelected())
	  		simulator.goSameVal = 1;
	  	else
	  		simulator.goSameVal = 0;
	  	if (goDiff.isSelected())
	  		simulator.goDiffVal = 1;
	  	else
	  		simulator.goDiffVal = 0;
	   
	  	if (talkRand.isSelected())
	  		simulator.talkRandVal = 1;
	  	else
	  		simulator.talkRandVal = 0;
	  	if (talkSame.isSelected())
	  		simulator.talkSameVal = 1;
	  	else
	  		simulator.talkSameVal = 0;
	  	if (talkDiff.isSelected())
	  		simulator.talkDiffVal = 1;
	  	else
	  		simulator.talkDiffVal = 0;

	    int newInt = 1;//grace
	    if (newInt >= 1) {
		timeInterval = newInt;
	    }
	    else {
		error ("Obs.Interval must be at least 1");
	    }

	    resetOccurred = false;
	    status ("Changed parameters: p=" + simulator.prob + "   Go: Rand=" + simulator.goRandVal + " ,Same=" + simulator.goSameVal + " ,Diff=" + simulator.goDiffVal
	    		+ "   Talk: Rand=" + simulator.talkRandVal + " ,Same=" + simulator.talkSameVal + " ,Diff=" + simulator.talkDiffVal);
	}
	catch (Exception e) {
	    error ("Improper number in parameter field");
	}
    }

    // Change animation speed.
    void changeSpeed ()
    {
	try {
	    double newSpeed = Double.parseDouble (speedField.getText());
	    if (newSpeed > 0) {
		speed = newSpeed;
	    }
	    status ("Changed speed: " + speed);
	}
	catch (NumberFormatException e) {
	    error ("Improper number in speed field");
	}
    }

    //------------------------------------------------------------------
    // Animation

    void animate ()
    {
	// Start a new thread each time.
	Thread t = new Thread () {
	    public void run ()
	    {
		runAnimation ();
	    }
	};
	t.start ();
    }

    void runAnimation ()
    {
		if (! resetOccurred) {
		    error ("Need to reset before starting animation");
		    return;
		}
	
		isStopped = false;
		while (! isStopped && currentTime < simulator.confTime ) {
		    try {
			int sleepTime = (int) (200.0 / speed);
			Thread.sleep (sleepTime);
		    }
		    catch (Exception e) {
		    }
		    nextStep();
		}
    }

    void stop ()
    {
	isStopped = true;
    }

    //------------------------------------------------------------------
    // Drawing and plotting
    void redraw ()
    {
    	int i;
	Dimension D = drawPanel.getSize ();
	Graphics g = drawPanel.getGraphics ();
	// Blank out draw area.
	g.setColor (Color.white);
	g.fillRect (0, 0, D.width, D.height);
	g.setColor(Color.black);
	g.drawLine(0, simulator.sessionHeight, simulator.confWidth, simulator.sessionHeight);
	drawNodes (simulator.nodes, Color.red);
	for(i=1; i<(simulator.numSession+1)/2; i++) 
		g.drawLine(i*simulator.sessionWidth, 0, i*simulator.sessionWidth, simulator.confHeight);
    }

    void drawNodes (Vector mol, Color color)
    {
		Graphics g = drawPanel.getGraphics ();
		Dimension D = drawPanel.getSize ();
		for (Iterator iter=mol.iterator(); iter.hasNext();) {
		    Node m = (Node) iter.next();
		    int topLeftX = m.x;
		    int topLeftY = m.y;
			g.setColor (m.color);
		    g.fillOval (topLeftX, topLeftY, 2*simulator.radius, 2*simulator.radius);
		}
    }

    void viewNetwork ()
    {
    	//JFrame f = new JFrame("Network View");
    	//f.setSize(600,600);
    	//f.setLocation(700, 0);
    	//f.setVisible(true);
    	generateCsvFile("Network View " + currentTime + ".csv");
    }
    
    void generateCsvFile(String sFileName)
    {
           try
            {
                FileWriter writer = new FileWriter(sFileName);
                writer.write("Source,");
                writer.write("Target,");
                writer.write("Role\n");
                for (int i=0; i<simulator.numNodes; i++){
                    Node n = (Node)simulator.nodes.get(i);
                    for (int j=0; j<n.groupList.size(); j++){
                        writer.write(Integer.toString(n.id));
                        writer.write(',');
                        writer.write(Integer.toString(n.groupList.get(j)));
                        writer.write(',');
                        writer.write(Integer.toString(n.role));
                        writer.write('\n');
                    }
                }
                writer.flush();
                writer.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
    }
    
    
    //------------------------------------------------------------------
    // Simulation: methods common to standard/spatial single run

    // A reset removes past data, initializes the molecule sets.
    void reset ()
    {
	currentTime = 0;
	updateLabels ();
	simulator.init();
	redraw ();
	resetOccurred = true;
    }

    // Each step in a simulation.
    void nextStep ()
    {
	if (! resetOccurred) {
	    error ("Need to reset before animating");
	    return;
	}

	simulator.nextstep();
	if (currentTime % timeInterval == 0) {
	    doStats ();
	}

	currentTime += simulator.talkTime;
	//System.out.println(currentTime);
	redraw ();
    }

    void doStats ()
    {
	updateLabels ();
    }

    // Update counters/labels on GUI.
    void updateLabels ()
    {
	ALabel.setText ("Node: " + df.format(simulator.getNodeNum()));
	BLabel.setText ("Edge: " + df.format(simulator.getEdgeNum()));
	CLabel.setText ("AvgDeg: " + df.format(simulator.calcAvgDegree()));
	timeLabel.setText ("Time: " + currentTime);
    }

}
