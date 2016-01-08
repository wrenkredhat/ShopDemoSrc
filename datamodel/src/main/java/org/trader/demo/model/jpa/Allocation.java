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
public class Allocation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private long prdid;

	@Column
	private long stockid;

	@Column
	private String status;

	@Column
	private int qty;

	@Column
	private long orderid;

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
		if (!(obj instanceof Allocation)) {
			return false;
		}
		Allocation other = (Allocation) obj;
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

	public long getPrdid() {
		return prdid;
	}

	public void setPrdid(long prdid) {
		this.prdid = prdid;
	}

	public long getStockid() {
		return stockid;
	}

	public void setStockid(long stockid) {
		this.stockid = stockid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public long getOrderid() {
		return orderid;
	}

	public void setOrder(long orderid) {
		this.orderid = orderid;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "prdid: " + prdid;
		result += ", stockid: " + stockid;
		if (status != null && !status.trim().isEmpty())
			result += ", status: " + status;
		result += ", qty: " + qty;
		result += ", order: " + orderid;
		return result;
	}
}