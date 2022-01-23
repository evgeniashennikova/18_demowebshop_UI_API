package com_demowebshop;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DemowebshopTest extends TestBase {

    @Test
    @DisplayName("Добавление товара в Wishlist без использования cookie")
    void addToWishlistWithoutCookie() {

        apiSteps.addProductToWishlist();

    }

    @Test
    @DisplayName("Добавление товара в Wishlist c использованием cookie")
    void addToWishlistWithCookie() {

        apiSteps.addProductToWishlistWithCookie();

    }

    @Test
    @DisplayName("Проверка функции поиска")
    void searchStore() {

        apiSteps.checkSearchStore();

    }

    @Test
    @DisplayName("Удаление товара из Wishlist c использованием cookie (API+UI)")
    void updateWishlistWithCookie() {

        apiSteps.addProductToWishlistWithCookie();
        webSteps.setCookieForWishlist();
        webSteps.deleteProduct();

    }

    @Test
    @DisplayName("Проверка информации в аккаунте")
    void checkProfile() {

       String cookies = apiSteps.authWebsite().cookie("NOPCOMMERCE.AUTH");
       webSteps.setCookieForAuth(cookies);
       webSteps.checkUserInfo();

    }

    @Test
    @Disabled
    // тест не работает, т.к. в body для запроса __RequestVerificationToken генерируется каждый раз рандомно, не нашла откуда его вытащить
    @DisplayName("Смена пароля и авторизация с новым паролем")
    void changePassword() {

        apiSteps.authWebsite();
        apiSteps.replacementPassword();
        String cookies = apiSteps.authWebsiteNewPass().cookie("NOPCOMMERCE.AUTH");
        webSteps.setCookieForAuth(cookies);
        webSteps.checkUserLogin();

    }

}
