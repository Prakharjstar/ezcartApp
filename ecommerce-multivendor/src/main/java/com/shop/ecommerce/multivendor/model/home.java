package com.shop.ecommerce.multivendor.model;

import jakarta.persistence.Entity;
import lombok.*;
import java.util.*;

@Data
public class home {

    private List<HomeCategory> gird;

    private List<HomeCategory> shopByCategories;

    private List<HomeCategory> electricCategories;

    private List<HomeCategory> dealCategories;

    private List<Deal> deals;

}
