(:*******************************************************:)
(: Test: K-DatesSubtract-1                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:36+02:00                       :)
(: Purpose: Simple testing involving operator '-' between xs:date and xs:date. :)
(:*******************************************************:)
xs:date("1999-07-19") - xs:date("1969-11-30")
	                	eq xs:dayTimeDuration("P10823D")