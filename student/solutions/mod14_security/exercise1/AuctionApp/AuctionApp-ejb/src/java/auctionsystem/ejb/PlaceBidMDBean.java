/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.dto.PlaceBidMessage;
import auctionsystem.exception.AuctionException;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Administrator
 */
@MessageDriven(mappedName = "jms/PlaceBidMD", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
public class PlaceBidMDBean implements MessageListener {
    
  @EJB AuctionManagerLocal auctionManager;
  
  public PlaceBidMDBean() {
  }

  public void onMessage(Message message) {
    ObjectMessage objectMessage = null;
    PlaceBidMessage placeBidMessage = null;
    Integer auctionID = null;
    Integer bidderID = null;
    double amount = 0.0;

    try {
      objectMessage = (ObjectMessage) message;
    } catch (ClassCastException cce) {
      throw new EJBException(cce);
    }
    try {
      placeBidMessage = (PlaceBidMessage) objectMessage.getObject();
    } catch (JMSException je) {
      throw new EJBException(je);
    }
    try {
      auctionID = placeBidMessage.getAuctionID();
      bidderID = placeBidMessage.getBidderID();
      amount = placeBidMessage.getAmount();
      auctionManager.placeBid(auctionID, bidderID, amount);
    } catch (AuctionException ae) {
      System.out.println("\n PlaceBidMDB: AuctionException " +
        "\n \t received when placing a bid for user " +
        bidderID + " in auction " +
        auctionID + " : \t " + ae.getMessage());
      //context.setRollbackOnly();  //***** action
    } 
  }
    
}
