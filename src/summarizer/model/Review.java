package summarizer.model;

/**
 * Created by FlyFish on 2014/12/29.
 * Modified in 2016/1/16
 */
public class Review {
    public String id; // unique

    public Review(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        return id.equals(review.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    public int getIndex() {
        String indexStr = id.split("_")[1];
        return Integer.parseInt(indexStr);
    }
}
