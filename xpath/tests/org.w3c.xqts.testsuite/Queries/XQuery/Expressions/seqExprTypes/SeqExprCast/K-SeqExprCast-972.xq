(:*******************************************************:)
(: Test: K-SeqExprCast-972                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: Casting from xs:gYearMonth to xs:duration isn't allowed. :)
(:*******************************************************:)
xs:gYearMonth("1999-11") cast as xs:duration