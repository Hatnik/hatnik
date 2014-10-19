(ns hatnik.worker.github-issue-action
  (:require [taoensso.timbre :as timbre]
            [hatnik.utils :as u]
            [clojure.string :as cstr]))


(defn perform
  "Creates github issue."
  [action user variables utils]
  (let [title (u/fill-template (:title action) variables)
        body (-> (:body action)
                 (str "\n\nThis issue is created on behalf of @"
                      (:github-login user))
                 (u/fill-template variables))
        [target-user target-repo] (cstr/split (:repo action) #"/")]
    ((:create-github-issue utils)
     {:body body
      :title title
      :user target-user
      :repo target-repo})
    nil))

(comment

  (perform {:repo "nbeloglazov/hatnik-test-lib"
            :title "[Hatnik] {{library}} {{version}}."
            :body "Released! {{library}} {{previous-version}}"}
           {:github-login "nbeloglazov"}
           {:library "quil"
            :version "2.2.2"
            :previous-version "2.2.1"}
           {:create-github-issue println})

)