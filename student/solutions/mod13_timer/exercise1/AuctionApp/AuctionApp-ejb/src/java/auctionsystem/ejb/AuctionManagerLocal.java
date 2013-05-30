/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.exception.AuctionException;
import javax.ejb.Local;

/**
 *
 * @author Administrator
 */
@Local
public interface AuctionManagerLocal {
  public void placeBid(Integer auctionID, Integer bidderID,  double bidAmount)
    throws AuctionException;         
  Object addItem(String itemDesc, String itemImage);
}
