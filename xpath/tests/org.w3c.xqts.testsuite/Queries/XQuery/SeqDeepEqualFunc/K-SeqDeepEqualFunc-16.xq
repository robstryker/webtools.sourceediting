(:*******************************************************:)
(: Test: K-SeqDeepEqualFunc-16                           :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:40+02:00                       :)
(: Purpose: A test whose essence is: `not(deep-equal(xs:decimal("1"), xs:anyURI("example.com")))`. :)
(:*******************************************************:)
not(deep-equal(xs:decimal("1"), xs:anyURI("example.com")))