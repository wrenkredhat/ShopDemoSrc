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
public class Product implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private String ProductID;

	@Column
	private String Name;
	
	@Column
	private Long Price;

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
		if (!(obj instanceof Product)) {
			return false;
		}
		Product other = (Product) obj;
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

	public String getProductID() {
		return ProductID;
	}

	public void setProductID(String ProductID) {
		this.ProductID = ProductID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}
	
	public Long getPrice() {
		return Price;
	}

	public void setPrice(Long Price) {
		this.Price = Price;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (ProductID != null && !ProductID.trim().isEmpty())
			result += "ProductID: " + ProductID;
		if (Name != null && !Name.trim().isEmpty())
			result += ", Name: " + Name;
		if (Price != null)
			result += ", Price: " + Price;
		return result;
	}
}