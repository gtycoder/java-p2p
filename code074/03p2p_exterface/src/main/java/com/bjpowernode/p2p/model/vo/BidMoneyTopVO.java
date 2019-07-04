package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class BidMoneyTopVO implements Serializable {
    private static final long serialVersionUID = 3883804804320572076L;
    private String phone;
    private Double bidMoney;
}
