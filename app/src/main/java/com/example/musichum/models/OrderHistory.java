package com.example.musichum.models;

import java.util.Date;
import java.util.List;

public class OrderHistory {
    String id;
    String username;
    Date timestamp;
    List<CartItem> cartItems;
}
