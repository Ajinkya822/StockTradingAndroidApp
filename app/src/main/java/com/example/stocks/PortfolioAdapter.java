package com.example.stocks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract{

    // private String[] s;
    Context context;
    private ArrayList<String> portfolioTickerList=new ArrayList<String>();
    private ArrayList<String> portfolioTotalSharesList=new ArrayList<String>();
    private ArrayList<String> portfolioTotalCostList=new ArrayList<String>();
    private ArrayList<String> portfolioMarketValueList=new ArrayList<String>();
    private ArrayList<String> portfolioPriceChangeList=new ArrayList<String>();
    private ArrayList<String> portfolioPercentPriceChangeList=new ArrayList<String>();
    private ArrayList<String> marketValueNetWorthCalculationList=new ArrayList<>();
    float cashBalance;
    Timer timer=new Timer();
    TimerTask portfolioUpdateTask;
    //private ArrayList<String> portfolioPercentPriceChangeColorList=new ArrayList<String>();
    String currentPrice;
    float currentPriceStockCalculation;
    NumberFormat formatter = new DecimalFormat("#0.00");

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvHomePortfolioTicker, tvHomePortfolioShares, tvHomePortfolioMarketValue, tvPortfolioHomePriceAndPercentChange;
        ImageView ivPortfolioHomeTrendingIcon, ivPortfolioHomeChevron;
        View rowView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            tvHomePortfolioTicker=(TextView) itemView.findViewById(R.id.textViewHomePortfolioTicker);
            tvHomePortfolioShares=(TextView) itemView.findViewById(R.id.textViewHomePortfolioShares);
            tvHomePortfolioMarketValue=(TextView) itemView.findViewById(R.id.textViewHomePortfolioMarketValue);
            tvPortfolioHomePriceAndPercentChange=(TextView) itemView.findViewById(R.id.textViewPortfolioHomePriceAndPercentChange);
            ivPortfolioHomeTrendingIcon=(ImageView) itemView.findViewById(R.id.imageViewPortfolioHomeTrendingIcon);
            ivPortfolioHomeChevron=(ImageView) itemView.findViewById(R.id.imageViewPortfolioHomeChevron);
            //Log.d("ajinkya", "in portfolioviewholder"+portfolioTickerList.size());
        }
    }

    public PortfolioAdapter(ArrayList<String> portfolioTickerList, ArrayList<String> portfolioTotalSharesList, ArrayList<String> portfolioTotalCostList, ArrayList<String> portfolioMarketValueList,
                            ArrayList<String> portfolioPriceChangeList,ArrayList<String> portfolioPercentPriceChangeList){
        this.portfolioTickerList=portfolioTickerList;
        this.portfolioTotalSharesList=portfolioTotalSharesList;
        this.portfolioTotalCostList=portfolioTotalCostList;
        this.portfolioMarketValueList=portfolioMarketValueList;
        this.portfolioPriceChangeList=portfolioPriceChangeList;
        this.portfolioPercentPriceChangeList=portfolioPercentPriceChangeList;
//        Log.d("ajinkya", "In portfolio adapter... ticker is"+this.portfolioTickerList);
//        Log.d("ajinkya", "in constructor size is"+portfolioTickerList.size());
        //this.portfolioPercentPriceChangeColorList=portfolioPercentPriceChangeColorList;
    }





    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v;
//        PortfolioViewHolder portfolioViewHolder;
//        v=LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_section,parent,false);
//        portfolioViewHolder=new PortfolioViewHolder(v,viewType);
//        return portfolioViewHolder;
        //Log.d("ajinkya", "in view holder size is"+portfolioTickerList.size());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_section, parent, false);
        context=parent.getContext();
        Log.d("checkingt", "on create view holder");
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Log.d("portfolio", "aj"+s[position]);
        Log.d("checkingt", "onBind view holder");


        portfolioUpdateTask=new TimerTask() {
            @Override
            public void run() {
                RequestQueue requestQueuePriceChange;
                SharedPreferences portfolioSharedPref= context.getSharedPreferences("portfolioAndroidLocal", context.MODE_PRIVATE);
                SharedPreferences.Editor editor=portfolioSharedPref.edit();
                Gson gsonMarketValueNetWorthCalculation=new Gson();
                String responseMarketValueNetWorthCalculation=portfolioSharedPref.getString("keyMarketValueNetWorthCalculation","[]");
                marketValueNetWorthCalculationList=gsonMarketValueNetWorthCalculation.fromJson(responseMarketValueNetWorthCalculation, new TypeToken<List<String>>() {}.getType());

//                Log.d("checkingt","portfolio tickers are in api call"+portfolioTickerList);
                //Log.d("ajinkya", "Calling api in portfolio");
                //Log.d("ajinkya", "Portfolio position"+holder.getAdapterPosition());
                String urlPriceChange="https://hw8april3final.wl.r.appspot.com/stockdetails_3priceChange?_keystock_=";
                requestQueuePriceChange= Volley.newRequestQueue(holder.tvHomePortfolioShares.getContext());
                if(holder.getAdapterPosition()!=-1){
                    JsonObjectRequest jsonObjectRequestPriceChange=new JsonObjectRequest(Request.Method.GET, urlPriceChange + portfolioTickerList.get(holder.getAdapterPosition()), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                //get index of ticker
//                                int tickerIndexLocalStorage=holder.getAdapterPosition();
//                                portfolioTickerList.set(tickerIndexLocalStorage, portfolioTickerList.get(tickerIndexLocalStorage));



                                //Log.d("checkingt","api call in portfolio");
                                //Log.d("checkingt","checking adapter position");
                                Log.d("tuesday","In Portfolio adapter, local tickers are"+portfolioTickerList);
                                Log.d("tuesday","In Portfolio adapter, specific ticker is"+portfolioTickerList.get(holder.getAdapterPosition()));
//                                Log.d("checkingt","portfolio tickers in try are"+portfolioTickerList);
                                //Log.d("checkingt","timer is"+portfolioUpdateTask.);
                                holder.tvHomePortfolioTicker.setText(portfolioTickerList.get(holder.getAdapterPosition()));
                                holder.tvHomePortfolioShares.setText(portfolioTotalSharesList.get(holder.getAdapterPosition())+" shares");
                                currentPrice=response.get("c").toString();
                                marketValueNetWorthCalculationList.set(holder.getAdapterPosition(), currentPrice);
                                String gsonToJsonmarketValueNetWorthCalculation=gsonMarketValueNetWorthCalculation.toJson(marketValueNetWorthCalculationList);
                                editor.putString("keyMarketValueNetWorthCalculation",gsonToJsonmarketValueNetWorthCalculation);
                                editor.apply();

                                currentPriceStockCalculation=Float.parseFloat(currentPrice);
                                //Log.d("ajinkya",portfolioTickerList.get(holder.getAdapterPosition())+" "+currentPrice);
                                float numOfShares=Float.parseFloat(portfolioTotalSharesList.get(holder.getAdapterPosition()));
                                float marketValueShare=numOfShares*currentPriceStockCalculation;
                                String formattedMktVal=formatter.format(marketValueShare);
                                holder.tvHomePortfolioMarketValue.setText("$"+formattedMktVal);
                                //price change=(latest price*num of shares)-(avg cost*num of shares)
                                float avgCostOfShare=Float.parseFloat(portfolioTotalCostList.get(holder.getAdapterPosition()))/numOfShares;
                                float priceChange=numOfShares*(currentPriceStockCalculation-avgCostOfShare);
                                //percent price change= (priceChange/total cost of stock)*100
                                float percentPriceChange=(priceChange/Float.parseFloat(portfolioTotalCostList.get(holder.getAdapterPosition())))*100;
                                String formattedPriceChange=formatter.format(priceChange);
                                String formattedPercentPriceChange=formatter.format(percentPriceChange);



                                if(Float.parseFloat(formattedPriceChange)>0){

                                    holder.tvPortfolioHomePriceAndPercentChange.setText("$"+formattedPriceChange+"("+formattedPercentPriceChange+"%)");
                                    holder.tvPortfolioHomePriceAndPercentChange.setTextColor(Color.parseColor("#228B22"));
                                    holder.ivPortfolioHomeTrendingIcon.setImageResource(R.drawable.trending_up_icon);
                                }
                                else if(Float.parseFloat(formattedPriceChange)<0){

                                    holder.tvPortfolioHomePriceAndPercentChange.setText("$"+formattedPriceChange+"("+formattedPercentPriceChange+"%)");
                                    holder.tvPortfolioHomePriceAndPercentChange.setTextColor(Color.parseColor("#FF0000"));
                                    holder.ivPortfolioHomeTrendingIcon.setImageResource(R.drawable.trending_down_icon);
                                }
                                else{

                                    holder.tvPortfolioHomePriceAndPercentChange.setText("$"+formattedPriceChange+"("+formattedPercentPriceChange+"%)");
                                    holder.tvPortfolioHomePriceAndPercentChange.setTextColor(Color.parseColor("#000000"));

                                }

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
                    requestQueuePriceChange.add(jsonObjectRequestPriceChange);
                }

            }
        };
        timer.schedule(portfolioUpdateTask, 0, 15000);
        //chevron click to start new activity
        holder.ivPortfolioHomeChevron.setImageResource(R.drawable.ic_chevron_right);
        holder.ivPortfolioHomeChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int portfolioAdapterPositionInView= holder.getAdapterPosition();
                Intent tempIntent=new Intent(view.getContext(),StockDetailsActivity.class);
                tempIntent.putExtra("TickerValue",portfolioTickerList.get(portfolioAdapterPositionInView).toUpperCase(Locale.ROOT));
                view.getContext().startActivity(tempIntent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return portfolioTickerList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(portfolioTickerList, i, i + 1);
                Collections.swap(portfolioTotalSharesList, i, i + 1);
                Collections.swap(portfolioTotalCostList, i, i + 1);
                Collections.swap(portfolioMarketValueList, i, i + 1);
                Collections.swap(portfolioPriceChangeList, i, i + 1);
                Collections.swap(portfolioPercentPriceChangeList, i, i + 1);
                Collections.swap(marketValueNetWorthCalculationList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(portfolioTickerList, i, i - 1);
                Collections.swap(portfolioTotalSharesList, i, i - 1);
                Collections.swap(portfolioTotalCostList, i, i - 1);
                Collections.swap(portfolioMarketValueList, i, i - 1);
                Collections.swap(portfolioPriceChangeList, i, i - 1);
                Collections.swap(portfolioPercentPriceChangeList, i, i - 1);
                Collections.swap(marketValueNetWorthCalculationList, i, i - 1);
            }
        }
        SharedPreferences portfolioSharedPref= context.getSharedPreferences("portfolioAndroidLocal", context.MODE_PRIVATE);
        SharedPreferences.Editor editor=portfolioSharedPref.edit();
        Gson gsonTicker=new Gson();
        Gson gsonTotalShares=new Gson();
        Gson gsonTotalCost=new Gson();
        Gson gsonMarketValue=new Gson();
        Gson gsonPriceChange=new Gson();
        Gson gsonPercentPriceChange=new Gson();
        String gsonToJsonTickerPortfolio=gsonTicker.toJson(portfolioTickerList);
        String gsonToJsonTotalShares=gsonTotalShares.toJson(portfolioTotalSharesList);
        String gsonToJsonTotalCost=gsonTotalCost.toJson(portfolioTotalCostList);
        String gsonToJsonMarketValue=gsonMarketValue.toJson(portfolioMarketValueList);
        String gsonToJsonPriceChange=gsonPriceChange.toJson(portfolioPriceChangeList);
        String gsonToJsonPercentPriceChange=gsonPercentPriceChange.toJson(portfolioPercentPriceChangeList);
        editor.putString("keyTickerPortfolioLocal",gsonToJsonTickerPortfolio);
        editor.putString("keyTotalSharesLocal", gsonToJsonTotalShares);
        editor.putString("keyTotalCostLocal", gsonToJsonTotalCost);
        editor.putString("keyMarketValueLocal", gsonToJsonMarketValue);
        editor.putString("keyPriceChangeLocal", gsonToJsonPriceChange);
        editor.putString("keyPercentPriceChangeLocal", gsonToJsonPercentPriceChange);
        Log.d("checkingt","portfolio tickers are after swap in function"+portfolioTickerList);
        editor.apply();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(MyViewHolder myViewHolder) {
        //myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(MyViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);

    }



}