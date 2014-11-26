public class Accuracy{
   private int successes;
   private int failures;
   private int nonDecisions;
   public Accuracy(){
      this.successes = 0;
      this.failures = 0;
      this.nonDecisions = 0;
   }
   
   public int getSuccesses(){
      return successes;
   }
   public int getFailures(){
      return failures;
   }
   public int getNonDecisions(){
      return nonDecisions;
   }
   public int getSuccessFailureTotal(){
      return successes + failures;
   }
   public int getTotal(){
      return successes + failures + nonDecisions;
   }

   public int addSuccess(){
      return ++successes;
   }
   public int addFailure(){
      return ++failures;
   }
   public int addNonDecision(){
      return ++nonDecisions;
   }

   public int addSuccess(int successes){
      return this.successes += successes;
   }
   public int addFailure(int failures){
      return this.failures += failures;
   }
   public int addNonDecision(int nonDecisions){
      return this.nonDecisions += nonDecisions;
   }

   public double getSuccessFailureAccuracy(){
      return (double)successes / getSuccessFailureTotal();
   }
   public double getTotalAccuracy(){
      return (double)successes / getTotal();
   }
}
