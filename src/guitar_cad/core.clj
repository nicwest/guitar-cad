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
  (let [height (if pretty? 12 16)
        topx (if pretty? 19 21)
        bottomx (if pretty? 46 50)
        neckx (if pretty? 28 30)
        raw-shape 
        (difference
          (extrude-linear {:height height} 
                          (polygon [[(- topx) 0]
                                    [topx 0]
                                    [bottomx 150]
                                    [(- bottomx) 150]]))
          (translate [bottomx 150]
                     (resize [40 60 20]
                             (cylinder 20 20)))
          (translate [(- bottomx) 150]
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
  (translate [0 0 -8]
  (difference
    (headstock-base {:pretty? pretty?})
    machine-head-holes
    (mirror [1 0 0] machine-head-holes)
    (translate [0 170 0]
      (cube 100 20 40)))))

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
      (->> (neck-profile-base {:pretty? pretty?})
           (resize [w 1 h])
           (translate [0 y 0])))))

(defn deg->rads [n] (* n (/ Math/PI 180)))

(defn headstock-neck-joint
  [{:keys [joint-width headstock-width headstock-height headstock-tilt-degs
           neck-width neck-height pretty? cut-radius lump-height]}]
  (difference
    (hull
      (->> (cube headstock-width 1 headstock-height)
           (translate [0 0 (- (/ headstock-height 2))])
           (rotate (deg->rads headstock-tilt-degs) [1 0 0]))
      (->> (difference 
             (cylinder (/ lump-height 2) 1)
             (translate [0 (- (/ lump-height 2)) 0]
                        (cube headstock-width headstock-width 2)))
           (rotate (deg->rads -90) [1 0 0])
           (translate [0 (* joint-width 1/5) 0]))
      (->> (neck-profile-base {:pretty? pretty?})
           (resize [neck-width 1 neck-height])
           (translate [0 joint-width 0])))
    (->> (cylinder cut-radius headstock-width)
         (rotate (deg->rads 90) [0 1 0])
         (translate [0 0 (- 0 cut-radius headstock-height)]))))

(defn neck
  [opts]
  (union
    (->> (headstock opts)
         (translate [0 -150 0])
         (rotate (deg->rads 5) [1 0 0])
         (translate [0 150 0]))
    (->> (headstock-neck-joint 
           {:joint-width 30 :headstock-width 60
            :headstock-height 16 :headstock-tilt-degs 5
            :lump-height 50
            :neck-width 60 :neck-height 20 :cut-radius 15 })
         (translate [0 150 0]))
    (->> (neck-profile opts [60 20 0] [70 35 400])
         (translate [0 180 0]))))

(defn gauge->mm
  [n]
  (* n 25.4))


(defn nut
  [{:keys [neck-width nut-width nut-height pretty?
           strings string-spacing]}]
  (let [offset (-> strings
                   (count)
                   (- 1)
                   (/ 2)
                   (* string-spacing)
                   (-)) ]
             (union
               (cube neck-width nut-width nut-height)
               (for [[i r] (map-indexed #(vector %1 (gauge->mm %2)) strings)]
                 (->> (cylinder r (* 2 nut-width))
                      (rotate (deg->rads 90) [1 0 0])
                      (translate [(+ offset (* i string-spacing)) 0 0]))))))

(defn render!
  [file-name part]
  (let [file-path (str "out/" file-name ".scad")]
    (make-parents file-path)
    (spit file-path
          (write-scad part))))

(def default-config
  {:pretty? false
   :strings [0.010 0.013 0.017 0.030 0.042 0.052]
   :scale-length 22
   :fretboard-radius [[0 254] [22 406]]
   :neck-radius [[0 60] [0 70]]
   :neck-wdith [[0 60] [0 70]]})

(render! "headstock" (headstock {:pretty? false}))

(render! "neck-profile" (neck-profile {:pretty? false} [40 10 0] [50 15 200]))

(render! "neck" (neck {:pretty? false}))

(render! "scratch" (nut {:pretty? false :neck-width 60 :nut-width 6 :nut-height 6
                         :strings [0.010 0.013 0.017 0.030 0.042 0.052]
                         :string-spacing 7}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
