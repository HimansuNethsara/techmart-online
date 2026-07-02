package com.techmart.jms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertMessage implements Serializable {
    private Integer productId;
    private int currentQuantity;
    private String message;
}