package com.example.stocks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentChart2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChart2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static String ticker;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentChart2(String ticker) {
        // Required empty public constructor
        this.ticker=ticker;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentChart2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentChart2 newInstance(String param1, String param2) {
        FragmentChart2 fragment = new FragmentChart2(ticker);
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
        return inflater.inflate(R.layout.fragment_chart2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        WebView webView2;
        webView2= (WebView) getView().findViewById(R.id.webViewChart2);
        WebSettings webSettings= webView2.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView2.loadUrl("file:///android_asset/histChart2.html?ticker="+ticker);

    }
}