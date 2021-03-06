/*
 * Copyright (C) 2020 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Edward H. Seufert
 */

package org.toasthub.trade.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "ta_trade")
public class Trade extends TradeBaseEntity {

	private static final long serialVersionUID = 1L;
	public static final String BOT = "Bot";
	public static final String BUY = "Buy";
	public static final String SELL = "Sell";
	public static final String[] SUPPORTED_ORDER_SIDES = {
			BOT, BUY, SELL
	};
	public static final String MARKET = "Market";
	public static final String TRAILING_STOP = "Trailing Stop";
	public static final String PROFIT_LIMIT = "Profit Limit";
	public static final String TRAILING_STOP_PROFIT_LIMIT = "Trailing Stop and Profit Limit";
	public static final String[] SUPPORTED_ORDER_TYPES = {
			MARKET, TRAILING_STOP, PROFIT_LIMIT, TRAILING_STOP_PROFIT_LIMIT
	};
	
	private String name;
	private String symbol;
	private String orderType;
	private String orderSide;
	private String status;
	private String evaluationPeriod;
	private String currencyType;
	private BigDecimal currencyAmount;
	private String buyCondition = "";
	private String parseableBuyCondition = "";
	private String sellCondition = "";
	private String parseableSellCondition = "";
	private String trailingStopType;
	private String profitLimitType;
	private BigDecimal trailingStopAmount;
	private BigDecimal profitLimitAmount;
	private String iterations;
	private int iterationsExecuted = 0;
	private BigDecimal budget;
	private BigDecimal availableBudget;
	private BigDecimal sharesHeld = BigDecimal.ZERO;
	private BigDecimal totalValue;
	private long firstOrder = 0;
	private long lastOrder = 0;
	
	private Set<TradeDetail> tradeDetails = new LinkedHashSet<TradeDetail>();
	
	// Constructors
	public Trade() {
		super();
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setIdentifier("Trade");
	}
	public Trade(String code, Boolean defaultLang, String dir) {
		super();
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setIdentifier("Trade");
	}
		
	// Setter/Getter
	@JsonView({View.Member.class})
	@Column(name = "last_order")
	public long getLastOrder() {
		return lastOrder;
	}
	public void setLastOrder(long lastOrder) {
		this.lastOrder = lastOrder;
	}

	@JsonView({View.Member.class})
	@Column(name = "first_order")
	public long getFirstOrder() {
		return firstOrder;
	}
	public void setFirstOrder(long firstOrder) {
		this.firstOrder = firstOrder;
	}
	
	@JsonView({View.Member.class})
	@Column(name = "parseable_sell_condition")
	public String getParseableSellCondition() {
		return parseableSellCondition;
	}
	public void setParseableSellCondition(String parseableSellCondition) {
		this.parseableSellCondition = parseableSellCondition;
	}

	@JsonView({View.Member.class})
	@Column(name = "parseable_buy_condition")
	public String getParseableBuyCondition() {
		return parseableBuyCondition;
	}
	public void setParseableBuyCondition(String parseableBuyCondition) {
		this.parseableBuyCondition = parseableBuyCondition;
	}

	@JsonView({View.Member.class})
	@Column(name = "iterations_executed")
	public int getIterationsExecuted() {
		return iterationsExecuted;
	}
	public void setIterationsExecuted(int iterationsExecuted) {
		this.iterationsExecuted = iterationsExecuted;
	}

	@JsonView({View.Member.class})
	@Column(name = "iterations")
	public String getIterations() {
		return iterations;
	}
	public void setIterations(String iterations) {
		this.iterations = iterations;
	}

	@JsonView({View.Member.class})
	@Column(name = "total_value")
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	@JsonView({View.Member.class})
	@OneToMany(mappedBy = "trade", cascade = CascadeType.ALL)
	public Set<TradeDetail> getTradeDetails() {
		return tradeDetails;
	}
	public void setTradeDetails(Set<TradeDetail> tradeDetails) {
		this.tradeDetails = tradeDetails;
	}

	@JsonView({View.Member.class})
	@Column(name = "evaluation_period")
	public String getEvaluationPeriod() {
		return evaluationPeriod;
	}
	public void setEvaluationPeriod(String evaluationPeriod) {
		this.evaluationPeriod = evaluationPeriod;
	}

	@JsonView({View.Member.class})
	@Column(name = "shares_held")
	public BigDecimal getSharesHeld() {
		return sharesHeld;
	}
	public void setSharesHeld(BigDecimal sharesHeld) {
		this.sharesHeld = sharesHeld;
	}

	@JsonView({View.Member.class})
	@Column(name = "budget")
	public BigDecimal getBudget() {
		return budget;
	}
	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	@JsonView({View.Member.class})
	@Column(name = "available_budget")
	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}
	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	@JsonView({View.Member.class})
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonView({View.Member.class})
	@Column(name = "symbol")
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@JsonView({View.Member.class})
	@Column(name = "profit_limit_amount")
	public BigDecimal getProfitLimitAmount() {
		return profitLimitAmount;
	}
	public void setProfitLimitAmount(BigDecimal profitLimitAmount) {
		this.profitLimitAmount = profitLimitAmount;
	}

	@JsonView({View.Member.class})
	@Column(name = "trailing_stop_amount")
	public BigDecimal getTrailingStopAmount() {
		return trailingStopAmount;
	}
	public void setTrailingStopAmount(BigDecimal trailingStopAmount) {
		this.trailingStopAmount = trailingStopAmount;
	}

	@JsonView({View.Member.class})
	@Column(name = "profit_limit_type")
	public String getProfitLimitType() {
		return profitLimitType;
	}
	public void setProfitLimitType(String profitLimitType) {
		this.profitLimitType = profitLimitType;
	}

	@JsonView({View.Member.class})
	@Column(name = "trailing_stop_type")
	public String getTrailingStopType() {
		return trailingStopType;
	}
	public void setTrailingStopType(String trailingStopType) {
		this.trailingStopType = trailingStopType;
	}

	@JsonView({View.Member.class})
	@Column(name = "currency_type")
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	@JsonView({View.Member.class})
	@Column(name = "sell_condition")
	public String getSellCondition() {
		return sellCondition;
	}
	public void setSellCondition(String sellCondition) {
		this.sellCondition = sellCondition;
	}

	@JsonView({View.Member.class})
	@Column(name = "buy_condition")
	public String getBuyCondition() {
		return buyCondition;
	}
	public void setBuyCondition(String buyCondition) {
		this.buyCondition = buyCondition;
	}

	@JsonView({View.Member.class})
	@Column(name = "currency_amount")
	public BigDecimal getCurrencyAmount() {
		return currencyAmount;
	}
	public void setCurrencyAmount(BigDecimal currencyAmount) {
		this.currencyAmount = currencyAmount;
	}

	@JsonView({View.Member.class})
	@Column(name = "order_side")
	public String getOrderSide() {
		return orderSide;
	}
	public void setOrderSide(String orderSide) {
		this.orderSide = orderSide;
	}

	@JsonView({View.Member.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JsonView({View.Member.class})
	@Column(name = "order_type")
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}
