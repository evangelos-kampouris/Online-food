package other;

import Handlers.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import DTO.*;
//
public class Handler implements Runnable{
    Socket connection;
    Entity entity;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Handler(Entity entity, Socket connection){
        this.connection = connection;
        this.entity = entity;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    /**
     * Holds the handling of different requests. Each handling request is a class.
     *
     */
    public void handle(Entity entity, Socket connection) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(connection.getInputStream());
            Object receivedObject = in.readObject();                        //ένα από τις επιλογές του request.txt

            if(receivedObject instanceof BuyRequestDTO buyRequestDTO){
                BuyRequestHandler buyRequestHandler = new BuyRequestHandler();
                buyRequestHandler.handle(entity, connection, buyRequestDTO);
            }

            else if(receivedObject instanceof AddStoreRequestDTO){
                AddStoreRequestDTO addStoreRequestDTO = (AddStoreRequestDTO) receivedObject;
                AddStoreRequestHandler addStoreRequestHandler = new AddStoreRequestHandler();
                addStoreRequestHandler.handle(entity, connection, addStoreRequestDTO);
            }

            else if(receivedObject instanceof ChangeStockDTO changeStockDTO){
                ChangeStockHandler changeStockHandler = new ChangeStockHandler();
                changeStockHandler.handle(entity, connection, changeStockDTO);
            }

            else if(receivedObject instanceof FilterMapDTO filterMapDTO){
                FilterMapHandler filterMapHandler = new FilterMapHandler();
                filterMapHandler.handle(entity, connection, filterMapDTO);
            }

            else if(receivedObject instanceof MapResultDTO mapResultDTO){
                MapResultHandler mapResultHandler = new MapResultHandler();
                mapResultHandler.handle(entity, connection, mapResultDTO);
            }

            else if(receivedObject instanceof ReducerResultDTO reducerResultDTO){
                ReducerResultHandler reducerResultHandler = new ReducerResultHandler();
                reducerResultHandler.handle(entity, connection, reducerResultDTO);
            }

            else if(receivedObject instanceof SearchRequestDTO searchRequestDTO){
                SearchRequestHandler searchRequestHandler = new SearchRequestHandler();
                searchRequestHandler.handle(entity, connection, searchRequestDTO);
            }

            else if(receivedObject instanceof StatsRequestDTO statsRequestDTO){
                StatsRequestHandler statsRequestHandler = new StatsRequestHandler();
                statsRequestHandler.handle(entity, connection, statsRequestDTO);
            }

            else if(receivedObject instanceof UpdateBuyDataDTO updateBuyDataDTO){
                UpdateBuyDataHandler updateBuyDataHandler = new UpdateBuyDataHandler();
                updateBuyDataHandler.handle(entity, connection, updateBuyDataDTO);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void run() {                                         //θα τρέξει το νήμα εδώ
        handle(this.entity, this.connection);

    }
}
