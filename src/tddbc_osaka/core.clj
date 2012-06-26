(ns tddbc-osaka.core
  (:gen-class))

(def initial-money-stock
     (zipmap [10 50 100 500 1000] (repeat 0)))

(defn new-vending
  []
  {:money-stock initial-money-stock})

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

; vim: lisp
