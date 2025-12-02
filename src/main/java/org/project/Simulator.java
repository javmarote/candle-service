package org.project;

import org.project.model.BidAskEvent;
import org.project.service.CandleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Simulator {

   private final CandleService candleService;

   private final Random random = new Random();

   private final String[] symbols = { "BTC-USD", "ETH-USD" };

   public Simulator(CandleService candleService) {
      this.candleService = candleService;
   }

   // Generates an event per symbol every second
   @Scheduled(fixedRate = 1000)
   public void generateEvent() {
      long timestampSec = System.currentTimeMillis() / 1000;
      for (String symbol : symbols) {
         double bid = 1000 + random.nextDouble() * 100;
         double ask = bid + random.nextDouble();
         BidAskEvent event = new BidAskEvent(symbol, bid, ask, timestampSec);
         candleService.ingestEvent(event, 5);
      }
   }
}
