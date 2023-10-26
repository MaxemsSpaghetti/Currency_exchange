package maxim.butenko.model;

import java.util.Objects;

public class ExchangeRate {

    private long id;

    private int baseCurrencyId;

    private int targetCurrencyId;

    private double rate;

    public ExchangeRate() {
    }

    public ExchangeRate(long id, int baseCurrencyId, int targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public long getId() {
        return this.id;
    }

    public int getBaseCurrencyId() {
        return this.baseCurrencyId;
    }

    public int getTargetCurrencyId() {
        return this.targetCurrencyId;
    }

    public double getRate() {
        return this.rate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public void setTargetCurrencyId(int targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRate that = (ExchangeRate) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return "ExchangeRate(id=" + this.getId() + ", baseCurrencyId=" + this.getBaseCurrencyId() + ", targetCurrencyId=" + this.getTargetCurrencyId() + ", rate=" + this.getRate() + ")";
    }
}
