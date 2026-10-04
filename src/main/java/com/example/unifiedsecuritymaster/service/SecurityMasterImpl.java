package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddSecurityMasterDTO;
import com.example.unifiedsecuritymaster.dto.response.SecuritiesInfoDTO;
import com.example.unifiedsecuritymaster.dto.response.SecurityInfoDTO;
import com.example.unifiedsecuritymaster.dto.response.SecurityPriceDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.exception.SecurityNotFoundException;
import com.example.unifiedsecuritymaster.model.*;
import com.example.unifiedsecuritymaster.model.enums.SecurityType;
import com.example.unifiedsecuritymaster.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SecurityMasterImpl implements SecurityMasterService {


    private final AssetRepository assetRepository;
    private final SecurityMasterRepository securityMasterRepository;
    private final StockDataRepository stockDataRepository;
    private final MutualFundNavRepository mutualFundNavRepository;
    private final CommoditySpotDataRepository commoditySpotDataRepository;
    private final BondRepository bondRepository;

    @Override
    public String addSecurity(AddSecurityMasterDTO addSecurityMasterDTO) {
        if(assetRepository.existsById(addSecurityMasterDTO.getAssertId())){
            Asset asset = assetRepository.findById(addSecurityMasterDTO.getAssertId()).get();
            SecurityMaster securityMaster = new SecurityMaster(
                    null,
                    addSecurityMasterDTO.getIsin(),
                    addSecurityMasterDTO.getSymbol(),
                    addSecurityMasterDTO.getName(),
                    addSecurityMasterDTO.getSecurityType(),
                    asset,
                    addSecurityMasterDTO.getGicsSector(),
                    addSecurityMasterDTO.getGicsGroup(),
                    addSecurityMasterDTO.getGicsIndustry(),
                    addSecurityMasterDTO.getGicsSubIndustry(),
                    addSecurityMasterDTO.getIssuerName(),
                    addSecurityMasterDTO.getFaceValue(),
                    addSecurityMasterDTO.getExchangeCode(),
                    addSecurityMasterDTO.getCurrencyCode(),
                    addSecurityMasterDTO.getCountryCode(),
                    addSecurityMasterDTO.getStatus(),
                    addSecurityMasterDTO.getListingDate(),
                    addSecurityMasterDTO.getLotSize()
            );

            securityMasterRepository.save(securityMaster);
            return "Security Added.";

        }else{
            throw new AssetNotFoundException();
        }
    }

    @Override
    public String updateSecurity(SecurityMaster securityMaster) {
        securityMasterRepository.save(securityMaster);
        return "Security Updated.";
    }

    @Override
    public String deleteSecurity(Long id) {
        if(securityMasterRepository.existsById(id)){
            SecurityMaster securityMaster = securityMasterRepository.findById(id).get();
            securityMaster.setStatus("INACTIVE");
            securityMasterRepository.save(securityMaster);
            return "Security Removed";
        }else{
            throw new SecurityNotFoundException();
        }
    }

    @Override
    public List<SecurityMaster> getAllSecurity() {
        return securityMasterRepository.findAll();
    }

    @Override
    public SecurityPriceDTO getSecurityLatestPrice(Long securityId) {
        if(securityMasterRepository.existsById(securityId)){
            SecurityMaster securityMaster = securityMasterRepository.findById(securityId).get();
            SecurityType type = securityMaster.getSecurityType();
            SecurityPriceDTO securityPriceDTO = new SecurityPriceDTO();
            securityPriceDTO.setSecurityMaster(securityMaster);
            switch (type) {
                case EQUITY, ETF -> {
                    StockData stockData = stockDataRepository.findLatestPrice(securityMaster.getSymbol());
                    securityPriceDTO.setStockData(stockData);

                }
                case MUTUAL_FUND -> {
                    MutualFundNav mutualFundNav = mutualFundNavRepository.findLatestPrice(securityMaster.getIsin());
                    securityPriceDTO.setMutualFundNav(mutualFundNav);
                }
                case BOND -> {
                    Bond bond = bondRepository.findByIsin(securityMaster.getIsin());
                    securityPriceDTO.setBond(bond);
                }
                case COMMODITY -> {
                    CommoditySpotData commoditySpotData = commoditySpotDataRepository.findLatestPrice(securityMaster.getSymbol());
                    securityPriceDTO.setCommoditySpotData(commoditySpotData);
                }

            }
            return securityPriceDTO;
        }else{
            throw new SecurityNotFoundException();
        }
    }

    @Override
    public SecuritiesInfoDTO getAllSecurityInfo() {
        SecuritiesInfoDTO securitiesInfoDTO = new SecuritiesInfoDTO(new ArrayList<SecurityInfoDTO>());
        List<SecurityMaster> securityMasters = getAllSecurity();
        for(SecurityMaster securityMaster:securityMasters){
            SecurityPriceDTO securityPriceDTO = getSecurityLatestPrice(securityMaster.getId());
            SecurityInfoDTO securityInfoDTO = new SecurityInfoDTO();
            securityInfoDTO.setAsset(securityMaster.getAsset());
            securityInfoDTO.setId(securityMaster.getId());
            securityInfoDTO.setSymbol(securityMaster.getSymbol());
            securityInfoDTO.setIsin(securityMaster.getIsin());
            securityInfoDTO.setName(securityMaster.getName());
            securityInfoDTO.setGicsSector(securityMaster.getGicsSector());
            SecurityType type = securityMaster.getSecurityType();
            switch(type){
                case EQUITY,ETF ->{
                    securityInfoDTO.setPrice(securityPriceDTO.getStockData().getClosePrice().doubleValue());
                }
                case MUTUAL_FUND -> {
                    securityInfoDTO.setPrice(securityPriceDTO.getMutualFundNav().getNav().doubleValue());
                }
                case COMMODITY -> {
                    securityInfoDTO.setPrice(securityPriceDTO.getCommoditySpotData().getSpotPrice().doubleValue());
                }
                case BOND -> {
                    securityInfoDTO.setPrice(securityPriceDTO.getBond().getCleanPrice());
                }
            }
            securitiesInfoDTO.getSecurities().add(securityInfoDTO);

        }
        return securitiesInfoDTO;
    }
}
