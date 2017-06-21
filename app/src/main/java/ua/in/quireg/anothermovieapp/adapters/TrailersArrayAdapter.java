package ua.in.quireg.anothermovieapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.MovieTrailer;


public class TrailersArrayAdapter extends ArrayAdapter {
    private final Activity activity;
    private final List<MovieTrailer> list;

    public TrailersArrayAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.activity = (Activity) context;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder view;

        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.movie_trailer_list_item, null);

            // Hold the view objects in an object, that way the don't need to be "re-  finded"
            view = new ViewHolder();
            view.trailer_title = (TextView) rowView.findViewById(R.id.trailer_title);
            view.trailer_quality = (TextView) rowView.findViewById(R.id.trailer_quality);

            rowView.setTag(view);
        } else {
            view = (ViewHolder) rowView.getTag();
        }
        MovieTrailer item = (MovieTrailer) list.get(position);
        view.trailer_title.setText(item.name);
        view.trailer_quality.setText(item.size);

        return rowView;
    }


    protected static class ViewHolder{
        protected TextView trailer_title;
        protected TextView trailer_quality;
    }
}
