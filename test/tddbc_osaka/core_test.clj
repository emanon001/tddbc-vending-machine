(ns tddbc-osaka.core-test
    (:use clojure.test
     tddbc-osaka.core))

(deftest deposit-test
         (testing "1つずつ投入できる"
                  (are [amount]
                       (do
                         (deposit (new-vending) amount)
                         true)
                       10 50 100 500 1000))

         (testing "複数回投入できる"
                  (are [any-amount]
                       (do
                         (reduce deposit
                                 (new-vending) any-amount)
                         true)
                       [10 50] [100 500 1000]))

         (testing "不正な値が投入された場合、例外を返す"
                  (are [amount]
                       (thrown?
                         IllegalArgumentException
                         (deposit (new-vending) amount))
                       1 5)))

(deftest get-total-amount-test
         (testing "一回も投入していない場合、0を取得できる"
                  (is (zero? (get-total-amount (new-vending)))))

         (testing "一回投入した場合、投入金額を取得できる"
                  (are [amount expected]
                       (= (get-total-amount
                            (deposit (new-vending) amount))
                          expected)
                       10 10))

         (testing "複数回投入した場合、投入金額の合計を取得できる"
                  (are [any-amount expected]
                       (= (get-total-amount
                            (reduce deposit (new-vending) any-amount))
                          expected)
                       [10 50] 60)))

(deftest refund-test
         (testing "初期状態で払い戻しを行うと0を取得できる"
                  (let [[amount _] (refund (new-vending))]
                    (is (zero? amount))
                    ))

         (testing "一回投入した状態で払い戻しを行うと投入金額を取得できる"
                  (are [amount expected]
                       (let [[_amount _] (refund (deposit (new-vending) amount))]
                         (= _amount
                            expected))
                       10 10
                       ))

         (testing "複数回投入した状態で払い戻しを行うと投入金額を取得できる"
                  (are [any-amount expected]
                       (let [[amount _]
                             (refund
                               (reduce deposit (new-vending) any-amount))]
                       (= amount expected))
                       [10 10] 20))
         )

(deftest get-juice-stock-test
         (testing "初期状態でコーラの状態を取得できる"
                  (let [s (get-juice-stock (new-vending))]
                    (is (= (count s) 1))
                    (let [{:keys [name price stock]} (first s)]
                      (is (= name "コーラ"))
                      (is (= price 120))
                      (is (= stock 5))))))

(deftest buyable-test
         (testing "初期状態でコーラを購入できるかを取得できる"
                  (is (false? (buyable (new-vending) "コーラ"))))
         (testing "120円投入状態でコーラを購入できる"
                  (is
                    (true?
                      (->
                        (new-vending)
                        (deposit 100)
                        (deposit 10)
                        (deposit 10)
                        (buyable "コーラ"))))))

; vim: lisp
