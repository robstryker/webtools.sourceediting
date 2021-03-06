(:*******************************************************:)
(: Test: K-DateTimeEQ-6                                  :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Test that zone offset +00:00 is equal to Z, in xs:dateTime. :)
(:*******************************************************:)
xs:dateTime("1999-12-04T16:00:12.345+00:00") eq
		xs:dateTime("1999-12-04T16:00:12.345Z")