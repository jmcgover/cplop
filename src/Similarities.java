import java.util.*;

public interface Similarities<I,R> {
   public Collection<SimilarityMetric<I,R>> getSimilarities();
}
