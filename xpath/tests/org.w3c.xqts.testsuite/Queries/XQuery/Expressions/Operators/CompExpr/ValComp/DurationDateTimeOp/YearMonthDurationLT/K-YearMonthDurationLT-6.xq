(:*******************************************************:)
(: Test: K-YearMonthDurationLT-6                         :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Simple test of 'le' for xs:yearMonthDuration, evaluating to false. :)
(:*******************************************************:)
not(xs:yearMonthDuration("P1999Y10M") le
			   xs:yearMonthDuration("P1999Y9M"))