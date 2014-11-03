package ch.creasystem.heater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import ch.creasystem.heater.lowlevel.LowLevelController;
import ch.creasystem.heater.lowlevel.LowLevelControllerMock;
import ch.creasystem.heater.lowlevel.LowLevelControllerPI4J;
import ch.creasystem.heater.scheduling.ScheduleHeater;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class HeaterApplication extends SpringBootServletInitializer {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(HeaterApplication.class, args);
		SpringApplication.run(ScheduleHeater.class);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HeaterApplication.class);
	}

	@Bean(name = "lowLevelController")
	@Profile("raspberrypi")
	public LowLevelController lowLevelControllerPI4J() {
		return new LowLevelControllerPI4J();
	}

	@Bean(name = "lowLevelController")
	@Profile("laptop")
	public LowLevelController lowLevelControllerMock() {
		return new LowLevelControllerMock();
	}

	@Bean(name = "scheduleHeater")
	public ScheduleHeater scheduleHeater() {
		return new ScheduleHeater();
	}

}
