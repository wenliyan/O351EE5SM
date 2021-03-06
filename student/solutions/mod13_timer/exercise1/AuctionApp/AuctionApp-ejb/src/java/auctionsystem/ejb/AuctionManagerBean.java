/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.dto.BidStatusMessage;
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
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.Query;


@Stateless
public class AuctionManagerBean implements AuctionManagerRemote, AuctionManagerLocal {

  @PersistenceContext private EntityManager em;
  private AuctionHelper auctionHelper = new AuctionHelper();

  // for messsaging
  @Resource(mappedName = "jms/bidsPlacedTopic")
  private Topic bidsPlacedTopic;
  @Resource(mappedName = "jms/TopicConnectionFactory")
  private ConnectionFactory connectionFactory;
  @Resource private SessionContext sessionContext;
  @Resource private TimerService timerService;
  
  public String communicationTest(String message) {
    System.out.println("AuctionManager.communicationTest: " + message);
    populateAuctionUserTable();
    return "Received " + message;
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
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

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public int addAuction(double startAmount, double increment, Date closeTime, String itemDesc, String itemImage, Integer sellerID) {
    try {
      Item item = null;
      AuctionUser seller = null;
      Auction auction = null;
      System.out.println("+++ aMan.addAuction: Creating new Item");
      AuctionManagerLocal auctionMgrLocal =
      sessionContext.getBusinessObject(AuctionManagerLocal.class);
      item = (Item)auctionMgrLocal.addItem(itemDesc, itemImage);
      //item = (Item) addItem(itemDesc, itemImage);
      System.out.println("+++ aMan.addAuction: Created new Item");
      seller = em.find(AuctionUser.class, sellerID);
      if (seller == null) throw new AuctionException("Unknown seller ID " + seller);
      System.out.println("+++ aMan.addAuction found seller " + sellerID);
      auction = new Auction(startAmount, increment, closeTime, item, seller);
      em.persist(auction);
      System.out.println("+++ aMan.addAuction created auction " + auction);
      createAuctionTimer(auction.getOpenTime(), auction.getCloseTime(), auction.getId());
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
    sendBidStatusUpdateMessage("OPEN", auctionID, bidderID, bidAmount); 
     
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
    String queryString = " SELECT OBJECT(a) FROM AuctionUser AS a WHERE a.displayName = '"
        + displayName +"'";

    AuctionUser seller = null;
    try {
      Query query = em.createQuery(" SELECT OBJECT(a) FROM AuctionUser AS a WHERE a.displayName = '"
        + displayName +"'");
     
      seller = (AuctionUser) query.getSingleResult();
      return seller;
    } catch (Exception e){
      throw new EJBException(e.getMessage());
    }
  }

  private void createAuctionTimer(Date openTime, Date closeTime, Integer auctionID){
    long duration = closeTime.getTime() - openTime.getTime();
    System.out.println("AuctionManager.createAuctionTimer " + duration + "  " +
      auctionID);
    timerService.createTimer(duration, "Auction time out: " + auctionID);
  }

  @Timeout
  public void timeout(Timer timer) {
    String infoString = (String) timer.getInfo();
    System.out.println("AuctionManager: timeout occurred " + infoString);
    String auctionIDString = infoString.substring(18);
    Integer auctionID = new Integer(Integer.parseInt(auctionIDString));
    System.out.print("Closing Auction " + auctionID);
    try {
      this.closeAuction(auctionID);   //uncomment later
    } catch (Exception e) {
      System.out.println("AuctionManager.timeout: failed to close auction " + auctionID + "  "
        + e.getMessage());
    }
  }
    
  public void closeAuction(Integer auctionID) throws AuctionException {
    Auction auction = null;
    // get auction to close:
    auction = em.find(Auction.class, auctionID);
    // close auction:
    if (auction == null) {
      throw new CloseException("Auction #" + auctionID + " is not open");
    }
    String status = auction.getStatus();
    if (status.compareTo("OPEN") == 0) {
      auction.setStatus("CLOSED");
      auction = em.merge(auction);
    } else {
      throw new CloseException("Auction #" + auctionID + " is not open");
    }
    // ** action notifyBidWinner(auction); <--- write this in AuctionManagerHelper
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
  // send message to bidsPlacedTopic
  private void sendBidStatusUpdateMessage(String status, Integer auctionID,
    Integer bidderID, double amount) {
    Connection connection = null;
    try {
      connection = connectionFactory.createConnection();
      Session session = connection.createSession(true, 0);
      MessageProducer producer = session.createProducer(bidsPlacedTopic);
      BidStatusMessage bidMessageDTO = new BidStatusMessage(status,
        bidderID, amount);
      ObjectMessage message = session.createObjectMessage(bidMessageDTO);
      int property = auctionID.intValue();
      message.setIntProperty("auctionID", property);
      producer.send(message);
    } catch (JMSException je) {
        throw new EJBException(je);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (JMSException je) {
          throw new EJBException(je);
        } finally {
          connection = null;
        }
      } // end if
    } // end finally
  } // end method bidStatusUpdateMessage

}
