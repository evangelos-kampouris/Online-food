package Handlers;

import DTO.AddProductDTO;
import DTO.Request;
import Entity.*;
import Node.WorkerNode;
import other.Product;
import other.ProductCategory;
import other.Shop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class AddProductHandler implements Handling {

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        AddProductDTO dto = (AddProductDTO) request;
        String storeName = dto.getStoreName();

        if (entity instanceof Master master) {
            WorkerNode worker = master.workers.getNodeForKey(storeName);

            if (worker == null) {
                System.out.println("No Worker found for store: " + storeName);
                return;
            }

            try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                 ObjectOutputStream handler_out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream handler_in = new ObjectInputStream(socket.getInputStream())) {

                handler_out.writeObject(dto);
                handler_out.flush();

                System.out.println("Forwarded Add product request to worker " + worker.getIp());

                // Receive the updated shop
                Shop updatedShop = (Shop) handler_in.readObject();
                out.writeObject(updatedShop);
                out.flush();
                System.out.println("Updated Shop sent.");

                closeConnection(socket, handler_out, handler_in);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error forwarding to worker: " + e.getMessage());
            }
        } else if (entity instanceof Worker worker) {
            Shop shop = worker.getShop(storeName);

            if (shop == null) {
                System.out.println("Store not found: " + storeName);
                return;
            }

            ProductCategory category = dto.getProductCategory();
            Product product = new Product(dto.getProductName(), category, dto.getPrice());
            shop.getCatalog().addProduct(product.getName(), product, dto.getQuantity(), true);

            System.out.println("Added product to store: " + dto.getProductName());

            try {
                out.writeObject(shop);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending back to master the updated Shop " + e.getMessage());
            }
        } else {
            System.err.println("Request forwarded to wrong entity, Entity is not a Master or Worker");
        }
    }
}
