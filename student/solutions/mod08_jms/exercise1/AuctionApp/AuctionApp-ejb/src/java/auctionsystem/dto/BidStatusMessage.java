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

/** This is the class of the data
 * transfer object which is sent back to
 * JMS clients when a bid has been
 * placed.
 * <br><br>
 *
 * It carries the following information:
 * <ul>
 * 	<li> the auction status
 * 	<li> the bidder ID
 *	<li> the bid amount
 * </ul>
 */
public class BidStatusMessage implements Serializable {
    private String status;
    private Integer bidderID;
    private double bidAmount;
    
    public BidStatusMessage(String status, Integer bidderID, double bidAmount) {
        this.status = status;
        this.bidderID = bidderID;
        this.bidAmount = bidAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Integer getBidderID() {
        return bidderID;
    }
    
    public double getBidAmount() {
        return bidAmount;
    }
    
    public String toString() {
        String s = "\n \t \n \t auction status = " + status +
                "\n \t BidderID = " + bidderID + "\n \t bidAmount = " + bidAmount;
        return s;
    }
}
