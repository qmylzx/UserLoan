package base.pojo;

import com.google.gson.Gson;

public class MoneyAndTime {
    private float money;
    private String index;

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
