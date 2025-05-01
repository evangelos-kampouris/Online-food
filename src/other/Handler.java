package other;

import DTO.*;
import Entity.Entity;
import Handlers.*;
import Responses.ResponseDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Handler implements Runnable{                   //Για να μπορεί να τρέχει σε δικό του thread
    Socket connection;
    Entity entity;
    ObjectOutputStream out;
    ObjectInputStream in;

    Map<Class<?>, Handling> handlerMap = new HashMap<>();

    public Handler(Entity entity, Socket connection){
        handlerMap.put(AddStoreRequestDTO.class, new AddStoreRequestHandler());
        handlerMap.put(AddProductDTO.class, new AddProductHandler());
        handlerMap.put(BuyRequestDTO.class, new BuyRequestHandler());
        handlerMap.put(ChangeStockDTO.class, new ChangeStockHandler());
        handlerMap.put(FilterMapDTO.class, new FilterMapHandler());
        handlerMap.put(MapResultDTO.class, new MapResultHandler());
        handlerMap.put(RateStoreRequestDTO.class, new RateStoreRequestHandler());
        handlerMap.put(ReducerResultDTO.class, new ReducerResultHandler());
        handlerMap.put(RemoveProductDTO.class, new RemoveProductHandler());
        handlerMap.put(SearchRequestDTO.class, new SearchRequestHandler());
        handlerMap.put(StatsRequestDTO.class, new StatsRequestHandler());
        handlerMap.put(UpdateBuyDataRequestDTO.class, new UpdateBuyDataHandler());

        this.connection = connection;
        this.entity = entity;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            System.err.println("Stream initialization failed: " + e.getMessage());
        }
    }

    /**
     * Holds the handling of different requests. Each handling request is a class.
     *
     */
    public void handle(Entity entity, Socket connection) {
        try {
            Request receivedObject = (Request) in.readObject();                 //Ο πελάτης έχει στείλει π.χ. ένα BuyRequestDTO, AddStoreRequestDTO, κτλ.
                                                                    //ένα από τις επιλογές του request.txt
            Handling handler = handlerMap.get(receivedObject.getClass());

            if (handler != null) {
                handler.handle(entity, connection, receivedObject, out, in);
            } else {
                // Handle unknown DTO
                System.out.println("No handler for: " + receivedObject.getClass());
            }
            //EACH CONNECTION IS TO BE CLOSED IN THE HANDLERS
        } catch (IOException e) {
            System.err.println(entity.getClass()+ " IO excpection Error handling request " + e.getMessage());
            e.printStackTrace();

            ResponseDTO responseDTO = new ResponseDTO(false, "Error");
            try {
                out.writeObject(responseDTO);
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.err.println(entity.getClass()+ " Class excpection Error handling request " + e.getMessage());
        }
    }

    @Override
    public void run() {//θα τρέξει το νήμα εδώ
        handle(this.entity, this.connection);
    }
}
