package com.chrisrisner.fragmentsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTwo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView saveListView;
    MySimpleArrayAdapter2 adapter;

    private OnFragmentInteractionListener mListener;

    public FragmentTwo() {
        // Required empty public constructor
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTwo newInstance(String param1, String param2) {
        FragmentTwo fragment = new FragmentTwo();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_fragment_two,
                container, false);
        ImageView setTagButton = (ImageView) rootView.findViewById(R.id.setTagButton);
        setTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> setOfString = getSharedPreferencesArrayList("SearchTags");
                EditText et = (EditText) rootView.findViewById(R.id.editTextTag);
                setOfString.add(et.getText().toString());
                setSharedPreferencesArrayList("SearchTags", setOfString);
                String[] aux = new String[setOfString.size()];
                aux = setOfString.toArray(aux);

                //update the listview
                saveListView = (ListView) rootView.findViewById(R.id.tagListView);
                adapter=new MySimpleArrayAdapter2(getActivity(),
                        aux);
                saveListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        ArrayList<String> setOfString = getSharedPreferencesArrayList("SearchTags");
        String[] aux = new String[setOfString.size()];
        aux = setOfString.toArray(aux);
        //update the listview
        saveListView = (ListView) rootView.findViewById(R.id.tagListView);
        adapter=new MySimpleArrayAdapter2(getActivity(),
                aux);
        saveListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ListView lv = (ListView) rootView.findViewById(R.id.tagListView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<String> setOfString = getSharedPreferencesArrayList("SearchTags");
                setOfString.remove(i);
                setSharedPreferencesArrayList("SearchTags", setOfString);
                setOfString = getSharedPreferencesArrayList("SearchTags");
                String[] aux = new String[setOfString.size()];
                aux = setOfString.toArray(aux);
                adapter=new MySimpleArrayAdapter2(getActivity(),
                        aux);
                saveListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
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
