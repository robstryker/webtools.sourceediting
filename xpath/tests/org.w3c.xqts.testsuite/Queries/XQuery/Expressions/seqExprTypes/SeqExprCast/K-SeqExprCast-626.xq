(:*******************************************************:)
(: Test: K-SeqExprCast-626                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: Casting from xs:integer to xs:gDay isn't allowed. :)
(:*******************************************************:)
xs:integer("6789") cast as xs:gDay