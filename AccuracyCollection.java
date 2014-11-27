import java.util.*;

public class AccuracyCollection extends Accuracy{
   public AccuracyCollection(List<Accuracy> individualAccuracies){
      super();
      for (Accuracy a : individualAccuracies) {
         this.addSuccess(a.getSuccesses());
         this.addFailure(a.getFailures());
         this.addNonDecision(a.getNonDecisions());
      }
   }
}
