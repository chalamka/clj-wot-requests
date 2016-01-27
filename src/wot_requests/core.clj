(ns wot-requests.core
  (:require [wot-requests.libraries.wotapi :as w]
            [wot-requests.libraries.slackapi :as s]
            [clj-yaml.core :as yaml]
            [clojure.java.io :as io]))

(defn load-config [filepath]
  (with-open [f (io/input-stream filepath)]
    (yaml/parse-string f)))

(defn total-trees-webhook [tanks-name target config]
  (let [text (str tanks-name ", you have cut down " (w/treecut-lookup tanks-name config) " trees." )]
    (s/send-incoming-webhook (s/construct-webhook-json {:text text :username "treecut-bot" :icon_emoji "siren" :channel (str "@" target)}) config)))

(defn avg-trees-webhook [tanks-name target config]
  (let [text (str tanks-name ", you cut down an average of " (format "%.2f" (w/avg-treecut-lookup tanks-name config)) " trees per battle.")]
    (s/send-incoming-webhook (s/construct-webhook-json {:text text :username "treecut-bot" :icon_emoji "palm_tree" :channel (str "@" target)}) config)))

(defn main [args]
  (load-config args))