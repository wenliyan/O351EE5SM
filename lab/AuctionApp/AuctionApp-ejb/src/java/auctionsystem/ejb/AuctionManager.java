/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.exception.AuctionException;
import java.util.Collection;
import java.util.Date;
import javax.ejb.Stateless;

/**
 *
 * @author mis
 */
@Stateless
public class AuctionManager implements AuctionManagerRemote, AuctionManagerLocal {

    public String communicationTest(String message) {
        System.out.println("AuctionManager.communicationTest: " + message);
        return "Received " + message;
    }

    public Object addItem(String itemDesc, String itemImage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object addBookItem(String itemDesc, String itemImage, String title, String author) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int addAuction(double startAmount, double increment, Date closeTime, String itemDesc, String itemImage, Integer sellerID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void placeBid(Integer auctionID, Integer bidderID, double bidAmount) throws AuctionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection getAllOpenAuctions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection getAllAuctionsOfSeller(Integer sellerID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object getSeller(String displayName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void closeAuction(Integer auctionID) throws AuctionException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
