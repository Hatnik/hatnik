(ns hatnik.web.server.renderer
  (:require [hiccup.core :as hc]))

(defn page-html-head []
  (hc/html
   [:head
    [:title "Hatnik"]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:rel "stylesheet" :href "/css/bootstrap.min.css"}]
    [:link {:rel "stylesheet" :href "/css/styles.css"}]]))


(defn github-link [config]
  (str "https://github.com/login/oauth/authorize?"
       "client_id=" (:github-id config)
       "&scope=user:email"))

(defn modal-frames []
  (hc/html
   [:div#iModal.modal.fade {:data-keyboard "true"}]
   [:div#iModalAddAction.modal.fade {:data-keyboard "true"}]
   [:div#iModalProjectMenu.modal.fade ]))

(defn page-menu [config user]
  [:nav.navbar.navbar-default {:role "navigation"}
   [:div.container
    [:div.navbar-header
     [:button.navbar-toggle {:type "button"
                             :data-toggle "collapse"
                             :data-target ".navbar-collapse"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand {:href "/"} "Hatnik"]]

    [:div#navbarCollapse.collapse.navbar-collapse
     (if user
                                        ; User logged in
       [:ul.nav.navbar-nav.navbar-right
        [:li
         [:a (:github-login user)]]
        [:li
         [:a.btn {:href "/api/logout"} "Logout"]]]

                                        ; User is not logged in
       [:ul.nav.navbar-nav.navbar-right
        [:li
         [:a.btn
          {:href (github-link config)} "Login via GitHub"]]])]]])

(defn work-main-page []
  (hc/html
   [:div#iAppView ""]))

(defn about-page []
  [:div.about
   [:div.page-header
    [:h1 "Hatnik " [:small "Don't miss a release"]]
    [:p "Hatnik is a web app that helps you to track library releases. Choose a library to track and setup an action that sends emails or creates Github issues when a new release of the library is published."]]

   [:div.row
    [:div.col-md-6.col-xs-12.use-cases
     [:h3 "When to use"]
     [:p "Use cases might be different. Here is some of them:"]
     [:ul
      [:li "You want to stay on the cutting edge and use the latest release of ClojureScript."]
      [:li "You keep forgetting to update wiki tutorials and examples "
       "to use the new version when you release your library."]
      [:li "You want to have a place shows the latest version of your favourite "
       "libraries."]
      [:li "You want your project to automatically update to the latest version of its dependencies."]]]
    [:div.col-md-6.col-xs-12.how-to
     [:h3 "How to start"]
     [:ol
      [:li "Login via GitHub. Sorry can't do without it."]
      [:li "Create and setup an action"
       [:ol
        [:li "Choose a library, for example " [:code "org.clojure/clojurescript"] "."]
        [:li "Fill in the action settings. For example send an email with custom text."]]]]
     [:p "That's all! Now you forget about Hatnik until the library you're tracking is released. "
      "You'll get an email with your text. You can create multiple actions and group them by projects. "
      "A project is simply a group of actions related to each other."]]]

   [:div.separator]

   [:h3 "Actions"]
   [:p "Hatnik is going to support multiple types of actions. Currently they're "
    "limited only to sending emails, but we're working on it! Here's the initial set of actions:"]
   [:ul.actions
    [:li.available [:strong  "Email"]
     " - send an email with custom text. You can use template variables to substitute the library name or the version."]
    [:li.available {:title "Work in progress"} [:strong "Noop"]
     " - does nothing. Useful if you want to "
     "have a place that lists the latest versions for selected libraries."]
    [:li.available {:title "Work in progress"} [:strong "GitHub Issue"]
     " - create an issue in the selected repo. The text can be customized just like in email."]
    [:li.unavailable {:title "Work in progress"} [:strong "GitHub Pull Request"]
     " - create a pull request in selected repo using simple find-and-replace-in-file operations."]
    [:li.unavailable {:title "Work in progress"} [:strong "GitHub Wiki"]
     " - update a wiki page using the same find-and-replace operations."]]
   ])

(defn core-page [config user]
  (hc/html
   (page-html-head)

   [:body
    (page-menu config user)

    [:div.container

     (if user
       (work-main-page)
       (about-page))]

    (when user (modal-frames))

    [:script {:src "/js/jquery-2.1.1.min.js"}]
    [:script {:src "/js/bootstrap.min.js"}]
    [:script {:src "/js/react.min.js"}]
    [:script {:src "/js/jquery.bootstrap-growl.min.js"}]

    (when user
      [:script {:src "/gen/js/hatnik.js"}])]))

