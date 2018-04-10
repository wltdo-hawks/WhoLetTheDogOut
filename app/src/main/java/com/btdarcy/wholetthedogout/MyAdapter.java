package com.btdarcy.wholetthedogout;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Logs> listItems;
    private Context context;

    public MyAdapter(List<Logs> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.door_log_test1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logs listItem = listItems.get(position);
        holder.textStatus.setText(listItem.getStatus());
        holder.textTime.setText(listItem.getTime());
        holder.textDog.setText(listItem.getDog());
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(listItem.getPic())
                .into(holder.mImage);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textStatus;
        public TextView textTime;
        public TextView textDog;
        public ImageView mImage;
        public ViewHolder(View itemView) {
            super(itemView);
            textStatus = itemView.findViewById(R.id.status);
            textTime = itemView.findViewById(R.id.time);
            textDog = itemView.findViewById(R.id.dogID);
            mImage = itemView.findViewById(R.id.testpic);
        }
    }
}