package com_demowebshop.steps;

import config.UserCredential;
import io.qameta.allure.Step;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.baseURI;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WebSteps {

    public static UserCredential credential = ConfigFactory.create(UserCredential.class);

    @Step("Открыть браузер и подставить cookie для Wishlist")
    public void setCookieForWishlist() {

        open("/Themes/DefaultClean/Content/images/logo.png");
        getWebDriver().manage().addCookie(new Cookie("Nop.customer", "a239f123-217f-45f0-b290-440e00538aec"));

    }

    @Step("Удалить добавленный товар из Wishlist")
    public void deleteProduct() {

        open("/wishlist");
        $("[name='removefromcart']").click();
        $(".update-wishlist-button").click();
        $(".wishlist-content").shouldHave(text("The wishlist is empty!"));

    }

    @Step("Открыть браузер и подставить cookie Auth")
    public void setCookieForAuth(String cookies) {

        open("/Themes/DefaultClean/Content/images/logo.png");
        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", cookies));

    }

    @Step("Проверить First name, Last name, Email пользователя на странице My account")
    public void checkUserInfo() {


        open("/customer/info");

        String firstNameActual = $("#FirstName").getValue();
        String lastNameActual = $("#LastName").getValue();
        String emailActual = $("#Email").getValue();

        assertThat(firstNameActual).isEqualTo(credential.firstName());
        assertThat(lastNameActual).isEqualTo(credential.lastName());
        assertThat(emailActual).isEqualTo(credential.email());

    }

    @Step("Проверить отображение логина пользователя")
    public void checkUserLogin() {

        open(baseURI);
        $(".account").shouldHave(text(credential.email()));

    }

}
