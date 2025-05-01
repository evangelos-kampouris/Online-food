package DTO;

import java.io.Serial;

public class UpdateBuyDataRequestDTO extends Request{
    @Serial
    private static final long serialVersionUID = 1L;
    //THIS IS A LAZY TEMPORARY APPROACH
    public BuyRequestDTO data;
    public UpdateBuyDataRequestDTO(BuyRequestDTO buyRequestDTO) {
        data = buyRequestDTO;
    }
}
