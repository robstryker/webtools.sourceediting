(:*******************************************************:)
(: Test: K-WhereExpr-1                                   :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: A for/where combination where the cardinality of the return statement is crucially affected by the binding sequence. :)
(:*******************************************************:)
(for $fo in (1, 2, 3) where $fo eq 3 return $fo) eq 3