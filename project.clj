(defproject clojurewerkz/mold "1.0.0-beta5-SNAPSHOT"
  :description "Clojure library for working with CloudFoundry services (primarily Cloud Controller)"
  :dependencies [[org.clojure/clojure                      "1.6.0"]
                 [org.cloudfoundry/cloudfoundry-client-lib "1.0.3"]
                 ;; HTTP client which uses HTTPCore 4.2.x, compatible
                 ;; with Spring 3 and CF Java client.
                 [clj-http                                 "0.7.6"]
                 [cheshire                                 "5.3.1"]]
  :profiles {:1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :master {:dependencies [[org.clojure/clojure "1.8.0-master-SNAPSHOT"]]}
             :dev {:resource-paths ["test/resources"]
                   :plugins [[codox "0.8.10"]]
                   :codox {:sources ["src/clojure"]
                           :output-dir "doc/api"}}}
  :aliases {"all" ["with-profile" "dev:dev,1.7:dev,master"]}
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :javac-options      ["-target" "1.6" "-source" "1.6"]
  :jvm-opts           ["-Dfile.encoding=utf-8"]
  :source-paths       ["src/clojure"]
  :java-source-paths  ["src/java"])
