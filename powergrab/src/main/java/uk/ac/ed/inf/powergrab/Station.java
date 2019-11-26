package uk.ac.ed.inf.powergrab;

public class Station {
	private double latitude;
	private double longitude;
	private double coins;
	private double power;
	private String label;
	private boolean empty;
	int code;
	
	public Station(double latitude, double longitude, double coins, double power, String label) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.coins = coins;
		this.power = power;
		this.label = label;
		this.empty = false;
	}

	public Station(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
		code = -1;
	}

	//-------------------------Setters and Getters-------------------------------------------------
	double getLatitude() {
		return latitude;
	}

	void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	double getLongitude() {
		return longitude;
	}

	void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	double getCoins() {
		return coins;
	}

	void setCoins(double coins) {
		this.coins = coins;
	}

	double getPower() {
		return power;
	}

	void setPower(double power) {
		this.power = power;
	}

	String getLabel() { return label; }

	boolean isEmpty() {
		return empty;
	}

	void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
