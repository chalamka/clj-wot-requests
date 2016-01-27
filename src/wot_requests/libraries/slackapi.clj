(ns wot-requests.libraries.slackapi
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all :as json]))

(defn construct-webhook-json [{message :text  bot-name :username emoji :icon_emoji channel :channel} ]
  (json/generate-string {:text       message
                         :username   bot-name
                         :icon_emoji (str ":" emoji ":")
                         :channel    channel}))

(defn send-incoming-webhook [payload {{slack-web-url :url} :slack}]
  (let [response (-> (http/post slack-web-url {:form-params {:payload payload}}) :body)]
    (if (= "ok" response)
      (print "Message sent"))))