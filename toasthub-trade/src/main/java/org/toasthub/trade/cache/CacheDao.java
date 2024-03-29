package org.toasthub.trade.cache;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.NoResultException;

import org.toasthub.core.common.BaseMemberDao;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.trade.model.AssetDay;
import org.toasthub.trade.model.AssetMinute;
import org.toasthub.trade.model.TechnicalIndicator;
import org.toasthub.trade.model.TechnicalIndicatorDetail;

public interface CacheDao extends BaseMemberDao {
        public void saveAll(RestRequest request, RestResponse response) throws Exception;

        public AssetDay getLatestAssetDay(String symbol) throws NoResultException;

        public AssetMinute getLatestAssetMinute(String symbol) throws NoResultException;

        public BigDecimal getSMAValue(String symbol, String evaluationPeriod, int evaluationDuration, long epochSeconds)
                        throws NoResultException;

        public BigDecimal getLBBValue(final String symbol, final String evaluationPeriod, final int evaluationDuration,
                        final long epochSeconds, final BigDecimal standardDeviations) throws NoResultException;

        public BigDecimal getUBBValue(final String symbol, final String evaluationPeriod, final int evaluationDuration,
                        final long epochSeconds, final BigDecimal standardDeviations) throws NoResultException;

        public void getAssetDays(RestRequest request, RestResponse response);

        public List<AssetDay> getAssetDays(String symbol, long startingEpochSeconds, long endingEpochSeconds);

        public List<AssetMinute> getAssetMinutes(String symbol, long startingEpochSeconds, long endingEpochSeconds);

        public TechnicalIndicator refreshTechnicalIndicator(TechnicalIndicator technicalIndicator);

        public void getAssetMinutes(RestRequest request, RestResponse response);

        public void refresh(RestRequest request, RestResponse response);

        public void getEarliestAlgTime(final RestRequest request, final RestResponse response) throws NoResultException;

        public List<TechnicalIndicator> getTechnicalIndicators();

        public List<TechnicalIndicatorDetail> getTechnicalIndicatorDetails(TechnicalIndicator t);

        public List<TechnicalIndicatorDetail> getIncompleteTechnicalIndicatorDetails(TechnicalIndicator t);

        public List<TechnicalIndicatorDetail> getCompleteTechnicalIndicatorDetails(TechnicalIndicator t);

        public long itemCount(String technicalIndicatorType, String evaluationPeriod, String technicalIndicatorKey,
                        String symbol);

        public Object saveItem(Object o);

        public void saveList(List<?> list);

        public TechnicalIndicator findTechnicalIndicatorById(long id);

        public List<AssetMinute> getSMAAssetMinuteFlashes(long startTime, long endTime,
                        String symbol, String evaluationPeriod, int shortSMAEvaluationDuration,
                        int longSMAEvaluationDuration);

        public List<AssetMinute> getLBBAssetMinuteFlashes(long startTime, long endTime, BigDecimal standardDeviations,
                        String symbol, String evaluationPeriod, int evaluationDuration);

        public List<AssetMinute> getUBBAssetMinuteFlashes(long startTime, long endTime, BigDecimal standardDeviations,
                        String symbol, String evaluationPeriod, int evaluationDuration);

        public BigDecimal getHighestAssetMinuteValueWithinTimeFrame(String symbol, long startTime, long endTime);

        public BigDecimal getLowestAssetMinuteValueWithinTimeFrame(String symbol, long startTime, long endTime);

        public long getAssetDayCountWithinTimeFrame(String symbol, long startTime, long endTime);

        public void flush();
}
