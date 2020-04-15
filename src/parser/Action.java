package parser;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private final String action;
    private final int status;
    private final Production production;
    // 如果action = "reduce" , production非null, status为null, 对应规约动作
    // 如果action = "shift",   production为null, status非null, 对应移入动作
    // 如果action = null,      production为null, status非null

    private Action(Builder builder) {
        status = builder.status;
        action = builder.action;
        production = builder.production;
    }

    public int getStatus() {
        return status;
    }

    public Production getProduction() {
        return production;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if ("reduce".equals(action)) {
            stringBuilder.append("r: " + production.toString());
        } else if ("shift".equals(action)) {
            stringBuilder.append("s: " + status);
        } else {
            stringBuilder.append(status);
        }
        return stringBuilder.toString();
    }

    public static class Builder {
        private int status;
        private String action;
        private Production production;

        public Builder status(int val) {
            this.status = val;
            return this;
        }

        public Builder action(String val) {
            this.action = val;
            return this;
        }

        public Builder production(Production val) {
            this.production = val;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }

    public static void main(String[] args) {
        Action action = new Builder().status(1).build();
        Action action1 = new Builder().action("shift").status(2).build();
        Action action2 = new Builder().action("reduce").production(new Production("S", new String[]{"P"})).build();
        System.out.println(action);
        System.out.println(action1);
        System.out.println(action2);

    }
}
