public class IndividualAccuracy<T> extends Accuracy{
   private T category;
   public IndividualAccuracy(T category){
      super();
      this.category = category;
   }
   public T getCategory(){
      return this.category;
   }
   public String toString(){
      return category.toString();
   }
}
