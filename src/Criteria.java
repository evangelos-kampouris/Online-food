import java.util.List;
import java.util.Set;

public interface Criteria {
    public Set<Shop> meetCriteria(Set<Shop> shops, Filtering filter);
}