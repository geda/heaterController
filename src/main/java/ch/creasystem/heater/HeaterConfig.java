package ch.creasystem.heater;

public class HeaterConfig {

	public Mode mode;
	public boolean modeManualHeaterOn;

	public boolean lowlevelHeaterOn;

	HeaterConfig() {
	}

	public HeaterConfig(Mode mode, boolean modeManualHeaterOn, boolean lowlevelHeaterOn) {
		super();
		this.mode = mode;
		this.modeManualHeaterOn = modeManualHeaterOn;
		this.lowlevelHeaterOn = lowlevelHeaterOn;
	}

	@Override
	public String toString() {
		return "HeaterConfig [mode=" + mode + ", modeManualHeaterOn=" + modeManualHeaterOn + ", lowlevelHeaterOn="
				+ lowlevelHeaterOn + "]";
	}

}
