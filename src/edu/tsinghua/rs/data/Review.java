package edu.tsinghua.rs.data;

/**
 * Created by FlyFish on 2014/12/29.
 */
public class Review {
    public String content; //评论内容
    public String source; //评论来源
    public String product; //产品名称
    public Review(String content, String source, String product) {
        this.content = content;
        this.source = source;
        this.product = product;
    }


    public int hashCode() {
        return this.content.hashCode();
    } //使用content字符串的hashCode作为类的hashCode
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
        Review r = (Review) o;
        return this.content.equals(r.content);
    }
}
