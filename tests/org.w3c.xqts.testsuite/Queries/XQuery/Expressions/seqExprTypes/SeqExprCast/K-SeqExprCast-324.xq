(:*******************************************************:)
(: Test: K-SeqExprCast-324                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:38+02:00                       :)
(: Purpose: Testing timezone field in xs:dateTime: the hour component cannot be +15. :)
(:*******************************************************:)
xs:dateTime("1999-12-01T23:59:12.432+15:00")