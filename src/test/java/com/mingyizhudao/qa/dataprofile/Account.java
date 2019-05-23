package com.mingyizhudao.qa.dataprofile;

import lombok.Data;

import static com.mingyizhudao.qa.utilities.Generator.randomPhone;

@Data
public class Account {
    private String phone;
    private String state;
    private String token;
    private String code;

    public Account() {
        this.phone = randomPhone();
    }
}

