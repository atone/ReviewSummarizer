package edu.tsinghua.rs.data;

/**
 * Created by atone on 15-1-6.
 */
public class Phrase {
    public int aspectID;
    public String aspect;
    public String opinion;
    public String content;

    public Phrase(int aspectID, String aspect, String opinion) {
        this.aspectID = aspectID;
        this.aspect = aspect;
        this.opinion = opinion;
        this.content = aspect + opinion;
    }

}
