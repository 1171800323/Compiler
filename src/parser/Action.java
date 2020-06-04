package parser;

public class Action {

    private final String action;
    private final int status;
    private final Production production;
    // ACTION表
    // 如果action = "error", 错误处理
    // 如果action = "acc", 接收
    // 如果action = "reduce" , production非null,  使用production进行规约
    // 如果action = "shift",   status非null, 移入状态status
    // GOTO表
    // 仅为status赋值即可

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

        if ("acc".equals(action)) {
            stringBuilder.append("acc");
        } else if ("reduce".equals(action)) {
            stringBuilder.append("r: ").append(production.toString());
        } else if ("shift".equals(action)) {
            stringBuilder.append("s").append(status);
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
        Action action3 = new Builder().action("error").build();
        System.out.println(action);
        System.out.println(action1);
        System.out.println(action2);
        System.out.println(action3);
    }
}
