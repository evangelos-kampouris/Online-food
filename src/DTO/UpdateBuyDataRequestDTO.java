package DTO;

public class UpdateBuyDataRequestDTO extends Request{
    //THIS IS A LAZY TEMPORARY APPROACH
    public BuyRequestDTO data;
    public UpdateBuyDataRequestDTO(BuyRequestDTO buyRequestDTO) {
        data = buyRequestDTO;
    }
}
