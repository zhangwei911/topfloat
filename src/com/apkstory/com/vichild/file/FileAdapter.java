package com.apkstory.com.vichild.file;

import android.content.Context;
import android.graphics.Color;
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
public class FileAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Context context;
    String[] fileName;
    String[] filePath;
    String[] fileUpdateTime;
    String[] isDir;
    int count=0;

    public FileAdapter(Context context,int count) {
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
            convertView = mInflater.inflate(R.layout.filesitems, null);
            vh = new ViewHodler();
            vh.fn_textView = (TextView) convertView.findViewById(R.id.fn_textView);
            vh.fp_textView = (TextView) convertView.findViewById(R.id.fp_textView);
            vh.id_textView = (TextView) convertView.findViewById(R.id.id_textView);
            vh.ut_textView = (TextView) convertView.findViewById(R.id.ut_textView);

            convertView.setTag(vh);
        } else {
            vh = (ViewHodler) convertView.getTag();
        }
Log.i("ViChildGetViewCount", position + "");
        if (fileName[position] != null) {
            vh.fn_textView.setText(fileName[position]);
            vh.fp_textView.setText(filePath[position]);
            vh.id_textView.setText(isDir[position]);
            vh.ut_textView.setText(fileUpdateTime[position]);
            if(isDir[position].equals("Dir")){
                vh.id_textView.setBackgroundColor(Color.YELLOW);
            }else{
                vh.id_textView.setBackgroundColor(Color.LTGRAY);
            }
        }
        return convertView;
    }

    class ViewHodler {
        TextView fn_textView, fp_textView, id_textView, ut_textView;
    }
}
