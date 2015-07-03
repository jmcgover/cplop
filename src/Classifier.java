public abstract class Classifier<T,L,S> {
   protected T unknown;
   protected L library;
   public Classifier(T unknown, L library) {
      this.unknown = unknown;
      this.library = library;
   }
   public abstract S classify(Integer k, Double alpha);
}
