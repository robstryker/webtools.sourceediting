(:*******************************************************:)
(: Test: K-CollationProlog-4                             :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:39+02:00                       :)
(: Purpose: A 'declare default collation' expression may occur only once. :)
(:*******************************************************:)

	declare default collation "http://www.w3.org/2005/xpath-functions/collation/codepoint";
	declare default collation "http://www.w3.org/2005/xpath-functions/collation/codepoint";
	default-collation() eq "http://www.w3.org/2005/xpath-functions/collation/codepoint"