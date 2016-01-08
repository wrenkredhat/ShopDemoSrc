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
public class Marketplace implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private String MarketID;

	@Column
	private String CCY;

	@Column
	private String URL;

	@Column
	private long ShippingCosts;

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
		if (!(obj instanceof Marketplace)) {
			return false;
		}
		Marketplace other = (Marketplace) obj;
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

	public String getMarketID() {
		return MarketID;
	}

	public void setMarketID(String MarketID) {
		this.MarketID = MarketID;
	}

	public String getCCY() {
		return CCY;
	}

	public void setCCY(String CCY) {
		this.CCY = CCY;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public long getShippingCosts() {
		return ShippingCosts;
	}

	public void setShippingCosts(long ShippingCosts) {
		this.ShippingCosts = ShippingCosts;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (MarketID != null && !MarketID.trim().isEmpty())
			result += "MarketID: " + MarketID;
		if (CCY != null && !CCY.trim().isEmpty())
			result += ", CCY: " + CCY;
		if (URL != null && !URL.trim().isEmpty())
			result += ", URL: " + URL;
		result += ", ShippingCosts: " + ShippingCosts;
		return result;
	}
}