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
 * when an auction is accessed which is not
 * open.
 */
public class CloseException extends AuctionException {
    public CloseException() {}
    
    public CloseException(String msg) {
        super(msg);
    }
}
