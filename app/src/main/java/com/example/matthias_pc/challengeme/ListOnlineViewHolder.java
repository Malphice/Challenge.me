package com.example.matthias_pc.challengeme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder {

    public TextView txtEmail;
    public ListOnlineViewHolder(View itemView){
        super(itemView);
        txtEmail= (TextView)itemView.findViewById(R.id.txt_email);
    }
}
