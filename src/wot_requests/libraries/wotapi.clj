(ns wot-requests.libraries.wotapi
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all :as json]
            [clj-time.coerce :as tf]))

;; api urls
(def player-api-url "https://api.worldoftanks.com/wot/account/list/")
(def stats-api-url "https://api.worldoftanks.com/wot/account/info/")
(def clans-api-url "https://api.worldoftanks.com/wot/globalwar/clans/")
(def globalmap-api-url "https://api.worldoftanks.com/wot/globalwar/maps/")
(def globalmap-battles-api-url "api.worldoftanks.com/wot/globalmap/clanbattles")
(def api-error (Throwable. "API error"))

(defn parse-json "Parse a String in JSON format to a dictionary" [x]
  (json/parse-string x true))

(defn get-player "Search world of tanks API for player" [player {{api-key :api} :wot}]
  (let [response (-> (http/get player-api-url {:query-params {:application_id api-key :search player :limit 1}}) :body parse-json)]
    (if (-> response :status (= "ok"))
      (if (-> response :meta :count (> 0))
        (let [player-info (-> response :data first)]
          { :account_id   (-> player-info :account_id)
           :name (-> player-info :nickname .toLowerCase)})
        nil)
      (throw api-error))))

(defn get-player-statistics "Search world of tanks API for player id statistics" [player {{api-key :api} :wot}]
  (let [response (-> (http/get stats-api-url {:query-params {:application_id api-key :account_id player}}) :body parse-json)]
    (if (-> response :status (= "ok"))
      (if (-> response :meta :count (> 0))
        (let [id (keyword (str player))
              player-statistics (-> response :data id)]
          ;; dictionary of all response fields for player id x
          player-statistics)
        nil)
      (throw api-error))))

(defn get-total-battles "Calculate how many random and clan battles a player has participated in" [player-stats]
  (let [stats (-> player-stats :statistics :all)]
    (+ (:wins stats) (:losses stats) (:draws stats))))

(defn get-winrate "Calculate win ratio for random and clan battles" [player-stats]
  (let [stats (-> player-stats :statistics :all)]
    (float (/ (:wins stats) (+ (:wins stats) (:losses stats) (:draws stats))))))

;; Clan Wars related functions

(defn get-clan-battles "Search world of tanks API for a given clan's battles on the global map" [{{api-key :api map-id :map_id clan-id :clan_id} :wot}]
  (let [response (-> (http/get globalmap-battles-api-url {:query-params {:application_id api-key :clan_id clan-id}}) :body parse-json)]
    (if (-> response :status (= "ok"))
      (if (-> response :meta :count (> 0))
        (let [id (keyword (str clan-id))
              battles (-> response :data id)]
          battles)
        nil)
      (-> response :error :message))))

;; Lookup functions combine API functions for ease of use

(defn battle-lookup "Search for a player x and return the number of battles" [player config]
  (-> player (get-player config) :account_id  (get-player-statistics config) get-total-battles))

(defn winrate-lookup "Search for a player and return their winrate" [player config]
  (-> player (get-player config) :account_id (get-player-statistics config) get-winrate))

(defn treecut-lookup "Search for a player and return the trees cut" [player config]
  (-> player (get-player config) :account_id (get-player-statistics config) :statistics :trees_cut))

(defn avg-treecut-lookup "Search for a player and return their trees cut per battle" [player config]
  (let [stats (-> player (get-player config) :account_id (get-player-statistics config))
        battles (get-total-battles stats)
        trees (-> stats :statistics :trees_cut)]
    (float (/ trees battles))))

