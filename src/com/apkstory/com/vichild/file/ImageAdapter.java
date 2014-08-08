package com.apkstory.com.vichild.file;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apkstory.R;

/**
 * Created by v on 13-11-10.
 */
public class ImageAdapter extends BaseAdapter {

    public ImageAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler vh;

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.gridviewitems, null);
            vh = new ViewHodler();
            vh.gridfile_imageView = (ImageView) convertView.findViewById(R.id.gridfile_imageView);
            vh.gridfile_textView = (TextView) convertView.findViewById(R.id.gridfile_textView);
            vh.gridfiletype_textView = (TextView) convertView.findViewById(R.id.gridfiletype_textView);

            convertView.setTag(vh);
        } else {
            vh = (ViewHodler) convertView.getTag();
        }
        if (fileName[position] != null) {
            vh.gridfile_textView.setText(fileName[position]);
            if (isDir[position].equals("Dir")) {
                vh.gridfile_imageView.setImageResource(R.drawable.dir);
                vh.gridfiletype_textView.setVisibility(View.GONE);
            } else {
                vh.gridfile_imageView.setImageResource(R.drawable.file);
                if (fileName[position].contains(".")) {
                    vh.gridfiletype_textView.setText(fileType[position]);
                }
            }
        }
        return convertView;
    }

    class ViewHodler {
        TextView gridfile_textView, gridfiletype_textView;
        ImageView gridfile_imageView;
    }

    private Context context;
    private LayoutInflater mInflater;
    String[] fileName;
    String[] filePath;
    String[] fileUpdateTime;
    String[] isDir;
    String[] fileType;
    int count = 0;
}
