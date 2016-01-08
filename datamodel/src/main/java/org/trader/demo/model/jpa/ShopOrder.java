package org.trader.demo.model.jpa;

import javax.persistence.Entity;

import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
@Entity
public class ShopOrder implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;
	
	@Column
	private long BusinessKey;

	@Column
	private String User;

	@Column
	private String MarketIdSrc;

	@Column
	private String MarketIdTgt;

	@Column
	private String ProductID;

	@Column
	private int Qty;

	@Column
	private double Rate;

	@Column
	private int ShippingCosts;

	@Column
	private int TotalPrice;

	@Column
	private Long Price;

	@Column
	private Long FrgPrice;

	@Column
	private String UserEmail;

	@Column
	private String Status;

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
		if (!(obj instanceof ShopOrder)) {
			return false;
		}
		ShopOrder other = (ShopOrder) obj;
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

	public String getUser() {
		return User;
	}
	
	public void setUser(String User) {
		this.User = User;
	}
	
	public long getBusinessKey() {
		return BusinessKey;
	}
	
	public void setBusinessKey(long BusinessKey) {
		this.BusinessKey = BusinessKey;
	}

	public String getMarketIdSrc() {
		return MarketIdSrc;
	}

	public void setMarketIdSrc(String MarketIdSrc) {
		this.MarketIdSrc = MarketIdSrc;
	}

	public String getMarketIdTgt() {
		return MarketIdTgt;
	}

	public void setMarketIdTgt(String MarketIdTgt) {
		this.MarketIdTgt = MarketIdTgt;
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

	public double getRate() {
		return Rate;
	}

	public void setRate(double Rate) {
		this.Rate = Rate;
	}

	public int getShippingCosts() {
		return ShippingCosts;
	}

	public void setShippingCosts(int ShippingCosts) {
		this.ShippingCosts = ShippingCosts;
	}

	public int getTotalPrice() {
		return TotalPrice;
	}

	public void setTotalPrice(int TotalPrice) {
		this.TotalPrice = TotalPrice;
	}

	public Long getPrice() {
		return Price;
	}

	public void setPrice(Long Price) {
		this.Price = Price;
	}

	public Long getFrgPrice() {
		return FrgPrice;
	}

	public void setFrgPrice(Long FrgPrice) {
		this.FrgPrice = FrgPrice;
	}

	public String getUserEmail() {
		return UserEmail;
	}

	public void setUserEmail(String UserEmail) {
		this.UserEmail = UserEmail;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
			result += "BusinessKey: " + BusinessKey;
		if (User != null && !User.trim().isEmpty())
			result += ", User: " + User;
		if (MarketIdSrc != null && !MarketIdSrc.trim().isEmpty())
			result += ", MarketIdSrc: " + MarketIdSrc;
		if (MarketIdTgt != null && !MarketIdTgt.trim().isEmpty())
			result += ", MarketIdTgt: " + MarketIdTgt;
		if (ProductID != null && !ProductID.trim().isEmpty())
			result += ", ProductID: " + ProductID;
		result += ", Qty: " + Qty;
		result += ", Rate: " + Rate;
		result += ", ShippingCosts: " + ShippingCosts;
		result += ", TotalPrice: " + TotalPrice;
		if (Price != null)
			result += ", Price: " + Price;
		if (FrgPrice != null)
			result += ", FrgPrice: " + FrgPrice;
		if (UserEmail != null && !UserEmail.trim().isEmpty())
			result += ", UserEmail: " + UserEmail;
		if (Status != null && !Status.trim().isEmpty())
			result += ", Status: " + Status;
		result += ", Version: " + version;
		return result;
	}
}