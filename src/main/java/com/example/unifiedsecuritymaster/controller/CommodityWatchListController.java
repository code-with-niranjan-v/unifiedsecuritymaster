package com.example.unifiedsecuritymaster.controller;

import com.example.unifiedsecuritymaster.dto.request.AddCommodityDTO;
import com.example.unifiedsecuritymaster.dto.request.AddMutualFundDTO;
import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import com.example.unifiedsecuritymaster.response.Response;
import com.example.unifiedsecuritymaster.service.CommodityWatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commodity-watchlist")
public class CommodityWatchListController {

    private final CommodityWatchListService commodityWatchListService;

    @PostMapping("/add-commodity")
    public Response<String> addCommodity(@RequestBody AddCommodityDTO addCommodityDTO){
        return new Response<>(HttpStatus.OK.value(), true, null, commodityWatchListService.addCommodity(addCommodityDTO) , LocalDateTime.now() );
    }

    @DeleteMapping("/delete-commodity/{id}")
    public Response<String> removeCommodity(@PathVariable Integer id){
        return new Response<>(HttpStatus.OK.value(), true, null,commodityWatchListService.deleteCommodity(id), LocalDateTime.now());
    }

}
