; http://devtesting.jp/tddbc/?TDDBC%E5%A4%A7%E9%98%AA2.0%2F%E8%AA%B2%E9%A1%8C

(ns tddbc-osaka.core
  (:gen-class))

(def initial-money-stock
  (zipmap [10 50 100 500 1000] (repeat 0)))

(defn new-vending
  []
  {:money-stock initial-money-stock
   :sales-amount 0})

(defn- invalid-amount?
  [machine amount]
  (nil? ((set (keys (machine :money-stock))) amount)))

(defn deposit
  [machine amount]
  (when (invalid-amount? machine amount)
    (throw (IllegalArgumentException.)))
  (let [v (inc ((machine :money-stock) amount))
        m (assoc (machine :money-stock) amount v)]
    (assoc machine :money-stock m)))

(defn get-total-amount
  [machine]
  (reduce
    (fn [s [v c]] (+ s (* v c)))
    0
    (machine :money-stock)))

(defn refund
  [machine]
  [(get-total-amount machine)
   (assoc machine :money-stock initial-money-stock)])

(defn get-juice-stock
  [machine]
  [{:name "コーラ" :price 120 :stock 5}])

(defn buyable
  [machine name]
  (<= (:price (first (filter (fn [{n :name}] (= n name)) (get-juice-stock machine))))
      (get-total-amount machine)))

(defn buy
  [machine name]
  (let [current-sales (machine :sales-amount)
        juice-price ((get-juice-stock-of machine name) :price)]
    (assoc machine :sales-amount
           (+ current-sales juice-price))))

(defn get-juice-stock-of
  [machine name]
  {:name "コーラ" :price 120 :stock 4})

