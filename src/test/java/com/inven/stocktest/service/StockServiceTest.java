package com.inven.stocktest.service;

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
public class StockServiceTest {

    //@Autowired
    //private StockService stockService;

    @Autowired
    private PessimisticLockStockService stockService;

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
    public void 재고감소(){
        stockService.descrease(1L,1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(99,stock.getQuantity());
    }

    @Test
    public void 동시100개요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService excutorService = Executors.newFixedThreadPool(32);

        // 다른 스레드가 실행 될때 까지 대기하는걸 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i<threadCount; i++){
            excutorService.submit(() -> {
                try{
                    stockService.descrease(1L,1L);
                } finally {
                    latch.countDown();
                }

            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0,stock.getQuantity());

    }
    /*
     에상과 다른 이유는 레이스 컨디션이 나옴
     레이스 컨디션 이란 둘 이상의 Thread가 공유 데이터를 엑세스 할 수 있고 , 동시에 변경을 하려고 할떄 발생하는 문제이다.
     */


}
