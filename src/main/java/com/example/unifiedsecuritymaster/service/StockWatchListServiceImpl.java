package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddStockDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.exception.StockNotFound;
import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.StockWatchList;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.StockWatchListRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockWatchListServiceImpl implements StockWatchListService {

    private final StockWatchListRepository stockWatchListRepository;
    private final AssetRepository assetRepository;

    @Override
    public String addStock(AddStockDTO addStockDTO) {
        if(!stockWatchListRepository.existsBySymbol(addStockDTO.getSymbol())){
            if(assetRepository.existsById(addStockDTO.getAssetId())){
                Asset asset = assetRepository.findById(addStockDTO.getAssetId()).get();
                StockWatchList stockWatchList = new StockWatchList(null,addStockDTO.getSymbol(),addStockDTO.getName(),addStockDTO.getExchange(),addStockDTO.getIsin(),addStockDTO.getGics(),addStockDTO.getCountry(),addStockDTO.getIndustry(),addStockDTO.getSector(),null,asset);
                stockWatchListRepository.save(stockWatchList);
                return "Stock is added to the watchlist.";
            }else{
                throw new AssetNotFoundException();
            }
        }else{
            throw new StockNotFound();
        }
    }

    @Override
    public String deleteStock(Integer id) {
        if(stockWatchListRepository.existsById(id)){
            stockWatchListRepository.deleteById(id);
            return "Stock is removed from the watchlist.";
        }else{
            throw new StockNotFound();
        }
    }
}
