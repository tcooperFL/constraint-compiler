(ns constraint-compiler.trace)

(def level (atom {}))
(defn- indent [i] (apply str (repeat i "  ")))

(defn enter
  "Simple indenting trace function. Suggested usage: {:pre [(enter <fnname> <arg>...)]}"
  [name & args]
  (do
    (apply println
           `(~(indent (dec ((swap! level update name (fnil inc 0)) name)))
              "->"
              ~name
              ~@args))
    (@level name)))

(defn leave
  "Simple indenting trace exit function. Suggested usage: {:post [(leave <fnname> %)]}"
  [name & args]
  (do
    (apply println
           `(~(indent ((swap! level update name #(max 0 (dec (or % 1)))) name))
              "<-" ~name ~@args))
    (@level name)))