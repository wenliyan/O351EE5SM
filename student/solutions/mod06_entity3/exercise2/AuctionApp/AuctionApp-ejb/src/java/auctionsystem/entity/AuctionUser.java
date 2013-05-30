/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Administrator
 */
@Entity
public class AuctionUser implements Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private String displayName;
  private String email;

  // relationship fields
  private Collection<Auction> auctions;  
  private Collection<Bid> bids;

  public AuctionUser() {
  }

  public AuctionUser (String displayName, String email) {
    setDisplayName(displayName);
    setEmail(email);
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
    if (!(object instanceof AuctionUser)) {
      return false;
    }
    AuctionUser other = (AuctionUser) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "auctionsystem.entity.AuctionUser[id=" + id + "]";
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @OneToMany (mappedBy = "seller")
  public Collection<Auction> getAuctions() {
    return auctions;
  }

  public void setAuctions(Collection<Auction> auctions) {
    this.auctions = auctions;
  }

  public void addAuction(Auction newAuction) {
    try {
      auctions.add(newAuction);
    }catch (UnsupportedOperationException uoe){
      // add code for alternative way of adding bid to collection
    }
  }

  @OneToMany(mappedBy = "bidder")
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
