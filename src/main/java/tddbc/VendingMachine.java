package tddbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private final Map<Money, Integer> moneyStock = new HashMap<>();
    private final Map<Juice, Integer> juiceStock = new HashMap<>();
    private int totalMoneyOfDeposit = 0;

    public enum Money {
        TEN(10),
        FIFTY(50),
        ONE_HUNDRED(100),
        FIVE_HUNDRED(500),
        ONE_THOUSAND(1000);

        private final int value;

        Money(int value) {
            this.value = value;
        }

        int toInteger() {
            return this.value;
        }
    }

    public enum Juice {
        COKE("コーラ", 120);

        private final String name;
        private final int price; 

        Juice(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return this.name;
        }
        public int getPrice() {
            return this.price;
        }
    }

    public VendingMachine() {
        initializeMoneyStock();
        initializeJuiceStock();
        replenishJuiceStock(Juice.COKE, 5);
    }

    public void deposit(Money money) {
        this.totalMoneyOfDeposit += money.toInteger();
        int currentStock = this.moneyStock.get(money);
        this.moneyStock.put(money, currentStock + 1);
    }

    public int getTotalMoneyOfDeposit() {
        return this.totalMoneyOfDeposit;
    }

    public void refund() {
        System.out.println(getTotalMoneyOfDeposit());
        this.totalMoneyOfDeposit = 0;
        initializeMoneyStock();
    }
    
    public void replenishJuiceStock(Juice juice, int stock) {
        int currentStock = this.juiceStock.get(juice);
        this.juiceStock.put(juice, currentStock + stock);
    }
    
    public Map<Juice, Integer> getJuiceStock() {
        return Collections.unmodifiableMap(this.juiceStock);
    }
    
    private void initializeMoneyStock() {
        for (Money money : Money.values()) {
            this.moneyStock.put(money, 0);
        }
    }
    
    private void initializeJuiceStock() {
        for (Juice juice : Juice.values()) {
            this.juiceStock.put(juice, 0);
        }
    }
}