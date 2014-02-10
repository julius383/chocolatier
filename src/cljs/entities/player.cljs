(ns chocolatier.entities.player
  ;; NOTE to use protocols from another ns, import them explicitely by
  ;; name and not their methods
  ;; Use them in this ns by refering to the name
  (:use [chocolatier.utils.logging :only [debug info warn error]])
  (:require [chocolatier.engine.components :refer [Entity
                                                   Renderable
                                                   UserInput]]))


(defrecord Player [id sprite screen-x screen-y map-x map-y]
  Entity
  (tick [this] this)
  
  Renderable
  (render [this state]
    (let [sprite (:sprite this)
          {:keys [screen-x screen-y]} this
          [sprite-x sprite-y] (map #(aget sprite "position" %) ["x" "y"])]
      (if (or (not= sprite-x screen-x) (not= sprite-y screen-y))
        (do
          (set! (.-position.x sprite) screen-x)
          (set! (.-position.y sprite) screen-y)
          (assoc this :sprite sprite))
        this)))

  UserInput
  ;; TODO update the player's velocity, direction
  (react-to-user-input [this state time]
    (let [sprite (:sprite this)
          input @(:input state)
          move-rate 5.0
          move #(condp = %2
                  :W (assoc %1 :screen-y (- (:screen-y %1) move-rate))
                  :A (assoc %1 :screen-x (- (:screen-x %1) move-rate))
                  :S (assoc %1 :screen-y (+ (:screen-y %1) move-rate))
                  :D (assoc %1 :screen-x (+ (:screen-x %1) move-rate))
                  ;; Otherwise do nothing
                  %1)]
      ;; Apply all the changes to the record in a recursive loop
      (loop [out this
             i (seq input)]
        (let [[k v] (first i)
              remaining (rest i)
              updated (if (= v "on") (move out k) out)]
          (if (empty? remaining)
            updated
            (recur updated remaining)))))))
