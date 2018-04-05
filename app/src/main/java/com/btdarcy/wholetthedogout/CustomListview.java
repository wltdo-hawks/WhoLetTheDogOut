package com.btdarcy.wholetthedogout;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by btdar on 3/31/2018.
 */

public class CustomListview extends ArrayAdapter<String> {

    private String[] status;
    private String[] dog;
    private Integer[] imgid;
    private Activity context;

    public CustomListview(Activity context ,String[] status, String[] dog, Integer[] imgid) {
        super(context, R.layout.door_log_test1, status);

            this.context = context;
            this.status = status;
            this.dog = dog;
            this.imgid = imgid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if(r==null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.door_log_test1,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) r.getTag();
        }
        viewHolder.ivw.setImageResource(imgid[position]);
        viewHolder.tvw1.setText(status[position]);
        viewHolder.tvw2.setText(dog[position]);
        return r;

    }
    class ViewHolder
    {
        TextView tvw1;
        TextView tvw2;
        ImageView ivw;
        ViewHolder(View v)
        {
            tvw1 = (TextView) v.findViewById(R.id.status);
            tvw2 = v.findViewById(R.id.dogID);
            ivw = v.findViewById(R.id.testpic);
        }
    }




}
