(:*******************************************************:)
(: Test: K-SeqDistinctValuesFunc-4                       :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:40+02:00                       :)
(: Purpose: A test whose essence is: `distinct-values("a string", "http://www.w3.org/2005/xpath-functions/collation/codepoint") eq "a string"`. :)
(:*******************************************************:)
distinct-values("a string", "http://www.w3.org/2005/xpath-functions/collation/codepoint")
			eq "a string"