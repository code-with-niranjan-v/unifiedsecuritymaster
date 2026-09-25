package com.example.unifiedsecuritymaster.batch;


import com.example.unifiedsecuritymaster.model.MutualFundNav;
import com.example.unifiedsecuritymaster.model.MutualFundWatchList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;

import java.util.List;

@Slf4j
public class MutualFundSkipListener
        implements SkipListener<MutualFundWatchList, List<MutualFundNav>> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("SKIP [read] {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(MutualFundWatchList item, Throwable t) {
        log.error("SKIP [process] isin={} scheme={} reason={}",
                item.getIsin(), item.getSchemeName(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(List<MutualFundNav> item, Throwable t) {
        log.error("SKIP [write] {} records reason={}",
                item == null ? 0 : item.size(), t.getMessage());
    }
}