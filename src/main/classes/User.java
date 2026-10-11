package main.classes;
//账号密码余额vip
public class User {
    String account;
    String password;
    double balance;
    boolean isVip;

    public User(String account, String password, double balance, boolean isVip) {
        this.account = account;
        this.password = password;
        this.balance = balance;
        this.isVip = isVip;
    }

    public String getAccount(){
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }


    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isVip() {
        return isVip;
    }


}
