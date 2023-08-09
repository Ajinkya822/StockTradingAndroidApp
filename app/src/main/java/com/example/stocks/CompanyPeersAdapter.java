package com.example.stocks;

import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class CompanyPeersAdapter extends RecyclerView.Adapter<CompanyPeersAdapter.PeersViewHolder> {

    ArrayList<String> companyPeers=new ArrayList<>();

    public CompanyPeersAdapter(ArrayList<String> companyPeers){
        this.companyPeers=companyPeers;
    }

    @NonNull
    @Override
    public PeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        CompanyPeersAdapter.PeersViewHolder peersViewHolder;
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.company_peers,parent,false);
        peersViewHolder=new CompanyPeersAdapter.PeersViewHolder(v,viewType);
        return peersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PeersViewHolder holder, int position) {
        String temp=companyPeers.get(position)+",";
        holder.tvCompanyPeers.setText(temp);
        holder.tvCompanyPeers.setPaintFlags(holder.tvCompanyPeers.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.tvCompanyPeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int peersAdapterPositionInView= holder.getAdapterPosition();
                Intent tempIntent=new Intent(view.getContext(),StockDetailsActivity.class);
                tempIntent.putExtra("TickerValue",companyPeers.get(peersAdapterPositionInView).toUpperCase(Locale.ROOT));
                tempIntent.putExtra("PeerValue","10");
                view.getContext().startActivity(tempIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return companyPeers.size();
    }

    public class PeersViewHolder extends RecyclerView.ViewHolder{
        TextView tvCompanyPeers;
        public PeersViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            tvCompanyPeers=(TextView) itemView.findViewById(R.id.textViewCompanyPeer);
        }
    }
}
