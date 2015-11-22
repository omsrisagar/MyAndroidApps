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

import java.util.ArrayList;

import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Status;

interface PeerGroupManagerInterface {
    
    public Status createGroup (String groupName);
    
    public Status createGroup (String groupName, boolean locked); 
    
    public Status destroyGroup (String groupName);
    
    public Status joinGroup (String groupName);
    
    public Status leaveGroup (String groupName);
    
    public JoinOrCreateReturn joinOrCreateGroup(String groupName); 
    
    public void cleanup();
    
    public Status unlockGroup(String groupName);
    
    public Status lockGroup(String groupName);
    
    public ArrayList<String> listFoundGroups();
    
    public ArrayList<String> listHostedGroups();
    
    public ArrayList<String> listJoinedGroups();
    
    public ArrayList<String> listLockedGroups();
    
    public ArrayList<String> getPeers (String groupName);
    
    public int getNumPeers(String groupName);
    
    public void addPeerGroupListener(PeerGroupListenerInterface peerGroupListener);

    public <T> T getRemoteObjectInterface(String peerId, String groupName, String objectPath, Class<T> iface);
    
    public <T> T getSignalInterface(String groupName, BusObject busObject, Class<T> iface);
    
    public <T> T getSignalInterface(String peerId, String groupName, BusObject busObject, Class<T> iface);
    
    public Status registerModule(PGModule module, String groupName);
    
    public String getGroupPrefix();
    
    public Status registerSignalHandlers(Object classWithSignalHandlers);
    
    public String getMyPeerId();
    
    public String getGUID();
    
    public String getSenderPeerId();
    
    public String getGroupHostPeerId(String groupName);
    
    public void setSessionPort(short sessionPort);
}
