(ns cider-test
  (:require [clojure.test :refer :all]
            [taoensso.timbre :as timbre]))

(deftest hello-test
  (timbre/info "hello")
  (is true))

