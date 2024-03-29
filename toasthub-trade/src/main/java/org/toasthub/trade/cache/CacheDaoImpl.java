package org.toasthub.trade.cache;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.trade.model.AssetDay;
import org.toasthub.trade.model.AssetMinute;
import org.toasthub.trade.model.TechnicalIndicator;
import org.toasthub.trade.model.TechnicalIndicatorDetail;
import org.toasthub.trade.model.TradeConstant;

@Repository("TACacheDao")
@Transactional("TransactionManagerData")
public class CacheDaoImpl implements CacheDao {

    @Autowired
    protected EntityManagerDataSvc entityManagerDataSvc;

    @Override
    public void delete(final RestRequest request, final RestResponse response) {

    }

    @Override
    public void refresh(final RestRequest request, final RestResponse response) {
        entityManagerDataSvc.getInstance().refresh(request.getParam(GlobalConstant.ITEM));
    }

    @Override
    public void save(final RestRequest request, final RestResponse response) throws Exception {
        entityManagerDataSvc.getInstance().merge(request.getParam(GlobalConstant.ITEM));
    }

    @Override
    public Object saveItem(final Object o) {
        return entityManagerDataSvc.getInstance().merge(o);
    }

    @Override
    public void saveList(final List<?> list) {
        for (final Object o : list) {
            entityManagerDataSvc.getInstance().merge(o);
        }
    }

    @Override
    public void saveAll(final RestRequest request, final RestResponse response) throws Exception {
        for (final Object o : ArrayList.class.cast(request.getParam(GlobalConstant.ITEMS))) {
            entityManagerDataSvc.getInstance().merge(o);
        }
    }

    @Override
    public void items(final RestRequest request, final RestResponse response) {
        final String queryStr = "SELECT DISTINCT x FROM TechnicalIndicator as x";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);

        final List<TechnicalIndicator> technicalIndicators = new ArrayList<TechnicalIndicator>();

        for (final Object o : ArrayList.class.cast(query.getResultList())) {
            final TechnicalIndicator t = TechnicalIndicator.class.cast(o);
            Hibernate.initialize(t.getDetails());
            technicalIndicators.add(t);
        }

        response.addParam(GlobalConstant.ITEMS, technicalIndicators);
    }

    @Override
    public List<TechnicalIndicator> getTechnicalIndicators() {
        final String queryStr = "SELECT DISTINCT x FROM TechnicalIndicator as x";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);

        final List<TechnicalIndicator> technicalIndicators = new ArrayList<TechnicalIndicator>();

        for (final Object o : query.getResultList()) {
            technicalIndicators.add(TechnicalIndicator.class.cast(o));
        }

        return technicalIndicators;

    }

    @Override
    public List<TechnicalIndicatorDetail> getTechnicalIndicatorDetails(
            final TechnicalIndicator technicalIndicator) {
        final String queryStr = "SELECT DISTINCT(x) FROM TechnicalIndicatorDetail x WHERE x.technicalIndicator =: technicalIndicator";

        final List<TechnicalIndicatorDetail> technicalIndicatorDetails = new ArrayList<TechnicalIndicatorDetail>();

        final Query query = entityManagerDataSvc.getInstance()
                .createQuery(queryStr)
                .setParameter("technicalIndicator", technicalIndicator)
                .setMaxResults(1000);

        for (final Object o : query.getResultList()) {
            technicalIndicatorDetails.add(TechnicalIndicatorDetail.class.cast(o));
        }

        return technicalIndicatorDetails;
    }

    @Override
    public List<TechnicalIndicatorDetail> getIncompleteTechnicalIndicatorDetails(
            final TechnicalIndicator technicalIndicator) {
        final String queryStr = "SELECT DISTINCT(x) FROM TechnicalIndicatorDetail x WHERE x.technicalIndicator =: technicalIndicator AND x.checked <: checked";

        final List<TechnicalIndicatorDetail> technicalIndicatorDetails = new ArrayList<TechnicalIndicatorDetail>();

        final Query query = entityManagerDataSvc.getInstance()
                .createQuery(queryStr)
                .setParameter("technicalIndicator", technicalIndicator)
                .setParameter("checked", 100)
                .setMaxResults(1000);

        for (final Object o : query.getResultList()) {
            technicalIndicatorDetails.add(TechnicalIndicatorDetail.class.cast(o));
        }

        return technicalIndicatorDetails;
    }

    @Override
    public List<TechnicalIndicatorDetail> getCompleteTechnicalIndicatorDetails(
            final TechnicalIndicator technicalIndicator) {
        final String queryStr = "SELECT DISTINCT(x) FROM TechnicalIndicatorDetail x WHERE x.technicalIndicator =: technicalIndicator AND x.checked =: checked";

        final List<TechnicalIndicatorDetail> technicalIndicatorDetails = new ArrayList<TechnicalIndicatorDetail>();

        final Query query = entityManagerDataSvc.getInstance()
                .createQuery(queryStr)
                .setParameter("technicalIndicator", technicalIndicator)
                .setParameter("checked", 100)
                .setMaxResults(1000);

        for (final Object o : query.getResultList()) {
            technicalIndicatorDetails.add(TechnicalIndicatorDetail.class.cast(o));
        }

        return technicalIndicatorDetails;
    }

    @Override
    public void itemCount(final RestRequest request, final RestResponse response) throws Exception {
        final String queryStr = "SELECT COUNT(DISTINCT x) FROM TechnicalIndicator as x WHERE x.technicalIndicatorType =:technicalIndicatorType AND x.evaluationPeriod =:evaluationPeriod AND x.technicalIndicatorKey =:technicalIndicatorKey AND x.symbol =:symbol";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
        query.setParameter("technicalIndicatorType", (String) request.getParam("TECHNICAL_INDICATOR_TYPE"));
        query.setParameter("evaluationPeriod", (String) request.getParam("EVALUATION_PERIOD"));
        query.setParameter("technicalIndicatorKey", (String) request.getParam("TECHNICAL_INDICATOR_KEY"));
        query.setParameter("symbol", ((String) request.getParam(TradeConstant.SYMBOL)));

        Long count = (Long) query.getSingleResult();
        if (count == null) {
            count = 0l;
        }
        response.addParam(GlobalConstant.ITEMCOUNT, count);
    }

    @Override
    public long itemCount(final String technicalIndicatorType, final String evaluationPeriod,
            final String technicalIndicatorKey,
            final String symbol) {
        final String queryStr = "SELECT COUNT(DISTINCT x) FROM TechnicalIndicator as x WHERE x.technicalIndicatorType =:technicalIndicatorType AND x.evaluationPeriod =:evaluationPeriod AND x.technicalIndicatorKey =:technicalIndicatorKey AND x.symbol =:symbol";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
        query.setParameter("technicalIndicatorType", technicalIndicatorType);
        query.setParameter("evaluationPeriod", evaluationPeriod);
        query.setParameter("technicalIndicatorKey", technicalIndicatorKey);
        query.setParameter("symbol", symbol);

        Long count = (Long) query.getSingleResult();
        if (count == null) {
            count = 0l;
        }

        return count;
    }

    @Override
    public void item(final RestRequest request, final RestResponse response) throws Exception {
        if (request.containsParam(GlobalConstant.ITEMID) && (request.getParam(GlobalConstant.ITEMID) != null)) {
            final String queryStr = "SELECT DISTINCT x FROM TechnicalIndicator AS x WHERE x.id =:id";
            final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);

            if (request.getParam(GlobalConstant.ITEMID) instanceof Integer) {
                query.setParameter("id", Long.valueOf((Integer) request.getParam(GlobalConstant.ITEMID)));
            }

            if (request.getParam(GlobalConstant.ITEMID) instanceof Long) {
                query.setParameter("id", (Long) request.getParam(GlobalConstant.ITEMID));
            }

            if (request.getParam(GlobalConstant.ITEMID) instanceof String) {
                query.setParameter("id", Long.valueOf((String) request.getParam(GlobalConstant.ITEMID)));
            }

            final TechnicalIndicator t = TechnicalIndicator.class.cast(query.getSingleResult());

            Hibernate.initialize(t.getDetails());

            response.addParam(GlobalConstant.ITEM, t);

            return;
        }

        final String queryStr = "SELECT DISTINCT x FROM TechnicalIndicator as x WHERE x.technicalIndicatorType =:technicalIndicatorType AND x.evaluationPeriod =:evaluationPeriod AND x.technicalIndicatorKey =:technicalIndicatorKey";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
        query.setParameter("technicalIndicatorType", (String) request.getParam("TECHNICAL_INDICATOR_TYPE"));
        query.setParameter("evaluationPeriod", (String) request.getParam("EVALUATION_PERIOD"));
        query.setParameter("technicalIndicatorKey", (String) request.getParam("TECHNICAL_INDICATOR_KEY"));

        response.addParam(GlobalConstant.ITEM, query.getSingleResult());
    }

    @Override
    public AssetDay getLatestAssetDay(final String symbol) throws NoResultException {
        final String queryStr = "SELECT DISTINCT x FROM AssetDay x WHERE x.symbol = :symbol ORDER BY x.epochSeconds DESC";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setMaxResults(1);

        return AssetDay.class.cast(query.getSingleResult());
    }

    @Override
    public void getEarliestAlgTime(final RestRequest request, final RestResponse response) throws NoResultException {
        String x = "";
        switch ((String) request.getParam(TradeConstant.IDENTIFIER)) {
            case "SMA":
                x = "SMA";
                break;
            case "EMA":
                x = "EMA";
                break;
            case "LBB":
                x = "LBB";
                break;
            case "UBB":
                x = "UBB";
                break;
            case "MACD":
                x = "MACD";
                break;
            case "SL":
                x = "SL";
                break;
            default:
                System.out.println("Algorithm not recognized ast cachedaoimpl");
                return;
        }

        final String queryStr = "SELECT x.EPOCH_SECONDS FROM "
                + x + " x WHERE x.symbol =: symbol ORDER BY x.EPOCH_SECONDS ASC";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", request.getParam(TradeConstant.SYMBOL))
                .setMaxResults(1);

        response.addParam(GlobalConstant.ITEM, query.getSingleResult());

    }

    @Override
    public AssetMinute getLatestAssetMinute(final String symbol) throws NoResultException {
        final String queryStr = "SELECT DISTINCT x FROM AssetMinute x WHERE x.symbol = :symbol ORDER BY x.epochSeconds DESC";
        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setMaxResults(1);

        return AssetMinute.class.cast(query.getSingleResult());
    }

    @Override
    public void getAssetDays(final RestRequest request, final RestResponse response) {
        final String queryStr = "SELECT DISTINCT x FROM AssetDay AS x WHERE x.symbol =:symbol AND x.EPOCH_SECONDS >=: startingEPOCH_SECONDS AND x.EPOCH_SECONDS <=: endingEPOCH_SECONDS";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", request.getParam(TradeConstant.SYMBOL))
                .setParameter("startingEPOCH_SECONDS", request.getParam("STARTING_EPOCH_SECONDS"))
                .setParameter("endingEPOCH_SECONDS", request.getParam("ENDING_EPOCH_SECONDS"));

        response.addParam(GlobalConstant.ITEMS, query.getResultList());
    }

    @Override
    public List<AssetDay> getAssetDays(final String symbol, final long startingEpochSeconds,
            final long endingEpochSeconds) {
        final String queryStr = "SELECT DISTINCT x FROM AssetDay AS x WHERE x.symbol =:symbol AND x.epochSeconds >=: startingEpochSeconds AND x.epochSeconds <=:endingEpochSeconds";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("startingEpochSeconds", startingEpochSeconds)
                .setParameter("endingEpochSeconds", endingEpochSeconds);

        final List<AssetDay> assetDays = new ArrayList<AssetDay>();

        for (final Object o : query.getResultList()) {
            assetDays.add(AssetDay.class.cast(o));
        }
        return assetDays;
    }

    @Override
    public List<AssetMinute> getAssetMinutes(final String symbol, final long startingEpochSeconds,
            final long endingEpochSeconds) {
        final String queryStr = "SELECT DISTINCT x FROM AssetMinute AS x WHERE x.symbol =:symbol AND x.epochSeconds >=: startingEpochSeconds AND x.epochSeconds <=:endingEpochSeconds";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
        query.setParameter("symbol", symbol);
        query.setParameter("startingEpochSeconds", startingEpochSeconds);
        query.setParameter("endingEpochSeconds", endingEpochSeconds);

        final List<AssetMinute> assetMinutes = new ArrayList<AssetMinute>();

        for (final Object o : query.getResultList()) {
            assetMinutes.add(AssetMinute.class.cast(o));
        }
        return assetMinutes;
    }

    @Override
    public void getAssetMinutes(final RestRequest request, final RestResponse response) {
        final String queryStr = "SELECT DISTINCT x FROM AssetMinute AS x WHERE x.symbol =:symbol AND x.EPOCH_SECONDS >=:startingEPOCH_SECONDS AND x.EPOCH_SECONDS <=: endingEPOCH_SECONDS";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", request.getParam(TradeConstant.SYMBOL))
                .setParameter("startingEPOCH_SECONDS", request.getParam("STARTING_EPOCH_SECONDS"))
                .setParameter("endingEPOCH_SECONDS", request.getParam("ENDING_EPOCH_SECONDS"));

        response.addParam(GlobalConstant.ITEMS, query.getResultList());
    }

    @Override
    public BigDecimal getSMAValue(final String symbol, final String evaluationPeriod, final int evaluationDuration,
            final long epochSeconds) throws NoResultException {
        final String queryStr = "SELECT x.value FROM SMA x WHERE x.epochSeconds =:epochSeconds AND x.evaluationPeriod =: evaluationPeriod AND x.evaluationDuration =: evaluationDuration AND x.symbol =:symbol";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("epochSeconds", epochSeconds)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("evaluationDuration", evaluationDuration)
                .setParameter("symbol", symbol);

        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public BigDecimal getLBBValue(final String symbol, final String evaluationPeriod, final int evaluationDuration,
            final long epochSeconds, final BigDecimal standardDeviations) throws NoResultException {
        final String queryStr = "SELECT x.value FROM LBB x WHERE x.epochSeconds =:epochSeconds AND x.evaluationPeriod =: evaluationPeriod AND x.evaluationDuration =: evaluationDuration AND x.symbol =:symbol AND x.standardDeviations =: standardDeviations";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("epochSeconds", epochSeconds)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("evaluationDuration", evaluationDuration)
                .setParameter("symbol", symbol)
                .setParameter("standardDeviations", standardDeviations);

        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public BigDecimal getUBBValue(final String symbol, final String evaluationPeriod, final int evaluationDuration,
            final long epochSeconds, final BigDecimal standardDeviations) throws NoResultException {
        final String queryStr = "SELECT x.value FROM LBB x WHERE x.epochSeconds =:epochSeconds AND x.evaluationPeriod =: evaluationPeriod AND x.evaluationDuration =: evaluationDuration AND x.symbol =:symbol AND x.standardDeviations =: standardDeviations";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("epochSeconds", epochSeconds)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("evaluationDuration", evaluationDuration)
                .setParameter("symbol", symbol)
                .setParameter("standardDeviations", standardDeviations);

        return (BigDecimal) query.getSingleResult();
    }

    @Override
    public TechnicalIndicator findTechnicalIndicatorById(final long id) {
        return entityManagerDataSvc.getInstance().find(TechnicalIndicator.class, id);
    }

    @Override
    public TechnicalIndicator refreshTechnicalIndicator(final TechnicalIndicator technicalIndicator) {
        final TechnicalIndicator managedTechnicalIndicator = entityManagerDataSvc.getInstance().find(
                TechnicalIndicator.class,
                technicalIndicator.getId());
        entityManagerDataSvc.getInstance().refresh(managedTechnicalIndicator);
        return managedTechnicalIndicator;
    }

    @Override
    public List<AssetMinute> getSMAAssetMinuteFlashes(final long startTime, final long endTime,
            final String symbol, final String evaluationPeriod, final int shortSMAEvaluationDuration,
            final int longSMAEvaluationDuration) {

        final List<AssetMinute> items = new ArrayList<AssetMinute>();

        final String queryStr = "SELECT DISTINCT assetMinute FROM AssetMinute as assetMinute, SMA AS shortSMA , SMA AS longSMA"
                + " WHERE longSMA.symbol = :symbol "
                + " AND longSMA.evaluationPeriod = :evaluationPeriod"
                + " AND longSMA.evaluationDuration = :longSMAEvaluationDuration "
                + " AND shortSMA.symbol = :symbol "
                + " AND shortSMA.evaluationPeriod = :evaluationPeriod"
                + " AND shortSMA.evaluationDuration = :shortSMAEvaluationDuration "
                + " AND assetMinute.symbol = :symbol "
                + " AND assetMinute.epochSeconds > :startTime"
                + " AND assetMinute.epochSeconds < :endTime"
                + " AND assetMinute.epochSeconds = shortSMA.epochSeconds "
                + " AND assetMinute.epochSeconds = longSMA.epochSeconds "
                + " AND shortSMA.value > longSMA.value ";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("shortSMAEvaluationDuration", shortSMAEvaluationDuration)
                .setParameter("longSMAEvaluationDuration", longSMAEvaluationDuration)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime);

        for (final Object o : query.getResultList()) {
            items.add(AssetMinute.class.cast(o));
        }

        return items;
    }

    @Override
    public List<AssetMinute> getLBBAssetMinuteFlashes(final long startTime, final long endTime,
            final BigDecimal standardDeviations,
            final String symbol, final String evaluationPeriod, final int evaluationDuration) {

        final List<AssetMinute> items = new ArrayList<AssetMinute>();

        final String queryStr = "SELECT DISTINCT assetMinute FROM AssetMinute as assetMinute, LBB AS lbb"
                + " WHERE lbb.standardDeviations = :standardDeviations "
                + " AND lbb.symbol = :symbol "
                + " AND lbb.evaluationPeriod = :evaluationPeriod"
                + " AND lbb.evaluationDuration = :evaluationDuration "
                + " AND assetMinute.symbol = :symbol "
                + " AND assetMinute.epochSeconds > :startTime"
                + " AND assetMinute.epochSeconds < :endTime"
                + " AND assetMinute.epochSeconds = lbb.epochSeconds "
                + " AND assetMinute.value < lbb.value ";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("evaluationDuration", evaluationDuration)
                .setParameter("standardDeviations", standardDeviations)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime);

        for (final Object o : query.getResultList()) {
            items.add(AssetMinute.class.cast(o));
        }

        return items;
    }

    @Override
    public List<AssetMinute> getUBBAssetMinuteFlashes(final long startTime, final long endTime,
            final BigDecimal standardDeviations,
            final String symbol, final String evaluationPeriod, final int evaluationDuration) {

        final List<AssetMinute> items = new ArrayList<AssetMinute>();

        final String queryStr = "SELECT DISTINCT assetMinute FROM AssetMinute as assetMinute, UBB AS ubb"
                + " WHERE ubb.standardDeviations = :standardDeviations "
                + " AND ubb.symbol = :symbol "
                + " AND ubb.evaluationPeriod = :evaluationPeriod"
                + " AND ubb.evaluationDuration = :evaluationDuration "
                + " AND assetMinute.symbol = :symbol "
                + " AND assetMinute.epochSeconds > :startTime"
                + " AND assetMinute.epochSeconds < :endTime"
                + " AND assetMinute.epochSeconds = ubb.epochSeconds "
                + " AND assetMinute.value > ubb.value ";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("evaluationPeriod", evaluationPeriod)
                .setParameter("evaluationDuration", evaluationDuration)
                .setParameter("standardDeviations", standardDeviations)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime);

        for (final Object o : query.getResultList()) {
            items.add(AssetMinute.class.cast(o));
        }

        return items;
    }

    public BigDecimal getHighestAssetMinuteValueWithinTimeFrame(final String symbol, final long startTime,
            final long endTime) {
        final String queryStr = "SELECT x.value FROM AssetMinute as x WHERE x.symbol = :symbol AND x.epochSeconds >= :startTime AND x.epochSeconds <= :endTime ORDER BY x.value DESC";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setMaxResults(1);

        return BigDecimal.class.cast(query.getSingleResult());

    }

    public BigDecimal getLowestAssetMinuteValueWithinTimeFrame(final String symbol, final long startTime,
            final long endTime) {
        final String queryStr = "SELECT x.value FROM AssetMinute as x WHERE x.symbol = :symbol AND x.epochSeconds >= :startTime AND x.epochSeconds <= :endTime ORDER BY x.value ASC";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setMaxResults(1);

        return BigDecimal.class.cast(query.getSingleResult());

    }

    public long getAssetDayCountWithinTimeFrame(final String symbol, final long startTime, final long endTime) {
        final String queryStr = "SELECT COUNT (DISTINCT x) FROM AssetDay as x WHERE x.symbol = :symbol AND x.epochSeconds >= :startTime AND x.epochSeconds <= :endTime";

        final Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
                .setParameter("symbol", symbol)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime);

        return Long.class.cast(query.getSingleResult());
    }

    public void flush() {
        entityManagerDataSvc.getInstance().flush();
    }
}
