package ru.netology.bdd.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.bdd.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    public final SelenideElement transferButton = $("[data-test-id='action-transfer']");
    public final SelenideElement amountInput = $("[data-test-id='amount'] input");
    public final SelenideElement fromInput = $("[data-test-id='from'] input");
    public final SelenideElement transferHead = $(byText("Пополнение карты"));
    public final SelenideElement errorMassage = $("[data-test-id='error-notification'] .notification__content");


    public TransferPage() {
        transferHead.shouldBe(Condition.visible);
    }

    public DashboardPage makeValidTransfer(String amountToTransfer, DataHelper.CardInfo cardInfo) {
        makeTransfer(amountToTransfer, cardInfo);
        return new DashboardPage();
    }

    public void makeTransfer(String amountTransfer, DataHelper.CardInfo cardInfo) {
        amountInput.setValue(amountTransfer);
        fromInput.setValue(cardInfo.getCardNumber());
        transferButton.click();
    }


    public void findErrorMassage(String expectedText) {
        errorMassage.shouldHave(Condition.text(expectedText), Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }
}
