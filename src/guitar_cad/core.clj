(ns guitar-cad.core
  (:require [clojure.java.io :refer [make-parents]]
            [scad-clj.scad :refer [write-scad]]
            [scad-clj.model :refer [polygon extrude-linear cylinder difference union
                                    translate mirror resize minkowski sphere
                                    with-fn cube square hull rotate]])
  (:gen-class))

(def machine-head-post-radius 3)
(def machine-head-hole-radius 5)
(def string-gap 7)

(defn headstock-base
  [{:keys [pretty?]}]
  (let [raw-shape 
        (difference
          (extrude-linear {:height 15} 
                          (polygon [[-21 0]
                                    [21 0]
                                    [50 150]
                                    [30 150]
                                    [30 170]
                                    [-30 170]
                                    [-30 150]
                                    [-50 150]]))
          (translate [50 150]
                     (resize [40 60 20]
                             (cylinder 20 20)))
          (translate [-50 150]
                     (resize [40 60 20]
                             (cylinder 20 20))))]
    (if pretty? 
      (minkowski raw-shape (with-fn 20 (sphere 2)))
      raw-shape)))

(def machine-head-holes
  (union 
      (translate [(+ string-gap machine-head-post-radius) 30]
                 (cylinder machine-head-hole-radius 20))
      (translate [(+ (* string-gap 2) machine-head-post-radius) 70]
                 (cylinder machine-head-hole-radius 20))
      (translate [(+ (* string-gap 3) machine-head-post-radius) 110]
                 (cylinder machine-head-hole-radius 20))))

(defn headstock
  [{:keys [pretty?]}]
  (difference
    (headstock-base {:pretty? pretty?})
    machine-head-holes
    (mirror [1 0 0] machine-head-holes)
    (translate [0 170 0]
      (cube 100 20 40))))

(defn neck-profile-base
  [{:keys [pretty?]}]
  (rotate (- (/ Math/PI 2)) [1 0 0]
          (difference
            (if pretty?
              (with-fn 100
                       (cylinder 20 5))
              (cylinder 20 5))
            (translate [0 -20 0]
                       (cube 40 40 20)))))

(defn neck-profile
  [{:keys [pretty?]} & cross-sections]
  (hull
    (for [[w h y] cross-sections]
        (translate [0 y 0]
               (resize [w 1 h]
                       (neck-profile-base {:pretty? pretty?}))))))

(defn render!
  [file-name part]
  (let [file-path (str "out/" file-name ".scad")]
    (make-parents file-path)
    (spit file-path
          (write-scad part))))

(render! "headstock" (headstock {:pretty? true}))

(render! "neck-profile" (neck-profile {:pretty? true} [40 10 0] [50 15 200]))

(render! "scratch" (neck-profile {:pretty? true} [40 10 0] [50 15 200]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
