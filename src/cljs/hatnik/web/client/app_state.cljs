(ns hatnik.web.client.app-state)

(def empty-action-form
  {:library ""
   :library-version nil
   :id nil
   :project-id nil
   :type "noop"
   :address ""
   :title "{{library}} {{version}} released"
   :body (str "{{library}} {{version}} has been released\n\n"
              "Previous version was {{previous-version}}\n\n")
   :github-repo ""})

(def app-state
  (atom {; Here we store data from the server
         :projects []
         :user {}
         :project-form {:name ""
                        :id nil}
         :action-form empty-action-form}))

(defn update-projects-list [reply]
  (let [json (.getResponseJson (.-target reply))
        data (js->clj json)]
    (when (= "ok" (get data "result"))
      (swap! app-state
             assoc-in [:projects]
             (get data "projects")))))

(defn add-new-project [id name]
  (swap! app-state
         assoc-in [:data :projects]
         (into [{"id" id "name" name}]
               (-> @app-state
                   :data
                   :projects))))

(defn update-user-data [reply]
  (let [json (.getResponseJson (.-target reply))
        data (js->clj json)]
    (when (= "ok" (get data "result"))
      (swap! app-state
             assoc-in [:user :email]
             (get data "email")))))

(defn set-current-project [id name]
  (swap! app-state  assoc :project-form {:id id :name name}))

(defn update-all-view []
  (.send goog.net.XhrIo "/api/projects" update-projects-list) )

(defn update-project-actions [action]
  (.send goog.net.XhrIo "/api/projects" update-projects-list))

