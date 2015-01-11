package com.apps.reinhardt2.love2brew;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/*
// SelfieViewAdapter extension of BaseAdapter class
// This extension of BaseAdapter makes the listView of images
// Contains a list of PicRecord objects
*/
public class BrewerViewAdapter extends BaseAdapter {

	private ArrayList<Brewer> list = new ArrayList<Brewer>();
	private static LayoutInflater inflater = null;
	private Context mContext;

	public BrewerViewAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		Brewer curr = list.get(position);
        if (curr==null) Log.d("Adpt", "HELP curr is null for position="+position);
		if (null == convertView) {
            Log.d("Adpt","Creating new List Element");
            holder = new ViewHolder();
			newView = inflater.inflate(R.layout.brew_item, null);
			holder.brewerImg = (ImageView) newView.findViewById(R.id.brewSmallImg);
			holder.brewerName = (TextView) newView.findViewById(R.id.brw_name);
            holder.brewTemp = (TextView) newView.findViewById(R.id.brw_name2);
			newView.setTag(holder);
        }
        else
        {
			holder = (ViewHolder) newView.getTag();
		}

		holder.brewerImg.setImageBitmap(curr.getImage());
		holder.brewerName.setText(curr.getName());
        if (curr.getTemp() ==1)
            holder.brewTemp.setText("Hot Temperature");
        else
            holder.brewTemp.setText("Cold Temperature");
		return newView;
    }
	
	static class ViewHolder {
        //Organize views in the listview item
        ImageView brewerImg;
        TextView brewerName;
        TextView brewTemp;
    }

	public void add(Brewer listItem) {
		list.add(listItem);
		notifyDataSetChanged();
	}
	
	public ArrayList<Brewer> getList(){
		return list;
	}
	
	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
	}
}
