
package com.anothermovieapp;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import com.anothermovieapp.repository.EntityDBMovie;

import static org.junit.Assert.*;

public class MovieItemUntiTest {
    @Test
    public void testFromJson() throws Exception {
        String curDir = System.getProperty("user.dir");
        Scanner scanner = new Scanner( new File(".\\src\\test\\java\\ua\\in\\quireg\\anothermovieapp\\json_single_movie"), "UTF-8" );
        String json = scanner.nextLine();
        scanner.close();
        EntityDBMovie created = EntityDBMovie.fromJSON(new JSONObject(json));
        EntityDBMovie sample = Helpers.getTestMovieItem();
        assertTrue (created.equals(sample));
    }

}