package ua.in.quireg.anothermovieapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.common.Constants;
import ua.in.quireg.anothermovieapp.core.MovieItem;


public class MovieDetailsActivityFragment extends Fragment {

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        TextView movie_title_textview = (TextView) view.findViewById(R.id.movie_title);
        if (getArguments() != null) {
            MovieItem movie = (MovieItem) getArguments().getSerializable(Constants.MOVIE);
            movie_title_textview.setText(movie.getName());
        }
        return view;
    }

}
