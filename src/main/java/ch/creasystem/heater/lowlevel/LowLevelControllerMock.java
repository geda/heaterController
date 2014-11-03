package ch.creasystem.heater.lowlevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class LowLevelControllerMock implements LowLevelController, DisposableBean {
	static final Logger LOG = LoggerFactory.getLogger(LowLevelControllerMock.class);

	private boolean heaterOn = false;

	@Override
	public boolean isHeaterOn() {
		return heaterOn;
	}

	@Override
	public void setHeater(boolean on) {
		heaterOn = on;
	}

	@Override
	public void destroy() throws Exception {
		LOG.info("LowLevelControllerMock shutted down");
	}

}
