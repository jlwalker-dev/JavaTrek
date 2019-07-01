package trekGame;

public class ObjectMovement {
	/*
	 *  class used in object animation
	 */
	int type=0;
	String action="";
	int row=-1;
	int col=-1;

	// constructor to short cut adding values
	public ObjectMovement(int type, String action, int row, int col) {
		this.type=type;
		this.action=action;
		this.row=row;
		this.col=col;
	}
}
