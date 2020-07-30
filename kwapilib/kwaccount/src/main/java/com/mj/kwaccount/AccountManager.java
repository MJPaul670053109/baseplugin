package com.mj.kwaccount;


public class AccountManager implements IAccount {


    private String username;

    static IAccount getInstance() {
        return HOLDER.instance;
    }

    private static class HOLDER {
        private static final AccountManager instance = new AccountManager();
    }


    private AccountManager() {
        username = "你好";
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public void saveUserName(String data) {
        this.username = data;
    }

}