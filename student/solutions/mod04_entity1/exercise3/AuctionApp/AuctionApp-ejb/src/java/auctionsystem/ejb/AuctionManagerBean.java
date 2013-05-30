/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.entity.AuctionUser;
import auctionsystem.exception.AuctionException;
import java.util.Collection;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import auctionsystem.entity.Item;

@Stateless
public class AuctionManagerBean implements AuctionManagerRemote, AuctionManagerLocal {

  @PersistenceContext private EntityManager em;
  
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
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int addAuction(double startAmount, double increment, Date closeTime, String itemDesc, String itemImage, Integer sellerID) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void placeBid(Integer auctionID, Integer bidderID, double bidAmount) throws AuctionException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Collection getAllOpenAuctions() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Collection getAllAuctionsOfSeller(Integer sellerID) {
    throw new UnsupportedOperationException("Not supported yet.");
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
