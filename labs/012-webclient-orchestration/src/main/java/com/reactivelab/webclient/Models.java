package com.reactivelab.webclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class User {
    private String id;
    private String username;
    private String email;
    private String preferenceId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Order {
    private String id;
    private String userId;
    private String product;
    private Double amount;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Preference {
    private String id;
    private String theme;
    private boolean notificationsEnabled;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserDashboard {
    private User user;
    private List<Order> orders;
    private Preference preference;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GlobalEvent {
    private String id;
    private String type;
    private String message;
}
