package com.inven.stocktest.service;


import com.inven.stocktest.domain.Stock;
import com.inven.stocktest.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    //@Transactional
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void descrease(Long id, Long quantity){
        // 재고 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고 감소
        stock.decrease(quantity);
        // 재고 저장
        stockRepository.saveAndFlush(stock);
    }
}
