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

/**
 * The PeerGroupListener is a listener that provides convenient empty
 * implementations of the PeerGroupListenerInterface methods, so that only the
 * desired call-back methods need to be overridden. The call-back methods are
 * equivalent to the foundAdvertisedname(), lostAdvertisedName(), sessionLost(),
 * sessionMemberAdded(), and sessionMemberRemoved() signals of AllJoyn.
 */
public class PeerGroupListener implements PeerGroupListenerInterface {
    
    /**
     * Called when a new group advertisement is found. This will not be
     * triggered for your own hosted groups.
     * 
     * @param groupName  the groupName that was found
     * @param transport  the transport that the groupName was discovered on
     */
    @Override
    public void foundAdvertisedName(String groupName, short transport) {
        return;
    }
    
    /**
     * Called when a group that was previously reported through 
     * foundAdvertisedName has become unavailable. This will not be triggered
     * for your own hosted groups.
     * 
     * @param groupName  the group name that has become unavailable
     * @param transport  the transport that stopped receiving the groupName 
     *                   advertisement
     */
    @Override
    public void lostAdvertisedName(String groupName, short transport) {
        return;
    }
    
    /**
     * Called when a group becomes disconnected.
     * 
     * @param groupName  the group that became disconnected
     */
    @Override
    public void groupLost(String groupName) {
        return;
    }
    
    /**
     * Called when a new peer joins a group.
     * 
     * @param peerId     the id of the peer that joined 
     * @param groupName  the group that the peer joined
     * @param numPeers   the current number of peers in the group
     */
    @Override
    public void peerAdded(String peerId, String groupName, int numPeers) {
        return;
    }
    
    /**
     * Called when a new peer leaves a group.
     * 
     * @param peerId     the id of the peer that left 
     * @param groupName  the group that the peer left
     * @param numPeers   the current number of peers in the group
     */
    @Override
    public void peerRemoved(String peerId, String groupName, int numPeers) {
        return;
    }
}
