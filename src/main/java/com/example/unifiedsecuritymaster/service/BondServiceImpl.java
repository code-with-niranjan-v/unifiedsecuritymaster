package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddBondDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.exception.BondNotFoundException;
import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.Bond;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.BondRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BondServiceImpl implements BondService {

    private final BondRepository bondRepository;

    private final AssetRepository assetRepository;

    @Override
    public String addBond(AddBondDTO addBondDTO) {


        if(assetRepository.existsById(addBondDTO.getAssetId())){
            Asset asset = assetRepository.findById(addBondDTO.getAssetId()).get();
            Bond bond = new Bond(null, addBondDTO.getIsin(), addBondDTO.getName(), addBondDTO.getIssuerName(), addBondDTO.getBondType(), addBondDTO.getExchange(), addBondDTO.getCurrency(), addBondDTO.getFaceValue(), addBondDTO.getCouponRate(), addBondDTO.getCouponFrequency(), addBondDTO.getIssueDate(), addBondDTO.getMaturityDate(), null, null, addBondDTO.getCreditRating(), null, asset,addBondDTO.getCountry());
            bondRepository.save(bond);
            return "Bond added successfully";
        }else{
            throw new AssetNotFoundException();
        }
    }

    @Override
    public String deleteBond(Integer id) {
        if(!bondRepository.existsById(id)){
            throw new BondNotFoundException();
        }
            bondRepository.deleteById(id);

        return "Bond has been deleted";
    }
}
