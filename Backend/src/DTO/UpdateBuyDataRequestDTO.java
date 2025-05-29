package DTO;

import java.io.Serial;

/**
 * A request DTO sent from the MasterNode to the WorkerNode after a purchase is confirmed.
 * Contains the original BuyRequestDTO to update stock and revenue locally.
 */
public class UpdateBuyDataRequestDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    //THIS IS A LAZY TEMPORARY APPROACH
    public BuyRequestDTO data;
    /**
     * Constructs an update request to forward the completed purchase data to the Worker.
     *
     * @param buyRequestDTO the original buy request to apply on the Worker side
     */
    public UpdateBuyDataRequestDTO(BuyRequestDTO buyRequestDTO) {
        data = buyRequestDTO;
    }
}
