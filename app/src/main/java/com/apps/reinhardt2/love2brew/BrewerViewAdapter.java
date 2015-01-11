package com.apps.reinhardt2.love2brew;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 1/10/2015.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * // This extension of BaseAdapter makes the listView of images
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 *
 * 2015
 */

/**********************************************************************************************
 BrewerViewActivity of Love2Brew App
 Extension of BaseAdapter that implements a custom ListView

 ***********************************************************************************************/
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

	public Brewer getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}


    /*****************************************************************************************
     getView()
     Creates a View Object with a coffee brewer image, name an hot or cold temp
     and returns it for use in the ListView Adapter
     *****************************************************************************************/
    public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;
        Log.d("Adpt","getView");
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
        if (curr.getTemp() ==1) {
            holder.brewTemp.setText("Hot Temperature");
            holder.brewTemp.setTextColor(Color.RED);
        }
        else {
            holder.brewTemp.setText("Cold Temperature");
            holder.brewTemp.setTextColor(Color.BLUE);
        }
		return newView;
    }

    /*****************************************************************************************
     ViewHolder()
     Helper class to transfer information from a Brewer Object into textView and imageView Objects
     *****************************************************************************************/
	static class ViewHolder {
        //Organize views in the listview item
        ImageView brewerImg;
        TextView brewerName;
        TextView brewTemp;
    }

    /*****************************************************************************************
     add()
     Adds a new brewer into the ArrayList controlling the Adapter
     *****************************************************************************************/
	public void add(Brewer listItem) {
		list.add(listItem);
        Log.d("Adpt", "List size: "+list.size());
		notifyDataSetChanged();
	}

    /*****************************************************************************************
     getList()
     Returns the ArrayList
     *****************************************************************************************/
	public ArrayList<Brewer> getList(){
		return list;
	}

    /*****************************************************************************************
     removeAllViews()
     Clears the array list
     *****************************************************************************************/
	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
	}
}
