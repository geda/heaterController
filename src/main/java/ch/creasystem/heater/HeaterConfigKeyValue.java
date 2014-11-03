package ch.creasystem.heater;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "heaterconfigkeyvalue")
public class HeaterConfigKeyValue {

	@Id
	@Column(name = "thekey")
	public String key;

	@NotNull
	@Column(name = "value")
	public String value;

	@NotNull
	@Column(name = "lastmodified")
	public Date lastModified;

	HeaterConfigKeyValue() {
	}

	public HeaterConfigKeyValue(String key, String value, Date lastModified) {
		super();
		this.key = key;
		this.value = value;
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "HeaterConfigKeyValue [key=" + key + ", value=" + value + ", lastModified=" + lastModified + "]";
	}

	public boolean valueAsBoolean() {
		return Boolean.valueOf(value);
	}

	public void setBoolean(Boolean booleanValue) {
		value = Boolean.toString(booleanValue);
	}

	public Mode valueAsMode() {
		return Mode.valueOf(value);
	}

	public void setMode(Mode mode) {
		value = mode.name();
	}

}
