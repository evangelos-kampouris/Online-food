package Handlers;

import DTO.FilterMapDTO;
import DTO.MapResultDTO;
import DTO.Request;
import Entity.Entity;
import Entity.Worker;
import Filtering.*;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

//master to worker
/**
 * Handles filtering requests sent from the MasterNode to a WorkerNode.
 * Applies multiple filters to the local shop data and forwards the filtered results to the ReducerNode.
 */
public class FilterMapHandler implements Handling{

    /**
     * Processes a FilterMapDTO request on a WorkerNode.
     * Groups filters by type and applies OR filtering within groups and AND filtering across groups.
     * Sends the final filtered shop results to the corresponding ReducerNode.
     *
     * @param entity the WorkerNode processing the request
     * @param connection the socket through which the request was received
     * @param request the filter map request containing selected filters
     * @param out the output stream (closed immediately)
     * @param in the input stream (closed immediately)
     */
    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {

        //All the information has been sent from the master, and it's connection is no longer needed.
        closeConnection(connection, out, in);

        //Initialize values
        Worker worker = (Worker) entity;
        FilterMapDTO dto = (FilterMapDTO) request;
        List<Filtering> filters = dto.getSelectedFilters();

        Map<String, Shop> results = null;   //The holding the final results
        MapResultDTO mapResultDTO = null;
        //Check for empty list
        if(filters.isEmpty()){
            System.out.println("No filters selected. Forwarding default ");
            results = new HashMap<>(worker.getShops());
        }
        else{
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
                Set<Shop> groupShops; //Shops that match the filtergroup will be saved here.
                Iterator<Filtering> iterator = group.iterator();

                if (group.size() == 1) {
                    Criteria criteria = buildCriteria(iterator.next());
                    groupCriteria = criteria;
                    groupShops = criteria.meetCriteria(allShops); //Filter
                }
                else {
                    // Combine multiple filters of same type with OR
                    List<Set<Shop>> criteriaResultList = new ArrayList<>(); //list that later is going to be joined

                    groupCriteria = buildCriteria(iterator.next()); //Getting the first Criteria
                    criteriaResultList.add(groupCriteria.meetCriteria(new HashSet<>(allShops)));

                    while (iterator.hasNext()) { //Composing the total OR criteria.
                        groupCriteria = buildCriteria(iterator.next()); //Getting the next Criteria
                        criteriaResultList.add(groupCriteria.meetCriteria(new HashSet<>(allShops)));
                    }
                    //Joining
                    groupShops = new HashSet<>();
                    for (Set<Shop> groupShop : criteriaResultList) {
                        groupShops.addAll(groupShop);
                    }
                }
                groupResults.put(groupCriteria,groupShops);
            }

            //AND FILTERING
            Set<Shop> filteredShops = new HashSet<>(allShops);
            for (Map.Entry<Criteria, Set<Shop>> entry : groupResults.entrySet()) {//change from entry to value for
                Criteria criteria = entry.getKey();
                Set<Shop> groupShops = entry.getValue();
                filteredShops.retainAll(groupShops);
            }
            results = new HashMap<>();
            //Transform it to the Mapping output.
            for (Shop shop : filteredShops) {
                results.put(shop.getName(), shop);
            }
        }

        //Initiating connection and forwarding the request to the reducer.
        try{
            Socket ReducerConnectionSocket = new Socket(worker.getREDUCER().getIp(), worker.getREDUCER().getPort());
            ObjectOutputStream handler_out = new ObjectOutputStream(ReducerConnectionSocket.getOutputStream());

            mapResultDTO = new MapResultDTO(results, dto.getRequestId());
            handler_out.writeObject(mapResultDTO);
            handler_out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Converts a filter object to the corresponding criteria implementation.
     *
     * @param filter the filtering condition to convert
     * @return the matching Criteria instance
     * @throws IllegalArgumentException if the filter type is not supported
     */
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
