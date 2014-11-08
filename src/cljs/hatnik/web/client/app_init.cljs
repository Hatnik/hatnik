(ns hatnik.web.client.app-init
  (:require goog.net.XhrIo
            [om.core :as om :include-macros true]
            [hatnik.web.client.components :as widget]
            [hatnik.web.client.app-state :as state]))

(enable-console-print!)

;(om/root widget/project-list state/app-state
;         {:target (. js/document (getElementById "iProjectList"))}) 

(om/root widget/app-view state/app-state
         {:target (. js/document (getElementById "iAppView"))})



