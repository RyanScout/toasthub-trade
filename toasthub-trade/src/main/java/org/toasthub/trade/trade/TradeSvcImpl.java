package org.toasthub.trade.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.trade.custom_technical_indicator.CustomTechnicalIndicatorDao;
import org.toasthub.trade.model.CustomTechnicalIndicator;
import org.toasthub.trade.model.RequestValidation;
import org.toasthub.trade.model.Trade;
import org.toasthub.trade.model.TradeConstant;

@Service("TATradeSvc")
public class TradeSvcImpl implements ServiceProcessor, TradeSvc {

	@Autowired
	@Qualifier("TATradeDao")
	protected TradeDao tradeDao;

	@Autowired
	@Qualifier("TACustomTechnicalIndicatorDao")
	private CustomTechnicalIndicatorDao customTechnicalIndicatorDao;

	final ExpressionParser parser = new SpelExpressionParser();

	final AtomicBoolean tradeAnalysisJobRunning = new AtomicBoolean(false);

	// Constructors
	public TradeSvcImpl() {
	}

	@Override
	public void process(final RestRequest request, final RestResponse response) {
		final String action = (String) request.getParams().get("action");

		switch (action) {
			case "ITEM":
				item(request, response);
				break;
			case "LIST":
				items(request, response);
				break;
			case "SAVE":
				save(request, response);
				break;
			case "DELETE":
				delete(request, response);
				break;
			case "RESET":
				reset(request, response);
				break;
			case "SYMBOL_DATA":
				getSymbolData(request, response);
				break;

			default:
				break;
		}

	}

	@Override
	public void save(final RestRequest request, final RestResponse response) {
		if ((!request.containsParam(GlobalConstant.ITEM)) || (request.getParam(GlobalConstant.ITEM) == null)) {
			response.setStatus(RestResponse.ERROR);
			return;
		}

		response.setStatus("Starting !");

		final Map<?, ?> m = Map.class.cast(request.getParam(GlobalConstant.ITEM));

		final Map<String, Object> tempMap = new HashMap<String, Object>();

		for (final Object o : m.keySet()) {
			tempMap.put(String.class.cast(o), m.get(String.class.cast(o)));
		}

		request.setParams(tempMap);

		if (request.getParam("name") == null || ((String) request.getParam("name")).trim().isEmpty()) {
			response.setStatus("Name cannot be empty");
			return;
		}

		if (request.getParam("orderSide") == null
				|| !Arrays.asList(Trade.SUPPORTED_ORDER_SIDES).contains((String) request.getParam("orderSide"))) {
			response.setStatus("Orderside cannot be empty");
			return;
		}

		if (request.getParam("orderType") == null
				|| !Arrays.asList(Trade.SUPPORTED_ORDER_TYPES).contains((String) request.getParam("orderType"))) {
			response.setStatus("Ordertype is not supported");
			return;
		}

		if (request.getParam("evaluationPeriod") == null) {
			response.setStatus("Evaluation period cannot be empty");
			return;
		}

		if (request.getParam("symbol") == null) {
			response.setStatus("Symbol cannot be empty");
			return;
		}

		if (request.getParam("status") == null) {
			response.setStatus("Status cannot be empty");
			return;
		}

		if (request.getParam("currencyType") == null) {
			response.setStatus("Currency type must be specified");
			return;
		}

		if (request.getParam("currencyAmount") == null) {
			response.setStatus("Currency amount must be specified");
			return;
		}

		switch ((String) request.getParam("currencyType")) {
			case "Dollars":
				RequestValidation.validateDollars(request, response);
				break;
			case "Shares":
				RequestValidation.validateShares(request, response);
				break;
			default:
				response.setStatus(RestResponse.ERROR);
				return;
		}

		switch ((String) request.getParam("orderType")) {
			case Trade.MARKET:
				break;
			case Trade.PROFIT_LIMIT:
				request.addParam("PROFIT_LIMIT_TYPE", request.getParam("profitLimitType"));
				RequestValidation.validateProfitLimitAmount(request, response);
				break;
			case Trade.TRAILING_STOP:
				request.addParam("TRAILING_STOP_TYPE", request.getParam("trailingStopType"));
				RequestValidation.validateTrailingStopAmount(request, response);
				break;
			case Trade.TRAILING_STOP_PROFIT_LIMIT:
				request.addParam("TRAILING_STOP_TYPE", request.getParam("trailingStopType"));
				request.addParam("PROFIT_LIMIT_TYPE", request.getParam("profitLimitType"));
				RequestValidation.validateProfitLimitAmount(request, response);
				RequestValidation.validateTrailingStopAmount(request, response);
				break;
			default:
				response.setStatus(RestResponse.ERROR);
				return;
		}

		Long itemId = null;

		if (request.getParam("id") instanceof Integer) {
			itemId = Long.valueOf((Integer) request.getParams().remove("id"));
		}

		switch ((String) request.getParam("orderSide")) {
			case Trade.BOT:
				validateBuyCondition(request, response);
				validateSellCondition(request, response);

				if (request.getParam("budget") == null) {
					response.setStatus("Budget cannot be empty");
					return;
				}
				RequestValidation.validateBudget(request, response);

				request.addParam("ITERATIONS", "unlimited");
				break;
			case Trade.BUY:
				validateBuyCondition(request, response);
				request.addParam("ITERATIONS", request.getParam("iterations"));
				break;
			case Trade.SELL:
				validateSellCondition(request, response);
				request.addParam("ITERATIONS", request.getParam("iterations"));
				break;
			default:
				response.setStatus("Invalid orderside");
				return;
		}

		request.addParam("NAME", request.getParam("name"));
		request.addParam("ORDER_SIDE", request.getParam("orderSide"));
		request.addParam("ORDER_TYPE", request.getParam("orderType"));
		request.addParam("EVALUATION_PERIOD", request.getParam("evaluationPeriod"));
		request.addParam("SYMBOL", request.getParam("symbol"));
		request.addParam("STATUS", request.getParam("status"));
		request.addParam("CURRENCY_TYPE", request.getParam("currencyType"));

		request.getParams().remove(GlobalConstant.ACTIVE);
		request.getParams().remove("RUNSTATUS");

		Trade trade = new Trade();

		if (itemId != null) {
			request.addParam(GlobalConstant.ITEMID, itemId);
			try {
				tradeDao.item(request, response);
			} catch (final Exception e) {
				e.printStackTrace();
				return;
			}
			trade = (Trade) response.getParam(GlobalConstant.ITEM);
		}

		if (itemId == null) {
			try {
				tradeDao.itemCount(request, response);
			} catch (final Exception e) {
				e.printStackTrace();
				return;
			}

			if ((long) response.getParam(GlobalConstant.ITEMCOUNT) > 0) {
				response.setStatus("Trade of the same name exists");
				return;
			}
		}

		if (!response.getStatus().equals("Starting !")) {
			return;
		}

		trade.setName((String) request.getParam("NAME"));
		trade.setOrderSide((String) request.getParam("ORDER_SIDE"));
		trade.setOrderType((String) request.getParam("ORDER_TYPE"));
		trade.setIterations((String) request.getParam("ITERATIONS"));
		trade.setEvaluationPeriod((String) request.getParam("EVALUATION_PERIOD"));
		trade.setSymbol((String) request.getParam("SYMBOL"));
		trade.setStatus((String) request.getParam("STATUS"));
		trade.setCurrencyType((String) request.getParam("CURRENCY_TYPE"));
		trade.setCurrencyAmount((BigDecimal) request.getParam("CURRENCY_AMOUNT"));

		if (request.getParam("ORDER_TYPE").equals(Trade.TRAILING_STOP)) {
			trade.setTrailingStopType((String) request.getParam("TRAILING_STOP_TYPE"));
			trade.setTrailingStopAmount((BigDecimal) request.getParam("TRAILING_STOP_AMOUNT"));
		}

		if (request.getParam("ORDER_TYPE").equals(Trade.PROFIT_LIMIT)) {
			trade.setProfitLimitType((String) request.getParam("PROFIT_LIMIT_TYPE"));
			trade.setProfitLimitAmount((BigDecimal) request.getParam("PROFIT_LIMIT_AMOUNT"));
		}

		if (request.getParam("ORDER_TYPE").equals(Trade.TRAILING_STOP_PROFIT_LIMIT)) {
			trade.setTrailingStopType((String) request.getParam("TRAILING_STOP_TYPE"));
			trade.setTrailingStopAmount((BigDecimal) request.getParam("TRAILING_STOP_AMOUNT"));
			trade.setProfitLimitType((String) request.getParam("PROFIT_LIMIT_TYPE"));
			trade.setProfitLimitAmount((BigDecimal) request.getParam("PROFIT_LIMIT_AMOUNT"));
		}

		if (request.getParam("BUY_CONDITION") != null) {
			trade.setParseableBuyCondition((String) request.getParam("BUY_CONDITION"));
		}

		if (request.getParam("SELL_CONDITION") != null) {
			trade.setParseableSellCondition((String) request.getParam("SELL_CONDITION"));
		}

		if (request.getParam("BUDGET") != null && itemId != null) {

			if (trade.getBudget().compareTo((BigDecimal) request.getParam("BUDGET")) != 0) {

				if (!trade.getStatus().equals("Not Running")) {
					response.setStatus("Cannot change budget while trade is running");
					return;
				}
				if (trade.getTradeDetails().size() > 0) {
					response.setStatus("Must reset trade before changing budget");
					return;
				}

				trade.setAvailableBudget((BigDecimal) request.getParam("BUDGET"));
				trade.setTotalValue((BigDecimal) request.getParam("BUDGET"));
			}

			trade.setBudget((BigDecimal) request.getParam("BUDGET"));
		}

		if (request.getParam("BUDGET") != null && itemId == null) {
			trade.setBudget((BigDecimal) request.getParam("BUDGET"));
			trade.setAvailableBudget((BigDecimal) request.getParam("BUDGET"));
			trade.setTotalValue((BigDecimal) request.getParam("BUDGET"));
		}

		request.addParam(GlobalConstant.ITEM, trade);

		try {
			tradeDao.save(request, response);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		response.setStatus(RestResponse.SUCCESS);
	}

	@Override
	public void delete(final RestRequest request, final RestResponse response) {
		try {
			tradeDao.delete(request, response);
			response.setStatus(RestResponse.SUCCESS);
		} catch (final Exception e) {
			response.setStatus(RestResponse.ACTIONFAILED);
			e.printStackTrace();
		}

	}

	public void reset(final RestRequest request, final RestResponse response) {
		try {
			tradeDao.resetTrade(request, response);
			response.setStatus(RestResponse.SUCCESS);
		} catch (final Exception e) {
			response.setStatus(RestResponse.ACTIONFAILED);
			e.printStackTrace();
		}

	}

	@Override
	public void item(final RestRequest request, final RestResponse response) {
		try {
			tradeDao.item(request, response);
			response.setStatus(RestResponse.SUCCESS);
		} catch (final Exception e) {
			response.setStatus(RestResponse.ACTIONFAILED);
			e.printStackTrace();
		}

	}

	@Override
	public void items(final RestRequest request, final RestResponse response) {
		try {
			tradeDao.itemCount(request, response);
			if ((Long) response.getParam(GlobalConstant.ITEMCOUNT) > 0) {
				tradeDao.items(request, response);

				for (final Object o : ArrayList.class.cast(response.getParam(TradeConstant.TRADES))) {
					final Trade trade = Trade.class.cast(o);

					String[] stringArr1 = trade.getParseableBuyCondition().split(" ");
					stringArr1 = Stream.of(stringArr1).map(s -> {
						if (s.equals("(") || s.equals(")") || s.equals("||") || s.equals("&&") || s.equals("")) {
							return s;
						}
						request.addParam(GlobalConstant.ITEMID, s);
						try {
							customTechnicalIndicatorDao.item(request, response);
						} catch (final Exception e) {
							e.printStackTrace();
						}

						final CustomTechnicalIndicator c = ((CustomTechnicalIndicator) response
								.getParam(GlobalConstant.ITEM));

						return String.valueOf(c.getName());

					}).toArray(String[]::new);

					trade.setBuyCondition(String.join(" ", stringArr1));

					String[] stringArr2 = trade.getParseableSellCondition().split(" ");
					stringArr2 = Stream.of(stringArr2).map(s -> {
						if (s.equals("(") || s.equals(")") || s.equals("||") || s.equals("&&") || s.equals("")) {
							return s;
						}
						request.addParam(GlobalConstant.ITEMID, s);
						try {
							customTechnicalIndicatorDao.item(request, response);
						} catch (final Exception e) {
							e.printStackTrace();
						}

						final CustomTechnicalIndicator c = ((CustomTechnicalIndicator) response
								.getParam(GlobalConstant.ITEM));

						return String.valueOf(c.getName());

					}).toArray(String[]::new);

					trade.setSellCondition(String.join(" ", stringArr2));
				}
			}
			response.setStatus(RestResponse.SUCCESS);
		} catch (final Exception e) {
			response.setStatus(RestResponse.ACTIONFAILED);
			e.printStackTrace();
		}

	}

	public void getSymbolData(final RestRequest request, final RestResponse response) {
		if (request.getParam("FIRST_POINT") == null || request.getParam("LAST_POINT") == null
				|| request.getParam("SYMBOL") == null || request.getParam("EVALUATION_PERIOD") == null ) {
			return;
		}
		tradeDao.getSymbolData(request, response);
	}

	public void validateBuyCondition(final RestRequest request, final RestResponse response) {
		String initialString = "";

		if (request.getParam("buyCondition") instanceof String) {
			initialString = (String) request.getParam("buyCondition");
		}

		final List<String> testStrings = new ArrayList<String>();

		String str = initialString.replaceAll("\\s+", "");

		str = str.replaceAll("[&]+", " && ").replaceAll("[|]+", " || ").replace("(", " ( ").replace(")", " ) ")
				.replaceAll("\\s+", " ").trim();

		str = String.join(" ", Stream.of(str.split(" ")).map(s -> {

			if (Arrays.asList("(", ")", "&&", "||", "").contains(s)) {
				testStrings.add(s);
				return s;
			}

			request.addParam("NAME", s);
			try {
				customTechnicalIndicatorDao.item(request, response);
			} catch (final NoResultException e) {
				response.setStatus("Invalid technical indicator in buy condition");
				testStrings.add("true");
				return s;
			} catch (final Exception e) {
			}
			
			final CustomTechnicalIndicator c = CustomTechnicalIndicator.class.cast(response.getParam(GlobalConstant.ITEM));

			if (!c.getEvaluationPeriod().equals((String) request.getParam("evaluationPeriod"))) {
				response.setStatus("\"" + c.getName() + "\" does not support this trades evaluation period");
			}

			if (!c.getSymbols().stream()
					.anyMatch(symbol -> symbol.getSymbol().equals((String) request.getParam("symbol")))) {
				response.setStatus("\"" + c.getName() + "\" does not support " + (String) request.getParam("symbol"));
			}

			final long id = c.getId();

			s = String.valueOf(id);

			testStrings.add("true");
			return s;

		}).toArray(String[]::new));

		final String testString = String.join(" ", testStrings);

		if (!testString.equals("")) {
			try {
				parser.parseExpression(testString).getValue(Boolean.class);
			} catch (final ParseException e) {
				response.setStatus("Invalid logic in buy condition");
				return;
			}
		}

		request.addParam("BUY_CONDITION", str);
	}

	public void validateSellCondition(final RestRequest request, final RestResponse response) {
		String initialString = "";

		if (request.getParam("sellCondition") instanceof String) {
			initialString = (String) request.getParam("sellCondition");
		}

		final List<String> testStrings = new ArrayList<String>();

		String str = initialString.replaceAll("\\s+", "");
		str = str.replaceAll("[&]+", " && ").replaceAll("[|]+", " || ").replace("(", " ( ").replace(")", " ) ")
				.replaceAll("\\s+", " ").trim();
		str = String.join(" ", Stream.of(str.split(" ")).map(s -> {

			if (Arrays.asList("(", ")", "&&", "||", "").contains(s)) {
				testStrings.add(s);
				return s;
			}

			request.addParam("NAME", s);
			try {
				customTechnicalIndicatorDao.item(request, response);
			} catch (final NoResultException e) {
				response.setStatus("Invalid technical indicator in sell condition");
				testStrings.add("true");
				return s;
			} catch (final Exception e) {
			}

			final CustomTechnicalIndicator c = CustomTechnicalIndicator.class.cast(response.getParam(GlobalConstant.ITEM));

			if (!c.getEvaluationPeriod().equals((String) request.getParam("evaluationPeriod"))) {
				response.setStatus("\"" + c.getName() + "\" does not support this trades evaluation period");
			}

			if (!c.getSymbols().stream()
					.anyMatch(symbol -> symbol.getSymbol().equals((String) request.getParam("symbol")))) {
				response.setStatus("\"" + c.getName() + "\" does not support " + (String) request.getParam("symbol"));
			}

			final long id = c.getId();

			s = String.valueOf(id);

			testStrings.add("true");
			return s;

		}).toArray(String[]::new));

		final String testString = String.join(" ", testStrings);

		if (!testString.equals("")) {
			try {
				parser.parseExpression(testString).getValue(Boolean.class);
			} catch (final ParseException e) {
				response.setStatus("Invalid logic in sell condition");
				return;
			}
		}

		request.addParam("SELL_CONDITION", str);
	}
}
