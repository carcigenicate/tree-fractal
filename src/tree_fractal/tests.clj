(ns tree-fractal.tests
  (:require [quil.middleware :as m]
            [quil.core :as q]))

(def rot-speed (/ q/TWO-PI 900))

(def font-size 50)

(defn setup-state []
  (q/text-font (q/create-font "Arial" font-size))
  {:rot 0 :dir 1})

(defn update-state [state]
  (let [{r :rot d :dir} state
        over? (>= r q/TWO-PI)
        under? (<= r (- q/TWO-PI))
        d' (if (or over? under?) (- d) d)
        r' (+ r (* d' rot-speed))]
    (assoc state :dir d'
                 :rot r')))

(defn draw-state [state]
  (q/background 150 150 150)

  (let [{r :rot} state]
    (q/with-stroke [0 0 0]
      (q/text (str r) font-size font-size))

    (q/with-translation [500 500]
      (q/with-rotation [r]
        (q/rect 0 0 300 300)))))

(defn -main
  [& args]
  (q/defsketch Tree-Fractal
               :size [1000 1000]

               :setup setup-state
               :update update-state
               :draw draw-state

               :middleware [m/fun-mode]))