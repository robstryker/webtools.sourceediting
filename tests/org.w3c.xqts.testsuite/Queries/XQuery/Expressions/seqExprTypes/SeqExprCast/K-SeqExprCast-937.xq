(:*******************************************************:)
(: Test: K-SeqExprCast-937                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: 'castable as' involving xs:date as source type and xs:gMonthDay as target type should always evaluate to true. :)
(:*******************************************************:)
xs:date("2004-10-13") castable as xs:gMonthDay