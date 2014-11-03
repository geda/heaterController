package ch.creasystem.heater.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.creasystem.heater.HeaterConfig;
import ch.creasystem.heater.HeaterService;
import ch.creasystem.heater.Period;

@RestController
@RequestMapping("/api/heater")
public class HeaterController {
	@Autowired
	private HeaterService heaterService;

	@RequestMapping(value = "/config", method = GET)
	public HeaterConfig getConfig() {
		return heaterService.getConfig();
	}

	@RequestMapping(value = "/saveConfig", method = POST)
	public HeaterConfig saveConfig(@RequestBody @Valid HeaterConfig config) {
		return heaterService.saveConfig(config);
	}

	@RequestMapping(value = "/switchHeater", method = POST)
	public HeaterConfig switchHeater(@RequestBody boolean on) {
		return heaterService.switchHeaterManual(on);
	}

	@RequestMapping(value = "/addPeriod", method = POST)
	public Period addPeriod(@RequestBody @Valid Period period) {
		return heaterService.addPeriod(period);
	}

	@RequestMapping(value = "/listPeriods", method = GET)
	public Iterable<Period> listAllPeriods() {
		return heaterService.listAllPeriods();
	}

	@RequestMapping(value = "/period/{id}", method = GET)
	public Period getPeriod(@PathVariable int id) {
		return heaterService.getPeriod(id);
	}

	@RequestMapping(value = "/period/{id}", method = POST)
	public Period change(@PathVariable int id, @RequestBody @Valid Period period) {
		return heaterService.changePeriod(id, period);
	}

	@RequestMapping(value = "/deletePeriod/{id}", method = GET)
	public Iterable<Period> deletePeriod(@PathVariable int id) {
		heaterService.deletePeriod(id);
		return heaterService.listAllPeriods();
	}
}
