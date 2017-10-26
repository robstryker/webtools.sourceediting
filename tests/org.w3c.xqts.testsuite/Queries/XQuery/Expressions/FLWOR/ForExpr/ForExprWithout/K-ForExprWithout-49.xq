(:*******************************************************:)
(: Test: K-ForExprWithout-49                             :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: For-expression involving a simple return statement that in some implementations trigger optimization paths. :)
(:*******************************************************:)
deep-equal((for $fo in trace((1, 2, 3), "msg") return $fo), (1, 2, 3))