/*
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package auctionsystem.exception;

/** This is the exception which is thrown
 * when placing a bid is not possible.
 */
public class PlaceBidException extends AuctionException {
    public PlaceBidException() {}
    
    public PlaceBidException(String msg) {
        super(msg);
    }
}
