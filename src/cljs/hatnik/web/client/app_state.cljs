(ns hatnik.web.client.app-state)

(def app-state 
  (atom {:projects []
         :form-type :email-action
         :user {}
         :current-project nil}))

(defn update-projects-list [reply]
  (let [json (.getResponseJson (.-target reply))
        data (js->clj json)]
    (when (= "ok" (get data "result"))
      (swap! app-state
             assoc :projects
             (get data "projects")))))

(defn set-form-type [action-type]
  (swap! app-state
         assoc :form-type
         action-type))

(defn set-current-project [id]
  (swap! app-state 
         assoc :current-project id))


(defn add-new-project [id name]
  (swap! app-state
         assoc :projects
         (into [{"id" id "name" name}]
               (:projects @app-state))))


(defn update-user-data [reply]
  (let [json (.getResponseJson (.-target reply))
        data (js->clj json)]
    (when (= "ok" (get data "result"))
      (swap! app-state
             assoc-in [:user :email]
             (get data "email")))))

(defn update-project-actions [action]
  (.log js/console action))