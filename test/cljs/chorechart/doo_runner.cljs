(ns chorechart.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [chorechart.core-test]))

(doo-tests 'chorechart.core-test)

