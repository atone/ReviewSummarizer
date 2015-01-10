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

    public int hashCode() { //使用content字符串的hashCode作为类的hashCode
        return this.content.hashCode();
    }

    public boolean equals(Object o) { //如果两个类的content字符串内容相等，则认为这两个类相等
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (this == o) {
            return true;
        }
        Phrase p = (Phrase) o;
        return this.content.equals(p.content);
    }

}
