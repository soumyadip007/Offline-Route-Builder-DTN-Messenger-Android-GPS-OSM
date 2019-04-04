package com.example.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class wifiMVC extends MenuDemo {


    static final int MESSAGE_READ = 1;
    Button bton, btdisc, btsend;
    ListView listView;
    TextView read_msg_box, connect;
    EditText write;
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;
    BroadcastReceiver pRec;
    IntentFilter pIntentFilter;
    WifiP2pManager pManager;
    WifiP2pManager.Channel pChannel;
    WifiManager wifiManager;
    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readbuff = (byte[]) msg.obj;
                    String tmpMsg = new String(readbuff, 0, msg.arg1);
                    read_msg_box.setText(tmpMsg);
                    break;

            }
            return true;
        }
    });
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerlist) {

            if (!peerlist.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerlist.getDeviceList());

                deviceNameArray = new String[peerlist.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerlist.getDeviceList().size()];
                int index = 0;

                Toast.makeText(getApplicationContext(), "Accessing Peers", Toast.LENGTH_SHORT).show();


                for (WifiP2pDevice device : peerlist.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    Toast.makeText(getApplicationContext(), "Adding Peers", Toast.LENGTH_SHORT).show();

                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);

            }

            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {

            final InetAddress groupOwnerAddress = info.groupOwnerAddress;


            if (info.groupFormed && info.isGroupOwner) {
                connect.setText("Host");
                serverClass = new ServerClass();
                serverClass.start();
            } else {
                connect.setText("Client");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intitialwork();
        exqListner();
    }

    private void exqListner() {

        bton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    bton.setText("On Wifi");

                } else {
                    wifiManager.setWifiEnabled(true);
                    bton.setText("Off Wifi");

                }
            }

        });

        btdisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pManager.discoverPeers(pChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connect.setText("Discovering started");
                    }

                    @Override
                    public void onFailure(int i) {
                        connect.setText("Discovery Starting Failed");
                    }
                });
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final WifiP2pDevice device = deviceArray[position];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                pManager.connect(pChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                        Toast.makeText(getApplicationContext(), "Connected to" + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {

                        Toast.makeText(getApplicationContext(), "Not connceted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = write.getText().toString();
                sendReceive.write(msg.getBytes());
            }
        });

    }

    private void intitialwork() {

        bton = findViewById(R.id.onOff);
        btdisc = findViewById(R.id.discover);
        btsend = findViewById(R.id.sendButton);
        listView = findViewById(R.id.peerListView);
        read_msg_box = findViewById(R.id.readMsg);
        connect = findViewById(R.id.connectionStatus);
        write = findViewById(R.id.writeMsg);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        pChannel = pManager.initialize(this, getMainLooper(), null);

        pRec = new WifiDirectBrodcastRcv(pManager, pChannel, this);

        pIntentFilter = new IntentFilter();
        pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(pRec);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(pRec, pIntentFilter);

    }

    private class SendReceive extends Thread {

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {

            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();

        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);

                sendReceive = new SendReceive(socket);
                sendReceive.start();


            } catch (IOException e) {

            }

        }
    }

}
/* public void onoff(View v)
 {
     if(wifiManager.isWifiEnabled())
     {
         wifiManager.setWifiEnabled(false);
         bton.setText("On Wifi");

     }
     else
     {

         wifiManager.setWifiEnabled(true);
         bton.setText("Off Wifi");

     }

 }*/

