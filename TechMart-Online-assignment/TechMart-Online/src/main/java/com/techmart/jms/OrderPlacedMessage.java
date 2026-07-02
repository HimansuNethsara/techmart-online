package com.techmart.jms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedMessage implements Serializable {

    private Integer orderId;
    private List<LineItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem implements Serializable {
        private Integer productId;
        private int quantity;
    }
}