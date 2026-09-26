package com.example.unifiedsecuritymaster.service;

import com.example.unifiedsecuritymaster.dto.request.AddSecurityMasterDTO;
import com.example.unifiedsecuritymaster.exception.AssetNotFoundException;
import com.example.unifiedsecuritymaster.exception.SecurityNotFoundException;
import com.example.unifiedsecuritymaster.model.Asset;
import com.example.unifiedsecuritymaster.model.SecurityMaster;
import com.example.unifiedsecuritymaster.repository.AssetRepository;
import com.example.unifiedsecuritymaster.repository.SecurityMasterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SecurityMasterImpl implements SecurityMasterService {


    private final AssetRepository assetRepository;
    private final SecurityMasterRepository securityMasterRepository;

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
}
