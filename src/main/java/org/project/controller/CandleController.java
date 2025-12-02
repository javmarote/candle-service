package org.project.controller;

import org.project.model.Candle;
import org.project.service.CandleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CandleController {

   private final CandleService candleService;

   public CandleController(CandleService candleService) {
      this.candleService = candleService;
   }

   @GetMapping("/history")
   public Object getHistory(
         @RequestParam String symbol,
         @RequestParam long interval
   ) {
      List<Candle> candles = candleService.getHistory(symbol, interval);

      return Map.of(
            "s", "ok",
            "t", candles.stream().map(Candle::time).collect(Collectors.toList()),
            "o", candles.stream().map(Candle::open).collect(Collectors.toList()),
            "h", candles.stream().map(Candle::high).collect(Collectors.toList()),
            "l", candles.stream().map(Candle::low).collect(Collectors.toList()),
            "c", candles.stream().map(Candle::close).collect(Collectors.toList()),
            "v", candles.stream().map(Candle::volume).collect(Collectors.toList())
      );
   }


}
