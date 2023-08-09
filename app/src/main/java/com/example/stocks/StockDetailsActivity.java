package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockDetailsActivity extends AppCompatActivity {
    ImageView ivImgLogo, ivTrendingIcon;
    WebView webView;
    Button tradingButton,buyButton,sellButton, doneButton;
    Menu menu;
    MenuItem starIcon;
    TextView tvTicker, tvCurrentPrice, tvCompanyName,tvPriceAndPercentChange, tvOpenPrice, tvLowPrice, tvHighPrice, tvPreviousClose, tvIpoStartDate, tvIndustry, tvWebpage;
    TextView tvTradeCompleteText;
    TextView tvPortfolioSharesOwned, tvPortfolioAvgCost, tvPortfolioTotalCost, tvPortfolioChange, tvPortfolioMarketValue;
    TextView tvRedditTotal, tvTwitterTotal, tvRedditPositive, tvTwitterPositive, tvRedditNegative, tvTwitterNegative, tvSocialSentimentCompany;
    String ticker;
    String logourl;
    String onPeerIntent;
    ProgressBar spinner;
    int totalSharesToTrade;
    String myapp="myapp";
    boolean starIconFlag=false;
    float totalCostCurrentTransaction;
    String colorPriceChange,openPrice,lowPrice,highPrice,previousClose,ipoStartDate,industry,webpageUrl;
    String currentPriceToStoreFav;
    ArrayList<String> newsDatetime=new ArrayList<String>();
    ArrayList<String> newsHeadline=new ArrayList<String>();
    ArrayList<String> newsImageUrl=new ArrayList<String>();
    ArrayList<String> newsSource=new ArrayList<String>();
    ArrayList<String> newsSummary=new ArrayList<String>();
    ArrayList<String> newsDetailUrl=new ArrayList<String>();
    ArrayList<String> portfolioTickerList=new ArrayList<String>();
    ArrayList<String> portfolioTotalSharesList=new ArrayList<String>();
    ArrayList<String> portfolioTotalCostList=new ArrayList<String>();
    ArrayList<String> portfolioMarketValueList=new ArrayList<String>();
    ArrayList<String> portfolioPriceChangeList=new ArrayList<String>();
    ArrayList<String> portfolioPercentPriceChangeList=new ArrayList<String>();

    ArrayList<String> favoritesTickerList=new ArrayList<String>();
    ArrayList<String> favoritesCompanyNameList=new ArrayList<String>();
    ArrayList<String> favoritesCurrentPriceList=new ArrayList<String>();
    ArrayList<String> favoritesPriceChangeList=new ArrayList<String>();
    ArrayList<String> favoritesPercentPriceChangeList=new ArrayList<String>();
    ArrayList<String> companyPeers=new ArrayList<String>();
    ArrayList<String> netWorthList=new ArrayList<>();
    ArrayList<String> balanceList=new ArrayList<>();
    ArrayList<String> marketValueNetWorthCalculationList=new ArrayList<>();

    int redditMention=0;
    int redditPositiveMention;
    int redditNegativeMention;
    int twitterMention=0;
    int twitterPositiveMention=0;
    int twitterNegativeMention=0;
    float currentPriceStockCalculation;
    int tempTotalShares=0;
    NumberFormat formatter = new DecimalFormat("#0.00");
    String companyName, currentPrice, priceChange, percentPriceChange, priceChangeAndPercentPriceChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        onPeerIntent="";
        Intent myIntent=getIntent();
        //Getting stock values from intent and setting them to local variables
        Log.d(myapp,"Getting stock values from intent and setting them to local variables");
        ticker=myIntent.getStringExtra("TickerValue");
        onPeerIntent=myIntent.getStringExtra("PeerValue");
        //Log.d("peerintent","value received is "+onPeerIntent);
        Log.d("intentcheck","value received is "+ticker);
        //Setting back button on current screen
        Log.d(myapp,"Setting back button on current screen");
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner=(ProgressBar) findViewById(R.id.progressBarStockDetailsPage);
        spinner.setVisibility(View.VISIBLE);
        spinner.bringToFront();

        //
        //get values from intent

        apiCallTopMostSection();
        apiCallPriceChangeSection();
        apiCallNewsSection();
        apiCallCompanyPeers();
//        apiCallSocialSentimentData();
        setRecommendationsChart();
        setHistoricalEpsChart();
        handleTrading();


        //Setting stock name as title bar text
        Log.d(myapp,"Setting stock name as title bar text");
        getSupportActionBar().setTitle(ticker);
        TabLayout tabLayout=findViewById(R.id.tabLayoutCharts);
        ViewPager2 viewPager2=findViewById(R.id.viewPagerChart);

        ViewPagerAdapter adapter=new ViewPagerAdapter(this, ticker);
        viewPager2.setAdapter(adapter);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#0000EE"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("tabselect","tab changed");
               // tab.getIcon().setColorFilter(Integer.parseInt("#0000EE"), PorterDuff.Mode.SRC_IN);
                tab.getIcon().setColorFilter(Color.BLUE,PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.BLACK,PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position==0){
                            tab.setIcon(R.drawable.chart_line);

                        }
                        else if(position==1){
                            tab.setIcon(R.drawable.clock_time_three);
                        }
                    }
                }).attach();






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu on stock details page
        Log.d(myapp,"Inflating the menu on stock details page");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_stockdetails, menu);
        this.menu=menu;
        //find starIcon on menu
        starIcon=this.menu.findItem(R.id.starIconFav);
        // initialize the star icon based on if it is present in local storage
        initializeStarIcon();
        return super.onCreateOptionsMenu(menu);
    }

    public void initializeStarIcon(){
        Log.d(myapp,"Creating arraylists for temp storage for ticker and company names");
        ArrayList<String> favoritesTickerList=new ArrayList<String>();
        ArrayList<String> favoritesCompanyNameList=new ArrayList<String>();
        Log.d(myapp,"Creating editor for favorites local storage to read");
        SharedPreferences favoritesSharedPref= getSharedPreferences("favoritesAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor=favoritesSharedPref.edit();
        Log.d(myapp,"Creating gson object for ticker and company name");
        Gson gsonTicker=new Gson();
        Gson gsonCompanyName=new Gson();
        String responseTicker=favoritesSharedPref.getString("keyTickerLocal","[]");
        String responseCompanyName=favoritesSharedPref.getString("keyCompanyNameLocal","[]");
        favoritesTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
        if(favoritesTickerList.contains(ticker)){
            starIcon.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_starfilled));
            starIconFlag=true;
        }
        else{
            starIcon.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_startoutline));
            starIconFlag=false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("iddd","id is"+R.id.starIconFav);
        Log.d("iddd","id is"+item.getItemId());
        if(item.getItemId()==R.id.starIconFav){
            if(starIconFlag){
                starIconFlag=false;
                starIcon.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_startoutline));
                //display toast
                Toast.makeText(StockDetailsActivity.this,ticker+" is removed from favorites", Toast.LENGTH_LONG).show();

                //remove from local storage
                SharedPreferences favoritesSharedPref= getSharedPreferences("favoritesAndroidLocal", MODE_PRIVATE);
                SharedPreferences.Editor editor=favoritesSharedPref.edit();
                Gson gsonTicker=new Gson();
                Gson gsonCompanyName=new Gson();
                Gson gsonCurrentPrice=new Gson();
                Gson gsonPriceChange=new Gson();
                Gson gsonPercentPriceChange=new Gson();
                String responseTicker=favoritesSharedPref.getString("keyTickerLocal","[]");
                String responseCompanyName=favoritesSharedPref.getString("keyCompanyNameLocal","[]");
                String responseCurrentPrice=favoritesSharedPref.getString("keyCurrentPriceFavLocal","[]");
                String responsePriceChange=favoritesSharedPref.getString("keyPriceChangeFavLocal","[]");
                String responsePercentPriceChange=favoritesSharedPref.getString("keyPercentPriceChangeFavLocal","[]");
                favoritesTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
                favoritesCompanyNameList=gsonCompanyName.fromJson(responseCompanyName, new TypeToken<List<String>>() {}.getType());
                favoritesCurrentPriceList=gsonCurrentPrice.fromJson(responseCurrentPrice, new TypeToken<List<String>>() {}.getType());
                favoritesPriceChangeList=gsonPriceChange.fromJson(responsePriceChange, new TypeToken<List<String>>() {}.getType());
                favoritesPercentPriceChangeList=gsonPercentPriceChange.fromJson(responsePercentPriceChange, new TypeToken<List<String>>() {}.getType());
                int tempTickerIndex=favoritesTickerList.indexOf(ticker);
                favoritesTickerList.remove(tempTickerIndex);
                favoritesCompanyNameList.remove(tempTickerIndex);
                favoritesCurrentPriceList.remove(tempTickerIndex);
                favoritesPriceChangeList.remove(tempTickerIndex);
                favoritesPercentPriceChangeList.remove(tempTickerIndex);

                String gsonToJsonTicker=gsonTicker.toJson(favoritesTickerList);
                String gsonToJsonCompanyName=gsonCompanyName.toJson(favoritesCompanyNameList);
                String gsonToJsonCurrentPrice=gsonCurrentPrice.toJson(favoritesCurrentPriceList);
                String gsonToJsonPriceChange=gsonPriceChange.toJson(favoritesPriceChangeList);
                String gsonToJsonPercentPriceChange=gsonPercentPriceChange.toJson(favoritesPercentPriceChangeList);

                editor.putString("keyTickerLocal",gsonToJsonTicker);
                editor.putString("keyCompanyNameLocal", gsonToJsonCompanyName);
                editor.putString("keyCurrentPriceFavLocal", gsonToJsonCurrentPrice);
                editor.putString("keyPriceChangeFavLocal", gsonToJsonPriceChange);
                editor.putString("keyPercentPriceChangeFavLocal", gsonToJsonPercentPriceChange);

                editor.apply();




            }
            else{
                starIconFlag=true;
                starIcon.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_starfilled));
                //display toast
                Toast.makeText(StockDetailsActivity.this,ticker+" is added to favorites", Toast.LENGTH_LONG).show();
                //adding to local storage
                SharedPreferences favoritesSharedPref= getSharedPreferences("favoritesAndroidLocal", MODE_PRIVATE);
                SharedPreferences.Editor editor=favoritesSharedPref.edit();
                Gson gsonTicker=new Gson();
                Gson gsonCompanyName=new Gson();
                Gson gsonCurrentPrice=new Gson();
                Gson gsonPriceChange=new Gson();
                Gson gsonPercentPriceChange=new Gson();
                String responseTicker=favoritesSharedPref.getString("keyTickerLocal","[]");
                String responseCompanyName=favoritesSharedPref.getString("keyCompanyNameLocal","[]");
                String responseCurrentPrice=favoritesSharedPref.getString("keyCurrentPriceFavLocal","[]");
                String responsePriceChange=favoritesSharedPref.getString("keyPriceChangeFavLocal","[]");
                String responsePercentPriceChange=favoritesSharedPref.getString("keyPercentPriceChangeFavLocal","[]");
                favoritesTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
                favoritesCompanyNameList=gsonCompanyName.fromJson(responseCompanyName, new TypeToken<List<String>>() {}.getType());
                favoritesCurrentPriceList=gsonCurrentPrice.fromJson(responseCurrentPrice, new TypeToken<List<String>>() {}.getType());
                favoritesPriceChangeList=gsonPriceChange.fromJson(responsePriceChange, new TypeToken<List<String>>() {}.getType());
                favoritesPercentPriceChangeList=gsonPercentPriceChange.fromJson(responsePercentPriceChange, new TypeToken<List<String>>() {}.getType());

                favoritesTickerList.add(ticker);
                favoritesCompanyNameList.add(companyName);
                favoritesCurrentPriceList.add(String.valueOf(currentPriceStockCalculation));
                favoritesPriceChangeList.add(priceChange);
                favoritesPercentPriceChangeList.add(percentPriceChange);

                String gsonToJsonTicker=gsonTicker.toJson(favoritesTickerList);
                String gsonToJsonCompanyName=gsonCompanyName.toJson(favoritesCompanyNameList);
                String gsonToJsonCurrentPrice=gsonCurrentPrice.toJson(favoritesCurrentPriceList);
                String gsonToJsonPriceChange=gsonPriceChange.toJson(favoritesPriceChangeList);
                String gsonToJsonPercentPriceChange=gsonPercentPriceChange.toJson(favoritesPercentPriceChangeList);

                editor.putString("keyTickerLocal",gsonToJsonTicker);
                editor.putString("keyCompanyNameLocal", gsonToJsonCompanyName);
                editor.putString("keyCurrentPriceFavLocal", gsonToJsonCurrentPrice);
                editor.putString("keyPriceChangeFavLocal", gsonToJsonPriceChange);
                editor.putString("keyPercentPriceChangeFavLocal", gsonToJsonPercentPriceChange);
                editor.apply();



            }
        }

        else{
            //for back button

            if(onPeerIntent!=null && Integer.parseInt(onPeerIntent)==10){
                onBackPressed();
                return true;
            }
            else{
                Intent tempIntent=new Intent(this,HomeActivity.class);
                startActivity(tempIntent);
            }

//            Intent tempIntent=new Intent(this,HomeActivity.class);
//            startActivity(tempIntent);
//            onBackPressed();
//            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    // function to implement back button on StockDetailsActivity
//    @Override
//    public boolean onSupportNavigateUp() {
//        finish();
//        return true;
//    }

    public void apiCallTopMostSection(){
        Log.d(myapp,"in apiCallTopMostSection"+ticker);
        RequestQueue requestQueue;
        String url="http://hw8april3final.wl.r.appspot.com/stockdetails_2?_keystock_="+ticker;
        Log.d(myapp,"in apiCallTopMostSection url is"+url);
        requestQueue= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ticker=response.get("ticker").toString();
                    logourl=response.get("logo").toString();
                    companyName=response.get("name").toString();
                    ipoStartDate=response.get("ipo").toString();
                    industry=response.get("finnhubIndustry").toString();
                    webpageUrl=response.get("weburl").toString();
                    Log.d(myapp,"apiCallTopMostSection"+ticker+logourl+companyName);
                    setValuesTopMostSection();
                    apiCallSocialSentimentData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "error is"+error);
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        requestQueue.add(jsonObjectRequest);
    }

    public void apiCallPriceChangeSection(){
        RequestQueue requestQueuePriceChange;
        Log.d(myapp,"4");
        String urlPriceChange="https://hw8april3final.wl.r.appspot.com/stockdetails_3priceChange?_keystock_=";
        requestQueuePriceChange=Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequestPriceChange=new JsonObjectRequest(Request.Method.GET, urlPriceChange + ticker, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    currentPrice=response.get("c").toString();
                    currentPriceStockCalculation=Float.parseFloat(currentPrice);
                    priceChange=response.get("d").toString();
                    percentPriceChange=response.get("dp").toString();
                    openPrice=response.get("o").toString();
                    lowPrice=response.get("l").toString();
                    highPrice=response.get("h").toString();
                    previousClose=response.get("pc").toString();
                    setPortfolioSection();
                    Log.d(myapp,"apiCallPriceChangeSection"+currentPrice+priceChange+percentPriceChange);
                    setValuesPriceChangeSection();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "error is"+error);
            }
        });
        jsonObjectRequestPriceChange.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        requestQueuePriceChange.add(jsonObjectRequestPriceChange);
    }

    public void apiCallNewsSection(){

        RequestQueue requestQueue;
        String urlNews="https://hw8april3final.wl.r.appspot.com/stockdetails_newsData?_keystock_=";
        requestQueue=Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, urlNews + ticker, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int newsCounter=0;
                    for(int i=0;i<response.length();i++){
                        JSONObject jsonObject=response.getJSONObject(i);
                      //  Log.d(myapp,i+"counter"+jsonObject.get("image").toString());
                        if(!jsonObject.get("headline").toString().equals("") && !jsonObject.get("image").toString().equals("")){

                            newsDatetime.add(newsCounter,jsonObject.get("datetime").toString());
                            newsHeadline.add(newsCounter,jsonObject.get("headline").toString());
                            newsImageUrl.add(newsCounter,jsonObject.get("image").toString());
                            newsSource.add(newsCounter,jsonObject.get("source").toString());
                            newsSummary.add(newsCounter,jsonObject.get("summary").toString());
                            newsDetailUrl.add(newsCounter,jsonObject.get("url").toString());
                            newsCounter++;
                           if(newsCounter==20){
                               break;
                           }
                        }
                      //  Log.d(myapp, jsonObject.get("headline").toString());
                    }
                    setNewsSection();

                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        requestQueue.add(jsonArrayRequest);
    }

    public void apiCallCompanyPeers(){
        RequestQueue requestQueue;
        String urlNews="https://hw8april3final.wl.r.appspot.com/stockdetails_companypeers?_keystock_=";
        requestQueue=Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, urlNews + ticker, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//               /

//                    for(int i=0;i<response.length();i++){
//                        JSONObject jsonObject=response.getJSONObject(i);
//                        companyPeers.add(jsonObject.toString());
//                        Log.d("peersData","Peers data is"+companyPeers);
//                    }
                try {
                    for(int i=0;i<response.length();i++){
                        companyPeers.add((String) response.get(i));
                    }
                    Log.d("peersData","Peers data is"+companyPeers);
                    setCompanyPeers(companyPeers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // }
//                catch (JSONException e){
//                    e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        requestQueue.add(jsonArrayRequest);
    }

    public void apiCallSocialSentimentData(){
        redditMention=0;
        redditPositiveMention=0;
        redditNegativeMention=0;
        twitterMention=0;
        twitterPositiveMention=0;
        twitterNegativeMention=0;
        RequestQueue requestQueueSSData;
        String urlPriceChange="https://hw8april3final.wl.r.appspot.com/stockdetails_socialSentimentData?_keystock_=";
        requestQueueSSData=Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectSSData=new JsonObjectRequest(Request.Method.GET, urlPriceChange + ticker, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //Log.d("ssdata","length is"+response.get("reddit").toString().length());
                    JSONArray jsonArrayReddit = null;
                    Object jsonReddit = null;
                    try {
                        jsonReddit = new JSONTokener(response.get("reddit").toString()).nextValue();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArrayReddit = (JSONArray) jsonReddit;
                 //   Log.d("ssdata","data is"+jsonArrayReddit.get(0));
                 //   Log.d("ssdata","data is"+jsonArrayReddit);

                    for(int i=0;i<jsonArrayReddit.length();i++){
                        JSONObject jb= (JSONObject) jsonArrayReddit.get(i);
                        redditMention=redditMention+Integer.parseInt(jb.get("mention").toString());
                        redditPositiveMention=redditPositiveMention+Integer.parseInt(jb.get("positiveMention").toString());
                        redditNegativeMention=redditNegativeMention+Integer.parseInt(jb.get("negativeMention").toString());
                    }

                    JSONArray jsonArrayTwitter = null;
                    Object jsonTwitter = null;
                    try {
                        jsonTwitter = new JSONTokener(response.get("twitter").toString()).nextValue();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArrayTwitter = (JSONArray) jsonTwitter;

                    for(int i=0;i<jsonArrayTwitter.length();i++){
                        JSONObject jb= (JSONObject) jsonArrayTwitter.get(i);
                        twitterMention=twitterMention+Integer.parseInt(jb.get("mention").toString());
                        twitterPositiveMention=twitterPositiveMention+Integer.parseInt(jb.get("positiveMention").toString());
                        twitterNegativeMention=twitterNegativeMention+Integer.parseInt(jb.get("negativeMention").toString());
                    }
                    Log.d("ssdata","mentions are"+redditMention+redditPositiveMention+redditNegativeMention+twitterMention+twitterPositiveMention+twitterNegativeMention
                    );
                    Log.d("reddit","before calling ssdata"+redditMention);
                    setSSData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "error is"+error);
            }
        });
        jsonObjectSSData.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        requestQueueSSData.add(jsonObjectSSData);
    }

    public void setValuesTopMostSection(){
        tvTicker=(TextView) findViewById(R.id.textViewTickerName);
        ivImgLogo=(ImageView) findViewById(R.id.imageViewStockLogo);
        tvCompanyName=(TextView) findViewById(R.id.textViewCompanyName);
        tvIpoStartDate=(TextView) findViewById(R.id.textViewIpoStartDate);
        tvIndustry=(TextView) findViewById(R.id.textViewIndustry);
        tvWebpage=(TextView) findViewById(R.id.textViewWebpageUrl);

        tvTicker.setText(ticker);
        Picasso.with(this).load(logourl).into(ivImgLogo);
        tvCompanyName.setText(companyName);
        tvIpoStartDate.setText(ipoStartDate);
        tvIndustry.setText(industry);;
        tvWebpage.setText(webpageUrl);
        tvWebpage.setPaintFlags(tvWebpage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        spinner.setVisibility(View.GONE);

        //tvWebpage.setText("ajinkya");
        tvWebpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webpageUrl));
                v.getContext().startActivity(browserIntent);
            }
        });

    }

    public void setValuesPriceChangeSection(){
        ivTrendingIcon=(ImageView) findViewById(R.id.imageViewTrendingIcon);
        tvCurrentPrice=(TextView) findViewById(R.id.textViewCurrentPrice);
        tvPriceAndPercentChange=(TextView) findViewById(R.id.textViewPriceAndPercentChange);
        tvOpenPrice=(TextView) findViewById(R.id.textViewOpenPrice);
        tvLowPrice=(TextView) findViewById(R.id.textViewLowPrice);
        tvHighPrice=(TextView) findViewById(R.id.textViewHighPrice);
        tvPreviousClose=(TextView) findViewById(R.id.textViewPreviousClose);

        String formattedCurrentPrice=formatter.format(Float.parseFloat(currentPrice));
        String formattedPriceChange=formatter.format(Float.parseFloat(priceChange));
        String formattedPercentPriceChange=formatter.format(Float.parseFloat(percentPriceChange));


        currentPrice="$"+currentPrice;

        //tvCurrentPrice.setText(currentPrice);
        tvCurrentPrice.setText("$"+formattedCurrentPrice);
        priceChangeAndPercentPriceChange="$"+formattedPriceChange+" "+"("+formattedPercentPriceChange+"%)";


       // Setting color and trending icon based on value of price change
        Log.d(myapp,"Setting color and trending icon based on value of price change");
        if(Float.parseFloat(formattedPriceChange)>0){
            colorPriceChange="#228B22";
            ivTrendingIcon.setImageResource(R.drawable.trending_up_icon);
        }
        else if(Float.parseFloat(formattedPriceChange)<0){
            colorPriceChange="#FF0000";
            ivTrendingIcon.setImageResource(R.drawable.trending_down_icon);
        }
        else{
            colorPriceChange="#000000";
        }
        tvPriceAndPercentChange.setTextColor(Color.parseColor(colorPriceChange));
        tvPriceAndPercentChange.setText(priceChangeAndPercentPriceChange);
        String formattedOpenPrice=formatter.format(Float.parseFloat(openPrice));
        String formattedLowPrice=formatter.format(Float.parseFloat(lowPrice));
        String formattedHighPrice=formatter.format(Float.parseFloat(highPrice));
        String formattedpreviousClose=formatter.format(Float.parseFloat(previousClose));
//        tvOpenPrice.setText(openPrice);
//        tvLowPrice.setText(lowPrice);
//        tvHighPrice.setText(highPrice);
//        tvPreviousClose.setText(previousClose);
        tvOpenPrice.setText("$"+formattedOpenPrice);
        tvLowPrice.setText("$"+formattedLowPrice);
        tvHighPrice.setText("$"+formattedHighPrice);
        tvPreviousClose.setText("$"+formattedpreviousClose);

    }

    public void setRecommendationsChart(){
        WebView webViewRTrends;
        webViewRTrends= (WebView) findViewById(R.id.webViewRTrends);
        WebSettings webSettings= webViewRTrends.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewRTrends.loadUrl("file:///android_asset/rChart.html?ticker="+ticker);
    }

    public void setHistoricalEpsChart(){
        WebView webViewHistoricalEPS;
        webViewHistoricalEPS= (WebView) findViewById(R.id.webViewHistoricalEPSSurprise);
        WebSettings webSettings1= webViewHistoricalEPS.getSettings();
        webSettings1.setJavaScriptEnabled(true);
        webViewHistoricalEPS.loadUrl("file:///android_asset/histEpsChart.html?ticker="+ticker);
    }

    public void setNewsSection(){
        RecyclerView recyclerViewNews=(RecyclerView) findViewById(R.id.recyclerViewNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this));

      //  String[] tickernames={"AAPL","TSLA","MSFT","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA"};
      //  String[] tickernames1={"HeyAAPl","HEYTSLA","HEYMSFT","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA","TSLA"};
      //  recyclerViewNews.setAdapter(new NewsAdapter(tickernames,tickernames1));
        recyclerViewNews.setAdapter(new NewsAdapter(newsDatetime, newsHeadline, newsImageUrl, newsSource, newsSummary, newsDetailUrl));
    }

    public void setCompanyPeers(ArrayList<String> companyPeers){
        RecyclerView recyclerViewCompanyPeers=(RecyclerView) findViewById(R.id.recyclerViewCompanyPeers);
        recyclerViewCompanyPeers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        //String[] peers={"AAPL","FDX","TSLA"};
        recyclerViewCompanyPeers.setAdapter(new CompanyPeersAdapter(companyPeers));
    }

    public void setPortfolioSection(){
        Log.d("wednesday","after sell just set is called"+portfolioTickerList);
        tvPortfolioSharesOwned=(TextView) findViewById(R.id.textViewPortfolioSharesOwned);
        tvPortfolioAvgCost=(TextView) findViewById(R.id.textViewPortfolioAvgCost);
        tvPortfolioTotalCost=(TextView) findViewById(R.id.textViewPortfolioTotalCost);
        tvPortfolioChange=(TextView) findViewById(R.id.textViewPortfolioChange);
        tvPortfolioMarketValue=(TextView) findViewById(R.id.textViewPortfolioMarketValue);
        //get local storage data for portfolio
        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor=portfolioSharedPref.edit();
        Gson gsonTicker=new Gson();
        Gson gsonTotalShares=new Gson();
        Gson gsonTotalCost=new Gson();
        String responseTicker=portfolioSharedPref.getString("keyTickerPortfolioLocal","[]");
        String responseTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");
        String responseTotalCost=portfolioSharedPref.getString("keyTotalCostLocal","[]");
        portfolioTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
        portfolioTotalSharesList=gsonTotalShares.fromJson(responseTotalShares, new TypeToken<List<String>>() {}.getType());
        portfolioTotalCostList=gsonTotalCost.fromJson(responseTotalCost, new TypeToken<List<String>>() {}.getType());
        Log.d("portfoliod","setting portfolio, reading ticker list "+portfolioTickerList);
        //find current ticker in local storage if exist
        if(portfolioTickerList.contains(ticker)){
            //get index of current ticker
            int tempIndex=portfolioTickerList.indexOf(ticker);
            //set values
            tvPortfolioSharesOwned.setText(portfolioTotalSharesList.get(tempIndex));
            String formattedPortfolioTotalCost=formatter.format(Float.parseFloat(portfolioTotalCostList.get(tempIndex)));
            //tvPortfolioTotalCost.setText("$"+portfolioTotalCostList.get(tempIndex));
            tvPortfolioTotalCost.setText("$"+formattedPortfolioTotalCost);
            //calculate average cost per share
            float numberOfShares= Float.parseFloat(portfolioTotalSharesList.get(tempIndex));
            float totalCostOfShare=Float.parseFloat(portfolioTotalCostList.get(tempIndex));
            float avgCostPerShare=totalCostOfShare/numberOfShares;
            String formattedPortfolioAvgCostPerShare=formatter.format(avgCostPerShare);
            //tvPortfolioAvgCost.setText("$"+avgCostPerShare);
            tvPortfolioAvgCost.setText("$"+formattedPortfolioAvgCostPerShare);
            //calculate market value
            float marketValueShare=numberOfShares*currentPriceStockCalculation;
            String formattedPortfolioMarketValueShare=formatter.format(marketValueShare);
            //tvPortfolioMarketValue.setText("$"+marketValueShare);
            tvPortfolioMarketValue.setText("$"+formattedPortfolioMarketValueShare);
            //calculate change and set value
            float changeCost=marketValueShare-(avgCostPerShare*numberOfShares);
            String formattedPortfolioChangeCost=formatter.format(changeCost);
            tvPortfolioChange.setText("$"+formattedPortfolioChangeCost);
//            tvPortfolioChange.setText("$"+changeCost);
            Log.d("fdxc","FEDEX after selling setting portfolio");
            Log.d("fdxc","change cost is"+formattedPortfolioChangeCost);
            if(Float.parseFloat(formattedPortfolioChangeCost)>0){
                tvPortfolioChange.setTextColor(Color.parseColor("#228B22"));
                tvPortfolioMarketValue.setTextColor(Color.parseColor("#228B22"));
            }
            else if(Float.parseFloat(formattedPortfolioChangeCost)<0){
                tvPortfolioChange.setTextColor(Color.parseColor("#FF0000"));
                tvPortfolioMarketValue.setTextColor(Color.parseColor("#FF0000"));
            }
            else{
                tvPortfolioChange.setTextColor(Color.parseColor("#000000"));
                tvPortfolioMarketValue.setTextColor(Color.parseColor("#000000"));
            }
        }
        else{
            Log.d("wednesday","after sell after set is called in else"+portfolioTickerList);
            tvPortfolioSharesOwned.setText("0");
            tvPortfolioAvgCost.setText("$0.00");
            tvPortfolioTotalCost.setText("$0.00");
            tvPortfolioChange.setText("$0.00");
            tvPortfolioMarketValue.setText("$0.00");
            tvPortfolioChange.setTextColor(Color.parseColor("#000000"));
            tvPortfolioMarketValue.setTextColor(Color.parseColor("#000000"));
        }

    }

    public void setSSData(){
        tvSocialSentimentCompany=(TextView) findViewById(R.id.textViewSocialSentimentCompanyName);
        tvRedditTotal=(TextView) findViewById(R.id.textViewSocialSentimentRT);
        tvRedditPositive=(TextView) findViewById(R.id.textViewSocialSentimentRP);
        tvRedditNegative=(TextView) findViewById(R.id.textViewSocialSentimentRN);
        tvTwitterTotal=(TextView) findViewById(R.id.textViewSocialSentimentTT);
        tvTwitterPositive=(TextView) findViewById(R.id.textViewSocialSentimentTP);
        tvTwitterNegative=(TextView) findViewById(R.id.textViewSocialSentimentTN);

        tvSocialSentimentCompany.setText(companyName);
        tvRedditTotal.setText(String.valueOf(redditMention));
        tvRedditPositive.setText(String.valueOf(redditPositiveMention));
        tvRedditNegative.setText(String.valueOf(redditNegativeMention));
        tvTwitterTotal.setText(String.valueOf(twitterMention));
        tvTwitterPositive.setText(String.valueOf(twitterPositiveMention));
        tvTwitterNegative.setText(String.valueOf(twitterNegativeMention));

    }

    public void handleTrading(){
        //fetch local storage
        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor=portfolioSharedPref.edit();

        SharedPreferences balanceSharedPref=getSharedPreferences("balanceAndroidLocal",MODE_PRIVATE);
        SharedPreferences.Editor editorBalance=balanceSharedPref.edit();

        Gson gsonTicker=new Gson();
        Gson gsonTotalShares=new Gson();
        Gson gsonTotalCost=new Gson();
        Gson gsonMarketValue=new Gson();
        Gson gsonPriceChange=new Gson();
        Gson gsonPercentPriceChange=new Gson();
        Gson gsonMarketValueNetWorthCalculation=new Gson();

        Gson gsonNetWorth=new Gson();
        Gson gsonBalance=new Gson();

        String responseTicker=portfolioSharedPref.getString("keyTickerPortfolioLocal","[]");
        String responseTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");
        String responseTotalCost=portfolioSharedPref.getString("keyTotalCostLocal","[]");
        String responseMarketValue=portfolioSharedPref.getString("keyMarketValueLocal","[]");
        String responsePriceChange=portfolioSharedPref.getString("keyPriceChangeLocal","[]");
        String responsePercentPriceChange=portfolioSharedPref.getString("keyPercentPriceChangeLocal","[]");
        String responseMarketValueNetWorthCalculation=portfolioSharedPref.getString("keyMarketValueNetWorthCalculation","[]");

        String responseNetWorth=balanceSharedPref.getString("keyNetWorthLocal","");
        String responseBalance=balanceSharedPref.getString("keyBalanceLocal","");

        portfolioTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
        portfolioTotalSharesList=gsonTotalShares.fromJson(responseTotalShares, new TypeToken<List<String>>() {}.getType());
        portfolioTotalCostList=gsonTotalCost.fromJson(responseTotalCost, new TypeToken<List<String>>() {}.getType());
        portfolioMarketValueList=gsonMarketValue.fromJson(responseMarketValue, new TypeToken<List<String>>() {}.getType());
        portfolioPriceChangeList=gsonPriceChange.fromJson(responsePriceChange, new TypeToken<List<String>>() {}.getType());
        portfolioPercentPriceChangeList=gsonPercentPriceChange.fromJson(responsePercentPriceChange, new TypeToken<List<String>>() {}.getType());
        marketValueNetWorthCalculationList=gsonMarketValueNetWorthCalculation.fromJson(responseMarketValueNetWorthCalculation, new TypeToken<List<String>>() {}.getType());

        netWorthList=gsonNetWorth.fromJson(responseNetWorth, new TypeToken<List<String>>() {}.getType());
        balanceList=gsonBalance.fromJson(responseBalance, new TypeToken<List<String>>() {}.getType());

        tradingButton=(Button) findViewById(R.id.tradeButton);

        tradingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Log.d(myapp,"clickedd on trade");
            AlertDialog.Builder builder=new AlertDialog.Builder(view.getRootView().getContext());
            View dialogView=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.trading_dialog,null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            AlertDialog tempAlert=builder.show();
            tempAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView tvTradeDialogTitle;
            EditText etTradeDialogEditText;
            TextView tvTradeDialogSharesAmount;
            TextView tvTradeDialogBalanceAmount;
            tvTradeDialogTitle=tempAlert.findViewById(R.id.textViewTradeDialogTitle);
            etTradeDialogEditText=tempAlert.findViewById(R.id.editTextTradeDialog);
            tvTradeDialogSharesAmount=tempAlert.findViewById(R.id.textViewTradeDialogSharesAmount);
            tvTradeDialogBalanceAmount=tempAlert.findViewById(R.id.textViewTradeDialogBalanceAmount);
            buyButton=tempAlert.findViewById(R.id.buttonTradingDialogBuy);
            sellButton=tempAlert.findViewById(R.id.buttonTradingDialogSell);
            tvTradeDialogTitle.setText("Trade "+companyName+" shares");
            String formattedCurrentPrice=formatter.format(currentPriceStockCalculation);
            tvTradeDialogSharesAmount.setText("0*$"+formattedCurrentPrice+"/share=0.00");
            String formattedBalanceAmount=formatter.format(Float.parseFloat(balanceList.get(0)));
            tvTradeDialogBalanceAmount.setText("$"+formattedBalanceAmount+" to buy "+ticker);
            etTradeDialogEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    float totalShares=Float.parseFloat(charSequence.toString());
                    totalSharesToTrade=Integer.parseInt(charSequence.toString());
                    float totalCost=totalShares*currentPriceStockCalculation;
                    totalCostCurrentTransaction=totalCost;
                    String formattedTotalCost=formatter.format(totalCost);
                    tvTradeDialogSharesAmount.setText(totalShares+"*$"+formattedCurrentPrice+"/share="+formattedTotalCost);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //add details to portfolio
                    //if ticker is already present in portfolio
                    if(portfolioTickerList.contains(ticker)){
                        Log.d("port","ticker is already present");
                        //get index of ticker
                        int tickerIndex=portfolioTickerList.indexOf(ticker);
                        int tempTotalShares;
                        //check if transaction is possible
                        //if it is possible
                        if(totalSharesToTrade>0 && totalCostCurrentTransaction<=Float.parseFloat(balanceList.get(0))){
                            //get existing number of total shares
                            tempTotalShares= Integer.parseInt(portfolioTotalSharesList.get(tickerIndex));
                            //add current number of shares to earlier bought shares
                            tempTotalShares+=totalSharesToTrade;
                            portfolioTotalSharesList.set(tickerIndex, String.valueOf(tempTotalShares));
                            //get existing total cost of share
                            float tempTotalCost=Float.parseFloat(portfolioTotalCostList.get(tickerIndex));
                            //add current transaction value to earlier total cost value
                            tempTotalCost+=totalCostCurrentTransaction;
                            portfolioTotalCostList.set(tickerIndex,String.valueOf(tempTotalCost));
                            //adding market value to portfolio list
                            float tempMarketValue=tempTotalShares*currentPriceStockCalculation;
                            portfolioMarketValueList.set(tickerIndex, String.valueOf(tempMarketValue));
                            //adding price change to portfolio list
                            float tempAvgPriceOfStock=tempTotalCost/tempTotalShares;
                            float tempPriceChange=tempTotalShares*(currentPriceStockCalculation-tempAvgPriceOfStock);
                            portfolioPriceChangeList.set(tickerIndex, String.valueOf(tempPriceChange));
                            //adding percent price change value to portfolio list
                            float tempPercentPriceChange=(tempPriceChange/tempTotalCost)*100;
                            portfolioPercentPriceChangeList.set(tickerIndex, String.valueOf(tempPercentPriceChange));
                            //updating remaining balance to local storage
                            balanceList.set(0, String.valueOf(Float.parseFloat(balanceList.get(0))-totalCostCurrentTransaction));

                            marketValueNetWorthCalculationList.set(tickerIndex,String.valueOf(currentPriceStockCalculation));

                            tempAlert.dismiss();
                            AlertDialog.Builder builderBuy=new AlertDialog.Builder(view.getRootView().getContext());
                            View dialogViewBuy=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.trade_complete_dialog,null);
                            builderBuy.setView(dialogViewBuy);
                            builderBuy.setCancelable(true);
                            AlertDialog tempAlertBuy=builderBuy.show();
                            tempAlertBuy.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            tvTradeCompleteText=tempAlertBuy.findViewById(R.id.textViewTradeCompleteText);
                            tvTradeCompleteText.setText("You have successfully bought "+totalSharesToTrade+" shares of "+ticker);

                            doneButton=tempAlertBuy.findViewById(R.id.buttonTradeCompleteDone);

                            doneButton=tempAlertBuy.findViewById(R.id.buttonTradeCompleteDone);
                            doneButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tempAlertBuy.dismiss();
                                }
                            });


                        }
                        //if it is not possible
                        else{
                            if(totalSharesToTrade<=0){
                                //display toast
                                Toast.makeText(StockDetailsActivity.this,"Please enter a valid amount", Toast.LENGTH_LONG).show();
                            }
                            else if(totalCostCurrentTransaction>Float.parseFloat(balanceList.get(0))){
                                Toast.makeText(StockDetailsActivity.this,"Not enough money to buy", Toast.LENGTH_LONG).show();
                            }
                        }










                    }
                    //if ticker is not present in portfolio
                    else{
                        Log.d("port","ticker is not present");

                        //if transaction is possible
                        if(totalSharesToTrade>0 &&totalCostCurrentTransaction<=Float.parseFloat(balanceList.get(0))){
                            //add ticker to portfolio list
                            portfolioTickerList.add(ticker);
                            //add number of shares to portfolio list
                            portfolioTotalSharesList.add(String.valueOf(totalSharesToTrade));
                            //add total cost to portfolio list
                            portfolioTotalCostList.add(String.valueOf(totalCostCurrentTransaction));
                            //add market value to portfolio list
                            portfolioMarketValueList.add(String.valueOf(totalCostCurrentTransaction));
                            //add price change value to portfolio list
                            portfolioPriceChangeList.add(String.valueOf(0));
                            //add percent price change value to portfolio list
                            portfolioPercentPriceChangeList.add(String.valueOf(0));
                            //updating remaining balance to local storage
                            balanceList.set(0, String.valueOf(Float.parseFloat(balanceList.get(0))-totalCostCurrentTransaction));
                            marketValueNetWorthCalculationList.add(String.valueOf(currentPriceStockCalculation));

                            tempAlert.dismiss();
                            AlertDialog.Builder builderBuy=new AlertDialog.Builder(view.getRootView().getContext());
                            View dialogViewBuy=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.trade_complete_dialog,null);
                            builderBuy.setView(dialogViewBuy);
                            builderBuy.setCancelable(true);
                            AlertDialog tempAlertBuy=builderBuy.show();
                            tempAlertBuy.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            tvTradeCompleteText=tempAlertBuy.findViewById(R.id.textViewTradeCompleteText);
                            tvTradeCompleteText.setText("You have successfully bought "+totalSharesToTrade+" shares of "+ticker);

                            doneButton=tempAlertBuy.findViewById(R.id.buttonTradeCompleteDone);

                            doneButton=tempAlertBuy.findViewById(R.id.buttonTradeCompleteDone);
                            doneButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tempAlertBuy.dismiss();
                                }
                            });
                        }
                        //if transaction is not possible
                        else{
                            if(totalSharesToTrade<=0){
                                //display toast
                                Toast.makeText(StockDetailsActivity.this,"Please enter a valid amount", Toast.LENGTH_LONG).show();
                            }
                            else if(totalCostCurrentTransaction>Float.parseFloat(balanceList.get(0))){
                                Toast.makeText(StockDetailsActivity.this,"Not enough money to buy", Toast.LENGTH_LONG).show();
                            }
                        }



                    }
                    String gsonToJsonTickerPortfolio=gsonTicker.toJson(portfolioTickerList);
                    String gsonToJsonTotalShares=gsonTotalShares.toJson(portfolioTotalSharesList);
                    String gsonToJsonTotalCost=gsonTotalCost.toJson(portfolioTotalCostList);
                    String gsonToJsonMarketValue=gsonMarketValue.toJson(portfolioMarketValueList);
                    String gsonToJsonPriceChange=gsonPriceChange.toJson(portfolioPriceChangeList);
                    String gsonToJsonPercentPriceChange=gsonPercentPriceChange.toJson(portfolioPercentPriceChangeList);
                    String gsonToJsonmarketValueNetWorthCalculation=gsonMarketValueNetWorthCalculation.toJson(marketValueNetWorthCalculationList);

                    String gsonToJsonPortfolioBalance=gsonBalance.toJson(balanceList);

                    Log.d("ajinkya","tickers after buy"+portfolioTickerList);
                    Log.d("ajinkya","market values after buy"+portfolioMarketValueList);
                    Log.d("trading","balance after trnasaction"+balanceList);
                    editor.putString("keyTickerPortfolioLocal",gsonToJsonTickerPortfolio);
                    editor.putString("keyTotalSharesLocal", gsonToJsonTotalShares);
                    editor.putString("keyTotalCostLocal", gsonToJsonTotalCost);
                    editor.putString("keyMarketValueLocal", gsonToJsonMarketValue);
                    editor.putString("keyPriceChangeLocal", gsonToJsonPriceChange);
                    editor.putString("keyPercentPriceChangeLocal", gsonToJsonPercentPriceChange);
                    editor.putString("keyMarketValueNetWorthCalculation",gsonToJsonmarketValueNetWorthCalculation);

                    editorBalance.putString("keyBalanceLocal",gsonToJsonPortfolioBalance);
                    editor.apply();
                    editorBalance.apply();
                    setPortfolioSection();

//                    Log.d(myapp,"Portfolio details");
//                    Log.d(myapp, String.valueOf(portfolioTickerList));
//                    Log.d(myapp, String.valueOf(portfolioTotalSharesList));
//                    Log.d(myapp, String.valueOf(portfolioTotalCostList));

//                    AlertDialog.Builder builderBuy=new AlertDialog.Builder(view.getRootView().getContext());
//                    View dialogViewBuy=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.trade_complete_dialog,null);
//                    builderBuy.setView(dialogViewBuy);
//                    builderBuy.setCancelable(true);
//                    AlertDialog tempAlertBuy=builderBuy.show();
//                    tempAlertBuy.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    tvTradeCompleteText=tempAlertBuy.findViewById(R.id.textViewTradeCompleteText);
//                    tvTradeCompleteText.setText("You have successfully bought "+totalSharesToTrade+" shares of "+ticker);

//                    doneButton=tempAlertBuy.findViewById(R.id.buttonTradeCompleteDone);
//                    doneButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            tempAlertBuy.dismiss();
//                        }
//                    });

                }
            });

            sellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if shares exist  in portfolio
                    if(portfolioTickerList.contains(ticker)){
                        //get index of ticker
                        int tickerIndex=portfolioTickerList.indexOf(ticker);
                        //get existing number of total shares
                        tempTotalShares= Integer.parseInt(portfolioTotalSharesList.get(tickerIndex));
                        Log.d("selling","Exiting number of share are"+tempTotalShares);
                        //check if selling is possible

                        if(totalSharesToTrade>=0 && tempTotalShares>=totalSharesToTrade){
                            //it is possible to sell
                            //subtract current number of shares to earlier bought shares
                            tempTotalShares=tempTotalShares-totalSharesToTrade;
                            portfolioTotalSharesList.set(tickerIndex, String.valueOf(tempTotalShares));

                            //if all shares are sold, remove it's entry from portfolio
                            if(tempTotalShares==0){
                                //get existing total cost of share
                                float tempTotalCost=Float.parseFloat(portfolioTotalCostList.get(tickerIndex));
                                //add current transaction value to earlier total cost value
                                tempTotalCost=tempTotalCost+totalCostCurrentTransaction;
                                portfolioTotalCostList.set(tickerIndex,String.valueOf(tempTotalCost));
                                //set market value
                                float tempMarketValue=tempTotalShares*currentPriceStockCalculation;
                                portfolioMarketValueList.set(tickerIndex, String.valueOf(tempMarketValue));
                                //set price change value
                                float tempAvgPriceOfStock=tempTotalCost/tempTotalShares;
                                float tempPriceChange=tempTotalShares*(currentPriceStockCalculation-tempAvgPriceOfStock);

                                portfolioPriceChangeList.set(tickerIndex, String.valueOf(tempPriceChange));
                                //set percent price change value
                                float tempPercentPriceChange=(tempPriceChange/tempTotalCost)*100;
                                portfolioPercentPriceChangeList.set(tickerIndex, String.valueOf(tempPercentPriceChange));


                                balanceList.set(0, String.valueOf(Float.parseFloat(balanceList.get(0))+totalCostCurrentTransaction));


                                portfolioTickerList.remove(tickerIndex);
                                portfolioTotalSharesList.remove(tickerIndex);
                                portfolioTotalCostList.remove(tickerIndex);
                                portfolioMarketValueList.remove(tickerIndex);
                                portfolioPriceChangeList.remove(tickerIndex);
                                portfolioPercentPriceChangeList.remove(tickerIndex);
                                marketValueNetWorthCalculationList.remove(tickerIndex);
                            }
                            else{
                                //get existing total cost of share
                                float tempTotalCost=Float.parseFloat(portfolioTotalCostList.get(tickerIndex));
                                //add current transaction value to earlier total cost value
                                tempTotalCost=tempTotalCost-totalCostCurrentTransaction;
                                portfolioTotalCostList.set(tickerIndex,String.valueOf(tempTotalCost));
                                //set market value
                                float tempMarketValue=tempTotalShares*currentPriceStockCalculation;
                                portfolioMarketValueList.set(tickerIndex, String.valueOf(tempMarketValue));
                                //set price change value
                                float tempAvgPriceOfStock=tempTotalCost/tempTotalShares;
                                float tempPriceChange=tempTotalShares*(currentPriceStockCalculation-tempAvgPriceOfStock);
                                Log.d("selling","Price change in sell button"+tempPriceChange);
                                portfolioPriceChangeList.set(tickerIndex, String.valueOf(tempPriceChange));
                                //set percent price change value
                                float tempPercentPriceChange=(tempPriceChange/tempTotalCost)*100;
                                portfolioPercentPriceChangeList.set(tickerIndex, String.valueOf(tempPercentPriceChange));
                                marketValueNetWorthCalculationList.set(tickerIndex,String.valueOf(currentPriceStockCalculation));

                                balanceList.set(0, String.valueOf(Float.parseFloat(balanceList.get(0))+totalCostCurrentTransaction));
                            }

                            tempAlert.dismiss();
                            //add details to portfolio
                            //fetch existing data and modify
                            AlertDialog.Builder builderSell=new AlertDialog.Builder(view.getRootView().getContext());
                            View dialogViewSell=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.trade_complete_dialog,null);
                            builderSell.setView(dialogViewSell);
                            builderSell.setCancelable(true);
                            AlertDialog tempAlertSell=builderSell.show();
                            tempAlertSell.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            tvTradeCompleteText=tempAlertSell.findViewById(R.id.textViewTradeCompleteText);
                            tvTradeCompleteText.setText("You have successfully sold "+totalSharesToTrade+" shares of "+ticker);

                            doneButton=tempAlertSell.findViewById(R.id.buttonTradeCompleteDone);
                            doneButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tempAlertSell.dismiss();
                                }
                            });




                        }
                        else{
                            //it is not possible to sell
                            if(totalSharesToTrade<=0){
                                //display toast
                                Toast.makeText(StockDetailsActivity.this,"Please enter a valid amount", Toast.LENGTH_LONG).show();
                            }
                            else if(tempTotalShares<totalSharesToTrade){
                                Toast.makeText(StockDetailsActivity.this,"Not enough shares to sell", Toast.LENGTH_LONG).show();
                            }

                        }








                        // push the changes to local storage

                        Log.d("trading","balance after trnasaction"+balanceList);

                        String gsonToJsonTickerPortfolio=gsonTicker.toJson(portfolioTickerList);
                        String gsonToJsonTotalShares=gsonTotalShares.toJson(portfolioTotalSharesList);
                        String gsonToJsonTotalCost=gsonTotalCost.toJson(portfolioTotalCostList);
                        String gsonToJsonMarketValue=gsonTotalCost.toJson(portfolioMarketValueList);
                        String gsonToJsonPriceChange=gsonTotalCost.toJson(portfolioPriceChangeList);
                        String gsonToJsonPercentPriceChange=gsonTotalCost.toJson(portfolioPercentPriceChangeList);
                        String gsonToJsonmarketValueNetWorthCalculation=gsonMarketValueNetWorthCalculation.toJson(marketValueNetWorthCalculationList);

                        String gsonToJsonPortfolioBalance=gsonBalance.toJson(balanceList);


                        editor.putString("keyTickerPortfolioLocal",gsonToJsonTickerPortfolio);
                        editor.putString("keyTotalSharesLocal", gsonToJsonTotalShares);
                        editor.putString("keyTotalCostLocal", gsonToJsonTotalCost);
                        editor.putString("keyMarketValueLocal", gsonToJsonMarketValue);
                        editor.putString("keyPriceChangeLocal", gsonToJsonPriceChange);
                        editor.putString("keyPercentPriceChangeLocal", gsonToJsonPercentPriceChange);
                        editor.putString("keyMarketValueNetWorthCalculation",gsonToJsonmarketValueNetWorthCalculation);

                        Log.d("wednesday","after sell before set is called"+portfolioTickerList);
                        editorBalance.putString("keyBalanceLocal",gsonToJsonPortfolioBalance);


                        editor.apply();
                        editorBalance.apply();
                        Log.d("fdxc","Calling setPortfoliosection from sell click event");
                        setPortfolioSection();

                        //Log.d("sell","selling all");
//                        Log.d(myapp,"Portfolio details");
//                        Log.d(myapp, String.valueOf(portfolioTickerList));
//                        Log.d(myapp, String.valueOf(portfolioTotalSharesList));
//                        Log.d(myapp, String.valueOf(portfolioTotalCostList));





                    }
                   // if a share does not exist in portfolio, you cannot sell
                    else{
                        //display toast
                        if(totalSharesToTrade<=0){
                            //display toast
                            Toast.makeText(StockDetailsActivity.this,"Please enter a valid amount", Toast.LENGTH_LONG).show();
                        }
                        else if(tempTotalShares<totalSharesToTrade){
                            Toast.makeText(StockDetailsActivity.this,"Not enough shares to sell", Toast.LENGTH_LONG).show();
                        }
                    }

                }
                });


            }
        });

    }


}