package com.example.stocks;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private String[] data,data1;
    ImageView ivChromeIcon,ivTwitterIcon,ivFacebookIcon;
    private ArrayList<String> newsDatetime=new ArrayList<String>();
    private ArrayList<String> newsHeadline=new ArrayList<String>();
    private ArrayList<String> newsImageUrl=new ArrayList<String>();
    private ArrayList<String> newsSource=new ArrayList<String>();
    private ArrayList<String> newsSummary=new ArrayList<String>();
    private ArrayList<String> newsDetailUrl=new ArrayList<String>();
    public NewsAdapter(ArrayList<String> newsDatetime,ArrayList<String> newsHeadline,ArrayList<String> newsImageUrl,ArrayList<String> newsSource,ArrayList<String> newsSummary
            ,ArrayList<String> newsDetailUrl){
//        this.data=data;
//        this.data1=data1;
        this.newsDatetime=newsDatetime;
        this.newsHeadline=newsHeadline;
        this.newsImageUrl=newsImageUrl;
        this.newsSource=newsSource;
        this.newsSummary=newsSummary;
        this.newsDetailUrl=newsDetailUrl;
    }



    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        NewsViewHolder newsViewHolder;
        switch (viewType){
            case 0:
                v=LayoutInflater.from(parent.getContext()).inflate(R.layout.news_heading,parent,false);
                break;
            default:
                v=LayoutInflater.from(parent.getContext()).inflate(R.layout.news_other,parent,false);
                break;
        }
        newsViewHolder=new NewsViewHolder(v,viewType);

//        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
//        View view=inflater.inflate(R.layout.news_sample,parent,false);
        return newsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.tvNewsSource.setText(newsSource.get(position));

        holder.tvNewsDateTime.setText(setTime(newsDatetime.get(position)));

        Log.d("datetest","news is"+newsSource.get(position));
       // holder.tvNewsDateTime.setText(newsDatetime.get(position));
        holder.tvNewsHeadline.setText(newsHeadline.get(position));
        Picasso.with(holder.ivNewsImage.getContext()).load(newsImageUrl.get(position)).into(holder.ivNewsImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newsAdapterPositionInView= holder.getAdapterPosition();
                Log.d("pos", String.valueOf(newsAdapterPositionInView));
                AlertDialog.Builder builder=new AlertDialog.Builder(view.getRootView().getContext());
                View dialogView=LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.news_dialog,null);
                TextView tvDialogNewsSource,tvDialogNewsDateTime,tvDialogNewsHeadline,tvDialogNewsSummary;
                tvDialogNewsSource=dialogView.findViewById(R.id.textViewDialogNewsSource);
                tvDialogNewsDateTime=dialogView.findViewById(R.id.textViewDialogNewsDateTime);
                tvDialogNewsHeadline=dialogView.findViewById(R.id.textViewDialogNewsHeadline);
                tvDialogNewsSummary=dialogView.findViewById(R.id.textViewDialogNewsSummary);
                tvDialogNewsSource.setText(newsSource.get(newsAdapterPositionInView));
                //tvDialogNewsDateTime.setText(setTime(newsDatetime.get(newsAdapterPositionInView)));
                DateFormat simple=new SimpleDateFormat("MMMM dd, yyyy");

                Date result=new Date(Long.parseLong(newsDatetime.get(newsAdapterPositionInView))*1000);
                Log.d("datetest","news is"+simple.format(result));

                tvDialogNewsDateTime.setText(simple.format(result));
                tvDialogNewsHeadline.setText(newsHeadline.get(newsAdapterPositionInView));
                tvDialogNewsSummary.setText(newsSummary.get(newsAdapterPositionInView));
                builder.setView(dialogView);
                builder.setCancelable(true);
                AlertDialog tempAlert=builder.show();
                tempAlert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                ivChromeIcon=tempAlert.findViewById(R.id.imageViewChromeIcon);
                ivTwitterIcon=tempAlert.findViewById(R.id.imageViewTwitterIcon);
                ivFacebookIcon=tempAlert.findViewById(R.id.imageViewFacebookIcon);
                String chromeUrl=newsDetailUrl.get(newsAdapterPositionInView);
                String twitterUrl="https://twitter.com/intent/tweet?text="+newsHeadline.get(newsAdapterPositionInView) +"%20"+newsDetailUrl.get(newsAdapterPositionInView);
                String facebookUrl="https://www.facebook.com/sharer/sharer.php?u="+newsDetailUrl.get(newsAdapterPositionInView);
                ivChromeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("chrome","clicked on chrome icon");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chromeUrl));
                        v.getContext().startActivity(browserIntent);
                    }
                });
                ivTwitterIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                        v.getContext().startActivity(browserIntent);
                    }
                });
                ivFacebookIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                        v.getContext().startActivity(browserIntent);
                    }
                });

            }
        });

    }

    public String setTime(String newsTime ){
        long timestamp=System.currentTimeMillis()/1000;
        long difference=timestamp-Long.parseLong(newsTime);
        int hours=(int) difference/3600;
        int days=(int) difference/(3600*24);
        int minutes=(int) difference/60;
        String timeToSet;

        if(days<1){
            if(hours>1){
                timeToSet = String.format("%d hours ago", hours);
            }
            else if(hours==1){
                timeToSet = "1 hour ago";
            }
            else{
                if(minutes>1){
                    timeToSet = String.format("%d mins ago", minutes);
                }
                else{
                    timeToSet = "1 min ago";
                }
            }
        }
        else if(days==1){
            timeToSet = "1 day ago";
        }
        else{
            timeToSet = String.format("%d days ago", days);
        }
        return timeToSet;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return newsSource.size();

    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,textViewNews1;
        ImageView ivNewsImage;
        TextView tvNewsSource, tvNewsDateTime, tvNewsHeadline;

        public NewsViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            ivNewsImage=(ImageView) itemView.findViewById(R.id.imageViewNewsImage);
            tvNewsSource=(TextView) itemView.findViewById(R.id.textViewNewsSource);
            tvNewsDateTime=(TextView) itemView.findViewById(R.id.textViewNewsDateTime);
            tvNewsHeadline=(TextView) itemView.findViewById(R.id.textViewNewsHeadline);

        }
    }
}
