package com.example.unifiedsecuritymaster.service;


import com.example.unifiedsecuritymaster.dto.request.AddSecurityMasterDTO;
import com.example.unifiedsecuritymaster.model.SecurityMaster;

import java.util.List;

public interface SecurityMasterService {

    public String addSecurity(AddSecurityMasterDTO addSecurityMasterDTO);

    public String updateSecurity(SecurityMaster securityMaster);

    public String deleteSecurity(Long id);

    public List<SecurityMaster> getAllSecurity();

}
