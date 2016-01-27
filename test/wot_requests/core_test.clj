(ns wot-requests.core-test
  (:require [clojure.test :refer :all]
            [wot-requests.core :refer :all]))

;{
;   "status": "ok",
;   "meta": {
;       "count": 3
;   },
;   "data": [
;       {
;           "front_id": "us_absolute_front",
;           "front_name": "us_absolute_front",
;           "time": 1440118800,
;           "province_id": "culiacan",
;           "type": "attack",
;           "province_name": "Culiacán"
;       },
;       {
;           "front_id": "us_absolute_front",
;           "front_name": "us_absolute_front",
;           "time": 1440118800,
;           "province_id": "inde",
;           "type": "attack",
;           "province_name": "province_inde"
;       },
;       {
;           "front_id": "us_absolute_front",
;           "front_name": "us_absolute_front",
;           "time": 1440118800,
;           "province_id": "tepache",
;           "type": "attack",
;           "province_name": "province_tepache"
;       }
;   ]
;}

(def test-stats {:statistics {:all {:wins 10 :losses 4 :draws 1}}})

(deftest test-get-total-battles
  (testing "Test that addition is working properly"
    (is (= 15 (get-total-battles test-stats)))))

(deftest test-get-winrate
  (testing "Test that division is working properly"
    (is (= (float (/ 2 3)) (get-winrate test-stats)))))
