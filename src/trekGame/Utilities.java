package trekGame;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import trekControllers.SRSControl;

/*
 * Generic utilities needed for the game
 * 
 */
public class Utilities {
	// enums used to help tailor the prompts and responses
	public static enum ControllerType {WARP,IMPULSE,TORPEDO,PHASER,COMS,SHIELDS};
	public static enum DurationControllerType {WARP,IMPULSE,PHASERS,SHIELDS};
	public static enum CourseControllerType {WARP,IMPULSE,TORPEDO};
	public static enum GameEndType{OUT_OF_TIME,OUT_OF_ENERGY,DESTROYED,FOR_THE_WIN,OUT_OF_THE_FIGHT};
	
	public static final Boolean isWindows=System.getProperty("os.name").toLowerCase().contains("win");
    public static final String EOL=(isWindows?"\r\n":"\n");
	public static final DateFormat USDTSDF = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    public static final DateFormat LOGSDF = new SimpleDateFormat("yyyyMMdd");
	
    
    /*
     * Write a message to a logger file appended with _YYYYMMDD
     * 
     * @param info string being written to log
     */
    public static void writeToLog(String info) {
    	writeToLog(info,"",false);
    }

    /*
     * Write message to log file with option to erase current file.
     * Typically used only at start of game.
     */
    public static void writeToLog(String info, Boolean eraseCurrent) {
    	writeToLog(info,"",eraseCurrent);
    }

    
    /*
     * Write a message to a logger file appended with _YYYYMMDD
     * 
     * @param info string being written to log
     * @param name (String) name to use instead of "XLogger"
     */
    public static void writeToLog(String info, String nameChange, Boolean eraseCurrent) {
	    Date date = new Date();
	    String infoOut=USDTSDF.format(date);
	    String logFileName=(nameChange.isEmpty()?"XLogger":nameChange)+"_"+LOGSDF.format(date)+".log";

	    // erase the current log file?
	    if(eraseCurrent) {
	    	File file=new File(logFileName);
	    	file.delete();
	    }
	    
	    // if there are leading EOLs then transfer them to before
	    // the date and time stamp before saving
	    while(info.length()>EOL.length() && info.substring(0,EOL.length()).equals(EOL)) {
	    	infoOut=EOL+infoOut;
	    	info=info.substring(EOL.length());
	    }
	    
   	 	infoOut+=" - "+info;

	
        try (PrintStream out = new PrintStream(new FileOutputStream(logFileName,true))) {
            out.println(infoOut);
        }
        catch(IOException ex) {
        	String msg=ex.getMessage();
            System.out.print(info+EOL+"XLogger (writeToLog) - "+msg);
        }       
	}

	
	/*
	 *  Simple dice roll... 1 - value
	 *  
	 */
	public static int diceRoll(int value) {
		return (value>0?(int)(Math.random()*value)+1:0);
	}
	
	
	/*
	 * The square of the hypotenuse is equal to the sum of the square of the other two sides
	 * See?  You really do use that stuff in real life!
	 *
	 */
	public static double getDistance(int r0, int c0, int r1, int c1) {
		return Math.sqrt(Math.abs(Math.pow(Double.valueOf(r0-r1),2)+Math.pow(Double.valueOf(c0-c1),2)));
	}


	/*
	 * Can't use the inverse square law since we're assuming it's a focused beam, so we 
	 * need a value along the line of how a laser lessens over distance.  The value of 
	 * 0.4 was used in the original game and is a reasonable value for this purpose.
	 *
	 */
	public static int hitPower(StarObject starObject, SRSControl srs, int power) {
		double distance=getDistance(starObject.row,starObject.col,srs.getMyRow(),srs.getMyCol());
		return (int) (Double.valueOf(power)/Math.pow(distance,0.4));
	}
	

	/*
	 * Logic to create the track list was pulled from the QuickBASIC source code
	 * located on Bob's Games page www.bobsoremweb.com/startrek.html
	 * 
	 * Thanks Bob!
	 */
	public static List<Track> createTrackList(int row, int col, double course) {
		List<Track> track=new ArrayList<>();
    	double y1=row;
    	double x1=col;
    	double c=(course-1D)*.785398;
    	double y0=Math.cos(c);
    	double x0= - Math.sin(c);
    	
    	int x2,y2;
    	
    	// go to 48 in case we've got a 24x24 board in play
    	while(track.size()<48) {
    		y1=y1-y0;
    		x1=x1-x0;

    		// rounding occurs here instead up in the init of y1,x2 because
    		// testing found that it creates a cleaner track.  Don't know if
    		// the original code had a bug or if the math routines worked a 
    		// bit different in the QuickBASIC version
        	y2=(int) (y1+0.5D);
			x2=(int) (x1+0.5D);

			// only record a track if the coordinates changed... sometimes you
			// get a repeat of the coordinates due to rounding issues.
			// We don't care if coordinates go out of grid confines, that's taken
			// care of later in the process
			if (track.size()==0 || track.get(track.size()-1).x!=x2 || track.get(track.size()-1).y!=y2) {
				Track t=new Track();
				t.x=x2;
				t.y=y2;
				track.add(t);
			}
    	}

    	return track;
	}

	
	public static double fixToOneDecimal(double value) {
		writeToLog("fixToOne="+value);
		return BigDecimal.valueOf(value).setScale(1,RoundingMode.HALF_UP).doubleValue();
	}
	/*
	 * Ask for a value with high/low range checking
	 */
	public static double getValue(String text, double lowval, double hival) {
		double result=-2D;
		String response;
		String crew="";
		
		// make sure hival is rounded to nearest 1/10
		hival=fixToOneDecimal(hival);

		String prompt="<html>"+crew+text.replaceAll("\n", "<BR>").replaceAll("\r", "")
				+"<BR>What value? ("+lowval+" - "+String.format("%.1f",hival)+")?";
		
		while(result< -1D) {
			try {
				response=JOptionPane.showInputDialog(prompt);
				if(response==null || response.length()==0) {
					result=-1D;
				}
				else {
					result=Double.parseDouble(response);
				}
			}
			catch (Exception ex) {
				crew="Mr Scot says, \"Sir, I dinna understan' what ye said!\"<br><br>";
			}

			if(result>hival || result<lowval || result== -1) {
				crew="You range of values are from "+lowval+" to "+hival+"<br><br>";
				result=-2;
			}
		}
		
		writeToLog("Utilities.getValue returns: "+result);
		return result;
	}

	
	/*
	 *  Pop up a dialog to get the duration/power
	 *  
	 */
	public static int setDuration(DurationControllerType dct) {
		int duration=-1;
		int max=12;
		String crew="";
		String response;
		
		while (duration == -1) {
			if(dct==Utilities.DurationControllerType.PHASERS || dct==Utilities.DurationControllerType.SHIELDS) {
				response=JOptionPane.showInputDialog(null,"<html>"+crew+ "Power level (0-100)? ","Phasers",JOptionPane.DEFAULT_OPTION);
				max=100;
			}
			else {
				String title=(dct==Utilities.DurationControllerType.WARP?"Warp":"Impulse");
				response=JOptionPane.showInputDialog(null,"<html>"+crew+ "Distance (0-12)? ",title,JOptionPane.DEFAULT_OPTION);
			}
			
			if (response==null || response.length()==0) {
				duration=-2;
			}
			else {
				try {
					duration=Integer.parseInt(response);
					if(duration>max) {
						duration=-2;
						crew="You are allowed a value from 0 to "+max+"<br><br>";
					}
		    	}
				catch (NumberFormatException ex) {
					switch (dct) {
						case PHASERS:
						case SHIELDS:
							crew="Mr Chekov says <i>\"Kiptan, I don't understand your order.\"</i><br><br>";
							break;
							
						default:
							crew="Mr Sulu says <i>\"I'm sorry Captain, but I didn't understand that.\"</i><br><br>";
					}
				}
			}
		}

		writeToLog("Utilities.setDuration returns: "+duration);
		return duration;
	}


	/*
	 * End of game dialog box
	 * 
	 */
	public static Integer gameEnd(GameEndType condition) {
		
		String dialog;
		String title="YOU LOOSE!";
		
		switch(condition) {
			case FOR_THE_WIN:
				dialog="<html><b>You won!</b><br>"
						+ "Congratulations on a job well done!<br><br>"
						+ "You have deated the Klingon empire's plans and kept the Federation<br>"
						+ "free for another day.<br><br>"
						+ "As a reward, you will be promoted to admiral and given a desk job at"
						+ "Starfleet headquarters!";
				title="YOU WON!";
				break;

			case OUT_OF_TIME:
				dialog="<html><b>You ran out of time!</b><br>"
		    			+ "Sesinge weakness, the Klingon empire has committed to all out war.<br>"
		    			+ "It's doubtful the Federation will be able to survive!";
				break;
				
			case OUT_OF_ENERGY:
				dialog="<html><b>You ran out of energy!</b><br>"
		    			+ "As the Enterprise slowly drifts through space, you get to think about<br>"
						+"how the Klingon empire will overrun the federation!";
				break;
			
			case DESTROYED:
				dialog="<html><b>The Enterprise was too badly damaged in battle and was destroyed!</b><br>"
		    			+ "If only you had decided on a career in Multi-Level Marketing, someone<br>"
						+ "else might have been commanding the Enterprise and able to save the<br>"
						+ "Federation from the Klingon horde!";
				break;
			
			default:
				// too badly damaged to continue
				dialog="<html><b>You Lose!</b><br>"
						+ "The Enterprise was too badly damaged during battle to continue operations.  As the<br>"
						+ "Klingons continue to fire on the ship, you contimplate the fate of the Federatoin.<br><br>"
		    			+ "Now the Klingon empire will overrun the federation. Widespread destruction and<br>"
						+ "will be visited on each of the planets and your name will be a derisive side-note<br>"
		    			+ "in the tomes of Klingon history.";
		}
		
		Object[] options= {"OK"};
		return JOptionPane.showOptionDialog(null, dialog, title, JOptionPane.PLAIN_MESSAGE,JOptionPane.PLAIN_MESSAGE,null, options,options[0]);
		
	}
	
	
	/*
	 * Pop up a dialog to get the course, showing the
	 * graphic for the courses available 1 to 8.9
	 * 
	 */
	public static double setCourse(CourseControllerType cct) {
		double course=-1;
		String crew="";
		String title;
		
		switch(cct) {
			case WARP:
				title="Set Course for Warp Drives";
				break;
			case IMPULSE:
				title="Set Course for Impulse Engines";
				break;
			default:
				title="Torpedo Firing Course";
		}

		
		while (course == -1) {
			String response=JOptionPane.showInputDialog(null,"<html>"+crew
	    			+ " &nbsp;8&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;2<br>"
	    			+ " &nbsp;&nbsp;&nbsp;\\&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;/<br>"
	    			+ "7&nbsp;---+---&nbsp;3<br>"
	    			+ " &nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;\\<br>"
	    			+ " &nbsp;6&nbsp;&nbsp;&nbsp;&nbsp;5&nbsp;&nbsp;&nbsp;&nbsp;4<br><br>"
	    			+ "Course value to set (0-8.9)? ",title,JOptionPane.DEFAULT_OPTION);
			
			
			if (response==null || response.length()==0) {
				course=-2;
			}
			else {
				try {
					course=Double.parseDouble(response);
					course=(course>8.9 || course<1?-1:course);
		    	}
				catch (NumberFormatException ex) {
					switch (cct) {
						case WARP:
							crew="Mr Chekov says <i>\"Kiptan, I don't understand what you said.\"</i><br><br>";
							break;
						case IMPULSE:
							crew="Mr Sulu says <i>\"Please repeat that, Captain, didn't understand your order.\"</i><br><br>";
							break;
						case TORPEDO:
							crew="Mr Sulu says <i>\"Could you repeat that, Captain? I didn't underdtand you.\"</i><br><br>";
					}
				}
			}
		}
		
		writeToLog("Utilities.setCourse returns: "+course);
		return course;
	}
	

	/* 
	 * Just open the instructions into the default browser
	 * 
	 */
	public static void Instructions(JavaTrek game) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI("Instructions.html"));
			}
		}
		catch (Exception ex) {
			game.comsChatter("Sorry, Captain.  The intsructions are unavailable at this time.");
			Utilities.writeToLog("Utilities.Instructions - Error "+ex.getMessage());
		}
	}
	
	
	/* 
	 * Calculate course to target.  Uses supplied position and you
	 * give it a target location (row,col).  
	 * 
	 */
	public static String courseCalculation(JavaTrek game, int size, int row, int col) {
		JFrame f=(JavaTrek) game; // not needed, but seems the right thing to do
		
		double r1=Double.valueOf(row);
		double c1=Double.valueOf(col);
		
		double dir=0;
		double r2=0D;
		double c2=0D;
		
		String results=null;
		String[] coords=null;

		// request the coordinates of the target.  If the user
		// exits without putting in a value, a null is returned
		// and we exit the routine sending back a blank string
		while(results==null) {
			results=JOptionPane.showInputDialog(f, "Enter coordinates of target position (row,col)","Targeting",JOptionPane.DEFAULT_OPTION);
			
			if(results==null) break;

			// they should have entered coordinates in format of row,col
			coords=results.split(",");
			
			if(coords.length==2) {
				try {
					// see if we can recover values from the input
					r2=Double.parseDouble(coords[0].trim())-1;
					c2=Double.parseDouble(coords[1].trim())-1;
				}
				catch (Exception ex) {
					results=null;
				}
				
				if(results!=null) {
					// make sure we have valid values and if not then we'll loop around
					if(r2>=0 && r2<size && c2>=0 && c2<size) {
						// we're good!
						break;
					}
					
					// didn't have valid coordinates
					results=null;
				}
			}
		}
		
		
		// My trigonometry is week, so it took me a few days
		// of research to come up with a working solution. 
		// I'm still not sure I have the best formula.
		//
		// If you're really good at math, then feel free to improve it!
		if(results!=null) {
			// Get the degrees, correct for the 90 degree skew + 360 to make
			// sure we get a positive angle, then get the mod of 360 which
			// is the correct degrees to target (per our perspective).  Now 
			// convert it to the 1 - 8.9 scale by multiplying it by 0.222223
			// (which is 8/360) and adding 1 to get the course to target.
			dir=0.0222223*((Math.toDegrees(Math.atan2(r2-r1,c2-c1))+450)%360D)+1;
			
			// round it to nearest tenth
			dir=fixToOneDecimal(dir);
			
			// now get a track to retrieve the distance, which is
			// the number of track iterations to the target
			int dist=0;
			int row2=(int) r2;
			int col2=(int) c2;
			List<Track> track=createTrackList(row,col,dir);
			for(int i=0;i<track.size();i++) {
				if(row2==track.get(i).y && col2==track.get(i).x)
					dist=i+1;
			}
			
			// return what the computer will say
			results=String.format("course %.1f, distance %d", dir, dist);
		}

		return (results==null?"":results);
	}
}
