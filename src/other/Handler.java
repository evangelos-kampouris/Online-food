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
            Object receivedObject = in.readObject();                        //ένα από τις επιλογές του request.txt

            if(receivedObject instanceof BuyRequestDTO DTO){
                BuyRequestHandler buyRequestHandler = new BuyRequestHandler();
                buyRequestHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof AddStoreRequestDTO DTO){
                AddStoreRequestHandler addStoreRequestHandler = new AddStoreRequestHandler();
                addStoreRequestHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof ChangeStockDTO DTO){
                ChangeStockHandler changeStockHandler = new ChangeStockHandler();
                changeStockHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof FilterMapDTO DTO){
                FilterMapHandler filterMapHandler = new FilterMapHandler();
                filterMapHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof MapResultDTO DTO){
                MapResultHandler mapResultHandler = new MapResultHandler();
                mapResultHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof ReducerResultDTO DTO){
                ReducerResultHandler reducerResultHandler = new ReducerResultHandler();
                reducerResultHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof SearchRequestDTO DTO){
                SearchRequestHandler searchRequestHandler = new SearchRequestHandler();
                searchRequestHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof StatsRequestDTO DTO){
                StatsRequestHandler statsRequestHandler = new StatsRequestHandler();
                statsRequestHandler.handle(entity, connection, DTO, out, in);
            }

            else if(receivedObject instanceof UpdateBuyDataRequestDTO DTO){
                UpdateBuyDataHandler updateBuyDataHandler = new UpdateBuyDataHandler();
                updateBuyDataHandler.handle(entity, connection, DTO, out, in);
            }
            //EACH CONNECTION IS TO BE CLOSED IN THE HANDLERS
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void run() {                                         //θα τρέξει το νήμα εδώ
        handle(this.entity, this.connection);
    }
}
