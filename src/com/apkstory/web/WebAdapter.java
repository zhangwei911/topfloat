package com.apkstory.web;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apkstory.R;

/**
 * Created by wei on 13-7-24.
 */
public class WebAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Context context;
    public String[] url;
    int count=0;

    public WebAdapter(Context context, int count) {
        this.context = context;
        this.count=count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHodler vh;
        if (convertView == null) {
            mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.web_list_items, null);
            vh = new ViewHodler();
            vh.web_url_textView = (TextView) convertView.findViewById(R.id.web_url_textView);

            convertView.setTag(vh);
        } else {
            vh = (ViewHodler) convertView.getTag();
        }
        Log.i("ViChildGetViewCount", position + "");
        if (url[position] != null) {
            vh.web_url_textView.setText((position+1)+". "+url[position]);
        }
        return convertView;
    }

    class ViewHodler {
        TextView web_url_textView;
    }
}
