(:*******************************************************:)
(: Test: K-SeqExprCast-1137                              :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: 'castable as' involving xs:gDay as source type and xs:time as target type should always evaluate to false. :)
(:*******************************************************:)
not(xs:gDay("---03") castable as xs:time)