package com.abcd.bncserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.abcd.bncserver.Interface.ItemClickListener;
import com.abcd.bncserver.R;

/**
 * Created by Karan Patel on 04-02-2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId=(TextView)itemView.findViewById(R.id.orderName);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.orderStatus);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.orderPhone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.orderAddress);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener=itemClickListener;

    }

    @Override
    public void onClick(View v) {
     itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the Action");

        menu.add(0,0,getAdapterPosition(),"Update");
        menu.add(0,1,getAdapterPosition(),"Delete");

    }
}
