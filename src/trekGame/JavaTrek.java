package trekGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import trekControllers.DamageControl;
import trekControllers.ImpulseControl;
import trekControllers.LRSControl;
import trekControllers.PhaserControl;
import trekControllers.ShieldControl;
import trekControllers.SRSControl;
import trekControllers.StarBaseControl;
import trekControllers.TorpedoControl;
import trekControllers.WarpControl;


/*
 * Project #1 - Java Trek
 * IDE: Eclipse - Version 2019-03 (4.11.0)
 * JDK: JavaSE-1.8
 * 
 * 3rd Party Libraries: none
 * 
 * ----------------------------------------------------------------------------------
 * 2019-07-01-Jon Walker (jlwalker.dev@gmail.com) - original release date
 * 
 * I'm a developer, primarily C#/.Net after years with Visual FoxPro and a bit
 * of JavaScript coding.  I decided to add Java to my resume, so I'm going 
 * to document a number of projects for others like me who want to go from 
 * coding in Java to developing in Java.
 * 
 * What's the difference between coding and developing?  A coder knows how to
 * make a computer do things... a developer can also, but knows why.  Further, a
 * good developer looks for simplicity and creates readable, well documented code.
 * </soapbox>
 * 
 * 
 * This is a remake of the  1970's Star Trek game which I designed from memory.
 * Gene Hilpert and I spent many an hour working on code and playing games 
 * in one of the many Dec terminal sites on the campus of UW Stevens Point, and
 * this one was at the top of our list.  
 * 
 * If you don't know what the original Star Trek game is like, there's lots of good
 * references on the web, and I can only say that I'm sorry you missed so much fun 
 * when people were actually inventing how this stuff should work.
 * 
 * We need to understand what we're doing.  This is essentially a board game, so it's a 
 * turn based model advanced by user interaction.  A real-time action game would require 
 * a different approach.  Nothing happens until the user takes a turn and then we update 
 * everything and wait for the user to take another turn.
 * 
 * I hope to use this and a couple other projects as starting points, for articles about
 * code and design updates, with evolution of them as I become more proficient at developing 
 * in Java.  Since there is no single "right way" to design things, I'll be interested
 * in seeing what you have to say and offer as ideas.
 * 
 * Original Design
 * =================================
 * Each 10 x 10 grid is randomly set up each time you enter
 * a sector.  It will always hold the correct number of objects
 * based on the game board object.
 * 
 * The pieces are very similar to the original game.
 * >k< - Klingon 
 *  .  - empty spot on grid
 *  *  - asteroid on grid
 * <S> - Starbase
 * <E> - Enterprise
 * 
 * Original Screen Concept
 *  --------------------------------------------------------------------
 * |    1  2  3  4  5  6  7  8  9 10                 Long Range Sensors |
 * | 1 >k< .  .  .  .  .  .  .  .  .   | WARP  |    ------------------- |
 * | 2   . .  .  .  .  .  .  *  .  .                | 000 | 130 | 010 | |
 * | 3  .  .  .  .  .  .  .  .  .  .   | TORP  |    ------------------- |
 * | 4  .  .  .  .  .  .  .  .  .  .                | 004 | 012 | 001 | |
 * | 5  .  .  *  .  .  .  .  .  .  .   |PHASERS|    ------------------- |
 * | 6  .  .  .  .  .  . <E> .  .  .                | 012 | 000 | 003 | |
 * | 7  .  .  .  .  .  .  .  .  .  .   |SHEILDS|    ------------------- |
 * | 8  .  .  .  .  .  .  .  .  .  .                                    |
 * | 9  .  .  *  .  .  .  .  .  .  .   |  MAP  |     Power ===58%---    |
 * |10  .  .  .  .  .  .  .  .  .  .   | PROBE |                                 |
 * |                                                                    |
 * | Starbase Alpha-6 reports movement in sector 1,6 of Beta Quadrant   |
 * |                                                                    |
 *  --------------------------------------------------------------------
 * 
 * 
 * Places where you've scanned will have accurate numbers.  Starbases will 
 * give you reliable information for nearby grid areas. However, the will
 * only tell you about movement for grid locations further afield and your
 * map will show 010 to indicate that... it will usually be accurate.
 *   
 * You can only scan the surrounding grids while starbases will give you 
 * numbers within their quadrant.   However, starbases only send information 
 * for one grid at a time every now and then.  Since the Delta Quadrant was
 * always a mysterious place, there are no starbases there.
 * 
 *  --------------------------------------------------------------------
 * | Gamma Quadrant                     Delta Quadrant                  |
 * | 000 130 010 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 004 012 001 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 012 000 003 005 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 022 003 010 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 002 000 032 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * |                                                                    |
 * | Alpha Quadrant                     Beta Quadrant                   |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 030 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 124 010 |
 * | 000 000 010 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 103 030 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 * | 000 000 000 000 000 000 000 000    000 000 000 000 000 000 000 000 |
 *  -------------------------------------------------------------------- 
 *
 * Other than the usual controls, there are MAP and PROBE.  Map will bring
 * up a dialog screen with the MyMap information.  Since the game is a bit
 * bigger than the original, we can fire a probe which will go through a
 * number of sectors in whatever direction you set and give accurate LRS 
 * readings.  For each Klingon in a sector it passes through, you'lll have 
 * a 10% chance of losing the probe.
 * 
 * Asteroids will block torpedo shots but not phasers.  Phasers fire is split 
 * between all Klingons in the sector.  Torpedos are manually fired based on 
 * the user supplied decimal indicating direction as:
 * 
 *     8   1   2
 *      \  |  /
 *       \ | /
 *   7 --- + --- 3
 *       / | \ 
 *      /  |  \
 *     6   5   4
 * 
 * A >k< can take 80 points of damage and gives up to 80pt, depending
 * on their current health level and distance. Klingons only have
 * beam weapons.
 * 
 * A  * can take 20 points of damage
 * 
 * A <S> can take 100 points of damage
 * 
 * Torpedos deliver 60 - 100 points of damage
 * 
 * Beam weapons deliver less points of damage at greater distance
 * 
 * Warping returns 5 points of power for every 1 sector warp

 * Phasers drain the <E> at 1/10 rate of deliver so 30 drains 3
 *    <E> has enough power for 10 shots of 100 power 
 * 
 * Shields drain .3 for every stardate
 * 
 *    If you lose all power, your core loses containment and you blow
 *    If you regain 1 point of power per stardate
 * 
 * A turn is defined as doing something.  Firing, checking sensors, warping
 * and any other board action that causes a change to the boards.  
 * 
 *
 * Design Notes
 * =================================
 * 
 * Here is a list of things you might want to try doing:
 * 
 * 1) Improve the code 
 * 2) Add some sound, graphics and additional animation
 * 3) Make it a bit more interactive and arcade like
 * 4) Add Klingon battle cruisers <K> that are a bit more
 *    powerful, a little harder to kill, and will slowly move 
 *    towards starbases to blow them up.
 * 5) Add the probe, I think that's a fun idea.  I was going
 *    to limit it to passing through 6 sectors and only carrying
 *    three at a time, but use your judgement.
 * 6) Make it multiuser so up to 4 people can play on a 24x24 grid
 * 
 * 
 * The various parts of the ship, warp drives, impulse engines, etc which you
 * control will be extending the ControllerClass.  This gives each device a
 * common framework and they will all work in a similar fashion.  Any special 
 * code is put into the subclass.
 * 
 * The GameBoard class holds the data for where things are and a few constants
 * need for game play.
 * 
 * The MyMap class holds your known version of the GameBoard and gets filled in
 * as you play the game.
 * 
 * Utilities is a static class that holds small routines that are used throughout 
 * the system.  Without it, we'd be instantiating it over and over, duplicating 
 * code, or sticking the code into places where it really doesn't belong.  We want 
 * to keep things encapsulated and all code relevant to each class.
 * 
 * 
 * The final board has some differences based on my decision for game play.  I 
 * decide to put the coms panel near the navigation and remove the LRS (since we
 * have the map available at all times) and use the bottom area for the MyMap 
 * display.  Now the only time you see a dialog is for user input.
 * 
 * The MyMap display uses a grid layout and some simple foot-work to colorize 
 * cells as needed.
 * 
 * I hope you enjoy looking through this and can get something out of it.  It was 
 * a really fun project that gave me an opportunity to learn new things and helped 
 * me flesh out some ideas and concepts.
 * 
 * ----------------------------------------------------------------------------------
 *
 * TODO
 * -----------------
 * 1) NewGame menu option
 * 2) Save/Load menu options
 * 3) Allow user to pick board size (8, 16, and 24)
 * 
 * 
 * KNOWN ISSUES
 * -----------------
 * 7/1/2019
 * 
 *   From time to time the game threw an exception which typically indicated
 *   a problem during rendering the screen due to a thread issue.  
 *   Ref: https://stackoverflow.com/questions/47491963/exception-in-thread-awt-eventqueue-0-java-lang-arrayindexoutofboundsexception
 *        https://javarevisited.blogspot.com/2013/08/why-swing-is-not-thread-safe-in-java-Swingworker-Event-thread.html#axzz5sQQ90KLJ
 *        https://stackoverflow.com/questions/182316/java-swing-libraries-thread-safety        
 *   
 *   According to the documentation, Swing is not thread-safe and it appears that the issue
 *   is occurring during SRS refresh with the task I'm running to create the animation.  
 *   
 *   I'm investigating making the operation thread safe.  The original code would refresh
 *   the SRS by recreating each label in the grid with the correct text.  This was likely
 *   taking too much time and accessing to many resources.  The easiest fix was to set up 
 *   the SRS similar to the LRS map and have individual labels in an array that get changed 
 *   as needed.  That way only one or two labels change instead of redrawing the entire SRS 
 *   map.  The fix seems to have helped as I haven't seen this happen for quite a while.  
 *   However, I'm not ready to sign off on the fix, either.  When I figure out thread-safe 
 *   swing calls, I'll likely come back to update this project.
 *   
 */

@SuppressWarnings("serial")
public class JavaTrek extends JFrame {
	// set up some constants
	public static final int REDALERT=2;
	public static final int YELLOWALERT=1;
	public static final int GREENALERT=0;
	
	
	// set up the environment
    public GameBoard myBoard=new GameBoard(this,8);
	public GameMap myMap=new GameMap(this);
	public LRSControl lrs=new LRSControl(this);
	public SRSControl srs=new SRSControl(this);
	public WarpControl warp=new WarpControl(this);
	public ImpulseControl impulse=new ImpulseControl(this);
	public PhaserControl phasers=new PhaserControl(this);
	public TorpedoControl torpedoes=new TorpedoControl(this);
	public ShieldControl shields=new ShieldControl(this);
	public StarBaseControl starBases=new StarBaseControl(this);
	public GameObjects gameObjects=new GameObjects(this);
	public DamageControl damageControl=new DamageControl(this);
	
	// form and SRS panel dimensions 
	private final int formHeight=650;
	private final int formWidth=810;
    private final int srsHeight=276;
    private final int srsWidth=348;
    
    // some custom colors for the buttons
    private final Color REDISH=new Color(255,200,200);
    private final Color GREENISH=new Color(180,255,180);
    private final Color LTGREEN=new Color(230,255,230);
    private final Color LTYELLOW=new Color(240,245,200);

    // instantiate the panels
    private JPanel srsPanel = new JPanel(new GridLayout(11,12));
    private JPanel controlPanel = new JPanel(null);
    private JPanel consolePanel = new JPanel(new GridLayout(1,2));
    private JPanel mapPanel;// = new JPanel(new GridLayout(GameBoard.BOARDSIZE+2,GameBoard.BOARDSIZE+2));
    
    // and some labels to update the user
    private JLabel lblDate=new JLabel("3421.0");
    private JLabel lblAlert=new JLabel("Green");
    private JLabel lblEnergy=new JLabel("100%");
    private JLabel lblQuadrant=new JLabel("Updating...");
    private JLabel lblSector=new JLabel("Updating...");
    private JLabel lblKlingons=new JLabel("");
    
    private JLabel lblImpulse=new JLabel("");
    private JLabel lblWarp=new JLabel("");
    private JLabel lblPhaser=new JLabel("");
    private JLabel lblTorpedo=new JLabel("");
    private JLabel lblTorpedoCount=new JLabel("");
    private JLabel lblShield=new JLabel("");
    private JLabel lblLRS=new JLabel("");
    
    // and the coms text area
    private JTextArea coms = new JTextArea();

    // and the buttons
    private JButton btnImpulse=new JButton();
    private JButton btnWarp=new JButton();
    private JButton btnPhaser=new JButton();
    private JButton btnTorpedo=new JButton();
    private JButton btnShield=new JButton();
    private JButton btnLRS=new JButton();
    private JButton btnDamage=new JButton();
    private JButton btnDP=new JButton();

    // and finally some variables
    private Boolean docked = false;
    private Boolean showingEnterpriseMovement=false;
    private Boolean showingEnemyMovement=false;
    private int alertLevel=0;
    private double lastStarDate=0;
    private double energyLevel=100.0;

    private long tickTock=0;
    private Calendar lastTick=Calendar.getInstance(); // trap for the timer
    private Timer timer;
    private Boolean inTimer=false; // making sure we don't overrun the clock
    
    /* 
     * Constructor to for the class.  When called, the
     * game begins.
     */
    public JavaTrek() {
		Utilities.writeToLog(" Game Start",true);
    	createTimerTask();

		JMenuBar menuBar=new GameMenu(menuActionListener);
		this.setJMenuBar(menuBar);

        // Set up the game board and map
        mapPanel = new JPanel(new GridLayout(GameBoard.MAPGRIDSIZE,GameBoard.MAPGRIDSIZE));

		// set up the frame
		this.setLayout(new GridLayout(2,1));
		this.setTitle("Super Duper Star Trek");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(10, 10, formWidth, formHeight); // start location on screen (x,y) and size (x,y)
		this.setMinimumSize(new Dimension(formWidth, formHeight));
		this.setResizable(false);

		// set up the SRS panel
        srsPanel.setSize(new Dimension(srsWidth,srsHeight));
        srsPanel.setMaximumSize(new Dimension(srsWidth,srsHeight));
        srsPanel.setMinimumSize(new Dimension(srsWidth,srsHeight));
        srsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        srsPanel.setBounds(0,0,srsWidth,srsHeight);

        // set up the control panel
        controlPanel.setSize(new Dimension(srsWidth,srsHeight));
        controlPanel.setMaximumSize(new Dimension(srsWidth,srsHeight));
        controlPanel.setMinimumSize(new Dimension(srsWidth,srsHeight));
        controlPanel.setBounds(0,0,srsWidth,srsHeight);

        // CP buttons
        controlPanel.add(newButton(btnImpulse,"IMPL",0,0));
        controlPanel.add(newButton(btnWarp,"WARP",1,0));
        controlPanel.add(newButton(btnPhaser,"PHAS",2,0));
        controlPanel.add(newButton(btnTorpedo,"TORP",3,0));
        controlPanel.add(newButton(btnShield,"SHLD",4,0));
        controlPanel.add(newButtonW(btnDP,"DP",4,5,50));
        controlPanel.add(newButton(btnLRS,"LRS",5,0));
        controlPanel.add(newButton(btnDamage,"DMG",6,0));

        // CP Health Levels
        controlPanel.add(newLabel(lblImpulse,75,0,3));
        controlPanel.add(newLabel(lblWarp,75,1,3));
        controlPanel.add(newLabel(lblPhaser,75,2,3));
        controlPanel.add(newLabel(lblTorpedo,75,3,3));
        controlPanel.add(newLabel(lblTorpedoCount,50,3,5));
        controlPanel.add(newLabel(lblShield,75,4,3));
        controlPanel.add(newLabel(lblLRS,75,5,3));

        // CP status area
        controlPanel.add(newLabel("Stardate:",100,0,9));
        controlPanel.add(newLabel("Alert Level:",100,1,9));
        controlPanel.add(newLabel("Energy:",100,2,9));
        controlPanel.add(newLabel("Quadrant:",100,3,9));
        controlPanel.add(newLabel("Sector:",100,4,9));
        controlPanel.add(newLabel("Klingons:",100,5,9));
        
        controlPanel.add(newLabel(lblDate,100,0,12));
        controlPanel.add(newLabel(lblAlert,100,1,12));
        controlPanel.add(newLabel(lblEnergy,100,2,12));
        controlPanel.add(newLabel(lblQuadrant,100,3,12));
        controlPanel.add(newLabel(lblSector,100,4,12));
        controlPanel.add(newLabel(lblKlingons,100,5,12));

        // CP coms display
        coms.setEnabled(false);
        coms.setLineWrap(true);
        coms.setWrapStyleWord(true);
        coms.setOpaque(true);
        coms.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        coms.setDisabledTextColor(Color.BLACK);
        comsChatter("Welcome Captain!");
        
        JScrollPane sp=new JScrollPane(coms);
        sp.setBounds(15,145,370,140); 
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        controlPanel.add(sp);

        // add the SRS and control panel to the console
        consolePanel.add(this.srsPanel);
        consolePanel.add(this.controlPanel);

		// Add the starbases to the controller
		for(int i=0;i<GameBoard.BOARDSIZE;i++) {
			for(int j=0;j<GameBoard.BOARDSIZE;j++) {
				if(myBoard.getGameBoard(i,j)>99) {
					starBases.addStarbase(i, j);
				}
			}
		}

        // Add the console 
        this.add(consolePanel);

        // Add the user's map
        setMapPanel();
        this.add(mapPanel);
        myBoard.randomEnterpriseLocation();
        this.srs.execute();
		setSRSPanel();
        
        // update the display and set up the hotkeys
        setCondition();
        setHotKeys();

        this.setVisible(true);
        this.setLocationRelativeTo(null); 
    }


    /*
     * Adjust the ship's energy level
     */
    public void adjustEnergy(double value) {
    	this.energyLevel=(this.energyLevel+value>100?100:this.energyLevel+value);
    }
    
    public double getEnergyLevel() {
    	return this.energyLevel;
    }

    
    /*
     * Set everything up when docking/undocking
     */
	public void setDocked(Boolean docked) {
		Utilities.writeToLog("Docked="+docked);
		
		if(this.docked && docked==false) {
			shields.docked();  // raise shields if lowered during docking
		}
		
		this.docked=docked;

		if(docked) {
			srs.docked();
			lrs.docked();
			phasers.docked();
			torpedoes.docked();
			shields.docked();
			impulse.docked();
			this.energyLevel=100;
			comsChatter("You've docked with the starbase.");
		}
	}
	

	public Boolean isDocked() {
		return this.docked;
	}

	
	/*
	 * Add com traffic to the coms box for user review
	 */
	public void comsChatter(String text) {
		Utilities.writeToLog("comsChatter: "+text);

		String sd=(this.lastStarDate==myBoard.currentStarDate()?"":String.format("%.1f: ",myBoard.currentStarDate()));
		this.lastStarDate=myBoard.currentStarDate();
		coms.setText(coms.getText()+(coms.getText().length()>0?"\r\n":"")+sd+text);
		coms.setCaretPosition(coms.getText().length());
 	}

	
	
	public int getAlertLevel() {
		return this.alertLevel;
	}

	
	/*
	 * Pretty early on in testing, I realized that nobody would play this
	 * game through to the end.  The usability was terrible as you had to use
	 * a mouse to do everything.  In other languages it's really simple
	 * to stick a hotkey onto a button, but Java maintains its philosophy 
	 * where such events must be defined in a similar way and maintains a
	 * consistent framework throughout.
	 * 
	 * Thus we need to set up an action handler and assign keystrokes
	 * to the buttons.  It's not as simple, but it is very structured
	 * and allows for a lot of control that you don't easily find in 
	 * other languages.
	 * 
	 * Another issue is the idea of a 16x16 or 24x24 board.  That's a lot of
	 * Klingons to take out in one sitting.  I'm sticking with the traditional 
	 * board size of 8x8 unless it's made into a multiplayer game.  To make
	 * it use the 16x16 or 24x24 grids, just initialize myBoard with values 16 or 24. 
	 * 
	 */
	private void setHotKeys() {
		// set up the action handler
		Action action=new AbstractAction("d") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JButton source=(JButton) arg0.getSource();
				String btn = source.getText();
				
				// call the routine to handle button actions
				executeButtonRequest(btn);
			}
		};
		
		// Torpedos - Ctrl T
		action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("control T"));
		btnTorpedo.getActionMap().put("torp", action);
		btnTorpedo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),"torp");

		// Phasers - Ctrl P
		action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("control P"));
	    btnPhaser.getActionMap().put("phas", action);
		btnPhaser.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),"phas");

		// Warp - Ctrl W
		action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("control W"));
		btnWarp.getActionMap().put("warp", action);
		btnWarp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),"warp");
		
		// Shields - Ctrl S
		action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("control S"));
		btnShield.getActionMap().put("shld", action);
		btnShield.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),"shld");

		// Impulse - Ctrl I
		action.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke("control I"));
		btnImpulse.getActionMap().put("impl", action);
		btnImpulse.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY),"impl");
	}
	
	
	/*
	 * Return the quadrant and sector
	 */
	private String getLocationInfo() {
		String[] info=myBoard.getLocationInfo();
		this.lblQuadrant.setText(info[0]);
		this.lblSector.setText(info[1]);
		return "sector "+info[1]+" of the "+info[0]+" quadrant.";
	}
	
	
	/*
	 * update the status display on the control panel
	 */
	private void setCondition() {
		int k=0;

		k = myBoard.getGameBoard()%100/10;
		
		this.setDocked(srs.areWeDocked());
		
		if(k>0) {
			this.alertLevel=REDALERT;
		}
		else {
			//energy <25% or any significant damage is yellow or shields up
			if(this.energyLevel<25 || shields.areUp()) {
				this.alertLevel=YELLOWALERT;
			}
			else {
				this.alertLevel=GREENALERT;
			}
		}
	
		switch(this.alertLevel) {
			case REDALERT:
				this.lblAlert.setText("RED");
				Utilities.writeToLog("    Alert=RED");
				break;
			case YELLOWALERT:
				this.lblAlert.setText("YELLOW");
				Utilities.writeToLog("    Alert=YELLOW");
				break;
			default:
				this.lblAlert.setText("GREEN");
				Utilities.writeToLog("    Alert=GREEN");
		}
		
		this.lblDate.setText(String.format("%.1f (%.1f)", this.myBoard.currentStarDate(),this.myBoard.timeLeft()));
		this.lblEnergy.setText(String.format("%.1f%%",this.energyLevel));

		// final updates to screen
		starBases.execute();
		
		// update button colors
		btnImpulse.setBackground((impulse.isHealthy()?LTGREEN:REDISH));
		btnWarp.setBackground((warp.isHealthy()?LTGREEN:REDISH));
		btnTorpedo.setBackground((torpedoes.isHealthy()?(torpedoes.getCurrentCount()>2?LTGREEN:LTYELLOW):REDISH));
		btnPhaser.setBackground((phasers.isHealthy()?LTGREEN:REDISH));
		btnLRS.setBackground((lrs.isHealthy()?GREENISH:REDISH));

		// Shields turn yell when 40% or under
		if(this.alertLevel!=REDALERT) {
			if (shields.healthPercent()>40)
				btnShield.setBackground(shields.areUp()?GREENISH:LTGREEN);
			else
				btnShield.setBackground((shields.isHealthy()?(shields.areUp()?LTGREEN:LTYELLOW):REDISH)); 
		}
		else {
			if (shields.healthPercent()>40)
				btnShield.setBackground(shields.areUp()?GREENISH:REDISH);
			else
				btnShield.setBackground((shields.isHealthy() && shields.areUp()?GREENISH:REDISH));
		}
		
		damageControl.execute();
		getLocationInfo();
		lrs.execute();
	

		lblImpulse.setText(impulse.healthPercent()+"%");
		lblWarp.setText(warp.healthPercent()+"%");
		lblPhaser.setText(phasers.healthPercent()+"%");
		lblTorpedo.setText(torpedoes.healthPercent()+"%");
		lblTorpedoCount.setText("("+torpedoes.getCurrentCount()+" left)");
		lblShield.setText(shields.healthPercent()+"%");
		lblLRS.setText(lrs.healthPercent()+"%");
		lblKlingons.setText(""+myBoard.getKlingonCount());
		
		Utilities.writeToLog("    starDate="+myBoard.currentStarDate()+"  energy="+this.energyLevel);

		if(myBoard.getKlingonCount()<1) {
			Utilities.writeToLog("WE WON!");
			Utilities.gameEnd(Utilities.GameEndType.FOR_THE_WIN);
			System.exit(0);
		}
		
		if(myBoard.timeLeft()<0) {
			Utilities.writeToLog("Out of Time");
			Utilities.gameEnd(Utilities.GameEndType.OUT_OF_TIME);
			System.exit(0);
		}

		if(this.energyLevel<.1) {
			Utilities.writeToLog("Out of Energy");
			Utilities.gameEnd(Utilities.GameEndType.OUT_OF_ENERGY);
			System.exit(0);
		}
		
		if(this.alertLevel==REDALERT) {
			if(impulse.isHealthy() || warp.isHealthy() || phasers.isHealthy() 
					|| (torpedoes.isHealthy() && torpedoes.getCurrentCount()>0)) {
				// we're still in the game!!
			}
			else {
				Utilities.writeToLog("No longer able to fight");
				Utilities.gameEnd(Utilities.GameEndType.DESTROYED);
				System.exit(0);
			}
		}
	}
	
	
	/*
	 * Set up a button and tie in the action listener
	 */
    private JButton newButton(JButton jb, String text, int line, int col) {
    	return newButtonW(jb,text,line,col,75);
    }

	/*
	 * Set up a smaller button and tie in the action listener
	 */
    private JButton newButtonW(JButton jb, String text, int line, int col, int width) {
    	jb.setText(text);
    	int l=line*20+5;
    	int c=col*25+15;
    	jb.setName(text);
    	jb.setBounds(c,l,width,17);
    	jb.addActionListener(buttonListener);
    	return jb;
    }

    
    /*
     * Menu action listener
     */
	ActionListener menuActionListener = (new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd=arg0.getActionCommand();
			menuAction(cmd);
		}
	});

	
	/*
	 * Menu action processor
	 */
	private void menuAction(String cmd) {
		switch(cmd.toLowerCase().substring(0,3)) {
			case "ins":
				Utilities.Instructions(this);
				break;
				
			case "tar":
				String a=Utilities.courseCalculation(this,SRSControl.SRSGRIDSIZE,srs.getMyRow(),srs.getMyCol());
				if(a.length()>0) comsChatter("Targeting computer returned: "+a);
				break;
				
			case "war":
				int row=myBoard.getMyLocation()/GameBoard.BOARDSIZE;
				int col=myBoard.getMyLocation()%GameBoard.BOARDSIZE;
				String b=Utilities.courseCalculation(this,GameBoard.BOARDSIZE,row,col);
				if(b.length()>0) comsChatter("Navigation computer returned: "+b);
				break;
				
			case "exi":
				System.exit(0);
				break;
			
		}
	}
    
    
    /*
     * Set up the button listener
     */
	private ActionListener buttonListener= (new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			buttonAction(arg0);
		}
	});

	
	/*
	 *  This is where the button action lands, so we
	 *  figure out which button was pressed and pass
	 *  the string on to the routine to execute it.
	 *  
	 */
	private void buttonAction(ActionEvent arg0) {
		String btn = arg0.getActionCommand();
		executeButtonRequest(btn);
	}
	
	
	/*
	 *  This is where we make the calls to do the work based on what 
	 *  button was pressed.  Both the button action and keyboard short 
	 *  cut handler send their requests here.
	 *  
	 */
	private void executeButtonRequest(String btn) {
		boolean tookTurn=false;

		Utilities.writeToLog(Utilities.EOL+"executeButtonRequest="+btn);

		// check the timer trap, time adjust can go either way
		// so get the absolute value of the difference
		long timerTrap=Math.abs(Calendar.getInstance().getTimeInMillis()-lastTick.getTimeInMillis());
		
		if(timerTrap>1000) {
			// sprung, restart the timer
			timer.cancel();
			timer.purge();
			this.createTimerTask();
		}

		if(this.showingEnterpriseMovement || this.showingEnemyMovement) {
			// buttons are disabled
			Utilities.writeToLog("    buttons disabled - tickTock="+tickTock);
			Utilities.writeToLog("    showingEnterpriseMovement="+this.showingEnterpriseMovement);
			Utilities.writeToLog("    showingEnemyMovement="+this.showingEnemyMovement);
		}
		else {
			try {
				switch(btn.toLowerCase()) {
					case "impl":
						setDocked(false);
						tookTurn=impulse.execute();
						break;
						
					case "warp":
						setDocked(false);
						tookTurn=warp.execute();

						// reset SRS only if we've moved
						if(tookTurn) {
							srs.execute();
							//refreshSRSPanel();
							lrs.execute();
						}
						
						break;
						
					case "phas":
						if(docked) {
							comsChatter("You can't fire weapons while docked with the starbase!");
						}
						else {
							tookTurn=phasers.execute();
						}
						break;
		
					case "torp":
						if(docked) {
							comsChatter("You can't fire weapons while docked with the starbase!");
						}
						else {
							if(torpedoes.getCurrentCount()<1) {
								comsChatter("You're out of torpedoes!");
							}
							else {
								tookTurn=torpedoes.execute();
							}
						}
						break;
						
					case "shld":
						shields.execute();
						break;
						
					case "dp":
						shields.divertPower();
						break;
						
					case "dmg":
						if(this.alertLevel!=REDALERT) {
							damageControl.fixAllDamage();
						}
						else {
							comsChatter("Sorry, damage control crews are busy");
						}
						break;
						
					default:
				}
			}
			catch (Exception ex) {
				Utilities.writeToLog("executeButtonRequest ERROR - "+ex.getMessage());
			}
			
			
			// after button action processing
	        //btnShield.setBackground((shields.areUp()?GREENISH:(alertLevel>GREENALERT?REDISH:LTGREEN)));
			if(tookTurn) {
				this.showingEnterpriseMovement=true;
			}
			else {
				setCondition();
			}
		}
	}
    

	/*
	 * helper to set up a label
	 * 
	 */
    private JLabel newLabel(JLabel jl, int width, int line, int col) {
    	int l=line*20+5;
    	int c=col*25+15;
    	jl.setBounds(c,l,width,20);
    	return jl;
    }


    /*
     * Set up an anonymous label
     * 
     */
    private JLabel newLabel(String text, int width, int line, int col) {
    	JLabel jl=new JLabel(text);
    	int l=line*20+5;
    	int c=col*25+15;
    	jl.setBounds(c,l,width,20);
    	return jl;
    }


    /*
     * Set up the SRS map.  Originally, this routine simply repainted the entire 
     * map each time.  When I started running into thread exception errors, I found
     * that Swing is not thread safe and if you have a thread taking a lot of time
     * repainting the screen, you need to take steps to make it play nice with the
     * other threads.
     * 
     * I've ordered the books to research that for later projects, but the simplest
     * solution was to set up a JLabel array similarly as in the setMapPanel().  Then
     * by altering the SRS controller to just update the correct label when something 
     * changes, it takes much less time and we're back to being psuedo-thread-safe.
     * 
     */
    private void setSRSPanel() {
    	int srsGridSize=SRSControl.SRSGRIDSIZE;
    	
		Utilities.writeToLog("refreshSRS");
		
		srsPanel.removeAll();
		
		srsPanel.add(new JLabel("",JLabel.CENTER));
		for (int i=0;i<srsGridSize;i++) {
            JLabel a0=new JLabel(""+(i+1),JLabel.CENTER);
            srsPanel.add(a0);
        }
		srsPanel.add(new JLabel("",JLabel.CENTER));
        
        for (int i=0;i<srsGridSize;i++) {
            JLabel a0 = new JLabel(""+(i+1),JLabel.CENTER);
            srsPanel.add(a0);

            for (int j=0;j<srsGridSize;j++) {
            	srsPanel.add(srs.getSRSLabel(i, j));
            }
            srsPanel.add(new JLabel(" ",JLabel.CENTER));
        }

        srsPanel.revalidate();
        srsPanel.repaint();
        
        Utilities.writeToLog("leaving refreshSRS");

    }

    
    /*
     * Set up the map panel
     * 
     * TODO - if >8x8 then break into quadrants
     * 
     */
    private void setMapPanel() {
    	String tmp;
    	int m;
    	int halfway=(GameBoard.BOARDSIZE==8?8:GameBoard.BOARDSIZE/2);
    	
    	Utilities.writeToLog("setMapPanel");
    			
    	// clear the map
    	mapPanel.removeAll();
    	
    	// top row of numbers
		mapPanel.add(new JLabel(""));

		for(int i=0;i<GameBoard.BOARDSIZE;i++) {
			// blank column between quadrants if bigger than 8x8
			if(i%halfway==0 && i>0)
				mapPanel.add(new JLabel(""));
			
    		mapPanel.add(new JLabel(""+(i+1),JLabel.CENTER));
    	}

		// body of map
		for(int i=0;i<GameBoard.BOARDSIZE;i++) {
			// blank row halfway through
			if(i%halfway==0 && i>0) {
				for(int j=0;j<GameBoard.MAPGRIDSIZE;j++) {
					mapPanel.add(new JLabel(""));
				}
			}

			// row label
    		mapPanel.add(new JLabel(""+(i+1),JLabel.CENTER));

    		for(int j=0;j<GameBoard.BOARDSIZE;j++) {
    			// blank column halfway through
    			if (j%halfway==0 && j>0)
    				mapPanel.add(new JLabel(""));

    			// what's in the box?
    			m=this.myMap.getMyMap(i,j);
    			if (m<0) 
    				tmp="---"; // unknown
    			else
    				tmp=String.format("%03d",m);

    			// fill it it
    			myMap.setMapLabels(i,j,new JLabel(tmp,JLabel.CENTER));
    			mapPanel.add(myMap.getMapLabels(i,j));
    		}
    	}
		
		//mapPanel.revalidate();
	}


	/*
	 *  routine to process move by the Enterprise and update the SRS display
	 */
	private String showObjectMovement() {
		String action=gameObjects.showObjectMovement();
		//refreshSRSPanel();
		return 	action;
	}
	
	
	/*
	 * routine to process enemy moves and update the SRS display
	 */
	private String showTakingFire() {
		String action=gameObjects.showObjectMovement();
		//refreshSRSPanel();
		return 	action;
	}
	
	
	/*
	 * what to do after the last Enterprise movement
	 */
	private void updateAfterEnterpriseMovement() {
		Utilities.writeToLog("updateAfterEnterpriseMovement");

		try {
			lrs.execute();
			gameObjects.takingFire();
		}
		catch (Exception ex) {
			Utilities.writeToLog("updateAfterEnterpriseMovement - ERROR "+ex.getMessage());
		}

		Utilities.writeToLog("leaving updateAfterEnterpriseMovement");
	}

	
	/*
	 * what to do after the last enemy movement
	 */
	private void updateAfterEnemyMovement() {
		Utilities.writeToLog("updateAfterEnemyMovement");

		try {
			//Utilities.writeToLog("main.updateAfterEnemyMovement");
			setCondition();
		}
		catch (Exception ex) {
			Utilities.writeToLog("updateAfterEnemyMovement - ERROR "+ex.getMessage());
		}

		Utilities.writeToLog("leaving updateAfterEnemyMovement");
	}

	
	/*
	 * I wanted to add some animation to the game for firing of a torpedo 
	 * or moving the Enterprise.   I coded the creation and execution of a list 
	 * of animation commands which are processed a rate of about 4/second.
	 * 
	 * We have two animation processes: The first being movement/firing by the 
	 * Enterprise and the second being  the response from the Klingons (with 
	 * movement saved for a later version).
	 * 
	 * 06/28/2019 - JW - I moved this routine to a separate method and put a trap
	 * onto it because I am developing in a VirtualBox VM which, for some reason, has
	 * a problem with it's clock.   The clock can get ahead or, more typically, far
	 * behind.  When the auto-adjust kicks in and resets the clock, the timer will 
	 * usually stop working.  By looking for the timer to be off by more than a second
	 * from it's last firing tells me it's time to create a new timer.
	 */
	private void createTimerTask() {
		Utilities.writeToLog(Utilities.EOL+"--------------- Creating timer ---------------");
		timer=new Timer();
        timer.schedule(new TimerTask() {
    		@Override
    		public void run() {
    			tickTock++; // allows me to keep track of which iteration
    			lastTick=Calendar.getInstance();  // used by the trap
    			
    			if(!inTimer && (showingEnterpriseMovement || showingEnemyMovement)) {
    				inTimer=true;

    				if(showingEnterpriseMovement) {
		    			try {
		    				Utilities.writeToLog("EMove - "+tickTock);
		    				showingEnterpriseMovement=(showObjectMovement()!=null);
		    				Utilities.writeToLog("EMove "+showingEnterpriseMovement);
		    			}
		    			catch (Exception ex) {
		    				Utilities.writeToLog("Timer Exception - Enterprise movement - ERROR "+ex.getMessage());
		    				Utilities.writeToLog("    moveObjects size = "+gameObjects.getGameObjects().size());
		    				
		    				gameObjects.clearObjects();
		    				showingEnterpriseMovement=false;
		    			}

		    			if(!showingEnterpriseMovement) {
	    					updateAfterEnterpriseMovement();
	    					showingEnemyMovement=true;
	    				}
	    			}
   					else {
   						try {
   							// show next enemy move until we're done
		    				Utilities.writeToLog("KMove "+tickTock);
   							showingEnemyMovement=(showTakingFire()!=null);
		    				Utilities.writeToLog("KMove "+showingEnemyMovement);
   						}
   		    			catch (Exception ex) {
   							showingEnemyMovement=false;
   							
   		    				Utilities.writeToLog("Timer Exceptions - Enemy movement ERROR "+ex.getMessage());
   		    				Utilities.writeToLog("    moveObjects size = "+gameObjects.getGameObjects().size());
   		    				
   		    				gameObjects.clearObjects();
   		    			}
   						
   						if(!showingEnemyMovement) {
   							updateAfterEnemyMovement();
   						}
   					}

    				inTimer=false;
    			}
    			else {
    				Utilities.writeToLog("Tick "+tickTock+" - no event");
    			}
    		}
        }, 50, 250);  // fire after 50ms and every 25ms thereafter
	}

	
	/*
	 * main to kick off the program
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		JavaTrek game=new JavaTrek();
	}
}
