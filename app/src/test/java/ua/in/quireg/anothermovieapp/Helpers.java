package ua.in.quireg.anothermovieapp;


import ua.in.quireg.anothermovieapp.core.MovieItem;

public class Helpers {

    public static final MovieItem getTestMovieItem(){
        return new MovieItem(
                7777, //id
                true, //adult
                "backdrop",
                1000000, //budget
                "www.homepage.com",
                123,  //imdb_id
                "originalLanguage",
                "originalTitle",
                "title",
                "overviewWWWWWwwwwwwwwwwwwwwwwwwwwWWWWWWWWWww",
                0.123, //popularity
                "poster_path",
                "release_date",
                123, //revenue
                1, //runtime
                "status",
                "tagline", //tagline
                true,
                0.7,
                19999
        );
    }

}
