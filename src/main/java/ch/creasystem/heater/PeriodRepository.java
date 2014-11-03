package ch.creasystem.heater;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends CrudRepository<Period, Integer> {
	@Query("SELECT p FROM Period p ORDER BY p.startDate asc")
	public List<Period> findAllOrderByStartDate();

	@Query("SELECT p FROM Period p WHERE :actualDate BETWEEN p.startDate AND p.stopDate")
	public Period findActualPeriod(@Param("actualDate") Date actualDate);

	@Query("SELECT p FROM Period p WHERE :actualDate > p.stopDate order by p.stopDate desc")
	public Period findLastPeriod(@Param("actualDate") Date actualDate);

	public List<Period> findByStopDateBeforeOrderByStopDateDesc(Date actualDate);
}
