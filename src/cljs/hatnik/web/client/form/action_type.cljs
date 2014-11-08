(ns hatnik.web.client.form.action-type
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hatnik.web.client.z-actions :as action]
            [hatnik.schema :as schm]
            [schema.core :as s]))

(def types
  [{:value "noop"
    :text "Noop"}
   {:value "email"
    :text "Email"}
   {:value "github-issue"
    :text "GitHub Issue"}])

(defn action-type-component [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "form-group action-type-component"}
               (dom/label #js {:htmlFor "action-type"
                               :className "control-label"} "Action type")
               (apply dom/select #js {:className "form-control"
                                      :id "action-type"
                                      :defaultValue (:type data)
                                      :onChange #(om/update! data :type (.. % -target -value))}
                      (for [{:keys [value text]} types]
                        (dom/option #js {:className "form-control"
                                         :value value
                                         :selected (if (= (:type data) value)
                                                     "selected"
                                                     nil)}
                                    text)))))))

