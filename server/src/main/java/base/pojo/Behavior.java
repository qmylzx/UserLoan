package base.pojo;

import com.google.gson.Gson;

public class Behavior {
    String behavior;
    int count;

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
