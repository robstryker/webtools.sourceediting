(:*******************************************************:)
(: Test: K-SeqExprCast-879                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: 'castable as' involving xs:time as source type and xs:date as target type should always evaluate to false. :)
(:*******************************************************:)
not(xs:time("03:20:00-05:00") castable as xs:date)