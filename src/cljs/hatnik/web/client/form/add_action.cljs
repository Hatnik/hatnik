(ns hatnik.web.client.form.add-action
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hatnik.web.client.z-actions :as action]
            [hatnik.web.client.app-state :as state]
            [hatnik.schema :as schm]
            [schema.core :as s])
  (:use [jayq.core :only [$]]
        [hatnik.web.client.form.artifact-input :only [artifact-input-component]]
        [hatnik.web.client.form.action-type :only [action-type-component]]
        [hatnik.web.client.form.email :only [email-component]]
        [hatnik.web.client.form.github-issue :only [github-issue-component]]))

(defn action-input-form [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/form nil
                (om/build artifact-input-component data)
                #_(om/build action-type-component data)

                #_(when (= "email" (:type data))
                  (om/build email-component data))

                #_(when (= "github-issue" (:type data))
                  (om/build github-issue-component data))))))

(defn action-footer [data owner]
  (reify
    om/IInitState
    (init-state [this]
      {:test-in-progress? false})

    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (if (:id data)
                 (dom/button
                  #js {:className "btn btn-primary pull-left"
                       :onClick #(action/update-action data)} "Update")
                 (dom/button
                  #js {:className "btn btn-primary pull-left"
                       :onClick #(action/send-new-action data)} "Submit"))

               (when-not (= "noop" (:type data))
                 (if (:test-in-progress? state)
                   (dom/span #js {:className "test-spinner"}
                             (dom/img #js {:src "/img/ajax-loader.gif"
                                           :alt "Testing"
                                           :title "Testing"}))
                   (dom/button
                    #js {:className "btn btn-default"
                         :onClick (fn []
                                    (om/set-state! owner :test-in-progress? true)
                                    (action/test-action data
                                                        #(om/set-state! owner :test-in-progress? false)))}
                    "Test")))))))

(defn action-header [data]
  (dom/h4 #js {:className "modal-title"}
          (if (:id data)
            "Update action"
            "Add new action")
          (when (:id data)
            (dom/button
             #js {:className "btn btn-danger pull-right"
                  :onClick #(action/delete-action (:id data))}
             "Delete"))))

(defn- add-action-component [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div
       #js {:className "modal-dialog"}
       (dom/div
        #js {:className "modal-content"}
        (dom/div #js {:className "modal-header"}
                 (action-header data))
        (dom/div #js {:className "modal-body"}
                 (om/build action-input-form data))
        (dom/div #js {:className "modal-footer"}
                 (om/build action-footer data)))))))

(defn show []
  (.modal ($ :#iModalAddAction)))

(om/root add-action-component
         state/app-state
         {:target (.getElementById js/document "iModalAddAction")
          :path [:action-form]})
