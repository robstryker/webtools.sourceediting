(:*******************************************************:)
(: Test: K-ContextLastFunc-10                            :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:41+02:00                       :)
(: Purpose: fn:last() can never return 0('eq'), #2.      :)
(:*******************************************************:)
empty((1, 2, 3, current-time(), current-date(), 6, 7, 8)
[0 eq last()])