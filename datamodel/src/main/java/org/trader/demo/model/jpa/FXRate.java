package org.trader.demo.model.jpa;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@XmlRootElement
public class FXRate implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private String BaseCCY;

	@Column
	private String TgtCCY;

	@Column
	private double Rate;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FXRate)) {
			return false;
		}
		FXRate other = (FXRate) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getBaseCCY() {
		return BaseCCY;
	}

	public void setBaseCCY(String BaseCCY) {
		this.BaseCCY = BaseCCY;
	}

	public String getTgtCCY() {
		return TgtCCY;
	}

	public void setTgtCCY(String TgtCCY) {
		this.TgtCCY = TgtCCY;
	}

	public double getRate() {
		return Rate;
	}

	public void setRate(double Rate) {
		this.Rate = Rate;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (BaseCCY != null && !BaseCCY.trim().isEmpty())
			result += "BaseCCY: " + BaseCCY;
		if (TgtCCY != null && !TgtCCY.trim().isEmpty())
			result += ", TgtCCY: " + TgtCCY;
		result += ", Rate: " + Rate;
		return result;
	}
}