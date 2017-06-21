package ua.in.quireg.anothermovieapp.common;

public class MovieTrailer {
    public String _id;
    public String iso_639_1;
    public String iso_3166_1;
    public String key;
    public String name;
    public String site;
    public String size;
    public String type;

    public MovieTrailer(String _id, String iso_639_1, String iso_3166_1, String key, String name, String site, String size, String type) {
        this._id = _id;
        this.iso_639_1 = iso_639_1;
        this.iso_3166_1 = iso_3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }
}
