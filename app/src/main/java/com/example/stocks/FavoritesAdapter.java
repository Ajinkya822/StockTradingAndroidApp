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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> implements ItemMoveCallbackFav.ItemTouchHelperContract {

    Context context;
    private String[] s;
    private ArrayList<String> favoritesTickerList=new ArrayList<String>();
    private ArrayList<String> favoritesCompanyNameList=new ArrayList<String>();
    private ArrayList<String> favoritesCurrentPriceList=new ArrayList<String>();
    private ArrayList<String> favoritesPriceChangeList=new ArrayList<String>();
    private ArrayList<String> favoritesPercentPriceChangeList=new ArrayList<String>();
    String currentPrice;
    String priceChange,percentPriceChange;
    float currentPriceStockCalculation;
    NumberFormat formatter = new DecimalFormat("#0.00");

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTicker, tvCompanyName, tvCurrentPrice, tvPriceAndPercentPriceChange;
        ImageView ivHomeFavoritesHomeChevron, ivTrendingIcon;
        View rowView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            tvTicker=(TextView) itemView.findViewById(R.id.textViewFavoritesTicker);
            tvCompanyName=(TextView) itemView.findViewById(R.id.textViewFavoritesCompanyName);
            tvCurrentPrice=(TextView) itemView.findViewById(R.id.textViewFavoritesCurrentPrice);
            tvPriceAndPercentPriceChange=(TextView) itemView.findViewById(R.id.textViewFavoritesHomePriceAndPercentChange);
            ivHomeFavoritesHomeChevron=(ImageView) itemView.findViewById(R.id.imageViewHomeFavoritesHomeChevron);
            ivTrendingIcon=(ImageView) itemView.findViewById(R.id.imageViewFavoritesHomeTrendingIcon);
        }
    }

//    public FavoritesAdapter(String s[]){
//        Log.d("favorites", String.valueOf(s.length));
//        this.s=s;
//    }
    public FavoritesAdapter(ArrayList<String> favoritesTickerList, ArrayList<String> favoritesCompanyNameList, ArrayList<String> favoritesCurrentPriceList, ArrayList<String> favoritesPriceChangeList,
                            ArrayList<String> favoritesPercentPriceChangeList){
        //Log.d("favorites", String.valueOf(s.length));
        this.favoritesTickerList=favoritesTickerList;
        this.favoritesCompanyNameList=favoritesCompanyNameList;
        this.favoritesCurrentPriceList=favoritesCurrentPriceList;
        this.favoritesPriceChangeList=favoritesPriceChangeList;
        this.favoritesPercentPriceChangeList=favoritesPercentPriceChangeList;
//        Log.d("ajinkya","In favorites constructor current price are"+this.favoritesCurrentPriceList);
//        Log.d("ajinkya","In favorites constructor price change are"+this.favoritesPriceChangeList);
//        Log.d("ajinkya","In favorites constructor percent price change are"+this.favoritesPercentPriceChangeList);
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_section, parent, false);
        context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       // Log.d("portfolio", "aj"+s[position]);
//        holder.tvTicker.setText(favoritesTickerList.get(position));
//        holder.tvCompanyName.setText(favoritesCompanyNameList.get(position));
        holder.ivHomeFavoritesHomeChevron.setImageResource(R.drawable.ic_chevron_right);
//        float tempCurrentPrice=Float.parseFloat(favoritesCurrentPriceList.get(position));
//        holder.tvCurrentPrice.setText("$"+tempCurrentPrice);

//        float tempPC=Float.parseFloat(favoritesPriceChangeList.get(position));
//        float tempPPC=Float.parseFloat(favoritesPercentPriceChangeList.get(position));
//        holder.tvPriceAndPercentPriceChange.setText("$"+tempPC+"("+tempPPC+"%)");

//        if(Float.parseFloat(favoritesPriceChangeList.get(position))>0){
//            holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#00FF00"));
//            holder.ivTrendingIcon.setImageResource(R.drawable.trending_up_icon);
//        }
//        else if(Float.parseFloat(favoritesPriceChangeList.get(position))<0){
//            holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#FF0000"));
//            holder.ivTrendingIcon.setImageResource(R.drawable.trending_down_icon);
//        }
//        else{
//            holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#000000"));
//        }

        Timer timer=new Timer();
        TimerTask favoritesUpdateTask=new TimerTask() {
            @Override
            public void run() {
                RequestQueue requestQueuePriceChange;
                String urlPriceChange="https://hw8april3final.wl.r.appspot.com/stockdetails_3priceChange?_keystock_=";
                requestQueuePriceChange= Volley.newRequestQueue(holder.tvCompanyName.getContext());
                if(holder.getAdapterPosition()!=-1){
                    JsonObjectRequest jsonObjectRequestPriceChange=new JsonObjectRequest(Request.Method.GET, urlPriceChange + favoritesTickerList.get(holder.getAdapterPosition()), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                               // Log.d("checkingt","api call in favorites");
                                holder.tvTicker.setText(favoritesTickerList.get(holder.getAdapterPosition()));
                                holder.tvCompanyName.setText(favoritesCompanyNameList.get(holder.getAdapterPosition()));
                                currentPrice=response.get("c").toString();
                                Log.d("ajinkya","Updating portfolio"+currentPrice);
                                float currentPriceFloatLive=Float.parseFloat(currentPrice);
                                String formattedCurrentPrice=formatter.format(currentPriceFloatLive);
                                holder.tvCurrentPrice.setText("$"+formattedCurrentPrice);

                                priceChange=response.get("d").toString();
                                float priceChangeFloatLive=Float.parseFloat(priceChange);
                                percentPriceChange=response.get("dp").toString();
                                float percentPriceChangeFloatLive=Float.parseFloat(percentPriceChange);
                                String formattedPriceChange=formatter.format(priceChangeFloatLive);
                                String formattedPercentPriceChange=formatter.format(percentPriceChangeFloatLive);
                                holder.tvPriceAndPercentPriceChange.setText("$"+formattedPriceChange+"("+formattedPercentPriceChange+"%)");

                                if(Float.parseFloat(formattedPriceChange)>0){
                                    holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#228B22"));
                                    holder.ivTrendingIcon.setImageResource(R.drawable.trending_up_icon);
                                }
                                else if(Float.parseFloat(formattedPriceChange)<0){
                                    holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#FF0000"));
                                    holder.ivTrendingIcon.setImageResource(R.drawable.trending_down_icon);
                                }
                                else{
                                    holder.tvPriceAndPercentPriceChange.setTextColor(Color.parseColor("#000000"));
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

        timer.schedule(favoritesUpdateTask, 0, 15000);



        //chevron click to start new activity
        holder.ivHomeFavoritesHomeChevron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int favoritesAdapterPositionInView= holder.getAdapterPosition();
                Intent tempIntent=new Intent(v.getContext(),StockDetailsActivity.class);
                tempIntent.putExtra("TickerValue",favoritesTickerList.get(favoritesAdapterPositionInView).toUpperCase(Locale.ROOT));
                v.getContext().startActivity(tempIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesTickerList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favoritesTickerList, i, i + 1);
                Collections.swap(favoritesCompanyNameList, i, i + 1);
                Collections.swap(favoritesCurrentPriceList, i, i + 1);
                Collections.swap(favoritesPriceChangeList, i, i + 1);
                Collections.swap(favoritesPercentPriceChangeList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favoritesTickerList, i, i - 1);
                Collections.swap(favoritesCompanyNameList, i, i - 1);
                Collections.swap(favoritesCurrentPriceList, i, i - 1);
                Collections.swap(favoritesPriceChangeList, i, i - 1);
                Collections.swap(favoritesPercentPriceChangeList, i, i - 1);
            }
        }

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
