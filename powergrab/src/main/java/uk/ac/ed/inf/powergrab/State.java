package uk.ac.ed.inf.powergrab;

public class State {
	private double latitude;
	private double longitude;
	private double coins;
	private double power;
	private String label;
	private boolean empty;
	public int code;
	
	public State(double latitude, double longitude, double coins, double power, String label) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.coins = coins;
		this.power = power;
		this.label = label;
		this.empty = false;
	}

	public State(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
		code = -1;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public String getLabel() { return label; }

	public void setLabel(String label) { this.label = label; }

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	
}
