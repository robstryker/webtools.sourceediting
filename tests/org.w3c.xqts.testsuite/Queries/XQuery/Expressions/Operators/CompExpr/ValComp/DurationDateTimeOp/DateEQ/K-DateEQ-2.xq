(:*******************************************************:)
(: Test: K-DateEQ-2                                      :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Simple test of 'eq' for xs:date.             :)
(:*******************************************************:)
not(xs:date("2004-08-12") eq xs:date("2003-08-12"))