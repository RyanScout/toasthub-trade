package org.toasthub.stock.model.cache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class TradeSignalCache {

    private Map<String,GoldenCross> goldenCrossMap = new ConcurrentHashMap<String, GoldenCross>();
    private Map<String,LowerBollingerBand> lowerBollingerBandMap = new ConcurrentHashMap<String, LowerBollingerBand>();
    private Map<String,SignalLineCross> signalLineCrossMap = new ConcurrentHashMap<String,SignalLineCross>();
    private Map<String,BigDecimal> recentClosingPriceMap = new ConcurrentHashMap<String, BigDecimal>();
    private Map<String, Long> recentEpochSecondsMap = new ConcurrentHashMap<String, Long>();


    public Map<String,GoldenCross> getGoldenCrossMap() {
        return goldenCrossMap;
    }
    public Map<String, Long> getRecentEpochSecondsMap() {
        return recentEpochSecondsMap;
    }
    public void setRecentEpochSecondsMap(Map<String, Long> recentEpochSecondsMap) {
        this.recentEpochSecondsMap = recentEpochSecondsMap;
    }
    public Map<String,BigDecimal> getRecentClosingPriceMap() {
        return recentClosingPriceMap;
    }
    public void setRecentClosingPriceMap(Map<String,BigDecimal> recentClosingPriceMap) {
        this.recentClosingPriceMap = recentClosingPriceMap;
    }
    public Map<String,SignalLineCross> getSignalLineCrossMap() {
        return signalLineCrossMap;
    }
    public void setSignalLineCrossMap(Map<String,SignalLineCross> signalLineCrossMap) {
        this.signalLineCrossMap = signalLineCrossMap;
    }
    public Map<String,LowerBollingerBand> getLowerBollingerBandMap() {
        return lowerBollingerBandMap;
    }
    public void setLowerBollingerBandMap(Map<String,LowerBollingerBand> lowerBollingerBandMap) {
        this.lowerBollingerBandMap = lowerBollingerBandMap;
    }
    public void setGoldenCrossMap(Map<String,GoldenCross> goldenCrossMap) {
        this.goldenCrossMap = goldenCrossMap;
    } 

}
