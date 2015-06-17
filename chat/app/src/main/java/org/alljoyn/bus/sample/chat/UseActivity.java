/*
 * Copyright (c) 2011, AllSeen Alliance. All rights reserved.
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
 */

package org.alljoyn.bus.sample.chat;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.sample.chat.ChatApplication;
import org.alljoyn.bus.sample.chat.Observable;
import org.alljoyn.bus.sample.chat.Observer;
import org.alljoyn.bus.sample.chat.DialogBuilder;

import Jama.Matrix;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.SystemClock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UseActivity extends Activity implements Observer {
    private static final String TAG = "chat.UseActivity";
    
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.use);
        
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//http://developer.android.com/reference/android/content/Context.html#registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)
        // Receive a broadcast notification whenever battery level changes.
        mHistoryList = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        ListView hlv = (ListView) findViewById(R.id.useHistoryList);
        hlv.setAdapter(mHistoryList);
        
        EditText messageBox = (EditText)findViewById(R.id.useMessage);
        messageBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                	String message = view.getText().toString();
                    Log.i(TAG, "useMessage.onEditorAction(): got message " + message + ")");
    	            mChatApplication.newLocalUserMessage(message);
    	            view.setText(""); //emptying the edittext area
                }
                return true;
            }
        });
                
        mServiceAdvertisement = (Button) findViewById(R.id.serviceAdvertisement);
        mServiceAdvertisement.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				services = "Info "+batteryStatus;
				setUpDeviceCharacteristics();
		        mHandler.postDelayed(startAdvertising, time_interval);
		        mHandler.postDelayed(getArbitrator, time_interval2);
			}
        	
        });
        
        mJoinButton = (Button) findViewById(R.id.useJoin);
        mJoinButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_JOIN_ID);
			}
		});
        
        mLeaveButton = (Button) findViewById(R.id.useLeave);
        mLeaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DIALOG_LEAVE_ID);
			}
		});
        
        mButtonSelectImage = (Button) findViewById(R.id.button_image_select);
		mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent imagePickIntent = new Intent(Intent.ACTION_PICK);
				imagePickIntent.setType("image/*");
				startActivityForResult(imagePickIntent, PICTURE_SELECTED);
				
			}
		});
        
        mChannelName = (TextView)findViewById(R.id.useChannelName);
        mChannelStatus = (TextView)findViewById(R.id.useChannelStatus);
        
        /*
         * Keep a pointer to the Android Appliation class around.  We use this
         * as the Model for our MVC-based application.    Whenever we are started
         * we need to "check in" with the application so it can ensure that our
         * required services are running.
         */
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.checkin();
        
        /*
         * Call down into the model to get its current state.  Since the model
         * outlives its Activities, this may actually be a lot of state and not
         * just empty.
         */
        updateChannelState();
        updateHistory();
        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        mChatApplication.addObserver(this);

    }
    
    // Broadcast reciever for battery status info -- Rohan
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
          int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
          batteryStatus = (String.valueOf(level) + "%");
        }
      };
    
	public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.deleteObserver(this);
    	super.onDestroy();
	}
    
    public static final int DIALOG_JOIN_ID = 0;
    public static final int DIALOG_LEAVE_ID = 1;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 2;
    public String services;

    protected Dialog onCreateDialog(int id) {
    	Log.i(TAG, "onCreateDialog()");
        Dialog result = null;
        switch(id) {
        case DIALOG_JOIN_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createUseJoinDialog(this, mChatApplication);
	        }        	
        	break;
        case DIALOG_LEAVE_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createUseLeaveDialog(this, mChatApplication);
	        }
	        break;
        case DIALOG_ALLJOYN_ERROR_ID:
	        { 
	        	DialogBuilder builder = new DialogBuilder();
	        	result = builder.createAllJoynErrorDialog(this, mChatApplication);
	        }
	        break;	        
        }
        return result;
    }
    
    public synchronized void update(Observable o, Object arg) {
        Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String)arg;
        
        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.USE_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }
        
        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }
    
    private void updateHistory() {
        Log.i(TAG, "updateHistory()");
	    mHistoryList.clear();
	    List<String> messages = mChatApplication.getHistory(); //returns a clone of mHistory in ChatApp
        for (String message : messages) {
            mHistoryList.add(message);
        }
	    mHistoryList.notifyDataSetChanged();
    }
    
    private void updateChannelState() {
        Log.i(TAG, "updateHistory()");
    	AllJoynService.UseChannelState channelState = mChatApplication.useGetChannelState();
    	String name = mChatApplication.useGetChannelName();
    	if (name == null) {
    		name = "Not set";
    	}
        mChannelName.setText(name);
        switch (channelState) {
        case IDLE:
            mChannelStatus.setText("Idle");
            mJoinButton.setEnabled(true);
            mLeaveButton.setEnabled(false);
            mServiceAdvertisement.setEnabled(false);
            mButtonSelectImage.setEnabled(true);
            break;
        case JOINED:
            mChannelStatus.setText("Joined");
            mJoinButton.setEnabled(false);
            mLeaveButton.setEnabled(true);
            mServiceAdvertisement.setEnabled(true);
            mButtonSelectImage.setEnabled(true);
            break;	
        }
    }
    
    /**
     * An AllJoyn error has happened.  Since this activity pops up first we
     * handle the general errors.  We also handle our own errors.
     */
    private void alljoynError() {
    	if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
    		mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
    		showDialog(DIALOG_ALLJOYN_ERROR_ID);
    	}
    }
    
    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;
    
    private static final int time_interval = 60000; // Time interval for service advertisements
    private static final int time_interval2 = 60000;
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_APPLICATION_QUIT_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
	                finish();
	            }
	            break; 
            case HANDLE_HISTORY_CHANGED_EVENT:
                {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
                    updateHistory();
                    break;
                }
            case HANDLE_CHANNEL_STATE_CHANGED_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
	                updateChannelState();
	                break;
	            }
            case HANDLE_ALLJOYN_ERROR_EVENT:
	            {
	                Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
	                alljoynError();
	                break;
	            }
            default:
                break;
            }
        }
    };
    
    // Method to start advertising the object containing information
    public void startAdvertising() throws IOException{

    	if(mChatApplication.useGetChannelState() == AllJoynService.UseChannelState.JOINED){//What is the need to check for this? You advertise only after you joined. Advertisement button is disabled before joining.

            //get string version of the serialized version of the object to advertise

            String toSend = setUpObjectForServiceAdvertisement();
            Log.d(TAG, "Service Advertisement object created!");
            mChatApplication.newLocalServiceAdvertisement(toSend);
            
            /*synchronized(mChatApplication.getListOfPeers()){
            	for(Map.Entry<String, Integer> e: mChatApplication.getListOfPeers().entrySet()){
            		e.setValue(0);
            	}
            }*/
        }
        else{
            		//do nothing
        }
    }
    
    // Runnable object to start advertising
    Runnable startAdvertising = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				setUpDeviceCharacteristics();
				startAdvertising();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.postDelayed(startAdvertising, time_interval);
		}
    	
    };
    
    // Runnable object to select the arbitrator
    Runnable getArbitrator = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			/*synchronized(mChatApplication.getListOfPeers()){
            	for(Map.Entry<String, Integer> e: mChatApplication.getListOfPeers().entrySet()){
            		if(e.getValue() == 0){
            			mChatApplication.getListOfPeers().remove(e.getKey());
            		}
            	}
            }*/
			if(mChatApplication.getHashtable().isEmpty() == false){
				Log.d(TAG,mChatApplication.getArbitrator());
				mChatApplication.setArbitrator(mChatApplication.getArbitrator());//Returns the device having highest battery %
			}
			else{
				Log.d(TAG,"No entry present in the hashtable");
			}
			
			mHandler.postDelayed(getArbitrator, time_interval2);
		}
    	
    };
    
    // Method to set up the object to send
    private String setUpObjectForServiceAdvertisement(){
    	try{
    		s = new ServAdv();
    		s.setVarBatteryStatus(services);
    		s.setVarCPUUsage(readCPUUsage());
    		s.setVarMemoryUsage(readMemoryUsage());
    		s.setVarCurrentVoltage(readCurrentnVoltage());
    		s.setModelName(readModelName());
    		ArrayList<String> temp = new ArrayList<String>();
    		temp.add("Bat");
    		temp.add("Ball");
    		s.setListOfDictionaries(temp);
    		ByteArrayOutputStream bo = new ByteArrayOutputStream(); //instead of file output stream (.ser file)
    		ObjectOutputStream so = new ObjectOutputStream(bo);
    		so.writeObject(s);
    		so.flush();
    		return bo.toString("ISO-8859-1");
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    public void setUpDeviceCharacteristics(){
    	mChatApplication.getHashtable().put(services,"Me"); //put(key,value)
		mChatApplication.getCPUHashtable().put("Me", readCPUUsage() );
     	mChatApplication.getMemoryUsageHashtable().put("Me", readMemoryUsage() );
     	mChatApplication.getCurrentVoltageHashtable().put("Me", readCurrentnVoltage() );
     	mChatApplication.getHashtableDevice().put("Me", readModelName());
    }
    
    public String readModelName(){
    	return android.os.Build.MODEL;
    }
    // Generic method to read CPU usage
    
    /*private String readCPUUsage() {
    	
    	try{
    		RandomAccessFile r = new RandomAccessFile("/proc/stat", "r");
    		int numberOfProcessors = Runtime.getRuntime().availableProcessors();
    		long[] idle = new long[numberOfProcessors];
    		long[] cpu = new long[numberOfProcessors];
    		String load;
    		for(int i=0; i<numberOfProcessors; i++){
    			load = r.readLine();
    			String[] toks = load.split(" ");
    			// Read the CPU usage
    	        idle[i] = Long.parseLong(toks[5]);
    	        cpu[i] = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
    	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]); 
    	        try {
    	            Thread.sleep(360);
    	        } catch (Exception e) {}
    		}
    		
     
    	}
    	catch (IOException ex) {
            ex.printStackTrace();
        }
    	
		return null;
	    
	} */
    
    private float readCPUUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                  + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
    
    private String ReadCPUinfo()
    {
    	ProcessBuilder cmd;
    	String result="";

    	try{
    		String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
    		cmd = new ProcessBuilder(args);

    		Process process = cmd.start();
    		InputStream in = process.getInputStream();
    		byte[] re = new byte[1024];
    		while(in.read(re) != -1){
    			if(new String(re).contains("BogoMIPS")){
    				result = new String(re);
    			}
    			
    		}
    		in.close();
    	} catch(IOException ex){
    		ex.printStackTrace();
    	}
    	return result;
    }
    
    // Method to read current memory usage
    private Long readMemoryUsage() {
    	
    	ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
    	ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo ();
    	actvityManager.getMemoryInfo( mInfo );
    	// Print to log and read in DDMS
    	return mInfo.availMem;

    }
    
    // Method to read current voltage
    private Long readCurrentnVoltage() {
        // samsung galaxy tab 10.1          
        File f = new File("/sys/class/power_supply/battery/batt_current");
        /*if (f.exists()){
        	Current = Long.toString(getValue(f, false));
        }*/
        f = new File("/sys/class/power_supply/battery/batt_vol");
        if (f.exists()){
        	return getValue(f, false);
        }
        
        return (long) 0.00;
	}

    
    public Long getValue(File _f, boolean _convertToMillis) {
        String text = null;
        try {
                FileInputStream fs = new FileInputStream(_f);
                DataInputStream ds = new DataInputStream(fs);
                text = ds.readLine();
                ds.close();             
                fs.close();     
        }

        catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage());
                ex.printStackTrace();
        }
        
        Long value = null;
        if (text != null)
        {
                try
                {
                        value = Long.parseLong(text);
                }
                catch (NumberFormatException nfe)
                {
                        Log.e("CurrentWidget", nfe.getMessage());
                        value = null;
                }
                
                if (_convertToMillis && value != null)
                        value = value/1000; // convert to milliampere
        }
        return value;
	}
    
    private ChatApplication mChatApplication = null;
    
    private ArrayAdapter<String> mHistoryList;
    
    //Button for service advertisements -- Rohan
    private Button mServiceAdvertisement;
    private Button mJoinButton;
    private Button mLeaveButton;
    
    private ServAdv s;
    
    // String for battery status -- Rohan
    private String batteryStatus;
    
    private TextView mChannelName;
      
    private TextView mChannelStatus;
    
    //-----------------------------------------------------------------------------------------------
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case PICTURE_SELECTED:
	        if(resultCode == RESULT_OK){  
	        	Uri selectedImage = imageReturnedIntent.getData();
	            InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            Bitmap mySelectedImage = BitmapFactory.decodeStream(imageStream);
	            /*DictionaryLearning(showArray(mySelectedImage));
	            sparseCoding();
	            displayDict();*/
	        }
	    }
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	private Button mButtonSelectImage;
	private static final int PICTURE_SELECTED = 1;
	
}



