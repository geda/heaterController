package ch.creasystem.heater;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Period")
public class Period {

	@Id
	@GeneratedValue
	public Integer id;

	@NotNull
	@Column(name = "start_date")
	public Date startDate;

	@Column(name = "effective_Start")
	public Date effectiveStart;

	@NotNull
	@Column(name = "stop_date")
	public Date stopDate;

	@Column(name = "effective_Stop")
	public Date effectiveStop;

	@NotEmpty
	@Length(min = 5, max = 50)
	public String comment;

	public Period() {
	}

	public Period(Date startDate, Date stopDate, String comment) {
		super();
		this.startDate = startDate;
		this.stopDate = stopDate;
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Period [id=" + id + ", startDate=" + startDate + ", effectiveStart=" + effectiveStart + ", stopDate="
				+ stopDate + ", effectiveStop=" + effectiveStop + ", comment=" + comment + "]";
	}
}
