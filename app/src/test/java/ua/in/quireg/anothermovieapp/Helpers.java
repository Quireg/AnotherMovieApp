package ua.in.quireg.anothermovieapp;


import ua.in.quireg.anothermovieapp.common.MovieItem;

public class Helpers {

    public static final MovieItem getTestMovieItem(){
        return new MovieItem(
                321612, //id
                false, //adult
                "6aUWe0GSl69wMTSWWexsorMIvwU.jpg",
                1000000, //budget
                "www.homepage.com",
                123,  //imdb_id
                "en",
                "Beauty and the Beast",
                "Beauty and the Beast",
                "A live-action adaptation of Disney's version of the classic 'Beauty and the Beast' tale of a cursed prince and a beautiful young woman who helps him break the spell.",
                152.656399, //popularity
                "tWqifoYuwLETmmasnGHO7xBjEtt.jpg",
                "2017-03-16",
                12345, //revenue
                1, //runtime
                "some_status", //status
                "some_tags", //tagline
                false, //video
                6.9, //vote average
                1988 //vote count
        );
    }

}
