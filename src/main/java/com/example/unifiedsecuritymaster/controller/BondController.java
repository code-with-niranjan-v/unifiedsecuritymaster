package com.example.unifiedsecuritymaster.controller;

import com.example.unifiedsecuritymaster.dto.request.AddBondDTO;
import com.example.unifiedsecuritymaster.model.Bond;
import com.example.unifiedsecuritymaster.response.Response;
import com.example.unifiedsecuritymaster.service.BondService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/bonds")
@RequiredArgsConstructor
public class BondController {

    final private BondService bondService;

    @PostMapping("/add-bond")
    public Response<String> addBond(@RequestBody AddBondDTO addBondDTO){
        return new Response<>(HttpStatus.OK.value(), true, null, bondService.addBond(addBondDTO), LocalDateTime.now() );
    }

    @DeleteMapping("/delete-bond/{id}")
    public Response<String> deleteBond(@PathVariable Integer id){
        return new Response<>(HttpStatus.OK.value(), true, null, bondService.deleteBond(id), LocalDateTime.now());
    }
}
