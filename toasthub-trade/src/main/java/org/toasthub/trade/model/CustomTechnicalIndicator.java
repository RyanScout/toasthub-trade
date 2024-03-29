package org.toasthub.trade.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "ta_custom_technical_indicator")
public class CustomTechnicalIndicator extends TradeBaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private String evaluationPeriod;
    private String technicalIndicatorType;
    private String technicalIndicatorKey;

    private int shortSMAEvaluationDuration;
    private int longSMAEvaluationDuration;
    private int lbbEvaluationDuration;
    private int ubbEvaluationDuration;

    private BigDecimal standardDeviations;

    private Set<Symbol> symbols = new LinkedHashSet<Symbol>();
    private Set<TISnapshot> snapshots = new LinkedHashSet<TISnapshot>();

    @Transient
    private List<String> effectiveSymbols = new ArrayList<String>();
    @Transient
    private List<TechnicalIndicator> technicalIndicators = new ArrayList<TechnicalIndicator>();

    // Setter/Getter

    @OneToMany(mappedBy = "customTechnicalIndicator", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<TISnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(final Set<TISnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    @JsonView({ View.Member.class })
    @Column(name = "technical_indicator_type")
    public String getTechnicalIndicatorType() {
        return technicalIndicatorType;
    }

    public void setTechnicalIndicatorType(final String technicalIndicatorType) {
        this.technicalIndicatorType = technicalIndicatorType;
    }

    @JsonView({ View.Member.class })
    @Column(name = "standard_deviations")
    public BigDecimal getStandardDeviations() {
        return standardDeviations;
    }

    public void setStandardDeviations(final BigDecimal standardDeviations) {
        this.standardDeviations = standardDeviations;
    }

    @JsonView({ View.Member.class })
    @Column(name = "short_sma_evaluation_duration")
    public int getShortSMAEvaluationDuration() {
        return shortSMAEvaluationDuration;
    }

    public void setShortSMAEvaluationDuration(final int shortSMAEvaluationDuration) {
        this.shortSMAEvaluationDuration = shortSMAEvaluationDuration;
    }

    @JsonView({ View.Member.class })
    @Column(name = "long_sma_evaluation_duration")
    public int getLongSMAEvaluationDuration() {
        return longSMAEvaluationDuration;
    }

    public void setLongSMAEvaluationDuration(final int longSMAEvaluationDuration) {
        this.longSMAEvaluationDuration = longSMAEvaluationDuration;
    }

    @JsonView({ View.Member.class })
    @Column(name = "lbb_evaluation_duration")
    public int getLbbEvaluationDuration() {
        return lbbEvaluationDuration;
    }

    public void setLbbEvaluationDuration(final int lbbEvaluationDuration) {
        this.lbbEvaluationDuration = lbbEvaluationDuration;
    }

    @JsonView({ View.Member.class })
    @Column(name = "ubb_evaluation_duration")
    public int getUbbEvaluationDuration() {
        return ubbEvaluationDuration;
    }

    public void setUbbEvaluationDuration(final int ubbEvaluationDuration) {
        this.ubbEvaluationDuration = ubbEvaluationDuration;
    }

    @JsonView({})
    @OneToMany(mappedBy = "customTechnicalIndicator", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(final Set<Symbol> symbols) {
        this.symbols = symbols;
    }

    @JsonView({ View.Member.class })
    @Column(name = "technical_indicator_key")
    public String getTechnicalIndicatorKey() {
        return technicalIndicatorKey;
    }

    public void setTechnicalIndicatorKey(final String technicalIndicatorKey) {
        this.technicalIndicatorKey = technicalIndicatorKey;
    }

    @JsonView({ View.Member.class })
    @Transient
    public List<TechnicalIndicator> getTechnicalIndicators() {
        return technicalIndicators;
    }

    public void setTechnicalIndicators(final List<TechnicalIndicator> technicalIndicators) {
        this.technicalIndicators = technicalIndicators;
    }

    @JsonView({ View.Member.class })
    @Transient
    public List<String> getEffectiveSymbols() {
        return effectiveSymbols;
    }

    public void setEffectiveSymbols(final List<String> effectiveSymbols) {
        this.effectiveSymbols = effectiveSymbols;
    }

    @JsonView({ View.Member.class })
    @Column(name = "evaluation_period")
    public String getEvaluationPeriod() {
        return evaluationPeriod;
    }

    public void setEvaluationPeriod(final String evaluationPeriod) {
        this.evaluationPeriod = evaluationPeriod;
    }

    @JsonView({ View.Member.class })
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
