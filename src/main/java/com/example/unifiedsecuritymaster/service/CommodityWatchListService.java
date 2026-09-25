package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddCommodityDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.CommoditySpotData;
import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.CommodityWatchListRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommodityWatchListService {

    private final CommodityWatchListRepository commodityWatchListRepository;
    private final AssetRepository assetRepository;


    public String addCommodity(AddCommodityDTO addCommodityDTO){
        if(assetRepository.existsById(addCommodityDTO.getAssetId())){
            Asset asset = assetRepository.findById(addCommodityDTO.getAssetId()).get();
            CommodityWatchList commodityWatchList = new CommodityWatchList(null,addCommodityDTO.getProductId(),addCommodityDTO.getSymbol(),addCommodityDTO.getName(),addCommodityDTO.getQuotation(),addCommodityDTO.getUnit(),addCommodityDTO.getExchange(),asset,true,null);
            commodityWatchListRepository.save(commodityWatchList);
            return "Commodity added to watchlist.";
        }else {
            throw new AssetNotFoundException();
        }
    }

    public String deleteCommodity(Integer id){
        if(commodityWatchListRepository.existsById(id)){
            CommodityWatchList commodityWatchList = commodityWatchListRepository.findById(id).get();
            commodityWatchList.setStatus(false);
            commodityWatchListRepository.save(commodityWatchList);
            return "Commodity removed from watchlist.";
        }else {
            throw new AssetNotFoundException();
        }
    }


}
