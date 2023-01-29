public class ATM {

    private Bank bank;

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public String getPerson(Account account){
        User person = bank.getUser(account.getCreditCard().getIdNumber());
        return person.getFirstName() + " " + person.getLastName();
    }

    public String attemptLogin(Account account){
        if (account.getCreditCard().getPin().equals(account.getPinInput())){
            return "Logged in successfully";
        }
        return loginTries(account);
    }

    public String loginTries(Account account) {
        int attempts = bank.attemptedLoginTries(account.getCreditCard().getIdNumber());

        if(attempts == 1){
            //attempts = attempts +1;
            return "1/3 wrong attempts";
        }
        else if(attempts == 2){
            //attempts = attempts +1;
            return "2/3 wrong attempts";
        }
        else if (attempts == 3){
            //attempts = attempts +1;
            return "3/3 wrong attempts, make sure last one is correct";
        }
        else{
            return "too many attempts, you will be blocked";
        }
    }

    public String cardStatus(Account account){
        if(bank.creditCardStatus(account.getCreditCard().getIdNumber())){
            return "Login Accessed";
        }
        else{
            return "Login Blocked";
        }
    }

    public int getAmount(Account account){
        if(account.getCreditCard().isAccessedLoggedIn())
            return bank.getMoney(account.getCreditCard().getIdNumber());
        else{
            return 0;
        }
    }

    public void deposit(Account account, Integer amountToAdd){
        bank.deposit(account.setDeposit(amountToAdd));
    }

    public String withdraw(Account account){
        if (getAmount(account) >= account.getWithdraw()){
            bank.withdrawMoney(account.getWithdraw());
            return "Amount withdrawn: " + account.getWithdraw();
        }
        else{
            return "You don't have enough money on your account to withdraw that amount, try again";
        }
    }

    public String getBank(Account account){
        return bank.getBank(account.getCreditCard().getIdNumber());
    }


}