(:*******************************************************:)
(: Test: K-SeqExprCast-167                               :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: Simple test of casting xs:yearMonthDuration to xs:duration. :)
(:*******************************************************:)

			xs:string(xs:duration(xs:yearMonthDuration("P543Y456M")))
			eq "P581Y"