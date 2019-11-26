package uk.ac.ed.inf.powergrab;
/**
* @author s1756255
*/

public class Position {
	public double latitude; // y
	public double longitude; // x

	/** 
	* constant variables
	* Pre-calculation improves efficiency
	*/
	private static final double r = 0.0003;
	private static final double pi = Math.PI;
	
	// Pre-calculation improves efficiency 
	// ENE - 1/8 pi
	private static final double ENE_x = Math.cos(pi/8) * r;
	private static final double ENE_y = Math.sin(pi/8) * r;
	// NE - 2/8 = 1/4 pi
	private static final double NE_x = Math.cos(pi/4) * r;
	private static final double NE_y = Math.sin(pi/4) * r;
	// NNE - 3/8 pi
	private static final double NNE_x = Math.cos(3*pi/8) * r;
	private static final double NNE_y = Math.sin(3*pi/8) * r;
	
	// NNW - 5/8 pi
	private static final double NNW_x = Math.cos(5*pi/8) * r;
	private static final double NNW_y = Math.sin(5*pi/8) * r;
	// NW - 6/8 = 3/4 pi
	private static final double NW_x = Math.cos(3*pi/4) * r;
	private static final double NW_y = Math.sin(3*pi/4) * r;
	// WNW - 7/8 pi
	private static final double WNW_x = Math.cos(7*pi/8) * r;
	private static final double WNW_y = Math.sin(7*pi/8) * r;
	
	// WSW - 9/8 pi
	private static final double WSW_x = Math.cos(9*pi/8) * r;
	private static final double WSW_y = Math.sin(9*pi/8) * r;
	// SW - 10/8 = 5/4 pi
	private static final double SW_x = Math.cos(5*pi/4) * r;
	private static final double SW_y = Math.sin(5*pi/4) * r;
	// SSW - 11/8 pi
	private static final double SSW_x = Math.cos(11*pi/8) * r;
	private static final double SSW_y = Math.sin(11*pi/8) * r;
	
	// SSE - 13/8 pi
	private static final double SSE_x = Math.cos(13*pi/8) * r;
	private static final double SSE_y = Math.sin(13*pi/8) * r;
	// SE - 14/8 7/4 pi
	private static final double SE_x = Math.cos(7*pi/4) * r;
	private static final double SE_y = Math.sin(7*pi/4) * r;
	// ESE - 15/8 pi
	private static final double ESE_x = Math.cos(15*pi/8) * r;
	private static final double ESE_y = Math.sin(15*pi/8) * r;

	/** Constructor */
	Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	/** Default constructor */
	public Position() {
		this.latitude = 0;
		this.longitude = 0;
	}

	/**
	 * Calculates next position after the movement
	 *
	 * @param direction   the direction of the movement
	 * @return            the position after movement
	 */
	Position nextPosition(Direction direction) {
		double x = 0; double y = 0;
		
		switch(direction) {
		case N: // 90 = pi/2
			x = longitude;
			y = latitude + r;
			break;
		case S: // 270 = 3pi/2
			x = longitude;
			y = latitude - r;
			break;
		case W: // 180 = pi
			x = longitude - r;
			y = latitude;
			break;
		case E: // 0 = 0 pi
			x = longitude + r;
			y = latitude;
			break;
		case ENE: //22.5 = pi/8
			x = longitude + ENE_x;
			y = latitude + ENE_y;
			break;
		case NE: //45 = pi/4
			x = longitude + NE_x;
			y = latitude + NE_y;
			break;
		case NNE: //67.5 = 3pi/8
			x = longitude + NNE_x;
			y = latitude + NNE_y;
			break;
			
		case NNW: //5pi/8
			x = longitude + NNW_x;
			y = latitude + NNW_y;
			break;
		case NW: // 6pi/8 = 3pi/4
			x = longitude + NW_x;
			y = latitude + NW_y;
			break;
		case WNW: // 7pi/8
			x = longitude + WNW_x;
			y = latitude + WNW_y;
			break;
			
		case WSW: // 9pi/8
			x = longitude + WSW_x;
			y = latitude + WSW_y;
			break;
		case SW: // 10pi/8 = 5pi/4
			x = longitude + SW_x;
			y = latitude + SW_y;
			break;
		case SSW: // 11pi/8
			x = longitude + SSW_x;
			y = latitude + SSW_y;
			break;
		
		case SSE: // 13pi/8
			x = longitude + SSE_x;
			y = latitude + SSE_y;
			break;
		case SE: // 14pi/8 = 7pi/4
			x = longitude + SE_x;
			y = latitude + SE_y;
			break;
		case ESE: // 15pi/8
			x = longitude + ESE_x;
			y = latitude + ESE_y;
			break;
		}
		Position nextP = new Position(y,x);
		return nextP;
	}

	/**
	 * Checks the current position
	 * @return      whether current position in the valid area
	 */
	boolean inPlayArea() {
		if (latitude <= 55.942617 || latitude >= 55.946233 || longitude <= -3.192473 || longitude >= -3.184319) {
			return false;
		}
		else 
			return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Position position = (Position) obj;
		return this.latitude == position.latitude && this.longitude == position.longitude;
	}
}
