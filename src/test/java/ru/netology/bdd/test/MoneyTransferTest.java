package ru.netology.bdd.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.bdd.data.DataHelper;
import ru.netology.bdd.page.DashboardPage;
import ru.netology.bdd.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setUp() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor();
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(0);
        secondCardBalance = dashboardPage.getCardBalance(1);
    }

    @Test
    void shouldTransferMoneyFrom1CardTo2Card()  {
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var expectedBalanceOfFirstCard = firstCardBalance - amount;
        var expectedBalanceOfSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();
        var newBalanceOfFirstCard = dashboardPage.getCardBalance(0);
        var newBalanceOfSecondCard = dashboardPage.getCardBalance(1);
        Assertions.assertAll(() -> assertEquals(expectedBalanceOfFirstCard, newBalanceOfFirstCard),
                () -> assertEquals(expectedBalanceOfSecondCard, newBalanceOfSecondCard));
    }
    @Test
    void shouldTransferMoneyFrom2CardTo1Card()  {
        var amount = DataHelper.generateValidAmount(secondCardBalance);
        var expectedBalanceOfFirstCard = firstCardBalance + amount;
        var expectedBalanceOfSecondCard = secondCardBalance - amount;
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);
        dashboardPage.reloadDashboardPage();
        var newBalanceOfFirstCard = dashboardPage.getCardBalance(0);
        var newBalanceOfSecondCard = dashboardPage.getCardBalance(1);
        Assertions.assertAll(() -> assertEquals(expectedBalanceOfFirstCard, newBalanceOfFirstCard),
                () -> assertEquals(expectedBalanceOfSecondCard, newBalanceOfSecondCard));

    }

    @Test
    void shouldNotTransferIfAmountMoreThenBalance() {
        var amount = DataHelper.generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMassage("Для перевода недостаточно средств на карте");
        var newBalanceOfFirstCard = dashboardPage.getCardBalance(0);
        var newBalanceOfSecondCard = dashboardPage.getCardBalance(1);
        Assertions.assertAll(() -> assertEquals(firstCardBalance, newBalanceOfFirstCard),
                () -> assertEquals(secondCardBalance, newBalanceOfSecondCard));
    }
    @Test
    void shouldNotTransferIfOneToOneCard()  {
        var amount = DataHelper.generateValidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMassage("В полях должны быть указаны разные карты");
        var newBalanceOfFirstCard = dashboardPage.getCardBalance(0);
        var newBalanceOfSecondCard = dashboardPage.getCardBalance(1);
        Assertions.assertAll(() -> assertEquals(firstCardBalance, newBalanceOfFirstCard),
                () -> assertEquals(secondCardBalance, newBalanceOfSecondCard));
    }
}
