package org.project.service;

import org.project.model.BidAskEvent;
import org.project.model.Candle;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CandleService {

   private final Map<String, Map<Long, List<Candle>>> candleStore = new ConcurrentHashMap<>();

   public void ingestEvent(BidAskEvent event, long intervalSeconds) {
      long candleTime = (event.timestamp() / intervalSeconds) * intervalSeconds;

      candleStore.putIfAbsent(event.symbol(), new ConcurrentHashMap<>());
      Map<Long, List<Candle>> intervalMap = candleStore.get(event.symbol());
      intervalMap.putIfAbsent(intervalSeconds, new ArrayList<>());
      List<Candle> candles = intervalMap.get(intervalSeconds);

      Candle candle;
      if (candles.isEmpty() || candles.get(candles.size() - 1).time() != candleTime) {
         candle = new Candle(candleTime, event.bid(), event.bid(), event.bid(), event.bid(), 1);
         candles.add(candle);
      } else {
         Candle last = candles.get(candles.size() - 1);
         double high = Math.max(last.high(), event.bid());
         double low = Math.min(last.low(), event.bid());
         candle = new Candle(last.time(), last.open(), high, low, event.bid(), last.volume() + 1);
         candles.set(candles.size() - 1, candle);
      }
   }

   public List<Candle> getHistory(String symbol, long intervalSeconds) {
      long now = System.currentTimeMillis() / 1000; // current time in seconds
      long from = now - 60; // last 60 secs
      long to = now;

      Map<Long, List<Candle>> intervalMap = candleStore.getOrDefault(symbol, Collections.emptyMap());
      List<Candle> candles = intervalMap.getOrDefault(intervalSeconds, Collections.emptyList());
      List<Candle> result = new ArrayList<>();
      for (Candle c : candles) {
         if (c.time() >= from && c.time() <= to) {
            result.add(c);
         }
      }
      return result;
   }
}
