import java.util.*;

public class TreeFilter {

   public TreeFilter() {
   }

   public Phylogeny removeBad(Phylogeny tree){
      Phylogeny filtered = new Phylogeny(tree);
      for (Species s : tree.getAllSpecies().values()) {
         for (Pyroprint p : s.getPyroprints().values()) {
            if (p.isErroneous()) {
               filtered.removePyroprint(p);
            }
         }
      }
      return filtered;
   }

   public Phylogeny removeSpeciesBelow(Phylogeny tree, int val){
      Phylogeny filtered = new Phylogeny(tree);
      for (Species s : tree.getAllSpecies().values()) {
         if (s.getIsolateCount() < val) {
            for (Host h : s.getHosts().values()) {
               for (Isolate i : h.getIsolates().values()) {
                  for (Pyroprint p : i.getPyroprints().values() ) {
                     if (null == filtered.removePyroprint(p))
                        System.err.printf("Failed to remove pyroprint %s.\n", p);
                  }
                  if (null == filtered.removeIsolate(i))
                     System.err.printf("Failed to remove isolate %s.\n", i);
               }
               if (null == filtered.removeHost(h))
                  System.err.printf("Failed to remove host %s.\n", h);
            }
            if (null == filtered.removeSpecies(s))
               System.err.printf("Failed to remove species %s.\n", s);
         }
      }
      return filtered;
   }

   public Phylogeny removeEnvironmental(Phylogeny tree){
      Phylogeny filtered = new Phylogeny(tree);
      for (Species s : tree.getAllSpecies().values()) {
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               if (i.isEnvironmental()) {
                  for (Pyroprint p : i.getPyroprints().values() ) {
                     if (null == filtered.removePyroprint(p))
                        System.err.printf("Failed to remove pyroprint %s.\n", p);
                  }
                  if (null == filtered.removeIsolate(i))
                     System.err.printf("Failed to remove isolate %s.\n", i);
               }
            }
         }
      }
      return filtered;
   }

   public Phylogeny removeEmptyIsolates(Phylogeny tree){
      Phylogeny filtered = new Phylogeny(tree);
      for (Species s : tree.getAllSpecies().values()) {
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               if (i.getPyroprintCount("16-23") == 0 && i.getPyroprintCount("23-5") == 0) {
                  if (null == filtered.removeIsolate(i))
                     System.err.printf("Failed to remove isolate %s.\n", i);
               }
            }
         }
      }
      return filtered;
   }

   public Phylogeny removeIncompleteIsolates(Phylogeny tree){
      Phylogeny filtered = new Phylogeny(tree);
      for (Species s : tree.getAllSpecies().values()) {
         for (Host h : s.getHosts().values()) {
            for (Isolate i : h.getIsolates().values()) {
               if (i.getPyroprintCount("16-23") == 0 || i.getPyroprintCount("23-5") == 0) {
                  if (null == filtered.removeIsolate(i))
                     System.err.printf("Failed to remove isolate %s.\n", i);
               }
            }
         }
      }
      return filtered;
   }
}
