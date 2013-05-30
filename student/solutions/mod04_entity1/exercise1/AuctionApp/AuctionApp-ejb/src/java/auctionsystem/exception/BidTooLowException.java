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
 * thrown when the bid amount is not increased
 * as required by the auction's bid increment.
 */
public class BidTooLowException extends PlaceBidException {
    public BidTooLowException() {}
    
    public BidTooLowException(String msg) {
        super(msg);
    }
}
