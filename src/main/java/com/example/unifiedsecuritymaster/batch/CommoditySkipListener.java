package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.CommoditySpotData;
import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;

import java.util.List;

@Slf4j
public class CommoditySkipListener
        implements SkipListener<CommodityWatchList, List<CommoditySpotData>> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("SKIP [read] {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(CommodityWatchList item, Throwable t) {
        log.error("SKIP [process] symbol={} reason={}", item.getSymbol(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(List<CommoditySpotData> item, Throwable t) {
        log.error("SKIP [write] {} records reason={}", item == null ? 0 : item.size(), t.getMessage());
    }
}
