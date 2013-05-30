/*
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package auctionsystem.dto;

import java.io.*;
/** This is the class of the bid data
 * transfer object which is sent from
 * the client to the auction system.
 *
 * <br><br>
 *
 * It carries the following information:
 * <ul>
 *  <li> the ID of the auction
 *  <li> the ID of the bidder
 *  <li> the bid amount
 * </ul>
 *
 * @author Matthias Weidmann
 */
public class PlaceBidMessage implements Serializable {
    private Integer auctionID;
    private Integer bidderID;
    private double amount;
    
    public PlaceBidMessage(Integer auctionID, Integer bidderID, double amount) {
        this.auctionID = auctionID;
        this.bidderID = bidderID;
        this.amount = amount;
    }
    
    public Integer getAuctionID() {
        return auctionID;
    }
    
    public Integer getBidderID() {
        return bidderID;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String toString() {
        String s = auctionID + " " + bidderID + " " + amount;
        return s;
    }
}
