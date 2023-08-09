package com.example.stocks;

import static com.example.stocks.R.color.white;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    TextView textViewCompanyName, tvDate, tvFinnhubLink, tvHomeNetWorth, tvHomeCashBalance;
    String ticker;
    String logourl;
    String companyName;
    String currentPrice;
    String priceChange;
    String percentPriceChange;
    String myapp="myapp";
    String checkCP;
    RecyclerView recyclerViewPortfolio;
    RecyclerView recyclerViewFavorites;
    PortfolioAdapter mAdapter;
    FavoritesAdapter fAdapter;
    ArrayList<String> favoriteTickerList=new ArrayList<String>();
    ArrayList<String> portfolioTickerList=new ArrayList<String>();
    ArrayList<String> portfolioTotalSharesList=new ArrayList<String>();
    ArrayList<String> portfolioTotalCostList=new ArrayList<String>();
    ArrayList<String> portfolioMarketValueList=new ArrayList<String>();
    ArrayList<String> portfolioPriceChangeList=new ArrayList<String>();
    ArrayList<String> portfolioPercentPriceChangeList=new ArrayList<String>();
    ArrayList<String> portfolioPercentPriceChangeColorList=new ArrayList<String>();
    ArrayList<String> favoritesTickerList=new ArrayList<String>();
    ArrayList<String> favoritesCompanyNameList=new ArrayList<String>();
    ArrayList<String> favoritesCurrentPriceList=new ArrayList<String>();
    ArrayList<String> favoritesPriceChangeList=new ArrayList<String>();
    ArrayList<String> favoritesPercentPriceChangeList=new ArrayList<String>();
    ArrayList<String> autoCompleteData=new ArrayList<String>();
    ArrayList<String> netWorthList=new ArrayList<>();
    ArrayList<String> balanceList=new ArrayList<>();
    ArrayList<String> formattedNetWorth=new ArrayList<>();
    ArrayList<String> marketValueNetWorthCalculationList=new ArrayList<>();
    ArrayList<String> netWorthCalculationTotalSharesList=new ArrayList<String>();
    ProgressBar spinner;
    Context context;
    float cashBalance;
    ArrayAdapter<String> autoCompleteAdapter;
    Context mContext;
    Paint mClearPaint;
    ColorDrawable mBackground;
    int backgroundColor;
    Drawable deleteDrawable;
    int intrinsicWidth;
    int intrinsicHeight;
    NumberFormat formatter = new DecimalFormat("#0.00");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);


        //finnhub link
        tvFinnhubLink=(TextView) findViewById(R.id.textViewFinnhubLink);
        tvFinnhubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://finnhub.io/"));
                v.getContext().startActivity(browserIntent);
            }
        });



        recyclerViewPortfolio=findViewById(R.id.recyclerViewPortfolio);
        recyclerViewFavorites=findViewById(R.id.recyclerViewFavorites);
        portfolioTickerList=new ArrayList<String>();
        portfolioTotalSharesList=new ArrayList<String>();
        portfolioTotalCostList=new ArrayList<String>();
        portfolioMarketValueList=new ArrayList<String>();
        portfolioPriceChangeList=new ArrayList<String>();
        portfolioPercentPriceChangeList=new ArrayList<String>();
        marketValueNetWorthCalculationList=new ArrayList<String>();
        netWorthCalculationTotalSharesList=new ArrayList<String>();
        favoritesTickerList=new ArrayList<String>();
        favoritesCompanyNameList=new ArrayList<String>();
        favoritesCurrentPriceList=new ArrayList<String>();
        favoritesPriceChangeList=new ArrayList<String>();
        favoritesPercentPriceChangeList=new ArrayList<String>();

        Log.d("tuesday","Calling setPortfolio from homeactivity");
        setPortfolioSection();
        setFavoritesSection();
        setNetWorthAndBalance();
       // arraylistTesting();

//        setZeroNetWorthAndBalance();
//        clearLocalStorageFavorites();
//        clearLocalStoragePortfolio();

     //   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(white)));
    }

    @Override
    protected void onResume() {


        super.onResume();
//        recyclerViewPortfolio=findViewById(R.id.recyclerViewPortfolio);
//        recyclerViewFavorites=findViewById(R.id.recyclerViewFavorites);
//        portfolioTickerList=new ArrayList<String>();
//        portfolioTotalSharesList=new ArrayList<String>();
//        portfolioTotalCostList=new ArrayList<String>();
//        portfolioMarketValueList=new ArrayList<String>();
//        portfolioPriceChangeList=new ArrayList<String>();
//        portfolioPercentPriceChangeList=new ArrayList<String>();
//        favoritesTickerList=new ArrayList<String>();
//        favoritesCompanyNameList=new ArrayList<String>();
//        favoritesCurrentPriceList=new ArrayList<String>();
//        favoritesPriceChangeList=new ArrayList<String>();
//        favoritesPercentPriceChangeList=new ArrayList<String>();


        Log.d("checkingt","I am resumed ticker list is"+portfolioTickerList);
      //  setPortfolioSection();
      //  setFavoritesSection();
        //testingUpdate();
     //   c();

    }


//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.d("checkingt","I am resuming");
//        setPortfolioSection();
//        setFavoritesSection();
//    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("tuesday","stopping");
        recyclerViewPortfolio.setAdapter(null);
        recyclerViewPortfolio.setAdapter(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //following code is referred from hints section
        //inflate menu
        StringBuilder sb=new StringBuilder();
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        Intent tempIntent=new Intent(this,StockDetailsActivity.class);
        //set the search menu
        MenuItem searchMenu=menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) searchMenu.getActionView();

        //set the date
        Date todaysDate=Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        String dateFormatted=sdf.format(todaysDate);
        tvDate=(TextView) findViewById(R.id.textViewDate);
        tvDate.setText(dateFormatted);
        context=tvDate.getContext();



        mContext = context;
        mBackground = new ColorDrawable();
        backgroundColor = Color.parseColor("#b80f0a");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.delete);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();





        //Search autocomplete
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);



        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteData);
        searchAutoComplete.setAdapter(autoCompleteAdapter);


        RequestQueue requestQueueAutoComplete;
        String urlAutoComplete="https://hw8april3final.wl.r.appspot.com/stockdetails_1?_keystock_=";
        requestQueueAutoComplete=Volley.newRequestQueue(context);


        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable[] workRunnable = {null};

        searchAutoComplete.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchAutoComplete.dismissDropDown();
                handler.removeCallbacks(workRunnable[0]);
                workRunnable[0] =()-> apiCallSample(charSequence);
                handler.postDelayed(workRunnable[0],1200);
               // apiCallSample(charSequence);

            }

            public void apiCallSample(CharSequence charSequence){
                Log.d("list12","delayed");
                ArrayList<String> tempList=new ArrayList<>();
                String tickerEntered=charSequence.toString();
                Log.d("list12","url is "+urlAutoComplete+tickerEntered);
                JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, urlAutoComplete + tickerEntered, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            Log.d("list12","url in try is "+urlAutoComplete+tickerEntered);
                            for(int i=0;i<response.length();i++){
                                JSONObject jsonObject=response.getJSONObject(i);

                                tempList.add(jsonObject.get("symbol").toString()+" | "+jsonObject.get("description").toString());
                                Log.d("list12", String.valueOf(tempList));
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            Log.d("list12","in catch ");
                        }

                        ArrayAdapter temp = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, tempList);
                        searchAutoComplete.setAdapter(temp);
                        temp.notifyDataSetChanged();
                        searchAutoComplete.showDropDown();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("list12","in error "+error);
                    }
                });
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

                ));

                requestQueueAutoComplete.add(jsonArrayRequest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //volley implementation

          RequestQueue requestQueue;
          String url="http://hw8april3final.wl.r.appspot.com/stockdetails_2?_keystock_=";
          requestQueue=Volley.newRequestQueue(this);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                String tickerToSend;
                String[] tempStringArray=queryString.split(" ",2);
                tickerToSend=tempStringArray[0];
                searchAutoComplete.setText("" + tickerToSend);

              //  Log.d("tickerSend","ticker to send is"+tickerToSend.toUpperCase(Locale.ROOT));


                tempIntent.putExtra("TickerValue",tickerToSend.toUpperCase(Locale.ROOT));
                startActivity(tempIntent);

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void setSearchCustom(){

    }

    public void arraylistTesting(){
        ArrayList<String> testing=new ArrayList<>();
        testing.add(0,"ajinkya");
        Log.d("arraylist","array is"+testing);
        testing.add(0,"jay");
        Log.d("arraylist","array is"+testing);
    }


    public void setNetWorthAndBalance(){
        tvHomeNetWorth=(TextView) findViewById(R.id.textViewHomeNetWorth);
        tvHomeCashBalance=(TextView) findViewById(R.id.textViewHomeCashBalance);

        SharedPreferences balanceSharedPref=getSharedPreferences("balanceAndroidLocal",MODE_PRIVATE);
        SharedPreferences.Editor editor=balanceSharedPref.edit();

        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editorPortfolio=portfolioSharedPref.edit();

        Gson gsonNetWorth=new Gson();
        Gson gsonBalance=new Gson();
        Gson gsonMarketValue=new Gson();
        Gson gsonMarketValueNetWorthCalculation=new Gson();
        Gson gsonNetWorthCalculationTotalShares=new Gson();

        String responseNetWorth=balanceSharedPref.getString("keyNetWorthLocal","");
        String responseBalance=balanceSharedPref.getString("keyBalanceLocal","");
        String responseMarketValue=portfolioSharedPref.getString("keyMarketValueLocal","[]");
//        String responseMarketValueNetWorthCalculation=portfolioSharedPref.getString("keyMarketValueNetWorthCalculation","[]");
//        String responseNetWorthCalculationTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");

//        marketValueNetWorthCalculationList=gsonMarketValueNetWorthCalculation.fromJson(responseMarketValueNetWorthCalculation, new TypeToken<List<String>>() {}.getType());
//        netWorthCalculationTotalSharesList=gsonNetWorthCalculationTotalShares.fromJson(responseNetWorthCalculationTotalShares, new TypeToken<List<String>>() {}.getType());


        netWorthList=gsonNetWorth.fromJson(responseNetWorth, new TypeToken<List<String>>() {}.getType());
        balanceList=gsonBalance.fromJson(responseBalance, new TypeToken<List<String>>() {}.getType());
        portfolioMarketValueList=gsonMarketValue.fromJson(responseMarketValue, new TypeToken<List<String>>() {}.getType());

        String cashBalanceFormatted=formatter.format(Float.parseFloat(balanceList.get(0)));
        tvHomeCashBalance.setText("$"+cashBalanceFormatted);

        float marketValueSum=0;
        for(int i=0;i<portfolioMarketValueList.size();i++){
            marketValueSum=marketValueSum+Float.parseFloat(portfolioMarketValueList.get(i));
        }
        cashBalance=Float.parseFloat(balanceList.get(0));


       // final TextView tvTempRahul = (TextView)findViewById(R.id.textViewHomeNetWorth);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        //Log.d("Rahul","I am updating");
                        String responseMarketValue=portfolioSharedPref.getString("keyMarketValueLocal","[]");
                        String responseBalance=balanceSharedPref.getString("keyBalanceLocal","");
                        String responseMarketValueNetWorthCalculation=portfolioSharedPref.getString("keyMarketValueNetWorthCalculation","[]");
                        String responseNetWorthCalculationTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");

                        portfolioMarketValueList=gsonMarketValue.fromJson(responseMarketValue, new TypeToken<List<String>>() {}.getType());
                        balanceList=gsonBalance.fromJson(responseBalance, new TypeToken<List<String>>() {}.getType());
                        marketValueNetWorthCalculationList=gsonMarketValueNetWorthCalculation.fromJson(responseMarketValueNetWorthCalculation, new TypeToken<List<String>>() {}.getType());
                        netWorthCalculationTotalSharesList=gsonNetWorthCalculationTotalShares.fromJson(responseNetWorthCalculationTotalShares, new TypeToken<List<String>>() {}.getType());

                        float marketValueSum=0;
                        for(int i=0;i<marketValueNetWorthCalculationList.size();i++){
                            marketValueSum=marketValueSum+((Float.parseFloat(marketValueNetWorthCalculationList.get(i)))*(Float.parseFloat(netWorthCalculationTotalSharesList.get(i))));
                        }
                        cashBalance=Float.parseFloat(balanceList.get(0));

                        float tempVariable=0;

                        tempVariable=cashBalance+marketValueSum;
                        String formattedtempVariable="";
                        formattedtempVariable=formatter.format(tempVariable);
                        Log.d("Rahul","temp variable is"+formattedtempVariable);
                        Log.d("Rahul","formattedtempVariable is"+formattedtempVariable);

                        tvHomeNetWorth.setText("$"+formatter.format(tempVariable));
                    }
                });
            }
        }, 0, 15000);

        String netWorthFormatted=formatter.format(Float.parseFloat(netWorthList.get(0)));
//        String cashBalanceFormatted=formatter.format(Float.parseFloat(balanceList.get(0)));
//        tvHomeCashBalance.setText("$"+cashBalanceFormatted);


    }



    public void testingUpdate(){
        tvHomeNetWorth=(TextView) findViewById(R.id.textViewHomeNetWorth);

        tvHomeNetWorth.setText("ajinkya");

        final TextView tvTempRahul = (TextView)findViewById(R.id.textViewHomeNetWorth);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("Rahul","I am updating");
                        tvTempRahul.setText("Rahul");
                    }
                });
            }
        }, 0, 5000);


    }

    public void setZeroNetWorthAndBalance(){


        SharedPreferences balanceSharedPref=getSharedPreferences("balanceAndroidLocal",MODE_PRIVATE);
        SharedPreferences.Editor editor=balanceSharedPref.edit();
        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editorPortfolio=portfolioSharedPref.edit();

        Gson gsonNetWorth=new Gson();
        Gson gsonBalance=new Gson();
        Gson gsonMarketValueNetWorthCalculation=new Gson();

        String responseNetWorth=balanceSharedPref.getString("keyNetWorthLocal","");
        String responseBalance=balanceSharedPref.getString("keyBalanceLocal","");
        String responseMarketValueNetWorthCalculation=portfolioSharedPref.getString("keyMarketValueNetWorthCalculation","[]");
        //netWorthString=gsonNetWorth.fromJson(responseNetWorth, new TypeToken<List<String>>() {}.getType());
        //balanceString=gsonBalance.fromJson(responseBalance, new TypeToken<List<String>>() {}.getType());
        netWorthList.add(String.valueOf(25000));
        balanceList.add(String.valueOf(25000));
        marketValueNetWorthCalculationList.clear();

        String gsonToJsonNetWorth=gsonNetWorth.toJson(netWorthList);
        String gsonToJsonBalance=gsonBalance.toJson(balanceList);
        String gsonToJsonmarketValueNetWorthCalculation=gsonMarketValueNetWorthCalculation.toJson(marketValueNetWorthCalculationList);
        editor.putString("keyNetWorthLocal",gsonToJsonNetWorth);
        editor.putString("keyBalanceLocal",gsonToJsonBalance);
        editorPortfolio.putString("keyMarketValueNetWorthCalculation",gsonToJsonmarketValueNetWorthCalculation);
        editor.apply();
        editorPortfolio.apply();
       // readbalance();



    }

    public void readbalance(){

        SharedPreferences balanceSharedPref=getSharedPreferences("balanceAndroidLocal",MODE_PRIVATE);
        SharedPreferences.Editor editor=balanceSharedPref.edit();
        Gson gsonNetWorth=new Gson();
        Gson gsonBalance=new Gson();
        String responseNetWorth=balanceSharedPref.getString("keyNetWorthLocal","");
        String responseBalance=balanceSharedPref.getString("keyBalanceLocal","");
        netWorthList=gsonNetWorth.fromJson(responseNetWorth, new TypeToken<List<String>>() {}.getType());
        balanceList=gsonBalance.fromJson(responseBalance, new TypeToken<List<String>>() {}.getType());
        Log.d("balance","Reading balance from local"+netWorthList+"  "+balanceList);
    }


    public void setPortfolioSection(){
//        recyclerViewPortfolio=findViewById(R.id.recyclerViewPortfolio);
        //Log.d("ajinkya","Inside set portfolio section findviewportfolio"+recyclerViewPortfolio);
        //Log.d("checkingt","I am getting called from details");

        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor=portfolioSharedPref.edit();
        Gson gsonTicker=new Gson();
        Gson gsonTotalShares=new Gson();
        Gson gsonTotalCost=new Gson();
        Gson gsonMarketValue=new Gson();
        Gson gsonPriceChange=new Gson();
        Gson gsonPercentPriceChange=new Gson();
        Gson gsonPercentPriceChangeColor=new Gson();
        String responseTicker=portfolioSharedPref.getString("keyTickerPortfolioLocal","[]");
        String responseTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");
        String responseTotalCost=portfolioSharedPref.getString("keyTotalCostLocal","[]");
        String responseMarketValue=portfolioSharedPref.getString("keyMarketValueLocal","[]");
        String responsePriceChange=portfolioSharedPref.getString("keyPriceChangeLocal","[]");
        String responsePercentPriceChange=portfolioSharedPref.getString("keyPercentPriceChangeLocal","[]");
        portfolioTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
        portfolioTotalSharesList=gsonTotalShares.fromJson(responseTotalShares, new TypeToken<List<String>>() {}.getType());
        portfolioTotalCostList=gsonTotalCost.fromJson(responseTotalCost, new TypeToken<List<String>>() {}.getType());
        portfolioMarketValueList=gsonMarketValue.fromJson(responseMarketValue, new TypeToken<List<String>>() {}.getType());
        portfolioPriceChangeList=gsonPriceChange.fromJson(responsePriceChange, new TypeToken<List<String>>() {}.getType());
        portfolioPercentPriceChangeList=gsonPercentPriceChange.fromJson(responsePercentPriceChange, new TypeToken<List<String>>() {}.getType());
        Log.d("tuesday","In setPortfolio from homeactivity, tickers are"+portfolioTickerList);
        recyclerViewPortfolio.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new PortfolioAdapter(portfolioTickerList, portfolioTotalSharesList, portfolioTotalCostList,portfolioMarketValueList,portfolioPriceChangeList,portfolioPercentPriceChangeList);
        ItemTouchHelper.Callback callback= new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewPortfolio);
        recyclerViewPortfolio.setAdapter(mAdapter);


    }



    public void setFavoritesSection(){
        //recyclerViewFavorites=findViewById(R.id.recyclerViewFavorites);

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

        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        fAdapter=new FavoritesAdapter(favoritesTickerList,favoritesCompanyNameList,favoritesCurrentPriceList,favoritesPriceChangeList,favoritesPercentPriceChangeList);
        ItemTouchHelper.Callback callback= new ItemMoveCallbackFav(fAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewFavorites);
        recyclerViewFavorites.setAdapter(fAdapter);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallbackFav);
        itemTouchHelper.attachToRecyclerView(recyclerViewFavorites);

    }

    ItemTouchHelper.SimpleCallback simpleCallbackFav=new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position=viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:

                    favoritesTickerList.remove(position);
                    favoritesCompanyNameList.remove(position);
                    favoritesCurrentPriceList.remove(position);
                    favoritesPriceChangeList.remove(position);
                    favoritesPercentPriceChangeList.remove(position);

                    SharedPreferences favoritesSharedPref= context.getSharedPreferences("favoritesAndroidLocal", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=favoritesSharedPref.edit();
                    Gson gsonTicker=new Gson();
                    Gson gsonCompanyName=new Gson();
                    Gson gsonCurrentPrice=new Gson();
                    Gson gsonPriceChange=new Gson();
                    Gson gsonPercentPriceChange=new Gson();
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


                    fAdapter.notifyItemRemoved(position);
                    break;

                case ItemTouchHelper.RIGHT:

                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            boolean isCancelled = dX == 0 && !isCurrentlyActive;

            if (isCancelled) {
                clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            mBackground.setColor(backgroundColor);
            mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;


            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);

    }








    public void clearLocalStorageFavorites(){
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
        favoritesTickerList.clear();
        favoritesCompanyNameList.clear();
        favoritesCurrentPriceList.clear();
        favoritesPriceChangeList.clear();
        favoritesPercentPriceChangeList.clear();
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

    public void clearLocalStoragePortfolio(){
        ArrayList<String> portfolioTickerList=new ArrayList<String>();
        ArrayList<String> portfolioTotalSharesList=new ArrayList<String>();
        ArrayList<String> portfolioTotalCostList=new ArrayList<String>();
        ArrayList<String> portfolioMarketValueList=new ArrayList<String>();
        ArrayList<String> portfolioPriceChangeList=new ArrayList<String>();
        ArrayList<String> portfolioPercentPriceChangeList=new ArrayList<String>();
        SharedPreferences portfolioSharedPref= getSharedPreferences("portfolioAndroidLocal", MODE_PRIVATE);
        SharedPreferences.Editor editor=portfolioSharedPref.edit();
        Gson gsonTicker=new Gson();
        Gson gsonTotalShares=new Gson();
        Gson gsonTotalCost=new Gson();
        Gson gsonMarketValue=new Gson();
        Gson gsonPriceChange=new Gson();
        Gson gsonPercentPriceChange=new Gson();
        String responseTicker=portfolioSharedPref.getString("keyTickerPortfolioLocal","[]");
        String responseTotalShares=portfolioSharedPref.getString("keyTotalSharesLocal","[]");
        String responseTotalCost=portfolioSharedPref.getString("keyTotalCostLocal","[]");
        String responseMarketValue=portfolioSharedPref.getString("keyMarketValueLocal","[]");
        String responsePriceChange=portfolioSharedPref.getString("keyPriceChangeLocal","[]");
        String responsePercentPriceChange=portfolioSharedPref.getString("keyPercentPriceChangeLocal","[]");
        portfolioTickerList=gsonTicker.fromJson(responseTicker, new TypeToken<List<String>>() {}.getType());
        portfolioTotalSharesList=gsonTotalShares.fromJson(responseTotalShares, new TypeToken<List<String>>() {}.getType());
        portfolioTotalCostList=gsonTotalCost.fromJson(responseTotalCost, new TypeToken<List<String>>() {}.getType());
        portfolioMarketValueList=gsonMarketValue.fromJson(responseMarketValue, new TypeToken<List<String>>() {}.getType());
        portfolioPriceChangeList=gsonPriceChange.fromJson(responsePriceChange, new TypeToken<List<String>>() {}.getType());
        portfolioPercentPriceChangeList=gsonPercentPriceChange.fromJson(responsePercentPriceChange, new TypeToken<List<String>>() {}.getType());
        portfolioTickerList.clear();
        portfolioTotalSharesList.clear();
        portfolioTotalCostList.clear();
        portfolioMarketValueList.clear();
        portfolioPriceChangeList.clear();
        portfolioPercentPriceChangeList.clear();
        String gsonToJsonTickerPortfolio=gsonTicker.toJson(portfolioTickerList);
        String gsonToJsonTotalShares=gsonTotalShares.toJson(portfolioTotalSharesList);
        String gsonToJsonTotalCost=gsonTotalCost.toJson(portfolioTotalCostList);
        String gsonToJsonMarketValue=gsonTotalCost.toJson(portfolioMarketValueList);
        String gsonToJsonPriceChange=gsonTotalCost.toJson(portfolioPriceChangeList);
        String gsonToJsonPercentPriceChange=gsonTotalCost.toJson(portfolioPercentPriceChangeList);
        editor.putString("keyTickerPortfolioLocal",gsonToJsonTickerPortfolio);
        editor.putString("keyTotalSharesLocal", gsonToJsonTotalShares);
        editor.putString("keyTotalCostLocal", gsonToJsonTotalCost);
        editor.putString("keyMarketValueLocal", gsonToJsonMarketValue);
        editor.putString("keyPriceChangeLocal", gsonToJsonPriceChange);
        editor.putString("keyPercentPriceChangeLocal", gsonToJsonPercentPriceChange);
        editor.apply();
    }










}