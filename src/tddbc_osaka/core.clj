; http://devtesting.jp/tddbc/?TDDBC%E5%A4%A7%E9%98%AA2.0%2F%E8%AA%B2%E9%A1%8C

(ns tddbc-osaka.core
  (:gen-class))

(declare add-sales-amount)

(def initial-money-stock
  (zipmap [10 50 100 500 1000] (repeat 0)))

(defn new-vending
  []
  {:money-stock initial-money-stock
   :sales-amount 0
   :juice-stock [{:name "コーラ" :price 120 :stock 5}]})

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

(defn set-juice-stock-of
  [machine name juice-stock]
  (assoc machine :juice-stock
         (conj (remove (fn [{n :name}] (= n name)) (machine :juice-stock))
               juice-stock)))

(defn add-juice-stock
  [machine name amount]
  (let [juice-stock (get-juice-stock-of machine name)]
    (set-juice-stock-of
      machine
      name
      (assoc juice-stock :stock (+ (juice-stock :stock) amount)))))

(defn buyable
  [machine name]
  (<= (:price (get-juice-stock-of machine name))
      (get-total-amount machine)))

(defn buy
  [machine name]
  (let [juice-price ((get-juice-stock-of machine name) :price)
        total-amount (get-total-amount machine)]
    (if (> juice-price total-amount)
      machine
      (-> machine
        (add-sales-amount juice-price)
        (add-juice-stock name -1)))))

(defn get-juice-stock-of
  [machine name]
  (first (filter (fn [{n :name}] (= n name)) (machine :juice-stock))))

(defn add-sales-amount
  [machine amount]
  (assoc machine :sales-amount
         (+ (machine :sales-amount) amount)))

