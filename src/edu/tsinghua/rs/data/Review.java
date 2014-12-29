package edu.tsinghua.rs.data;

/**
 * Created by FlyFish on 2014/12/29.
 */
public class Review {
    public String content;
    public String source;
    public String url;
    public Review(String content, String source, String url) {
        this.content = content;
        this.source = source;
        this.url = url;
    }


    public int hashCode() {
        return this.content.hashCode();
    }
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (this == o) {
            return true;
        }
        Review r = (Review) o;
        return this.content.equals(r.content);
    }
}
