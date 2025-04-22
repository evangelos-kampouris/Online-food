package Handlers;

import DTO.FilterMapDTO;
import DTO.MapResultDTO;
import DTO.Request;
import DTO.UpdateBuyDataRequestDTO;
import Filtering.Filtering;
import Node.ReducerNode;
import other.Entity;
import other.Shop;
import other.Worker;
import Filtering.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

//master to worker
public class FilterMapHandler implements Handling{


    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Worker worker = (Worker) entity;
        FilterMapDTO dto = (FilterMapDTO) request;
        List<Filtering> filters = dto.getSelectedFilters();

        //Check for empty list
        if(filters.isEmpty()){
            System.out.println("No filters selected.");
            return;
        }

        //Creating a new Set with the workers shops
        Set<Shop> allShops = new HashSet<>();
        for(Map.Entry<String, Shop> entry: worker.getShops().entrySet()){
            allShops.add(entry.getValue());
        }

        //Group Filter by classes
        Map<Class<? extends Filtering>, Set<Filtering>> grouped = new HashMap<>(); //Holds the grouping
        Map<Criteria,Set<Shop>> groupResults = new HashMap<>(); //The resulting filtering from OR operations

        //Adds the filter to the appropriate group. e.x. key = PriceFilter, values = {$,$}   -> meaning that two price filters have been selected.
        for (Filtering filter : filters) {
            grouped.computeIfAbsent(filter.getClass(), k -> new HashSet<>()).add(filter);
        }

        //OR FILTERING
        for (Map.Entry<Class<? extends Filtering>, Set<Filtering>> entry : grouped.entrySet()) {
            Set<Filtering> group = entry.getValue();

            Criteria groupCriteria;
            Set<Shop> groupShops = new HashSet<>(allShops); //Shops that match the filtergroup will be saved here.
            Iterator<Filtering> iterator = group.iterator();

            if (group.size() == 1) {
                Criteria criteria = buildCriteria(iterator.next());
                groupCriteria = criteria;
                criteria.meetCriteria(groupShops); //Filter
            }
            else {
                // Combine multiple filters of same type with OR
                List<Criteria> criteriaList = new ArrayList<>();
                Criteria combined = buildCriteria(iterator.next()); //Getting the first Criteria
                groupCriteria = combined;
                while (iterator.hasNext()) { //Composing the total OR criteria.
                    combined = new CriteriaOR(combined, buildCriteria(iterator.next()));
                    criteriaList.add(combined);
                }
                for (Criteria criteria : criteriaList) {
                    criteria.meetCriteria(groupShops); //Filter
                }

            }
            groupResults.put(groupCriteria,groupShops);
        }

        //AND FILTERING
        Set<Shop> filteredShops = new HashSet<>(allShops);
        for (Map.Entry<Criteria, Set<Shop>> entry : groupResults.entrySet()) {
            Criteria criteria = entry.getKey();
            Set<Shop> groupShops = entry.getValue();
            filteredShops.retainAll(groupShops);
        }

        //Transform it to the Mapping output.
        Map<String, Shop> results = new HashMap<>();
        for (Shop shop : filteredShops) {
            results.put(shop.getName(), shop);
        }

        //Initiating connection and forwarding the request to the reducer.
        try(Socket ReducerConnectionSocket = new Socket(worker.getREDUCER().getIp(), worker.getREDUCER().getPort());) {
            ObjectOutputStream handler_out = new ObjectOutputStream(ReducerConnectionSocket.getOutputStream());

            MapResultDTO mapResultDTO = new MapResultDTO(results, dto.getRequestId());
            handler_out.writeObject(mapResultDTO);
            handler_out.flush();
            closeConnection(ReducerConnectionSocket,handler_out,null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private Criteria buildCriteria(Filtering filter) {
        if (filter instanceof FilterPrice) {
            return new CriteriaPrice((FilterPrice) filter);
        }
        else if (filter instanceof FilterRating) {
            return new CriteriaRating((FilterRating) filter);
        }
        else if (filter instanceof FilterFoodCategory) {
            return new CriteriaFoodCategories((FilterFoodCategory) filter);
        }
        else if (filter instanceof FilterCords) {
            return new CriteriaCords((FilterCords) filter);
        }
        throw new IllegalArgumentException("Unsupported filter type: " + filter.getClass());
    }
}
