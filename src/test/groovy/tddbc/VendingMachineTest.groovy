package tddbc

import spock.lang.Specification
import tddbc.VendingMachine.Money;
import tddbc.VendingMachine.Juice;

class VendingMachineTest extends Specification {
    def VendingMachine vendingMachine
    def _baos
    def _out

    def static final TEN = Money.TEN
    def static final FIFTY = Money.FIFTY
    def static final ONE_HUNDRED = Money.ONE_HUNDRED
    def static final FIVE_HUNDRED = Money.FIVE_HUNDRED
    def static final ONE_THOUSAND = Money.ONE_THOUSAND

    def setup() {
        vendingMachine = new VendingMachine()
        _baos = new ByteArrayOutputStream()
        _out = System.out
        System.setOut(new PrintStream(new BufferedOutputStream(_baos)))
    }

    def cleanup() {
        System.setOut(_out)
    }

    def "お金を一つずつ投入できる"() {
        when:
        vendingMachine.deposit(money)

        then:
        notThrown(Exception)

        where:
        money << [TEN, FIFTY, ONE_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND]
    }

    def "お金を複数回投入できる"() {
        when:
        anyMoney.each {
            vendingMachine.deposit it
        }

        then:
        notThrown(Exception)

        where:
        anyMoney << [[TEN, FIFTY], [TEN, FIFTY, ONE_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND]]
    }

    def "一回もお金を投入していない場合、投入金額として 0(円)を取得できる"() {
        expect:
        vendingMachine.getTotalMoneyOfDeposit() == expected

        where:
        expected = 0
    }

    def "一回だけお金を投入した場合、投入金額を取得できる"() {
        when:
        vendingMachine.deposit money

        then:
        vendingMachine.getTotalMoneyOfDeposit() == expected

        where:
        money        | expected
        TEN          | 10
        FIFTY        | 50
        ONE_HUNDRED  | 100
        FIVE_HUNDRED | 500
        ONE_THOUSAND | 1000
    }

    def "複数回お金を投入した場合、投入金額の総計を取得できる"() {
        when:
        anyMoney.each {
            vendingMachine.deposit it
        }

        then:
        vendingMachine.getTotalMoneyOfDeposit() == expected

        where:
        anyMoney                     | expected
        [TEN, FIFTY, ONE_HUNDRED]    | 160
        [FIVE_HUNDRED, ONE_THOUSAND] | 1500
    }

    def "複数回同じお金を投入した場合、投入金額の総計を取得できる"() {
        when:
        anyMoney.each {
            vendingMachine.deposit it
        }

        then:
        vendingMachine.getTotalMoneyOfDeposit() == expected

        where:
        anyMoney      | expected
        [TEN, TEN]    | 20
    }

    def "一回もお金を投入していない場合、払い戻しを行うと 0(円)がお釣りとして出力される"() {
        when:
        vendingMachine.refund()

        then:
        getActualOutput() == expected

        where:
        expected = toExpectedOutput([0])
    }

    def "一回だけお金を投入した場合、払い戻しを行うと投入金額がお釣りとして出力される"() {
        when:
        vendingMachine.deposit money
        vendingMachine.refund()

        then:
        getActualOutput() == expected

        where:
        money    | expected
        TEN      | toExpectedOutput([10])
    }

    def "複数回お金を投入した場合、払い戻しを行うと投入金額の総計がお釣りとして出力される"() {
        when:
        anyMoney.each {
            vendingMachine.deposit it
        }
        vendingMachine.refund()

        then:
        getActualOutput() == expected

        where:
        anyMoney                     | expected
        [TEN, FIFTY, ONE_HUNDRED]    | toExpectedOutput([160])
        [FIVE_HUNDRED, ONE_THOUSAND] | toExpectedOutput([1500])
    }

    def "払い戻しを行うと投入金額の総計が 0(円)になる"() {
        when:
        anyMoney.each {
            vendingMachine.deposit it
        }
        vendingMachine.refund()

        then:
        vendingMachine.getTotalMoneyOfDeposit() == 0

        where:
        anyMoney << [[TEN, FIFTY, ONE_HUNDRED], [FIVE_HUNDRED, ONE_THOUSAND]]
    }

    def "初期状態で格納されているジュースの情報を取得できる"() {
        expect:
        def juiceStock = vendingMachine.getJuiceStock()
        juice.getName() == name
        juice.getPrice() == price
        juiceStock.get(juice) == stock
        
        where:
        juice      | name    | price | stock
        Juice.COKE | "コーラ" | 120   | 5
    }

    def "ジュースを補充できる"() {
        when:
        vendingMachine.replenishJuiceStock(Juice.COKE, 3)
        
        then:
        def juiceStock = vendingMachine.getJuiceStock()
        juice.getName() == name
        juice.getPrice() == price
        juiceStock.get(juice) == stock
        
        where:
        juice      | name    | price | stock
        Juice.COKE | "コーラ" | 120   | 8
    }
    
    def getActualOutput() {
        System.out.flush()
        _baos.toString()
    }

    def toExpectedOutput(list) {
        def sep = System.properties['line.separator']
        list.join(sep) + sep
    }
}