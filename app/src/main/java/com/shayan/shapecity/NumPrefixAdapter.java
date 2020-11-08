package com.shayan.shapecity;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class NumPrefixAdapter extends ArrayAdapter<String>{

    private int tvbgcolor, tvtextcolor;
    private float tvDIPsize;

    public NumPrefixAdapter(Context cxt){
        super(cxt, 0);
    }


    @Override
    public int getCount(){
        return super.getCount();
    }


    @Override
    public void remove(String object) {
        super.remove(object);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String text = getItem(position);
        RelativeLayout layout;
        TextView tv;

        Spinner par = (Spinner)parent;

        if(convertView == null){
            tv = new TextView(getContext());

            layout = new RelativeLayout(getContext());
            layout.addView(tv);
        }else{
            layout = (RelativeLayout)convertView;
            tv = (TextView)layout.getChildAt(0);
        }

        layout.setLayoutParams(new Spinner.LayoutParams(parent.getWidth(), parent.getHeight()));
        layout.setBackgroundColor(tvbgcolor);

        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        tv.setY(par.getHeight() / 2);
        tv.setText(text);
        tv.setGravity(Gravity.RIGHT);
        tv.setTextColor(tvtextcolor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tvDIPsize);

        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        String text = getItem(position);
        TextView tv;

        if(convertView == null){
            tv = new TextView(getContext());
        }else{
            tv = (TextView)convertView;
        }

        tv.setText(text);
        tv.setGravity(Gravity.RIGHT);
        tv.setTextColor(tvtextcolor);
        tv.setBackgroundColor(tvbgcolor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tvDIPsize);

        return tv;
    }

    public void setTextColor(int color){
        tvtextcolor = color;
    }


    public void setBackGroundColor(int color){
        tvbgcolor = color;
    }

    public void setTextDIPSize(float dip){
        tvDIPsize = dip;
    }
}
