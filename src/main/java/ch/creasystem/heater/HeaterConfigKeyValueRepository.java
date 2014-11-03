package ch.creasystem.heater;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaterConfigKeyValueRepository extends CrudRepository<HeaterConfigKeyValue, String> {

	public static final String MODE_KEY = "MODE_KEY"; // Mode of the heater
														// (manual or automatic)
	public static final String MODE_MANUAL_HEATER_ON = "MODE_MANUAL_HEATER_ON"; // True
																				// or
																				// false
																				// if
																				// the
																				// heater
																				// is
																				// on
																				// when
																				// mode
																				// is
																				// manual
}
