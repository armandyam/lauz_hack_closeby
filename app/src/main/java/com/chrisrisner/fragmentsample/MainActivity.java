package com.chrisrisner.fragmentsample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.uepaa.p2pkit.P2PKitClient;
import ch.uepaa.p2pkit.P2PKitStatusCallback;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.StatusResultHandling;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2PListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentOne.OnFragmentInteractionListener,
            FragmentTwo.OnFragmentInteractionListener, FragmentThree.OnFragmentInteractionListener,
        FragmentFourth.OnFragmentInteractionListener{

    //First two have to come from the user when he edits FragmentOne using the pencil
    String ProfileFirstName = "Ajay      ";
    String ProfileLastName = "Rangarajan";
    //The next four have to come from the website
    String ProfileAge = "20";
    String Gender = "M";
    String ShirtColor = "White     ";
    String Glasses = "N";

    public ArrayList<String> getSharedPreferencesArrayList(String key){
        ArrayList<String> sharedArrayList = new ArrayList<String>();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        Set<String> set = sharedPreferences.getStringSet(key, null);
        if (set != null )
            for (String str : set)
                sharedArrayList.add(str);
        return sharedArrayList;
    }

    public void setSharedPreferencesArrayList(String key, ArrayList<String> sharedArrayList){
        Set<String> setsharedlist = new HashSet<String>();
        setsharedlist.addAll(sharedArrayList);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(key, setsharedlist);
        editor.commit();
    }

    public String getSharedPreferencesString(String key){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String dataToReturn= sharedPreferences.getString(key, null);
        return dataToReturn;
    }

    public void setSharedPreferencesString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private final P2PListener mP2pDiscoveryListener = new P2PListener() {

        public void checkingTags(String codedPeerInfo)
        {
            if(codedPeerInfo.length()>30)
            {
                ArrayList<String> PeerTags = new ArrayList<String>();
                PeerTags.add(codedPeerInfo.substring(0,10).replaceAll("\\s",""));
                PeerTags.add(codedPeerInfo.substring(10,20).replaceAll("\\s",""));
                PeerTags.add(codedPeerInfo.substring(20,22).replaceAll("\\s",""));
                PeerTags.add(codedPeerInfo.substring(22,23).replaceAll("\\s",""));
                PeerTags.add(codedPeerInfo.substring(23,33).replaceAll("\\s",""));
                PeerTags.add(codedPeerInfo.substring(33,34).replaceAll("\\s",""));
                int index = 34;
                while ( codedPeerInfo.length() > index && index < 200 && codedPeerInfo.substring(index, index+10).replaceAll("\\s","").length() > 0)
                {
                    PeerTags.add(codedPeerInfo.substring(index, index+10).replaceAll("\\s",""));
                    index += 10;
                }
                Log.d("P2PListener", "Peer tshirt color is " + PeerTags.get(5));

                //actually check with the tags that we have and try to find a match
                ArrayList<String> ourSearchingTags = getSharedPreferencesArrayList("SearchTags");
                for (int i =0; i < ourSearchingTags.size(); ++i){
                    List<String> items = Arrays.asList(ourSearchingTags.get(i).split("\\s*,\\s*"));
                    int count = 0;
                    for (int j = 0; j < items.size(); ++j){
                        for (int k = 0; k < PeerTags.size(); ++k ){
                            Log.d("p2pListner", "item.get(j) / Peertags.get(k) " + String.valueOf(items.get(j)) + " / " + String.valueOf(PeerTags.get(k)));
                            if (items.get(j).contains(PeerTags.get(k)) ) {
                                ++count;
                                Log.d("p2pListener", "Removing found");
                                break;
                            }
                        }
                    }
                    if (items.size() == count){
                        Log.d("p2pListener", "Succeed we found a match");
                        //succeed, we found one match.
                        String PeerTagsRaw = PeerTags.toString();
                        ArrayList<String> peersMatched = getSharedPreferencesArrayList("PeersMatched");
                        peersMatched.add(PeerTagsRaw);
                        setSharedPreferencesArrayList("PeersMatched", peersMatched);

                        //push notification
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent, 0);
                        Notification n  = new Notification.Builder(MainActivity.this)
                                .setContentTitle("CloseBy")
                                .setContentText("Peer found! Check the app.")
                                .setSmallIcon(R.drawable.cblogo)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true).build();
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, n);
                    }
                }
            }
        }

        @Override
        public void onP2PStateChanged(final int state) {
            Log.d("P2PListener", "State changed: " + state);
            //((TextView) Profile.this.findViewById(R.id.profile_title)).setText(String.valueOf(state));
        }

        @Override
        public void onPeerDiscovered(ch.uepaa.p2pkit.discovery.entity.Peer peer) {
            if (peer.getDiscoveryInfo() != null )
            {
                Log.d("P2PListener", "Peer discovered: " + peer.getNodeId() + " with info: " + new String(peer.getDiscoveryInfo()));
                checkingTags(new String(peer.getDiscoveryInfo()));

            }
            else Log.d("P2PListener", "Peer discovered: " + peer.getNodeId());
        }

        @Override
        public void onPeerLost(ch.uepaa.p2pkit.discovery.entity.Peer peer) {
            Log.d("P2PListener", "Peer lost: " + peer.getNodeId());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(ch.uepaa.p2pkit.discovery.entity.Peer peer) {
            Log.d("P2PListener", "Peer updated: " + peer.getNodeId() + " with new info: " + new String(peer.getDiscoveryInfo()));
            checkingTags(new String(peer.getDiscoveryInfo()));
        }

        @Override
        public void onProximityStrengthChanged(ch.uepaa.p2pkit.discovery.entity.Peer peer) {
            Log.d("P2pListener", "Peer " + peer.getNodeId() + " changed proximity strength: " + peer.getProximityStrength());
        }
    };

    private final P2PKitStatusCallback mStatusCallback = new P2PKitStatusCallback() {
        @Override
        public void onEnabled() {
            // ready to start discovery
            Log.d("P2PListener", "p2p kit enabled");
            try {
                String Send = ProfileFirstName + ProfileLastName + ProfileAge + Gender + ShirtColor + Glasses;
                ArrayList<String> myTags = getSharedPreferencesArrayList("MyTags");
                for (int i = 0; i < myTags.size(); ++i){
                    if (myTags.get(i).length()<10){
                        char[] chars = new char[10-myTags.get(i).length()];
                        Arrays.fill(chars, ' ');
                        Send = Send + myTags.get(i)+new String(chars);
                    }

                }
                P2PKitClient.getInstance(MainActivity.this).getDiscoveryServices().setP2pDiscoveryInfo(Send.getBytes());
            } catch (InfoTooLongException e) {
                Log.e("P2PListener", "The discovery info is too long");
            }
            P2PKitClient.getInstance(MainActivity.this).getDiscoveryServices().addP2pListener(mP2pDiscoveryListener);

            // save the first 4 numbers of my UUID
            String UUID = String.valueOf(P2PKitClient.getInstance(MainActivity.this).getNodeId()).substring(0,4);
            setSharedPreferencesString("UUID", UUID);

            TextView tv = (TextView) findViewById(R.id.profile_big_title);
            tv.setText(getSharedPreferencesString("ProfileName")+" "+getSharedPreferencesString("ProfileSurname")+"  #"+UUID);
        }

        @Override
        public void onSuspended() {
            // p2pkit is temporarily suspended
            Log.d("P2PListener", "suspended");
        }

        @Override
        public void onResumed() {
            // coming back from a suspended state
            Log.d("P2PListener", "resumed");
        }

        @Override
        public void onDisabled() {
            // p2pkit has been disabled
            Log.d("P2PListener", "disabled");
        }

        @Override
        public void onError(StatusResult statusResult) {
            // enabling failed, handle statusResult
            Log.d("P2PListener", "onError");
        }
    };

    public void updatep2p(){
        P2PKitClient client = P2PKitClient.getInstance(MainActivity.this);
        client.enableP2PKit(mStatusCallback, getResources().getString(R.string.api_key));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedPreferencesString("ProfileName", "Arnau");
        setSharedPreferencesString("ProfileSurname", "Bago");
        NukeSSLCerts.nuke();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = FragmentOne.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        Log.d("ONCreate","Just before the p2p setting");
        //setting the p2p
        final StatusResult result = P2PKitClient.isP2PServicesAvailable(MainActivity.this);
        if (result.getStatusCode() == StatusResult.SUCCESS) {
            P2PKitClient client = P2PKitClient.getInstance(MainActivity.this);
            client.enableP2PKit(mStatusCallback, getResources().getString(R.string.api_key));
        } else {
            Log.d("OnCreate", "failed to connect");
            StatusResultHandling.showAlertDialogForStatusError(this, result);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set default values as usernmane *profile name
        setSharedPreferencesString("ProfileName", "Arnau");
        setSharedPreferencesString("ProfileSurname", "Bago");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_camera) {
            fragmentClass = FragmentOne.class;
        } else if (id == R.id.nav_gallery) {
            fragmentClass = FragmentTwo.class;
        } else if (id == R.id.nav_slideshow) {
            fragmentClass = FragmentThree.class;
        } else if (id == R.id.about) {
            fragmentClass = FragmentFourth.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
