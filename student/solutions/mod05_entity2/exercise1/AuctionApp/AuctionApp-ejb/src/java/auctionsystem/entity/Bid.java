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
import java.util.Date;
import java.util.Calendar;
import javax.persistence.ManyToOne;

/**
 *
 * @author Administrator
 */
@Entity
public class Bid implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private double amount;
  private Date bidTime;
  private String approval;

  // relationship fields
  private Auction auction;
  private AuctionUser bidder;

  public Bid() {
  }

  public Bid(double amount, String authorization, Auction auction,   AuctionUser bidder){
    setAmount(amount);
    setApproval(authorization);
    setBidTime(Calendar.getInstance().getTime());
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
    if (!(object instanceof Bid)) {
      return false;
    }
    Bid other = (Bid) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "auctionsystem.entity.Bid[id=" + id + "]";
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Temporal(TemporalType.DATE)
  public Date getBidTime() {
    return bidTime;
  }

  public void setBidTime(Date bidTime) {
    this.bidTime = bidTime;
  }

  public String getApproval() {
    return approval;
  }

  public void setApproval(String approval) {
    this.approval = approval;
  }

  @ManyToOne
  public AuctionUser getBidder() {
    return bidder;
  }

  public void setBidder(AuctionUser bidder) {
    this.bidder = bidder;
  }

  @ManyToOne
  public Auction getAuction() {
    return auction;
  }

  public void setAuction(Auction auction) {
    this.auction = auction;
  }

}
