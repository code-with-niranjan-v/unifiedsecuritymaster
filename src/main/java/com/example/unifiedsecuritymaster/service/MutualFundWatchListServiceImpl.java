package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddMutualFundDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.exception.MutualFundWatchListNotFoundException;
import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.MutualFundWatchList;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.MutualFundWatchListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MutualFundWatchListServiceImpl implements MutualFundWatchListService{

    private final MutualFundWatchListRepository mutualFundWatchListRepository;
    private final AssetRepository assetRepository;

    @Override
    public String addMutualFund(AddMutualFundDTO addMutualFundDTO) {
        if(assetRepository.existsById(addMutualFundDTO.getAssetId())){
            Asset asset =assetRepository.findById(addMutualFundDTO.getAssetId()).get();
            MutualFundWatchList mutualFundWatchList = new MutualFundWatchList(null,addMutualFundDTO.getIsin(),addMutualFundDTO.getSchemeName(),asset,true,null,addMutualFundDTO.getEquityCategory());
            mutualFundWatchListRepository.save(mutualFundWatchList);
            return "Mutual fund added to the watchlist.";
        }else {
            throw new AssetNotFoundException();
        }


    }

    @Override
    public String removeMutualFund(Integer id) {
        if(mutualFundWatchListRepository.existsById(id)){
            MutualFundWatchList mutualFundWatchList =mutualFundWatchListRepository.findById(id).get();
            mutualFundWatchList.setStatus(false);
            mutualFundWatchListRepository.save(mutualFundWatchList);
            return "MutualFund remove from the watchlist.";
        }else{
            throw new MutualFundWatchListNotFoundException();
        }
    }

    public List<MutualFundWatchList> getAllMutualFunds(){
        return mutualFundWatchListRepository.findAll();
    }
}
