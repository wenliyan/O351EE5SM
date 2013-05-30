/*
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package auctionsystem.exception;

/** This is a subclass of the
 * <code>PlaceBidException</code> which is
 * thrown when a to-be-placed bid has a bid
 * amount which is smaller than the current
 * highest bid amount in the auction.
 */
public class OutbidException extends PlaceBidException {
    public OutbidException() {}
    
    public OutbidException(String msg) {
        super(msg);
    }
}
