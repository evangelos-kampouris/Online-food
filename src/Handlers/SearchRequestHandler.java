package Handlers;

import DTO.FilterMapDTO;
import DTO.Request;
import DTO.SearchRequestDTO;
import Filtering.Filtering;
import Entity.Entity;
import Entity.Master;
import Node.WorkerNode;
import other.PendingRequests;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SearchRequestHandler implements Handling{

    @Override
    public void handle(Entity entity, Socket connection, Request request, ObjectOutputStream out, ObjectInputStream in) {
        Master master = (Master) entity;
        SearchRequestDTO dto = (SearchRequestDTO) request;
        List<Filtering> filters = dto.getSelectedFilters();

        master.pendingRequests.put(dto.getRequestId(), new PendingRequests(out, in));
        List<WorkerNode> workers = master.getWorkersList();
        List<Thread> threads = new ArrayList<>();

        for (WorkerNode worker : workers) {
            Thread thread = new Thread(() -> {
                try (Socket socket = new Socket(worker.getIp(), worker.getPort());
                     ObjectOutputStream outTOWorker = new ObjectOutputStream(socket.getOutputStream())) {

                    FilterMapDTO filterMapDTO = new FilterMapDTO(filters);
                    outTOWorker.writeObject(filterMapDTO);
                    outTOWorker.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupt flag
            }
        }
    }
}
