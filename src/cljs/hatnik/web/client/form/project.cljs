(ns hatnik.web.client.form.project
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hatnik.web.client.z-actions :as action]
            [hatnik.web.client.app-state :refer [app-state]]
            [hatnik.schema :as schm]
            [schema.core :as s])
  (:use [jayq.core :only [$]]))


(defn- project-form [data owner]
  (reify
    om/IInitState
    (init-state [this]
      (let [name (:name data)]
        {:name name
         :status (if (or (nil? name)
                         (= "" name))
                   "has-warning"
                   "has-success")}))

    om/IRenderState
    (render-state [this state]
      (let [name (:name data)
            id (:id data)]
        (dom/div
         #js {:className "modal-dialog"}
         (dom/div
          #js {:className "modal-content"}
          (dom/div #js {:className "modal-header"}
                   (dom/h4 #js {:className "modal-title"}
                           (if id
                             "Update project"
                             "Create project")))

          (dom/div
           #js {:className "modal-body"}
           (dom/form nil
                     (dom/div #js {:className (str "form-group " (:status state))}
                              (dom/input #js {:className "form-control"
                                              :type "text"
                                              :value (:name data)
                                              :onChange #(let [v (.. % -target -value)]
                                                           (om/update! data :name v)
                                                           (om/set-state! owner :status
                                                                          (if (s/check (schm/string-of-length 1 128) v)
                                                                            "has-error"
                                                                            "has-success")))}))))

          (dom/div #js {:className "modal-footer"}
                   (if id
                     (dom/div #js {:className "btn btn-primary pull-left"
                                   :onClick #(action/update-project id name)} 
                              "Update")
                     (dom/div #js {:className "btn btn-primary pull-left"
                                   :onClick #(action/create-project name)} 
                              "Create"))
                   (when id
                    (dom/div #js {:className "btn btn-danger pull-right"
                                  :onClick #(action/delete-project id)} 
                             "Delete")))))))))

(defn show []
  (.modal ($ "#project-form")))

(om/root project-form
         app-state
         {:target (.getElementById js/document "project-form")
          :path [:project-form]})
