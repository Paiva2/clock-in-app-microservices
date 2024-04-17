package com.root.employeeservice.strategy.context;

import com.root.employeeservice.strategy.CloneClassStrategy;

public class Cloner {
    private final CloneClassStrategy cloneClassStrategy;

    public Cloner(CloneClassStrategy cloneClassStrategy) {
        this.cloneClassStrategy = cloneClassStrategy;
    }

    public void cloneNonNullProps(Object source, Object target) {
        this.cloneClassStrategy.cloneNonNullProps(source, target);
    }
}
