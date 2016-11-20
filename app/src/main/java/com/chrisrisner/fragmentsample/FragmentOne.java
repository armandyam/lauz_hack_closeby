package com.chrisrisner.fragmentsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.os.*;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import cz.msebera.android.httpclient.*;

import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentOne.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOne extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    MySimpleArrayAdapter adapter;
    ArrayList<String> listItems=new ArrayList<String>();
    private ListView saveListView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View myFragmentView;

    public String getSharedPreferencesString(String key){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String dataToReturn= sharedPreferences.getString(key, null);
        return dataToReturn;
    }

    public void setSharedPreferencesString(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public ArrayList<String> getSharedPreferencesArrayList(String key){
        ArrayList<String> sharedArrayList = new ArrayList<String>();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        Set<String> set = sharedPreferences.getStringSet(key, null);
        if (set != null )
            for (String str : set)
                sharedArrayList.add(str);
        return sharedArrayList;
    }

    public void setSharedPreferencesArrayList(String key, ArrayList<String> sharedArrayList){
        Set<String> setsharedlist = new HashSet<String>();
        setsharedlist.addAll(sharedArrayList);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(key, setsharedlist);
        editor.commit();
    }

    private OnFragmentInteractionListener mListener;

    public FragmentOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOne newInstance(String param1, String param2) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e("MakeServiceCall", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("MakeServiceCall", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("MakeServiceCall", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("MakeServiceCall", "Exception: " + e.getMessage());
        }
        return response;
    }

    private StringBuffer request(String urlString) {
        // TODO Auto-generated method stub

        StringBuffer chaine = new StringBuffer("");
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }
        return chaine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Setting up the list view of the profiles with the tags

        final View rootView = inflater.inflate(R.layout.fragment_fragment_one,
                container, false);
        ImageView iv = (ImageView) rootView.findViewById(R.id.imageView8);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> setOfString = getSharedPreferencesArrayList("MyTags");
                EditText et = (EditText) rootView.findViewById(R.id.editTextProfile);
                setOfString.add(et.getText().toString());
                setSharedPreferencesArrayList("MyTags", setOfString);
                String[] aux = new String[setOfString.size()];
                aux = setOfString.toArray(aux);

                //update the listview
                saveListView = (ListView) rootView.findViewById(R.id.list_view);
                adapter=new MySimpleArrayAdapter(getActivity(),
                        aux);
                saveListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //MainActivity.updatep2p();
            }
        });
        saveListView = (ListView) rootView.findViewById(R.id.list_view);
        ArrayList<String> setOfString = getSharedPreferencesArrayList("MyTags");
        String[] aux = new String[setOfString.size()];
        aux = setOfString.toArray(aux);
        adapter=new MySimpleArrayAdapter(getActivity(),
                aux);
        saveListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ListView lv = (ListView) rootView.findViewById(R.id.list_view);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> setOfString = getSharedPreferencesArrayList("MyTags");
                setOfString.remove(i);
                setSharedPreferencesArrayList("MyTags", setOfString);
                setOfString = getSharedPreferencesArrayList("MyTags");
                String[] aux = new String[setOfString.size()];
                aux = setOfString.toArray(aux);
                adapter=new MySimpleArrayAdapter(getActivity(),
                        aux);
                saveListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //MainActivity.updatep2p();
            }
        });

        ImageView refresh = (ImageView) rootView.findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RetrieveFeedTask().execute("skdfhj");
            }
        });

        ImageView addName = (ImageView) rootView.findViewById(R.id.edit_name_profile);
        addName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) rootView.findViewById(R.id.editTextName);
                String nameAndSurname = et.getText().toString();
                TextView tv = (TextView) rootView.findViewById(R.id.profile_big_title);
                tv.setText(nameAndSurname +"    #" +getSharedPreferencesString("UUID"));
                String[] arraynamesur = nameAndSurname.split(" ");
                String name = arraynamesur[0];
                String surname = arraynamesur[1];
                setSharedPreferencesString("ProfileName", name);
                setSharedPreferencesString("ProfileSurname", surname);
                ArrayList<String> myTags = getSharedPreferencesArrayList("MyTags");
                myTags.add(name);
                myTags.add(surname);
                setSharedPreferencesArrayList("MyTags", myTags);

                String[] aux = new String[myTags.size()];
                aux = myTags.toArray(aux);
                adapter=new MySimpleArrayAdapter(getActivity(),
                        aux);
                saveListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            HttpClient Client = new DefaultHttpClient();
            try {

                String url = "https://lauzhack.herokuapp.com/id/"+getSharedPreferencesString("UUID");
                Log.d("htmlrequest", "Requesting url: " +url);
                HttpGet httpget = new HttpGet(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String response = Client.execute(httpget, responseHandler);
                try {

                    JSONObject obj = new JSONObject(response);
                    Iterator<String> iter = obj.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = obj.get(key);
                            ArrayList<String> myTags = getSharedPreferencesArrayList("MyTags");
                            myTags.add(String.valueOf(value));
                            setSharedPreferencesArrayList("MyTags", myTags);
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }


                    Log.d("My App", obj.toString());

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }


                Log.d("htmlrequest", response);
                return response;

            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(StringBuffer feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
