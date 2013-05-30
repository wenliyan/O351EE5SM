/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.exception.AuctionException;
import java.util.Collection;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import auctionsystem.entity.Item;
import auctionsystem.entity.AuctionUser;
import auctionsystem.entity.Auction;
import auctionsystem.entity.Bid;
import auctionsystem.entity.BookItem;
import auctionsystem.exception.*;
import javax.ejb.EJBException;
import auctionsystem.helper.AuctionHelper;
import javax.persistence.Query;


@Stateless
public class AuctionManagerBean implements AuctionManagerRemote, AuctionManagerLocal {

  @PersistenceContext private EntityManager em;
  private AuctionHelper auctionHelper = new AuctionHelper();
  
  public String communicationTest(String message) {
    System.out.println("AuctionManager.communicationTest: " + message);
    populateAuctionUserTable();
    return "Received " + message;
  }

  public Object addItem(String itemDesc, String itemImage) {
    System.out.println("++++++++ AuctionManager.addItem: Creating new Item");
    Item item = new Item(itemDesc, itemImage);
    em.persist(item);
    return item;
  }

  public Object addBookItem(String itemDesc, String itemImage, String title, String author) {
      System.out.println("++++++++ AuctionManager.addBookItem: Creating new BookItem");
      BookItem bookItem = new BookItem(itemDesc, itemImage, title, author);
      em.persist(bookItem);
      return bookItem;
  }

  public int addAuction(double startAmount, double increment, Date closeTime, String itemDesc, String itemImage, Integer sellerID) {
    try {
      Item item = null;
      AuctionUser seller = null;
      Auction auction = null;
      System.out.println("+++ aMan.addAuction: Creating new Item");
      item = (Item) addItem(itemDesc, itemImage);
      System.out.println("+++ aMan.addAuction: Created new Item");
      seller = em.find(AuctionUser.class, sellerID);
      if (seller == null) throw new AuctionException("Unknown seller ID " + seller);
      System.out.println("+++ aMan.addAuction found seller " + sellerID);
      auction = new Auction(startAmount, increment, closeTime, item, seller);
      em.persist(auction);
      System.out.println("+++ aMan.addAuction created auction " + auction);
      return auction.getId();
    } catch (Exception ex) {
      throw new EJBException(ex.getMessage());
    }
  }

  public void placeBid(Integer auctionID, Integer bidderID, double bidAmount) throws AuctionException {
    // Hint 1: Find auction corresponding to the auctionID: 
    Auction auction = em.find(Auction.class, auctionID);
    // Hint 2: Find bidder corresponding to the bidderID:
    AuctionUser bidder = em.find(AuctionUser.class, bidderID);
    
    System.out.println("AMB.checkBid(auction, bidder, bidAmount" + auction.getId() 
      + "  " + bidder.getId() + "  " + bidAmount);
    
    // Get the payment authorization:    
    String authorization = auctionHelper.getBidAuthorization(bidAmount, bidderID);      
    if (authorization.compareTo("DENIED") == 0) {
      throw new CreditCardException("Authorization denied");
    }
    // Invoke checkBid to check if this is a valid auction
    // and whether this bid can be placed:
    Bid previousHighestBid = auctionHelper.checkBid(auction, bidder, bidAmount);
  
    // Step 3.1: create the bid object
    Bid newBid = new Bid(bidAmount, authorization, auction, bidder);
    // Step 3.2: persist the bid object    
    em.persist(newBid);
    // Step 4.1: Add the bid to the auction's bid collection
    auction.addBid(newBid);       // add it
    auction = em.merge(auction);  // merge it
    // Step 4.2: Add the bid to the bidder's bid collection
    bidder.addBid(newBid);        // add it
    bidder = em.merge(bidder);    // merge it
    // System.out.println("AMB.checkBid: " + newBid);
    
    // Step 5: Inform current and previous highest bidders
    auctionHelper.informAffectedBidders(auction, previousHighestBid, newBid);
    // sendBidStatusUpdateMessage("OPEN", auctionID, bidderID, bidAmount); 
     
  }

  public Collection getAllOpenAuctions() {
    Query query=em.createNamedQuery("FindAllOpenAuctions");
    return ((Collection) query.getResultList());
  }

  public Collection getAllAuctionsOfSeller(Integer sellerID) {
    System.out.println("AMB.getAllAuctionOfSeller: sellerID = " + sellerID);
    Query query=em.createNamedQuery("FindAllAuctionsOfSeller");
    query.setParameter(1, sellerID);
    return ((Collection) query.getResultList());
  }

  public Object getSeller(String displayName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void closeAuction(Integer auctionID) throws AuctionException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
 
  public void populateAuctionUserTable () { 
    // This method creates 3 rows in the AUCTIONUSER Table
    // The rows are created only if it cannot find a row with 
    // primary key value of 1.
    System.out.println("AuctionManager.populateAuctionUserTable");
    Object  au =  null;
    au = em.find(AuctionUser.class, new Integer(1)); 
    if (au == null) {
      em.persist(new AuctionUser("auctionman", "auctionman@yahoo.com"));
      em.persist(new AuctionUser("catwoman", "jsmithson@abc.net"));
      em.persist(new AuctionUser("toyseller", "samF@home.com"));
    }
  }
}
