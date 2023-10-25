package maxim.butenko.model;

import java.util.Objects;

public class ExchangeRate {

    private long id;

    private String baseCurrencyId;

    private String targetCurrencyId;

    private double rate;

    public ExchangeRate() {
    }

    public ExchangeRate(long id, String baseCurrencyId, String targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public long getId() {
        return this.id;
    }

    public String getBaseCurrencyId() {
        return this.baseCurrencyId;
    }

    public String getTargetCurrencyId() {
        return this.targetCurrencyId;
    }

    public double getRate() {
        return this.rate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBaseCurrencyId(String baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public void setTargetCurrencyId(String targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExchangeRate other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$baseCurrencyId = this.getBaseCurrencyId();
        final Object other$baseCurrencyId = other.getBaseCurrencyId();
        if (!Objects.equals(this$baseCurrencyId, other$baseCurrencyId))
            return false;
        final Object this$targetCurrencyId = this.getTargetCurrencyId();
        final Object other$targetCurrencyId = other.getTargetCurrencyId();
        if (!Objects.equals(this$targetCurrencyId, other$targetCurrencyId))
            return false;
        return Double.compare(this.getRate(), other.getRate()) == 0;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ExchangeRate;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $baseCurrencyId = this.getBaseCurrencyId();
        result = result * PRIME + ($baseCurrencyId == null ? 43 : $baseCurrencyId.hashCode());
        final Object $targetCurrencyId = this.getTargetCurrencyId();
        result = result * PRIME + ($targetCurrencyId == null ? 43 : $targetCurrencyId.hashCode());
        final long $rate = Double.doubleToLongBits(this.getRate());
        result = result * PRIME + (int) ($rate >>> 32 ^ $rate);
        return result;
    }

    public String toString() {
        return "ExchangeRate(id=" + this.getId() + ", baseCurrencyId=" + this.getBaseCurrencyId() + ", targetCurrencyId=" + this.getTargetCurrencyId() + ", rate=" + this.getRate() + ")";
    }
}
