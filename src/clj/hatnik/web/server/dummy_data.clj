(ns hatnik.web.server.dummy-data
  (:require [taoensso.timbre :as timbre]
            [hatnik.db.storage :as stg]))


(defn create-default-project [db user-id]
  (let [proj-id (stg/create-project! db {:name "Default"
                                         :user-id user-id})]
    (stg/create-action! db user-id {:project-id proj-id
                                    :type "email"
                                    :template "Hey {{LIBRARY}} released. New version {{VERSION}}."
                                    :address "nikelandjelo@gmail.com"
                                    :library "com.nbeloglazov/hatnik-test-lib"
                                    :last-processed-version "0.0.9"})))

(defn create-quil-project [db user-id]
  (let [proj-id (stg/create-project! db {:name "Quil"
                                         :user-id user-id})]
    (stg/create-action! db user-id {:project-id proj-id
                                    :type "email"
                                    :template "Quil {{VERSION}} was released. Go and update wiki and examples!"
                                    :address "nikelandjelo@gmail.com"
                                    :library "quil"
                                    :last-processed-version "2.2.2"})
    (stg/create-action! db user-id {:project-id proj-id
                                    :type "noop"
                                    :library "org.clojure/clojurescript"
                                    :last-processed-version "0.0-0"})))

(defn create-hatnik-project [db user-id]
  (stg/create-project! db {:name "Hatnik"
                           :user-id user-id}))

(defn create-dummy-data [db user-id]
  (create-default-project db user-id)
  (create-quil-project db user-id)
  (create-hatnik-project db user-id))