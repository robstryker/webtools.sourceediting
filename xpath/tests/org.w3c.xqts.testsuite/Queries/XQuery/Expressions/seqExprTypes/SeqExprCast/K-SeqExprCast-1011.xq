(:*******************************************************:)
(: Test: K-SeqExprCast-1011                              :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: It is not possible to extract an Effective Boolean Value from the type xs:gYear, with the boolean() function. :)
(:*******************************************************:)
boolean(xs:gYear("1999"))