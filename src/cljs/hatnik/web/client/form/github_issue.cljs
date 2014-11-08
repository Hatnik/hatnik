(ns hatnik.web.client.form.github-issue
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hatnik.web.client.z-actions :as action]
            [hatnik.schema :as schm]
            [schema.core :as s])
  (:use [clojure.string :only [split replace]]))

(defn set-repo-status [owner status]
  (om/set-state! owner :repo-status status))

(defn github-repos-handler [reply owner repo]
  (let [rest (js->clj reply)
        exists? (some #(= repo (get % "name")) rest)]
    (set-repo-status owner
                     (if exists? "has-success" "has-error"))))

(defn github-issue-on-change [gh-repo timer owner]
  (js/clearTimeout timer)
  (set-repo-status owner "has-warning")
  (let [[user repo] (split gh-repo "/")]
    (when-not (or (nil? repo) (nil? user)
                  (= "" user) (= "" repo))
      (let [timer (js/setTimeout
                   (fn []
                     (action/get-github-repos
                      user
                      #(github-repos-handler % owner repo)
                      #(set-repo-status owner "has-error")))
                   1000)]
        (om/set-state! owner :timer timer)))))

(defn github-issue-component [data owner]
  (reify
    om/IInitState
    (init-state [this]
      {:repo-status
       (let [v (:github-repo data)]
         (if (or (nil? v) (= "" v))
           "has-warning"
           "has-success"))
       :title-status "has-success"
       :body-status "has-success"
       :timer nil})
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/div #js {:className (str "form-group " (:repo-status state))}
                        (dom/label #js {:htmlFor "gh-repo"
                                        :className "control-label"} "GitHub repository")
                        (dom/input #js {:type "text"
                                        :className "form-control"
                                        :id "gh-repo"
                                        :value (:github-repo data)
                                        :placeholder "user/repository or organization/repository"

                                        :onChange
                                        #(let [repo (.. % -target -value)]
                                           (github-issue-on-change repo (:timer state) owner)
                                           (om/update! data :github-repo repo))}))

               (dom/div #js {:className (str "form-group " (:title-status state))}
                        (dom/label #js {:htmlFor "gh-issue-title"
                                        :className "control-label"} "Issue title")
                        (dom/input #js {:type "text"
                                        :className "form-control"
                                        :id "gh-issue-title"
                                        :value (:title data)
                                        :onChange #(let [title (.. % -target -value)]
                                                     (om/update! data :title title)
                                                     (om/set-state! owner :title-status
                                                                    (if (s/check schm/TemplateTitle title)
                                                                      "has-error"
                                                                      "has-success")))}))
               (dom/div #js {:className (str "form-group " (:body-status state))}
                        (dom/label #js {:htmlFor "gh-issue-body"
                                        :className "control-label"} "Issue body")
                        (dom/textarea #js {:cols "40"
                                           :className "form-control"
                                           :id "gh-issue-body"
                                           :value (:body data)
                                           :onChange #(let [body (.. % -target -value)]
                                                        (om/update! data :body body)
                                                        (om/set-state! owner :body-status
                                                                       (if (s/check schm/TemplateBody body)
                                                                         "has-error"
                                                                         "has-success")))}))))))
