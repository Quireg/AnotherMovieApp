
package ua.in.quireg.anothermovieapp;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import ua.in.quireg.anothermovieapp.core.MovieItem;

import static org.junit.Assert.*;

public class MovieItemTest {
    @Test
    public void testFromJson() throws Exception {
        Scanner scanner = new Scanner( new File(".\\app\\src\\test\\java\\ua\\in\\quireg\\anothermovieapp\\json_single_movie"), "UTF-8" );
        String json = scanner.nextLine();
        scanner.close();
        MovieItem created = MovieItem.fromJSON(new JSONObject(json));
        MovieItem sample = Helpers.getTestMovieItem();
        assert (created.equals(sample));
    }
}