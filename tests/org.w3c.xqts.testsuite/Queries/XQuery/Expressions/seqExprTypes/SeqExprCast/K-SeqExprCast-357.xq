(:*******************************************************:)
(: Test: K-SeqExprCast-357                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: '21:01:60' is an invalid lexical representation for xs:time; seconds part can never be larger than 59. :)
(:*******************************************************:)
xs:time("21:01:60")