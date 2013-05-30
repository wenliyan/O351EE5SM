/*
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package auctionsystem.exception;

/** This is the parent class for the application
 * exceptions defined by the auction system.
 */
public class AuctionException extends Exception {
    public AuctionException() {}
    
    public AuctionException(String msg) {
        super(msg);
    }
}
