package edu.tsinghua.rs.data;

import java.util.HashSet;

/**
 * Created by atone on 14/12/29.
 */
public class ShortSentence {
    public String content;
    public int aspect;
    public int opinion;

    public ShortSentence(String content, int aspect, int opinion) {
        this.content = content;
        this.aspect = aspect;
        this.opinion = opinion;
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
        ShortSentence s = (ShortSentence) o;
        return this.content.equals(s.content);
    }
}
