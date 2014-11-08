(ns hatnik.web.client.z-actions
  (:require [jayq.core :as jq]
            [hatnik.web.client.app-state :as state]
            [hatnik.web.client.message :as msg]
            [hatnik.schema :as schm]
            [schema.core :as s])
  (:use [jayq.core :only [$]]))

(def default-error-message "Fields highlighted in red are invalid. Please check them out.")

(defn get-data-from-input [id]
  (.-value (.getElementById js/document id)))

(defn wrap-error-alert [callback]
  (fn [reply]
    (let [resp (js->clj reply)]
      (when (= "error" (get resp "result")) (msg/danger (get resp "message")))
      (callback reply))))

(defn ajax [url type data callback]
  (jq/ajax url
           {:type type
            :data (.stringify js/JSON
                              (clj->js data))
            :contentType "application/json"
            :dataType "json"
            :async true
            :error #(msg/danger "Invalid request. Please, check out request data.")
            :success callback}))

(defn get-github-repos [github-name callback error-handler]
  (jq/ajax (str "https://api.github.com/users/" github-name "/repos")
           {:type "GET"
            :success callback
            :error error-handler}))

(defn common-update-callback [msg data reply]
  (let [resp (js->clj reply)]
    (when (= "ok" (get resp "result"))
      (state/update-all-view))))

(defn create-new-project-callback [name reply]
  (let [resp (js->clj reply)]
    (when (= "ok" (get resp "result"))
      (state/update-all-view))))

(defn create-project [project-name]
  (let [project {:name project-name}]
    (if (s/check schm/Project project)
      (msg/danger "Project name should be between 1 and 128 character long.")
      (do
        (.modal ($ :#project-form) "hide")
        (ajax "/api/projects" "POST" project #(create-new-project-callback project-name %))))))

(defn create-new-action-callback [data reply]
  (let [resp (js->clj reply)]
    (when (= "ok" (get resp "result"))
      (state/update-all-view))))

(defn build-email-action
  [{:keys [project-id body title library]}]
  {:project-id project-id
   :type "email"
   :body body
   :subject title
   :library library})

(defn build-gh-issue-action
  [{:keys [project-id body title library github-repo]}]
  {:project-id project-id
   :type "github-issue"
   :repo github-repo
   :title title
   :body body
   :library library})

(defn build-noop-action
  [{:keys [project-id library]}]
  {:type "noop"
   :project-id project-id
   :library library})

(defmulti send-new-action :type)

(defmethod send-new-action "email" [action-form]
  (let [data (build-email-action action-form)]
    (if (s/check schm/EmailAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax "/api/actions" "POST" data
              (wrap-error-alert #(create-new-action-callback data %)))))))

(defmethod send-new-action "noop" [action-form]
  (let [data (build-noop-action action-form)]
    (if (s/check schm/NoopAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax "/api/actions" "POST" data
              (wrap-error-alert #(create-new-action-callback data %)))))))

(defmethod send-new-action "github-issue" [action-form]
  (let [data (build-gh-issue-action action-form)]
    (if (s/check schm/GithubIssueAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax "/api/actions" "POST" data
              (wrap-error-alert #(create-new-action-callback data %)))))))


(defmulti test-action :type)
(defmethod test-action "email" [action-form done-callback]
  (let [data (build-email-action action-form)]
    (if (s/check schm/EmailAction data)
      (do (msg/danger default-error-message)
          (done-callback))
      (do
        (msg/info "Sending test email...")
       (ajax "/api/actions/test" "POST" data
            (wrap-error-alert
             (fn [e]
               (done-callback)
               (msg/success "The email is sent. Check your inbox."))))))))

(defmethod test-action "github-issue" [action-form done-callback]
  (let [data (build-gh-issue-action action-form)]
    (if (s/check schm/GithubIssueAction data)
      (do (msg/danger default-error-message)
          (done-callback))
      (do
        (msg/info "Creating test issue on Github...")
        (ajax "/api/actions/test" "POST" data
              (wrap-error-alert
               (fn [e]
                 (done-callback)
                 (msg/success "The issue is created. Check out your project."))))))))

(def action-update-error-message "Couldn't update the action. Please file a bug if the issue persists.")

(defmulti update-action :type)
(defmethod update-action "email" [action-form]
  (let [data (build-email-action action-form)]
    (if (s/check schm/EmailAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax
         (str "/api/actions/" (:id action-form)) "PUT"
         data (wrap-error-alert
               #(common-update-callback action-update-error-message data %)))))))

(defmethod update-action "noop" [action-form]
  (let [data (build-noop-action action-form)]
    (if (s/check schm/NoopAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax
         (str "/api/actions/" (:id action-form)) "PUT"
         data (wrap-error-alert
               #(common-update-callback action-update-error-message data %)))))))

(defmethod update-action "github-issue" [action-form]
  (let [data (build-gh-issue-action action-form)]
    (if (s/check schm/GithubIssueAction data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#iModalAddAction) "hide")
        (ajax
         (str "/api/actions/" (:id action-form)) "PUT"
         data (wrap-error-alert
               #(common-update-callback action-update-error-message data %)))))))

(defn delete-action [action-id]
  (.modal ($ :#iModalAddAction) "hide")
  (ajax
   (str "/api/actions/" action-id) "DELETE"
   {} (wrap-error-alert
       #(common-update-callback "Couldn't delete the action. Please file a bug if the issue persists." {} %))))

(defn ^:export delete-project [project-id]
  (.modal ($ :#project-form) "hide")
  (ajax
   (str "/api/projects/" project-id) "DELETE"
   {} (wrap-error-alert
       #(common-update-callback "Couldn't delete the project. Please file a bug if the issue persists." {} %))))

(defn ^:export update-project [project-id new-name]
  (let [data {:name new-name}]
    (if (s/check schm/Project data)
      (msg/danger default-error-message)
      (do
        (.modal ($ :#project-form) "hide")
        (ajax
         (str "/api/projects/" project-id) "PUT"  data
         (wrap-error-alert #(common-update-callback "Couldn't rename the project. Please file a bug if the issue persists." {} %)))))))


(defn get-library [library callback]
  (ajax
   (str "/api/library-version?library=" library) "GET"
   {} callback))
