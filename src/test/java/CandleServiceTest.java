import org.project.model.BidAskEvent;
import org.project.model.Candle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.service.CandleService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandleServiceTest {

   private CandleService candleService;

   @BeforeEach
   void setup() {
      candleService = new CandleService();
   }

   @Test
   void testSingleEventCreatesCandle() {
      long now = System.currentTimeMillis() / 1000;

      BidAskEvent event = new BidAskEvent("BTC-USD", 100.0, 101.0, now);
      candleService.ingestEvent(event, 5);

      List<Candle> candles = candleService.getHistory("BTC-USD", 5);

      assertEquals(1, candles.size());
      Candle candle = candles.get(0);

      assertEquals(100.0, candle.open());
      assertEquals(100.0, candle.high());
      assertEquals(100.0, candle.low());
      assertEquals(100.0, candle.close());
      assertEquals(1, candle.volume());
   }


   @Test
   void testMultipleEventsUpdateCandle() {
      long nowMs = System.currentTimeMillis();
      long nowSec = nowMs / 1000;
      long interval = 5;

      long alignedNow = (nowSec / interval) * interval;

      candleService.ingestEvent(new BidAskEvent("BTC-USD", 100, 101, alignedNow), interval);
      candleService.ingestEvent(new BidAskEvent("BTC-USD", 105, 106, alignedNow + 1), interval);

      List<Candle> candles = candleService.getHistory("BTC-USD", interval);
      long from = nowSec - 60;
      long to = nowSec;

      assertEquals(1, candles.size());
      Candle candle = candles.get(0);
      assertEquals(100, candle.open());
      assertEquals(105, candle.high());
      assertEquals(100, candle.low());
      assertEquals(105, candle.close());
      assertEquals(2, candle.volume());
   }

   @Test
   void timeStampTest() {
      long nowSec = System.currentTimeMillis() / 1000;
      System.out.println("Current UNIX timestamp in seconds: " + nowSec);
   }
}
