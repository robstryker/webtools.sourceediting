(:*******************************************************:)
(: Test: K-gYearEQ-5                                     :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Test that zone offset -00:00 is equal to Z, in xs:gYear. :)
(:*******************************************************:)
xs:gYear("1956-00:00") eq xs:gYear("1956Z")