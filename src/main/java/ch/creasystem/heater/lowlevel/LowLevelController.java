package ch.creasystem.heater.lowlevel;

public interface LowLevelController {

	public boolean isHeaterOn();

	public void setHeater(boolean on);
}
