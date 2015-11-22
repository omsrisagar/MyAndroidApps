/******************************************************************************
 * Copyright (c) 2013, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.alljoyn.cops.peergroupmanager;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Status;

/**
 * The interface a module needs to implement if it wants to interact with 
 * the groups created by the PeerGroupManager.
 *
 */
public interface PGModule {
    /**
     * register will be called by the PeerGroupManager when the 
     * registerModule() method of the PeerGroupManager is called to provide
     * the module with the AllJoyn components necessary to interact with
     * its groups
     * 
     * @param bus        the bus attachment created and used by the 
     *                   PeerGroupManager
     * @param sessionId  the session id of the group
     * @return  OK if successful
     */
    public Status register(BusAttachment bus, int sessionId);
}
