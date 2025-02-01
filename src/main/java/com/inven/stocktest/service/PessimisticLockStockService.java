package com.inven.stocktest.service;

import com.inven.stocktest.domain.Stock;
import com.inven.stocktest.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public synchronized void descrease(Long id, Long quantity){
        // 재고 조회
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        // 재고 감소
        stock.decrease(quantity);
        // 재고 저장
        stockRepository.save(stock);
    }

}
