package com.ecommerce.app.constants

enum class ScreenName (val value: String) {

    //Screens
    FRAGMENT_HOME_TOP_CATEGORY("FragmentHomeTopCategory"),
    FRAGMENT_CATEGORY("FragmentCategory"),
    FRAGMENT_ADDRESS("FragmentAddress"),
    FRAGMENT_CART("FragmentCart"),

    //Events
    ACTION_DELETE_ADDRESS("ActionDeleteAddress"),
    ACTION_EDIT_ADDRESS("ActionEditAddress"),
    ACTION_DEFAULT_ADDRESS("ActionDefaultAddress"),

    ACTION_DELETE_CART("ActionDeleteCart"),

    // Request API's
    REQUEST_ADD_ADDRESS("RequestAddAddress"),
    REQUEST_UPDATE_ADDRESS("RequestUpdateAddress"),

    REQUEST_CART_LIST("RequestCartList"),
    REQUEST_DELETE_CART_ITEM("RequestDeleteCartItem"),

}
