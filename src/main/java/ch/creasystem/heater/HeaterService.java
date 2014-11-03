package ch.creasystem.heater;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.creasystem.heater.lowlevel.LowLevelController;

@Service
@Transactional(readOnly = true)
@RolesAllowed({ "ADMIN" })
public class HeaterService {
	static final Logger LOG = LoggerFactory.getLogger(HeaterService.class);

	@Autowired
	private PeriodRepository periodRepo;

	@Autowired
	private HeaterConfigKeyValueRepository heaterConfigKeyValueRepo;

	@Resource(name = "lowLevelController")
	private LowLevelController lowLevelController;

	public Iterable<Period> listAllPeriods() {
		Iterable<Period> allPeriods = periodRepo.findAll();
		if (allPeriods instanceof Collection<?>) {
			LOG.debug("Periods found: {}", ((Collection<?>) allPeriods).size());
		}

		return allPeriods;
	}

	public Period getPeriod(int id) {
		return periodRepo.findOne(id);
	}

	public Period findActualPeriod(Date actualDate) {
		return periodRepo.findActualPeriod(actualDate);
	}

	@Transactional
	public Period addPeriod(Period period) {
		return periodRepo.save(period);
	}

	@Transactional
	public Period changePeriod(int id, Period changedPeriod) {
		LOG.debug("Update periodIS{} with values: {}", id, changedPeriod);

		// change thid with update
		Period period = periodRepo.findOne(id);

		period.startDate = changedPeriod.startDate;
		period.stopDate = changedPeriod.stopDate;
		period.comment = changedPeriod.comment;
		return period;
	}

	@Transactional
	public HeaterConfig getConfig() {

		HeaterConfigKeyValue mode = heaterConfigKeyValueRepo.findOne(HeaterConfigKeyValueRepository.MODE_KEY);
		HeaterConfigKeyValue modeManualHeaterOn = heaterConfigKeyValueRepo
				.findOne(HeaterConfigKeyValueRepository.MODE_MANUAL_HEATER_ON);
		boolean lowlevelHeaterOn = lowLevelController.isHeaterOn();

		HeaterConfig config = new HeaterConfig(mode.valueAsMode(), modeManualHeaterOn.valueAsBoolean(),
				lowlevelHeaterOn);
		LOG.debug("Actual heater config:{}", config);
		return config;
	}

	@Transactional
	public HeaterConfig switchHeaterManual(boolean on) {
		LOG.debug("switching heater {}", on ? "on" : "off");

		HeaterConfigKeyValue modeManualHeaterOn = heaterConfigKeyValueRepo
				.findOne(HeaterConfigKeyValueRepository.MODE_MANUAL_HEATER_ON);
		modeManualHeaterOn.setBoolean(on);
		modeManualHeaterOn.lastModified = new Date();

		lowLevelController.setHeater(on);
		heaterConfigKeyValueRepo.save(modeManualHeaterOn);
		return getConfig();

	}

	@Transactional
	public HeaterConfig saveConfig(HeaterConfig config) {

		HeaterConfigKeyValue mode = heaterConfigKeyValueRepo.findOne(HeaterConfigKeyValueRepository.MODE_KEY);
		if (!config.mode.equals(mode.valueAsMode())) {
			mode.setMode(config.mode);
			mode.lastModified = new Date();
			heaterConfigKeyValueRepo.save(mode);
		}

		HeaterConfigKeyValue modeManualHeaterOn = heaterConfigKeyValueRepo
				.findOne(HeaterConfigKeyValueRepository.MODE_MANUAL_HEATER_ON);
		if (config.modeManualHeaterOn != modeManualHeaterOn.valueAsBoolean()) {
			modeManualHeaterOn.setBoolean(config.modeManualHeaterOn);
			modeManualHeaterOn.lastModified = new Date();
			heaterConfigKeyValueRepo.save(modeManualHeaterOn);
		}

		HeaterConfig savedConfig = getConfig();
		LOG.debug("saved heater config: {}", savedConfig);
		return savedConfig;
	}

	@Transactional
	public void deletePeriod(int id) {
		periodRepo.delete(id);
	}
}
