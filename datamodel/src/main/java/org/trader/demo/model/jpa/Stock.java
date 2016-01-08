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
public class Stock implements Serializable {

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
	private String ProductID;

	@Column
	private int Qty;

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
		if (!(obj instanceof Stock)) {
			return false;
		}
		Stock other = (Stock) obj;
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

	public String getProductID() {
		return ProductID;
	}

	public void setProductID(String ProductID) {
		this.ProductID = ProductID;
	}

	public int getQty() {
		return Qty;
	}

	public void setQty(int Qty) {
		this.Qty = Qty;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (MarketID != null && !MarketID.trim().isEmpty())
			result += "MarketID: " + MarketID;
		if (ProductID != null && !ProductID.trim().isEmpty())
			result += ", ProductID: " + ProductID;
		result += ", Qty: " + Qty;
		return result;
	}
}