#### Cider Bug

1. Open `test/cider_test.clj` in emacs.
2. Run `cider-jack-in`.
3. Set `nrepl-log-messages` to `t`.
4. Run all tests using `C-c ,`.
5. Open `*nrepl-messages*`. One of messages doesn't have an id.  


Example `*nrepl-messages*` (check the 5th message):
```text
(--->
  op  "load-file"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  file  "(ns hatnik.db.memory-storage-test\n  (:require [clojure.test :refer :all]\n            [taoensso.timbre :as timbre]))\n\n(deftest hello-test\n  (timbre/info \"hello\")\n  (is true))\n\n"
  file-path  "/home/nbeloglazov/repos/hatniktemp/test/cider_test.clj"
  file-name  "cider_test.clj"
  id  "6"
)
(<-
  id  "6"
  ns  "user"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  value  "#'hatnik.db.memory-storage-test/hello-test"
)
(<-
  id  "6"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  status  ("done")
)
(--->
  ns  "hatnik.db.memory-storage-test"
  op  "test"
  tests  nil  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  id  "7"
)
(<-
  out  "2014-Oct-16 07:54:07 +0100 nbeloglazov INFO [hatnik.db.memory-storage-test] - hello\n"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
)
(<-
  id  "7"
  ns  "hatnik.db.memory-storage-test"
  results  (dict
             hello-test  ((dict "context" nil "index" 0 "message" nil "ns" "hatnik.db.memory-storage-test" "type" "pass" "var" "hello-test"))
           )
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  summary  (dict
             error  0
             fail  0
             pass  1
             test  1
             var  1
           )
)
(--->
  op  "info"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  ns  "user"
  symbol  "hatnik.db.memory-storage-test/hello-test"
  id  "8"
)
(<-
  id  "7"
  session  "af9de59b-dbe1-4894-9d33-0ca9b2500b23"
  status  ("done")
)
```
