package org.toasthub.stock.model.cache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.toasthub.common.Symbol;

@Component
@Scope("singleton")
public class TradeSignalCache {

    private Map<String, GoldenCross> goldenCrossMap = new ConcurrentHashMap<String, GoldenCross>();
    private Map<String, LowerBollingerBand> lowerBollingerBandMap = new ConcurrentHashMap<String, LowerBollingerBand>();
    private Map<String, UpperBollingerBand> upperBollingerBandMap = new ConcurrentHashMap<String, UpperBollingerBand>();
    private Map<String, SignalLineCross> signalLineCrossMap = new ConcurrentHashMap<String, SignalLineCross>();
    private Map<String, BigDecimal> recentClosingPriceMap = new ConcurrentHashMap<String, BigDecimal>();
    private Map<String, Long> recentEpochSecondsMap = new ConcurrentHashMap<String, Long>();
    private Map<String, Object> algorithmStatisticsMap = new ConcurrentHashMap<String, Object>();

    private TradeSignalCache() {
        for (String symbol : Symbol.SYMBOLS) {
            algorithmStatisticsMap.put("MINUTE::CHECKED::" + symbol, 0);
            algorithmStatisticsMap.put("DAY::CHECKED::" + symbol, 0);

            algorithmStatisticsMap.put("DAY::GOLDENCROSS::FLASHED::" + symbol, 0);
            algorithmStatisticsMap.put("MINUTE::GOLDENCROSS::FLASHED::" + symbol, 0);
            algorithmStatisticsMap.put("DAY::LOWERBOLLINGERBAND::FLASHED::" + symbol, 0);
            algorithmStatisticsMap.put("MINUTE::LOWERBOLLINGERBAND::FLASHED::" + symbol, 0);
            algorithmStatisticsMap.put("DAY::UPPERBOLLINGERBAND::FLASHED::" + symbol, 0);
            algorithmStatisticsMap.put("MINUTE::UPPERBOLLINGERBAND::FLASHED::" + symbol, 0);
        }
    }

    public Map<String, GoldenCross> getGoldenCrossMap() {
        return goldenCrossMap;
    }

    public Map<String, Object> getAlgorithmStatisticsMap() {
        return algorithmStatisticsMap;
    }

    public void setAlgorithmStatisticsMap(Map<String, Object> algorithmStatisticsMap) {
        this.algorithmStatisticsMap = algorithmStatisticsMap;
    }

    public Map<String, UpperBollingerBand> getUpperBollingerBandMap() {
        return upperBollingerBandMap;
    }

    public void setUpperBollingerBandMap(Map<String, UpperBollingerBand> upperBollingerBandMap) {
        this.upperBollingerBandMap = upperBollingerBandMap;
    }

    public Map<String, Long> getRecentEpochSecondsMap() {
        return recentEpochSecondsMap;
    }

    public void setRecentEpochSecondsMap(Map<String, Long> recentEpochSecondsMap) {
        this.recentEpochSecondsMap = recentEpochSecondsMap;
    }

    public Map<String, BigDecimal> getRecentClosingPriceMap() {
        return recentClosingPriceMap;
    }

    public void setRecentClosingPriceMap(Map<String, BigDecimal> recentClosingPriceMap) {
        this.recentClosingPriceMap = recentClosingPriceMap;
    }

    public Map<String, SignalLineCross> getSignalLineCrossMap() {
        return signalLineCrossMap;
    }

    public void setSignalLineCrossMap(Map<String, SignalLineCross> signalLineCrossMap) {
        this.signalLineCrossMap = signalLineCrossMap;
    }

    public Map<String, LowerBollingerBand> getLowerBollingerBandMap() {
        return lowerBollingerBandMap;
    }

    public void setLowerBollingerBandMap(Map<String, LowerBollingerBand> lowerBollingerBandMap) {
        this.lowerBollingerBandMap = lowerBollingerBandMap;
    }

    public void setGoldenCrossMap(Map<String, GoldenCross> goldenCrossMap) {
        this.goldenCrossMap = goldenCrossMap;
    }

}
