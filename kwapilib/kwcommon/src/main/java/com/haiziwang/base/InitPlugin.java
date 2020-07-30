package com.haiziwang.base;

public class InitPlugin {

    public static InitPlugin getInstance() {
        return HOLDER.instance;
    }

    private static class HOLDER {
        private static final InitPlugin instance = new InitPlugin();
    }

    public void init() {

    }
}