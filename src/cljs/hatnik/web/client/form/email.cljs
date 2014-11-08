(ns hatnik.web.client.form.email
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hatnik.web.client.z-actions :as action]
            [hatnik.schema :as schm]
            [schema.core :as s]))

(defn email-component [data owner]
  (reify
    om/IInitState
    (init-state [this]
      {:subject-status "has-success"
       :body-status "has-success"})

    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/div #js {:className "form-group"}
                        (dom/label nil "Address")
                        (dom/p #js {:id "email-input"}
                               (:email data)))
               (dom/div #js {:className (str "form-group " (:subject-status state))}
                        (dom/label #js {:htmlFor "email-subject-input"
                                        :className "control-label"} "Subject")
                        (dom/input #js {:type "text"
                                        :className "form-control"
                                        :id "email-subject-input"
                                        :value (:title data)
                                        :onChange #(let [title (.. % -target -value)]
                                                     (om/update! data :title title)
                                                     (om/set-state! owner :subject-status
                                                                   (if (s/check schm/TemplateTitle title)
                                                                     "has-error"
                                                                     "has-success")))}))
               (dom/div #js {:className (str "form-group " (:body-status state))}
                        (dom/label #js {:htmlFor "email-body-input"
                                        :className "control-label"} "Body")
                        (dom/textarea #js {:cols "40"
                                           :className "form-control"
                                           :id "email-body-input"
                                           :value (:body data)
                                           :onChange #(let [body (.. % -target -value)]
                                                        (om/update! data :body body)
                                                        (om/set-state! owner :body-status
                                                                       (if (s/check schm/TemplateBody (.. % -target -value))
                                                                         "has-error"
                                                                         "has-success")))}))))))
