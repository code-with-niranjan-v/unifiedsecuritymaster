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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
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
                    addSecurityMasterDTO.getLotSize(),
                    addSecurityMasterDTO.getEquityCategory()
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
    public Page<SecurityMaster> getAllSecurityPage(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("name").ascending());
        return securityMasterRepository.findAll(pageable);
    }

    @Override
    public Page<SecurityMaster> searchSecurityByName(String name, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;
        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by("name").ascending());

        String searchTerm = name == null ? "" : name.trim();
        if (searchTerm.isEmpty()) {
            return securityMasterRepository.findAll(pageable);
        }

        return securityMasterRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
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

//    @Override
//    public SecuritiesInfoDTO getAllSecurityInfo() {
//        SecuritiesInfoDTO securitiesInfoDTO = new SecuritiesInfoDTO(new ArrayList<SecurityInfoDTO>());
//        List<SecurityMaster> securityMasters = getAllSecurity();
//        for(SecurityMaster securityMaster:securityMasters){
//            SecurityPriceDTO securityPriceDTO = getSecurityLatestPrice(securityMaster.getId());
//            SecurityInfoDTO securityInfoDTO = new SecurityInfoDTO();
//            securityInfoDTO.setAsset(securityMaster.getAsset());
//            securityInfoDTO.setId(securityMaster.getId());
//            securityInfoDTO.setSymbol(securityMaster.getSymbol());
//            securityInfoDTO.setIsin(securityMaster.getIsin());
//            securityInfoDTO.setName(securityMaster.getName());
//            securityInfoDTO.setGicsSector(securityMaster.getGicsSector());
//            securityInfoDTO.setEquityCategory(securityMaster.getEquityCategory() == null ? null : securityMaster.getEquityCategory().name());
//            SecurityType type = securityMaster.getSecurityType();
//            switch(type){
//                case EQUITY,ETF ->{
//                    StockData stockData = securityPriceDTO.getStockData();
//                    if(stockData!=null){
//                        securityInfoDTO.setPrice(securityPriceDTO.getStockData().getClosePrice().doubleValue());
//                    }else{
//                        securityInfoDTO.setPrice(552d);
//                    }
//
//                }
//                case MUTUAL_FUND -> {
//                    if(securityPriceDTO.getMutualFundNav()==null){
//                        log.error("Mutual Fund value is null for "+securityPriceDTO.getSecurityMaster());
//                        securityInfoDTO.setPrice(123.0d);
//                    }else{
//                        securityInfoDTO.setPrice(securityPriceDTO.getMutualFundNav().getNav().doubleValue());
//                    }
//
//                }
//                case COMMODITY -> {
//                    if(securityPriceDTO.getCommoditySpotData()!=null){
//                        securityInfoDTO.setPrice(securityPriceDTO.getCommoditySpotData().getSpotPrice().doubleValue());
//                    }else{
//                        securityInfoDTO.setPrice(10000.0d);
//                    }
//                }
//                case BOND -> {
//                    securityInfoDTO.setPrice(securityPriceDTO.getBond().getCleanPrice());
//                }
//            }
//            securitiesInfoDTO.getSecurities().add(securityInfoDTO);
//
//        }
//        return securitiesInfoDTO;
//    }

    public SecuritiesInfoDTO getAllSecurityInfo() {
        return getAllSecurityInfoWithLatestPrice();
    }

    @Override
    public SecuritiesInfoDTO getAllSecurityInfoWithLatestPrice() {
        List<SecurityInfoDTO> securityInfoList = new ArrayList<>();
        List<Object[]> rows = securityMasterRepository.findAllSecurityInfoWithLatestPrice();

        for (Object[] row : rows) {
            SecurityInfoDTO securityInfoDTO = new SecurityInfoDTO();
            securityInfoDTO.setId(((Number) row[0]).longValue());
            securityInfoDTO.setName((String) row[1]);
            securityInfoDTO.setSymbol((String) row[2]);
            securityInfoDTO.setIsin((String) row[3]);
            securityInfoDTO.setGicsSector((String) row[4]);
            securityInfoDTO.setEquityCategory((String) row[5]);

            Asset asset = new Asset();
            Object assetId = row[6];
            if (assetId != null) {
                asset.setId(((Number) assetId).intValue());
            }
            asset.setAssetClass((String) row[7]);
            asset.setDescription((String) row[8]);
            asset.setAssetSubclass((String) row[9]);
            asset.setRisk(row[10] != null ? com.example.unifiedsecuritymaster.model.enums.Risk.valueOf(row[10].toString()) : null);
            asset.setInvestmentHorizon(row[11] != null ? com.example.unifiedsecuritymaster.model.enums.InvestmentHorizon.valueOf(row[11].toString()) : null);
            asset.setSubAssetDescription((String) row[12]);
            asset.setStatus(row[13] != null && Boolean.parseBoolean(row[13].toString()));
            securityInfoDTO.setAsset(asset);

            Object currentPrice = row[14];
            if (currentPrice != null) {
                securityInfoDTO.setPrice(((Number) currentPrice).doubleValue());
            }

            if(securityInfoDTO.getSymbol().equals("GOLD")){
                securityInfoDTO.setPrice(securityInfoDTO.getPrice()/10);
            }

            securityInfoList.add(securityInfoDTO);
        }

        return new SecuritiesInfoDTO(securityInfoList);
    }

    @Override
    public SecuritiesInfoDTO searchSecurityInfo(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        SecuritiesInfoDTO allSecurities = getAllSecurityInfoWithLatestPrice();
        if (normalizedQuery.isEmpty() || allSecurities.getSecurities() == null) {
            return allSecurities;
        }

        List<SecurityInfoDTO> matches = allSecurities.getSecurities().stream()
                .filter(security -> containsIgnoreCase(security.getName(), normalizedQuery)
                        || containsIgnoreCase(security.getSymbol(), normalizedQuery)
                        || containsIgnoreCase(security.getIsin(), normalizedQuery)
                        || containsIgnoreCase(security.getGicsSector(), normalizedQuery)
                        || containsIgnoreCase(security.getEquityCategory(), normalizedQuery))
                .toList();
        return new SecuritiesInfoDTO(matches);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
