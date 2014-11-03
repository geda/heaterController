package ch.creasystem.heater;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HeaterApplication.class)
@Transactional
@ActiveProfiles(profiles = "laptop")
public class PeriodRepositoryTest {

	@Autowired
	private PeriodRepository periodRepo;

	@Test
	@Rollback
	public void crudPeriod() {

		Iterable<Period> periods = periodRepo.findAll();
		// make sure the table is empty
		Assert.assertFalse(periods.iterator().hasNext());

		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MONTH, 6);
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date stopDate = calendar.getTime();
		Period firstPeriod = new Period(startDate, stopDate, "this my second period");
		firstPeriod = periodRepo.save(firstPeriod);

		Assert.assertNotNull(firstPeriod.id);
		firstPeriod = periodRepo.findOne(firstPeriod.id);

		calendar.add(Calendar.MONTH, -1);
		startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		stopDate = calendar.getTime();

		Period secondPeriod = new Period(startDate, stopDate, "this my first period");
		secondPeriod = periodRepo.save(secondPeriod);

		List<Period> findAllOrderByStartDate = periodRepo.findAllOrderByStartDate();
		Assert.assertEquals(2, findAllOrderByStartDate.size());
		Assert.assertTrue(findAllOrderByStartDate.get(0).comment.equals("this my first period"));
		Assert.assertTrue(findAllOrderByStartDate.get(1).comment.equals("this my second period"));

		periodRepo.deleteAll();
	}

	@Test
	@Rollback
	public void findActualPeriod() {

		// create a period in 6 month for 1 week
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MONTH, 6);
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		Date stopDate = calendar.getTime();
		Period firstPeriod = new Period(startDate, stopDate, "this a week period");
		firstPeriod = periodRepo.save(firstPeriod);

		Assert.assertNull(periodRepo.findActualPeriod(new Date()));
		calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_MONTH, 4);
		Assert.assertNotNull(periodRepo.findActualPeriod(calendar.getTime()));
	}

	@Test
	@Rollback
	public void findLastPeriod() {

		// create a period 6 month in the pastfor 1 week
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MONTH, -6);
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		Date stopDate = calendar.getTime();
		Period firstPeriod = new Period(startDate, stopDate, "this is the first period");
		firstPeriod = periodRepo.save(firstPeriod);

		// create a less older period
		calendar.add(Calendar.MONTH, 1);
		startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		stopDate = calendar.getTime();
		Period lastPeriod = new Period(startDate, stopDate, "this is the last period");
		lastPeriod = periodRepo.save(lastPeriod);

		// create a future period
		calendar.add(Calendar.YEAR, 2);
		startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		stopDate = calendar.getTime();
		Period futurePeriod = new Period(startDate, stopDate, "this is the last period");
		futurePeriod = periodRepo.save(futurePeriod);

		List<Period> lastPeriods = periodRepo.findByStopDateBeforeOrderByStopDateDesc(new Date());

		Assert.assertEquals(2, lastPeriods.size());
		Assert.assertEquals(lastPeriod, lastPeriods.get(0));
	}
}
