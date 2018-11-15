(ns constraint-compiler.core)

(declare satisfies-constraints?)

; Validity test is based on the constraint property name.
(defmulti valid? (fn [_ [k _]] k))

(defmethod valid? :$lt [x [k constraint]]
  (and (number? x) (< x constraint)))

(defmethod valid? :$le [x [_ constraint]]
  (and (number? x) (<= x constraint)))

(defmethod valid? :$ge [x [_ constraint]]
  (and (number? x) (>= x constraint)))

(defmethod valid? :$gt [x [_ constraint]]
  (and (number? x) (> x constraint)))

(defmethod valid? :$in [x [_ constraint]]
  (contains? (set constraint) x))

(defmethod valid? :$like [x [_ constraint]]
  (and (= (type x) java.lang.String) (re-matches (re-pattern constraint) x)))

(defmethod valid? :$or [x [_ constraint]]
  (some (partial satisfies-constraints? x) constraint))

; Interpret all other non-reserved property names as exact matches
(defmethod valid? :default [x [k constraint]]
  (if (map? constraint)
    (satisfies-constraints? (get x k) constraint)
    (= constraint (get x k))))

(defn- satisfies-constraints?
  "Return true if the given content x satisfies the constraints m"
  [x m]
  (every? (partial valid? x) m))

;; Exported function
(defn create-pred
  "Compile the given constraint into a unary function that takes a map and returns true if it satisfies that constraint"
  [m]
  #(satisfies-constraints? % m))
