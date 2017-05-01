(ns tree-fractal.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [helpers.general-helpers :as g]
            [helpers.quil-helpers :as qh])

  (:gen-class))

(def width 2500)
(def height 1500)

(def min-branch-length 5)
(def child-branch-length-perc 0.6)
(def trunk-length (/ height 4))

(def rot-speed (/ q/TWO-PI 300))

(defrecord Branch [length left-branch right-branch])

(defn bare-trunk [length]
  (->Branch length nil nil))

(defn sub-branch-of [parent-length sub-branch-length-perc]
  (let [sub-length (* parent-length sub-branch-length-perc)]
    (->Branch sub-length nil nil)))

(defn populate-branch [trunk-branch min-length sub-branch-length-perc]
  (let [{length :length} trunk-branch]
    (if (< length min-length)
      nil

      (let [sub-length (* length sub-branch-length-perc)
            r #(populate-branch (bare-trunk sub-length) min-length sub-branch-length-perc)]
        (assoc trunk-branch :left-branch (r)
                            :right-branch (r))))))

(defn draw-branch [branch rot-angle]
  (let [{length :length lb :left-branch rb :right-branch} branch
        w #(g/wrap % 0 255)
        l length]
    (q/with-stroke [(w l) (w (* l l)) (w (+ l l))]
      (q/line 0 0 0 (- length)))

    (q/with-translation [0 (- length)]
      (when lb
        (q/with-rotation [(- rot-angle)]
          (draw-branch lb rot-angle)))

      (when rb
        (q/with-rotation [rot-angle]
          (draw-branch rb rot-angle))))))

(defn setup-state []
  (let [trunk (bare-trunk trunk-length)
        pop-trunk (populate-branch trunk min-branch-length child-branch-length-perc)]

    (println "Populated...")

    {:tree pop-trunk, :dir 1, :rot 0}))

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

  (let [{tree :tree rot :rot} state]
    (q/with-translation [(/ width 2) height]
      (qh/with-weight 3
         (draw-branch tree rot)))))

(defn -main
  [& args]
  (q/defsketch Tree-Fractal
               :size [width height]

               :setup setup-state
               :update update-state
               :draw draw-state

               :middleware [m/fun-mode]))

