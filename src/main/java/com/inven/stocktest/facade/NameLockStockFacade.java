package com.inven.stocktest.facade;

import com.inven.stocktest.repository.LockRepository;
import com.inven.stocktest.repository.StockRepository;
import com.inven.stocktest.service.StockService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class NameLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;

    public NameLockStockFacade(LockRepository lockRepository, StockService stockService, StockRepository stockRepository) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    @Transactional
    public void decrease(Long id, Long quantity){
        try {
            lockRepository.getLock(id.toString());
            stockService.descrease(id,quantity);
        }finally {
            lockRepository.releaseLock(id.toString());
        }
    }

}
