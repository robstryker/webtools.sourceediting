(:*******************************************************:)
(: Test: K-SeqExprCast-617                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: 'castable as' involving xs:integer as source type and xs:time as target type should always evaluate to false. :)
(:*******************************************************:)
not(xs:integer("6789") castable as xs:time)