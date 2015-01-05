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
    } //使用字符串的hashCode作为类的hashCode
    public boolean equals(Object o) { //如果两个类的字符串内容相等，则认为这两个类相等
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
