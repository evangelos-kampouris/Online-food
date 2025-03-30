import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CriteriaPrice implements Criteria {


    @Override
    public Set<Shop> meetCriteria(Set<Shop> shops, Filtering filter) {
        Set<Shop> shopMeetCriteria = new HashSet<>();

        if(filter instanceof FilterPrice selected_filter){
            Price price = (Price) selected_filter.getFilter();
            for (Shop shop : shops) {
                if(shop.getPrice() == price){
                    shopMeetCriteria.add(shop);
                }
            }
        }
        return shopMeetCriteria;
    }
}
