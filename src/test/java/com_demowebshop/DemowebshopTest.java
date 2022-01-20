package com_demowebshop;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DemowebshopTest extends TestBase {

    @Test
    @DisplayName("Добавление товара в Wishlist без использования cookie")
    void addToWishlistWithoutCookie() {

        String data = "addtocart_14.EnteredQuantity=2"; // добавляем в Wishlist товар в кол-ве 2 штук

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body(data)
                .when()
                .post("/addproducttocart/details/14/2")
                .then()
                .statusCode(200)
                .body("success", is(true),
                        "message", is("The product has been added to your \u003ca href=\"/wishlist\"\u003ewishlist\u003c/a\u003e"),
                        "updatetopwishlistsectionhtml", is("(2)"));

    }

    @Test
    @DisplayName("Добавление товара в Wishlist c использованием cookie")
    void addToWishlistWithCookie() {

        String data = "addtocart_43.EnteredQuantity=1";

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .cookie("Nop.customer=fc65de54-c983-4252-a666-e993e9ccc699; " +
                        "ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8;")
                .body(data)
                .when()
                .post("/addproducttocart/details/43/2")
                .then()
                .statusCode(200)
                .body("success", is(true),
                        "message",
                        is("The product has been added to your \u003ca href=\"/wishlist\"\u003ewishlist\u003c/a\u003e"),
                        "updatetopwishlistsectionhtml", notNullValue());

    }

    @Test
    @DisplayName("Удаление товара из Wishlist c использованием cookie (API+UI)")
    void updateWishlistWithCookie() {

        String cookie = "Nop.customer=fc65de54-c983-4252-a666-e993e9ccc699; " +
                "ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8;";

        step("Добавить товар в Wishlist", () -> {

            String data = "addtocart_43.EnteredQuantity=1";

            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .cookie(cookie)
                    .body(data)
                    .when()
                    .post("/addproducttocart/details/43/2")
                    .then()
                    .statusCode(200)
                    .body("success", is(true),
                            "message",
                            is("The product has been added to your \u003ca href=\"/wishlist\"\u003ewishlist\u003c/a\u003e"),
                            "updatetopwishlistsectionhtml", notNullValue());
        });

        step("Открыть браузер и подставить cookie", () -> {
            open("/Themes/DefaultClean/Content/images/logo.png");
            getWebDriver().manage().addCookie(new Cookie("Nop.customer", "fc65de54-c983-4252-a666-e993e9ccc699"));

        });

        step("Удалить добавленный товар из Wishlist", () -> {

            open("/wishlist");
            $("[name='removefromcart']").click();
            $(".update-wishlist-button").click();
            $(".wishlist-content").shouldHave(text("The wishlist is empty!"));

        });
    }

    @Test
    @Disabled
    @DisplayName("Проверка функции поиска")
    void searchStore() {

        String valueSearch = "book";

        given()
                .when()
                .get("/search?q=" + valueSearch)
                .then()
                .statusCode(200);

    }

    @Test
    @DisplayName("Проверка информации в аккаунте")
    void checkProfile() {

        String login = "TeddyTest@gmail.com";
        String password = "12345678";

        step("Авторизоваться на сайте demowebshop.tricentis.com ", () -> {

            String authCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", login)
                            .formParam("Password", password)
                            .when()
                            .post("login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");


            step("Открыть браузер и подставить cookie", () -> {

                open("/Themes/DefaultClean/Content/images/logo.png");
                getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authCookie));

            });

        });

        step("Зайти на страницу My account, проверить First name, Last name, Email пользователя", () -> {

            String firstName = "Teddy";
            String lastName = "Test";
            String email = "TeddyTest@gmail.com";

            open("/customer/info");

            String firstNameActual = $("#FirstName").getValue();
            String lastNameActual = $("#LastName").getValue();
            String emailActual = $("#Email").getValue();

            assertThat(firstNameActual).isEqualTo(firstName);
            assertThat(lastNameActual).isEqualTo(lastName);
            assertThat(emailActual).isEqualTo(email);

        });
    }

    @Test
    @DisplayName("Смена пароля и авторизация с новым паролем")
    void changePassword() {

        String cookie = "Nop.customer=fc65de54-c983-4252-a666-e993e9ccc699; " +
                "ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8;";

        String login = "TeddyTest@gmail.com";
        String password = "12345678";
        String newPassword = "123456789";

        step("Поменять пароль", () -> {

            step("Авторизоваться на сайте demowebshop.tricentis.com ", () -> {

                String requestCookie =
                        given()
                                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                                .formParam("Email", login)
                                .formParam("Password", password)
                                .when()
                                .post("login")
                                .then()
                                .statusCode(302)
                                .extract()
                                .cookie("__RequestVerificationToken, NOPCOMMERCE.AUTH");


                String data = "__RequestVerificationToken:" + requestCookie + "OldPassword:"+ password +
                        " NewPassword:" + newPassword +
                        " ConfirmNewPassword:" + newPassword;
                given()
                        .contentType("application/x-www-form-urlencoded")
                        .cookie(requestCookie)
                        .body(data)
                        .when()
                        .post("/customer/changepassword");

            });
        });

        step("Авторизация с новым паролем", () -> {

                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", login)
                            .formParam("Password", newPassword)
                            .when()
                            .post("login")
                            .then()
                            .statusCode(302);
                });

            step("Проверка успешной авторизации с новым паролем (API+UI)", () -> {

                String authCookie =
                        given()
                                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                                .formParam("Email", login)
                                .formParam("Password", newPassword)
                                .when()
                                .post("login")
                                .then()
                                .statusCode(302)
                                .extract()
                                .cookie("NOPCOMMERCE.AUTH");


                step("Открыть браузер и подставить cookie", () -> {

                    open("/Themes/DefaultClean/Content/images/logo.png");
                    getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authCookie));

                });

                step("Проверить, что на главной странице отображается логин пользователя", () -> {

                    open(baseURI);
                    $(".account").shouldHave(text(login));

                });

            });

        }

    }
