import java.util.*;

public interface CPLOPData<S, H extends S, I extends H, P extends I>{
   public Map<String, S> getAllSpecies();
   public Map<String, H> getAllHosts();
   public Map<String, I> getAllIsolates();
   public Map<String, P> getAllPyroprints();
}
