(ns constraint-compiler.core-test
  (:require [clojure.test :refer :all]
            [constraint-compiler.core :refer :all]))

(deftest primitive-tests
  (testing "single property test"
    (let [pred (create-pred {:firstName "John"})]
      (is (true? (pred {:firstName "John" :lastName "Doe"})))
      (is (false? (pred {:lastName "John"})))
      (is (false? (pred {:firstName "not John"})))
      (is (false? (pred {})))))

  (testing "value operators"
    ; lt|le|gt|ge|ne on values ranging from 0 to 4 and a string
    (let [expected {:$lt [0 1] :$le [0 1 2] :$ge [2 3 4] :$gt [3 4]}]
      (doseq [op (keys expected)]
        (is (= (expected op)
               (->> (conj (range 5) "foo")
                    (map #(hash-map :property %))
                    (filter (create-pred {:property {op 2}}))
                    (map :property))))))))

(deftest special-forms
  (testing "nil or missing values"
    (let [pred (create-pred {:property nil})]
      (is (true? (pred {})))
      (is (true? (pred {:property nil})))
      (is (false? (pred {:property "not-nil"})))
      ))

  (testing "in operator"
    (let [values ["a" 42 true nil 3.14]
          pred (create-pred {:property {:$in values}})]
      (is (true? (pred {})))
      (is (false? (pred {:property "foo"})))
      (doseq [v values] (is (true? (pred v))))))

  (testing "like operator"
    (let [pred (create-pred {:property {:$like "J.*"}})]
      (is (false? (pred {})))
      (is (false? (pred {:property 32})))
      (is (false? (pred {:property "Sam Jones"})))
      (is (true? (pred {:property "J"})))
      (is (true? (pred {:property "Jones"}))))))

(deftest combining
  (testing "nesting"
    (let [pred (create-pred {:firstName "John"
                             :lastName "Doe"
                             :job {:title "teacher"
                                   :subject {:$in ["math" "science"]}}})]
      (is (false? (pred {})))
      (is (false? (pred {:lastName "Doe" :job {:title "teacher" :job "math"}})))
      (is (true? (pred {:firstName "John" :lastName "Doe" :job {:title "teacher" :subject "math"}})))))

  (testing "disjunctions"
    (let [pred (create-pred {:person {:$or [{:firstName "John" :lastName "Doe"}
                                          {:job {:subject {:$in ["math" "science"]}}}]}})]
      ; Note the disjunctive clauses are not mutually exclusive here.
      (is (false? (pred {})))
      (is (false? (pred {:person "foo"})))
      (is (false? (pred {:person {:firstName "John"}})))
      (is (true? (pred {:person {:firstName "John" :lastName "Doe"}})))
      (is (true? (pred {:person {:job {:subject "math"}}})))
      (is (true? (pred {:person {:firstName "John" :lastName "Doe" :job {:subject "math"}}}))))))
