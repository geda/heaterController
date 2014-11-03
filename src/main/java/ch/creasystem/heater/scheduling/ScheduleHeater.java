package ch.creasystem.heater.scheduling;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import ch.creasystem.heater.HeaterConfigKeyValue;
import ch.creasystem.heater.HeaterConfigKeyValueRepository;
import ch.creasystem.heater.HeaterService;
import ch.creasystem.heater.Mode;
import ch.creasystem.heater.Period;
import ch.creasystem.heater.PeriodRepository;
import ch.creasystem.heater.lowlevel.LowLevelController;

public class ScheduleHeater {
	static final Logger LOG = LoggerFactory.getLogger(ScheduleHeater.class);

	@Autowired
	private PeriodRepository periodRepo;

	@Autowired
	private HeaterConfigKeyValueRepository heaterConfigKeyValueRepo;

	@Autowired
	private HeaterService heaterService;

	@Resource(name = "lowLevelController")
	private LowLevelController lowLevelController;

	@Scheduled(fixedRate = 4000)
	@Transactional
	public void startStopHeater() {
		HeaterConfigKeyValue mode = heaterConfigKeyValueRepo.findOne(HeaterConfigKeyValueRepository.MODE_KEY);
		HeaterConfigKeyValue modeManualHeaterOn = heaterConfigKeyValueRepo
				.findOne(HeaterConfigKeyValueRepository.MODE_MANUAL_HEATER_ON);
		boolean lowlevelHeaterOn = lowLevelController.isHeaterOn();

		if (mode == null || modeManualHeaterOn == null) {
			LOG.warn("config is not ok. mode and modeManualHeaterOn are not defined");
			return;
		}

		if (Mode.MANUAL.equals(mode.valueAsMode())) {

			if (modeManualHeaterOn.valueAsBoolean() != lowlevelHeaterOn) {
				LOG.debug(
						"Manual heater mode is {}, but actually the heater is {}. The heater will be setted to the configured heater mode",
						modeManualHeaterOn.valueAsBoolean(), lowlevelHeaterOn);
				lowLevelController.setHeater(modeManualHeaterOn.valueAsBoolean());
			}
			LOG.debug("Mode is {}. Scheduling task for heater will exit.", mode.valueAsMode());
		} else {

			Period actualPeriod = periodRepo.findActualPeriod(new Date());
			if (actualPeriod == null) {
				if (!lowlevelHeaterOn) {
					LOG.debug("Mode is {}. Out of automatic period, switch heater on", mode.valueAsMode());
					lowLevelController.setHeater(true);
					saveLastPeriodEffectiveStop();
				}
			} else {
				if (lowlevelHeaterOn) {
					LOG.debug("Mode is {}, period={}. Switch heater off", mode.valueAsMode(), actualPeriod);
					lowLevelController.setHeater(false);
					actualPeriod.startDate = new Date();
				}
			}
		}
	}

	private void saveLastPeriodEffectiveStop() {
		List<Period> lastPeriods = periodRepo.findByStopDateBeforeOrderByStopDateDesc(new Date());
		if (lastPeriods.size() > 1) {
			Period lastPeriod = lastPeriods.get(0);
			lastPeriod.effectiveStop = new Date();
		}
	}
}
