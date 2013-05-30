package auctionsystem.ejb;

import javax.ejb.Remote;
import java.util.Date;
import java.util.Collection;
import auctionsystem.exception.AuctionException;


@Remote
public interface AuctionManagerRemote {
  public String communicationTest(String message);
  public Object addItem(String itemDesc, String itemImage);
  public Object addBookItem(String itemDesc, String itemImage, 
    String title, String author);
  public int addAuction(double startAmount, double increment, 
    Date closeTime, String itemDesc, String itemImage, Integer sellerID);
  public void placeBid(Integer auctionID, Integer bidderID, 
    double bidAmount) throws AuctionException;
  public Collection getAllOpenAuctions();
  public Collection getAllAuctionsOfSeller(Integer sellerID);
  public Object getSeller(String displayName);
  public void closeAuction(Integer auctionID) throws AuctionException;    
}
