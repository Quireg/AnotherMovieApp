package ua.in.quireg.anothermovieapp.interfaces;

import java.util.List;

import ua.in.quireg.anothermovieapp.core.MovieItem;

public interface IMovieListListener {

    public void setMoviesList(List<MovieItem> list, String requestedList);

    public List<MovieItem> getMoviesList(String requestedList);

}
