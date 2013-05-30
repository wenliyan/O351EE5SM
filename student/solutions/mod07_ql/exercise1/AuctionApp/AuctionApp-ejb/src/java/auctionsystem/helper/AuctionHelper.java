/*
 * AuctionHelper.java
 *
 * Created on April 13, 2006, 1:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package auctionsystem.helper;
import auctionsystem.ejb.*;
import auctionsystem.entity.*;
import auctionsystem.exception.*;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author asokp
 */
public class AuctionHelper {
  
  /**
   * Creates a new instance of AuctionHelper
   */
  public AuctionHelper() {
  }
  
  public String getBidAuthorization(double bidAmount, Integer bidderID){
    // For now approve all bids
    // For future add business logic to check bidder's credit rating
    return "Approved";
  }
  
  /** checks if a bid can be placed.
   * The following checks are performed:
   * <ul>
   *  <li> is the auction open,
   *  <li> has there been a higher bid in the
   *       meantime,
   *  <li> is the bid high enough,
   *  <li> is the bidder the seller (not allowed),
   *  <li> is the bidder the high bidder already
   *       (not allowed)
   * </ul>
   */
  public Bid checkBid(Auction auction, AuctionUser bidder,
    double newAmount) throws AuctionException {
    // Check parameters
    if (auction == null) throw new AuctionException("auction does not exist in DB");
    if (bidder == null) throw new AuctionException("bidder does not exist in DB");   
    // check auction state:
    String status = auction.getStatus();
    if (status.compareTo("OPEN") != 0) {
      throw new CloseException("This auction is not open anymore");
    }
    // check if the bidder is the seller
    if (bidder.getId().intValue() ==
      auction.getSeller().getId().intValue()) {
      throw new PlaceBidException("The seller cannot bid");
    }
    Bid currentHighestBid = getHighestBid(auction);
    // check if the bidder who is placing
    // the bid is the high bidder already:
    if (currentHighestBid != null) {
      if (currentHighestBid.getBidder().getId().intValue() ==
        bidder.getId().intValue()) {
        throw new PlaceBidException("The bidder is the high bidder already");
      }
    }
    double currentHighestAmount = 0;
    if (currentHighestBid == null) {
      // if there is not a highest bid,
      // there haven't been bids placed in
      // this auction already, and then
      // the highest bid is the start amount
      // of the auction:
      currentHighestAmount = auction.getStartAmount();
    } else {
      currentHighestAmount = currentHighestBid.getAmount();
    }
    // check if there has been placed a
    // higher bid in the meantime:
    if (currentHighestAmount > newAmount) {
      throw new OutbidException("This bid has been outbid " + currentHighestAmount);
    }
    // check if the bid is increased
    // as required:
    if (currentHighestAmount + auction.getIncrement() > newAmount) {
      throw new BidTooLowException("This bid is too low");
    }
    return currentHighestBid;
  } // end method checkBid
  
  
  /** computes the highest bid of an auction.
   */
  public Bid getHighestBid(Auction auction) {
    Bid highestBid = null;
    Bid tempBid = null;
    Collection bids = auction.getBids();
    if (!bids.isEmpty()) {
      Iterator i = bids.iterator();
      highestBid = (Bid) i.next();
      while (i.hasNext()) {
        tempBid = (Bid) i.next();
        if (tempBid.getAmount() > highestBid.getAmount()) {
          highestBid = tempBid;
        }
      }
    }
    return highestBid;
  } // end method getHighestBid

  public void sendNotification(String emailAddress, String message) {
    // simulating emails here:
    System.out.println("\n Sending email to " + emailAddress + ":");
    System.out.println("\t" + message + "\n");
  } // end method sendNotification
  
  public void informAffectedBidders(Auction auction, Bid formerHighestBid, Bid newBid) {
    // inform the former highest bidder by email about being outbid:
    // (email simulated)
    String itemDescription = auction.getItem().getDescription();
    if (formerHighestBid != null) {
      AuctionUser formerHighestBidder = formerHighestBid.getBidder();
      String emailAddress = formerHighestBidder.getEmail();
      String message = "You've been outbid in the " + itemDescription +" auction";
      sendNotification(emailAddress, message);
    }
    // inform the new highest bidder by
    // email that the bid was placed
    // sucessfully:
    // (email simulated)
    AuctionUser newHighestBidder = newBid.getBidder();
    String emailAddress = newHighestBidder.getEmail();
    String message = "You've placed a bid on the " + itemDescription + " successfully";
    //***********sendNotification(emailAddress, message);
    sendNotification(emailAddress, message);
  }
  
}  
  
