(:*******************************************************:)
(: Test: K-LogicExpr-24                                  :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: An or-test applied on fn:count().            :)
(:*******************************************************:)
count((1, 2, 3, timezone-from-time(current-time()), 4)) or count((1, 2, 3, timezone-from-time(current-time()), 4))