package summarizer.model;

/**
 * Created by atone on 15-1-6.
 */
public class Phrase {
    public int aspectID;
    public int polarity;
    public String aspect;
    public String opinion;
    public String opinion_adj;
    public String opinion_adv;
    public String content;
    public String detailedContent;

    public Phrase(int aspectID, String aspect, String opinion, String opinion_adv, String opinion_adj, int polarity) {
        this.aspectID = aspectID;
        this.polarity = polarity;
        this.aspect = aspect;
        this.opinion = opinion;
        this.opinion_adv = opinion_adv;
        this.opinion_adj = opinion_adj;
        this.detailedContent = aspect + opinion;

        if (!opinion_adj.isEmpty()) {
            this.content = aspect + opinion_adj;
        }
        else if (!opinion_adv.isEmpty()) {
            this.content = aspect + opinion.replaceFirst(opinion_adv, "");
            this.opinion_adj = opinion.replaceFirst(opinion_adv, "");
        }
        else {
            this.content = aspect + opinion;
            this.opinion_adj = opinion;
        }

    }

    @Override
    public String toString() {
        return "(" + this.content + ", " + this.polarity + ")";
        //return this.content;
    }

    @Override
    public int hashCode() { //使用content字符串的hashCode作为类的hashCode
        return this.content.hashCode();
    }

    @Override
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
