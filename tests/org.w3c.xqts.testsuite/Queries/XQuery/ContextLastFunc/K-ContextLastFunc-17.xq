(:*******************************************************:)
(: Test: K-ContextLastFunc-17                            :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:41+02:00                       :)
(: Purpose: fn:last() can never return anything less or equal to 0(le). :)
(:*******************************************************:)
empty((1, 2, 3, current-time(), current-date(), 6, 7, 8)
[last() le 0])