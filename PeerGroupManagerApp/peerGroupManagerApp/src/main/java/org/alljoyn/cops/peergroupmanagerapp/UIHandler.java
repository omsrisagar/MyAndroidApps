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

package org.alljoyn.cops.peergroupmanagerapp;

import org.alljoyn.bus.Status;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UIHandler extends Handler {
    
    private static final String TAG = "GroupManagerApp";

    /* UI Handler Codes */
    public static final int TOAST_MSG = 0;
    public static final int TOGGLE_DISCOVERY_BUTTONS = 1;
    public static final int UPDATE_GROUP_LIST_SPINNER = 2;
    
    private MainActivity mActivity;
    
    public UIHandler (MainActivity activity) {
        mActivity = activity;
    }
    
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case TOAST_MSG:
            Toast.makeText(mActivity.getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
            break;
        case UPDATE_GROUP_LIST_SPINNER:
            Bundle data = msg.getData();
            mActivity.updateGroupListSpinner(data.getStringArray("availableGroupList"), 
            		data.getStringArray("hostedGroupList"), 
            		data.getStringArray("joinedGroupList"), 
            		data.getStringArray("lockedGroupList"));
            break;
        default:
            break;
        }
    }
    
    public void logInfo(String msg) {
        Log.i(TAG, msg);
    }
    
    public void logStatus(String msg, Status status) {
        String log = String.format("%s: %s", msg, status);
        if (status == Status.OK) {
            Log.i(TAG, log);
        } else {
            Message toastMsg = obtainMessage(TOAST_MSG, log);
            sendMessage(toastMsg);
            Log.e(TAG, log);
        }
    }

    public void logError(String msg) {
        Message toastMsg = obtainMessage(TOAST_MSG, msg);
        sendMessage(toastMsg);
        Log.e(TAG, msg);
    }

    public void logException(String msg, Exception ex) {
        String log = String.format("%s: %s", msg, ex);
        Message toastMsg = obtainMessage(TOAST_MSG, log);
        sendMessage(toastMsg);
        Log.e(TAG, log, ex);
    }
}
