package com.inven.stocktest.facade;

import com.inven.stocktest.domain.Stock;
import com.inven.stocktest.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade OptimisticLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before(){
        stockRepository.saveAndFlush(new Stock(1L,100L));
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @Test
    public void 동시100개요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService excutorService = Executors.newFixedThreadPool(32);
        // 다른 스레드가 실행 될때 까지 대기하는걸 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i<threadCount; i++){
            excutorService.submit(() -> {
                try {
                    OptimisticLockStockFacade.decrease(1L, 1L);
                }catch( InterruptedException e ){
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0,stock.getQuantity());

    }
}