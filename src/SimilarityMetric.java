import java.util.*;

public interface SimilarityMetric<I,R> {
   public R similarity(I a, I b);
}
