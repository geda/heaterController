package ch.creasystem.heater;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HeaterApplication.class)
@Transactional
@ActiveProfiles(profiles = "laptop")
public class HeaterServiceTest {

	@Autowired
	private PeriodRepository periodRepo;

	@Autowired
	private HeaterConfigKeyValueRepository heaterConfigRepository;

	@Autowired
	private HeaterService heaterService;

	@Before
	public void setup() {
		HeaterConfigKeyValue mode = new HeaterConfigKeyValue(HeaterConfigKeyValueRepository.MODE_KEY,
				Mode.MANUAL.name(), new Date());
		heaterConfigRepository.save(mode);

		HeaterConfigKeyValue modeManualHeaterOn = new HeaterConfigKeyValue(
				HeaterConfigKeyValueRepository.MODE_MANUAL_HEATER_ON, Boolean.FALSE.toString(), new Date());
		heaterConfigRepository.save(modeManualHeaterOn);

		setCredentials();
	}

	@Test
	@Rollback
	public void createPeriod() {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date stopDate = calendar.getTime();

		Period period = new Period(startDate, stopDate, "this is a period");
		Period savedPeriod = heaterService.addPeriod(period);

		Assert.assertNotNull(savedPeriod.id);
		savedPeriod = periodRepo.findOne(savedPeriod.id);
		Assert.assertNotNull(savedPeriod);
	}

	@Test
	@Rollback
	public void switchHeaterManual() {
		HeaterConfig config = heaterService.getConfig();
		Assert.assertNotNull(config);

		boolean manualMode = config.modeManualHeaterOn;
		heaterService.switchHeaterManual(!manualMode);

		config = heaterService.getConfig();
		Assert.assertTrue(config.modeManualHeaterOn == !manualMode);
	}

	@Test
	@Rollback
	public void getConfig() {
		HeaterConfig config = heaterService.getConfig();

		config.mode = Mode.AUTOMATIC;
		config.modeManualHeaterOn = false;
		heaterService.saveConfig(config);

		config = heaterService.getConfig();
		Assert.assertEquals(Mode.AUTOMATIC, config.mode);
		Assert.assertEquals(false, config.modeManualHeaterOn);

		config.mode = Mode.MANUAL;
		config.modeManualHeaterOn = true;
		heaterService.saveConfig(config);

		config = heaterService.getConfig();
		Assert.assertEquals(Mode.MANUAL, config.mode);
		Assert.assertEquals(true, config.modeManualHeaterOn);
	}

	private void setCredentials() {
		SecurityContext ctx = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(ctx);
		ctx.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", Arrays
				.asList(new SimpleGrantedAuthority("ADMIN"))));
	}

}
