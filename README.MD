# Candle Aggregation Service

## Project Overview

This project provides a lightweight candle-generation engine built on Spring Boot.  
It ingests real-time bid/ask events and transforms them into OHLCV candles for configurable time intervals (e.g., 5-second candles). It also exposes a REST endpoint to query recent candle history.

The system is composed of:

- **CandleService**: Core service performing interval alignment, candle creation, and candle updating.
- **CandleController**: REST API that returns OHLCV series for a given symbol and interval.
- **Simulator**: A scheduled component producing synthetic market events every second for demonstration and testing.
- **Unit tests** validating correct candle creation, updates, and timestamp handling.

This module behaves similarly to a simplified real-time market data aggregation engine used in trading systems and crypto exchanges.

---

## Assumptions and Trade-offs

### 1. In-memory store
Candles are stored in an in-memory `ConcurrentHashMap`.  
This simplifies development but implies:
- No persistence across restarts.
- No horizontal scalability.
- Suitable only for demos, tests, or prototype architecture.

### 2. 60-second retrieval window
`getHistory()` returns only candles from the last 60 seconds.  
This keeps responses small and avoids unbounded growth.  
A production implementation would make this configurable.

### 3. Interval alignment
Candles are grouped by:
candleTime = (timestamp / intervalSeconds) * intervalSeconds


Meaning:
- Events inside the same bucket update the same candle.
- A new candle is created only when the aligned timestamp changes.

### 4. Bid-driven OHLC
Only the **bid** price is used to compute OHLC.  
Ask price is ignored for candle aggregation.

### 5. Concurrency approach
`ConcurrentHashMap` is used for symbol and interval maps, but candle lists use `ArrayList`.  
High-throughput ingestion would require finer concurrency controls.

---

## Instructions for Running Tests

### Requirements
- Java 17+
- Maven (assumed)

### Run all tests


### Test coverage
The included tests validate:
- Creation of a candle from a single event.
- Updating a candle when multiple events fall inside the same interval.
- Timestamp alignment logic.
- Behavior of the 60-second history window.

---

## Notes on Bonus Features 

### 1. Multi-interval aggregation
Extend the system to generate candles for multiple intervals simultaneously (e.g., 1s, 5s, 10s, 1m), sharing the same event stream.

### 2. Persistent storage
Replace in-memory structures with Redis, PostgreSQL/TimescaleDB, or Kafka to support durability and long-term queryability.

### 3. Performance and scalability improvements
Introduce optimizations such as:
- Lock-free data structures.
- Shared storage per symbol.
- Dedicated ingestion threads.
- Backpressure-aware pipelines (e.g., Reactor, Kafka Streams).

---