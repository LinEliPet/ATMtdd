import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class ATMTest {
    private Bank bank;
    private ATM atm;
    private ArgumentCaptor<Integer>depositCaptor;
    private ArgumentCaptor<Integer>withdrawCaptor;

    @BeforeEach
    public void setUp(){
        bank = mock(Bank.class);
        atm = new ATM(bank);
        depositCaptor = ArgumentCaptor.forClass(Integer.class);
        withdrawCaptor = ArgumentCaptor.forClass(Integer.class);
    }

    private static List<Arguments> testData(){
        return Arrays.asList(
                Arguments.of("1234 1234 1234 1234", "Oliver","Man", "0613"),
                Arguments.of("1111 2222 3333 4444", "Linda","Kvinna", "0415")
        );
    }

    /*
        Du har i uppgift att bygga en bankautomat, men du har inte tillgång till banken. Du får helt enkelt
    använda Mockito för att mocka banken för denna uppgift. Utveckla med Test Driven Development.
     */

    @ParameterizedTest
    @MethodSource("testData")
    public void whenUserInsertCard_UseIdToFetchUser(String cardId, String firstName, String lastName){
        Account account = new Account(new CreditCard(cardId,"",""));
        when(bank.getUser(cardId)).thenReturn(new User(firstName, lastName));

        String expected = firstName + " " + lastName;
        String actual = atm.getPerson(account);

        assertEquals(expected, actual);
    }

    @Test
    public void userPin_ifCorrectPinLogin(){
    }

    @Test
    public void shouldViewAmountOfMoneyOnCreditCard(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "");
        creditCard.setAccessedLoggedIn(true);
        Account account = new Account(creditCard);
        int moneyInBank = 45000;
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);
        int expected = moneyInBank;
        int actual = atm.getAmount(account);

        assertEquals(expected, actual);

    }

    @Test
    public void shouldDepositCorrectAmountToCreditCard(){
        CreditCard creditCard = new CreditCard("9999 8888 7777 6666", "", "");
        creditCard.setAccessedLoggedIn(true);
        int depositAmount = 2000;
        int moneyInBank = 45000;
        Account account = new Account(creditCard, depositAmount);
        when(bank.getMoney("9999 8888 7777 6666")).thenReturn(moneyInBank);
        atm.deposit(account, depositAmount);
        verify(bank,times(1)).deposit(depositCaptor.capture());
        assertEquals(depositAmount, depositCaptor.getValue());


    }

    @Test
    public void shouldWithdrawCorrectAmountFromCreditCardIfMoreMoneyIsInBank(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "4444");
        creditCard.setAccessedLoggedIn(true);
        int withdrawAmount = 1000;
        int moneyInBank = 1400;
        Account account = new Account(creditCard, "4444", withdrawAmount,0);
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);

        atm.withdraw(account);
        verify(bank,times(1)).withdrawMoney(withdrawCaptor.capture());
        assertEquals(withdrawAmount, withdrawCaptor.getValue());
    }

    @Test
    public void shouldNotBeAbleToWithdrawBecauseLessMoneyIsInBank(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "");
        creditCard.setAccessedLoggedIn(true);
        int withdrawAmount = 1500;
        int moneyInBank = 1000;
        Account account = new Account(creditCard, withdrawAmount);
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);
        atm.withdraw(account);
        verify(bank,times(1)).withdrawMoney(withdrawCaptor.capture());
        assertNotEquals(withdrawAmount, withdrawCaptor.getValue());
    }

    @Test
    public void shouldAddOneFailedLoginAttempt(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "hej", "1234");
        String wrongPIN = "1111";
        Account account = new Account(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(1);
        String expected = "1/3 wrong attempts";
        String actual = atm.attemptLogin(account);
        assertEquals(expected, actual);
    }
    @Test
    public void shouldAddTreeFailedLoginAttempts(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "hej", "1234");
        String wrongPIN = "1111";
        Account account = new Account(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(3);
        String expected = "3/3 wrong attempts, make sure last one is correct";
        String actual = atm.attemptLogin(account);
        assertEquals(expected, actual);
    }
    @Test
    public void shouldBlockCardWhenMoreThanThreeLoginAttempts(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "hej", "1234");
        String wrongPIN = "1111";
        Account account = new Account(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(4);
        String expected = "too many attempts, you will be blocked";
        String actual = atm.attemptLogin(account);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldAllowAccessToCreditCard(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "hej", "1234");
        Account account = new Account(creditCard);
        boolean status = bank.creditCardStatus("1234 1234 1234 1234");
        when(status).thenReturn(true);
        String expected = "Login Accessed";
        String actual = atm.cardStatus(account);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotAllowAccessToCreditCard(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "hej", "1234");
        Account account = new Account(creditCard);
        boolean status = bank.creditCardStatus("1234 1234 1234 1234");
        when(status).thenReturn(false);
        String expected = "Login Blocked";
        String actual = atm.cardStatus(account);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnBankName(){
        CreditCard creditCard = new CreditCard("2222 2222 2222 2222", "", "");
        Account account = new Account(creditCard);
        when(atm.getBank(account)).thenReturn("Ikano Bank");
        String expected = "Ikano Bank";
        String actual = atm.getBank(account);
        assertEquals(expected, actual);
    }

}