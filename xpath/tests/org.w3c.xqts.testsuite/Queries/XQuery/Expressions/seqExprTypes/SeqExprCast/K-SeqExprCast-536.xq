(:*******************************************************:)
(: Test: K-SeqExprCast-536                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: 'castable as' involving xs:double as sourceType and xs:NOTATION should fail due to it involving xs:NOTATION. :)
(:*******************************************************:)
not(xs:double("3.3e3") castable as xs:NOTATION)