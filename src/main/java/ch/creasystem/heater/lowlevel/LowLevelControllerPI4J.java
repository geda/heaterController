package ch.creasystem.heater.lowlevel;

import org.springframework.beans.factory.DisposableBean;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LowLevelControllerPI4J implements LowLevelController, DisposableBean {
	private final GpioController gpio;
	private final GpioPinDigitalOutput pin;

	public LowLevelControllerPI4J() {
		gpio = GpioFactory.getInstance();

		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED", PinState.LOW);
	}

	@Override
	public boolean isHeaterOn() {
		return pin.isHigh();
	}

	@Override
	public void setHeater(boolean on) {
		if (on) {
			pin.low();
		} else {
			pin.high();
		}
	}

	@Override
	public void destroy() throws Exception {
		pin.setShutdownOptions(true, PinState.LOW);
		gpio.shutdown();
	}

}
