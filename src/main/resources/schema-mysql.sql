DROP TABLE period;

DROP TABLE heaterconfigkeyvalue;

CREATE TABLE heaterconfigkeyvalue (
	thekey VARCHAR(50) NOT NULL,
	value VARCHAR(50) NOT NULL,
	lastmodified DATETIME NOT NULL,
	PRIMARY KEY (thekey)
);

CREATE TABLE period (
	id INT NOT NULL AUTO_INCREMENT,
	comment VARCHAR(50) NOT NULL,
	effective_start DATETIME,
	effective_stop DATETIME,
	start_date DATETIME NOT NULL,
	stop_date DATETIME NOT NULL,
	PRIMARY KEY (id)
);

commit;