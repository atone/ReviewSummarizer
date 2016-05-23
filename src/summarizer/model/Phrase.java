package summarizer.model;

import java.util.Set;

/**
 * Created by atone on 15-1-6.
 */
public class Phrase {
    public int aspectID;
    public int polarity;
    public int clusterID;
    public String aspect;
    public String opinion;
    public String content;

    public Set<Review> reviews;

    public Phrase(int aspectID, int clusterID, int polarity, String content, Set<Review> reviews) {
        this.aspectID = aspectID;
        this.polarity = polarity;
        this.clusterID = clusterID;
        this.content = content;
        this.aspect = Aspect.getAspect(content, aspectID);
        this.opinion = content.replace(this.aspect, "");
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        //return "(" + this.content + ", " + this.polarity + ")";
        return this.content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Phrase phrase = (Phrase) o;

        return content.equals(phrase.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}
