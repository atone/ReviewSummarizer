package edu.tsinghua.rs.data;

import java.util.HashSet;

/**
 * Created by atone on 15/1/13.
 */
public class PRCollection {
    public HashSet<Review> reviews;
    public HashSet<String> phrases;
    public PRCollection() {
        reviews = new HashSet<Review>();
        phrases = new HashSet<String>();
    }
}
