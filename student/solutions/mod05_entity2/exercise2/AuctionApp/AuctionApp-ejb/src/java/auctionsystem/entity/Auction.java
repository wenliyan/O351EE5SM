/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ejb.EJBException;
import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Administrator
 */
@Entity
public class Auction implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private double startAmount;
  private double increment;
  private String status;
  private Date openTime;
  private Date closeTime;
  
  // relationship fields
  private Item item;
  private AuctionUser seller;
  private Collection <Bid> bids;

  public Auction() {
  }

  public Auction(double startAmount, double increment, Date closeTime,
    Item item, AuctionUser seller){
    setOpenTime(Calendar.getInstance().getTime());
    if (openTime.after(closeTime)) {
      throw new EJBException("To open this auction successfully, " +
        "a later close time must be chosen");
    }
    setCloseTime(closeTime);
    setStartAmount(startAmount);
    setIncrement(increment);
    setStatus("OPEN");
    setItem(item);
    setSeller(seller);
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Integer getId() {
    return id;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Auction)) {
      return false;
    }
    Auction other = (Auction) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "auctionsystem.entity.Auction[id=" + id + "]";
  }

  public double getStartAmount() {
    return startAmount;
  }

  public void setStartAmount(double startAmount) {
    this.startAmount = startAmount;
  }

  public double getIncrement() {
    return increment;
  }

  public void setIncrement(double increment) {
    this.increment = increment;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Temporal(javax.persistence.TemporalType.DATE)
  public Date getOpenTime() {
    return openTime;
  }

  public void setOpenTime(Date openTime) {
    this.openTime = openTime;
  }

  @Temporal(TemporalType.DATE)
  public Date getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(Date closeTime) {
    this.closeTime = closeTime;
  }

  @OneToOne
  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  @ManyToOne
  public AuctionUser getSeller() {
    return seller;
  }

  public void setSeller(AuctionUser seller) {
    this.seller = seller;
  }

  @OneToMany (mappedBy = "auction")
  public Collection<Bid> getBids() {
    return bids;
  }

  public void setBids(Collection<Bid> bids) {
    this.bids = bids;
  }
  
  public void addBid(Bid newBid) {
    try {
      bids.add(newBid);
    }catch (UnsupportedOperationException uoe){
      // add code for alternative way of adding bid to collection
    }
  }

}
