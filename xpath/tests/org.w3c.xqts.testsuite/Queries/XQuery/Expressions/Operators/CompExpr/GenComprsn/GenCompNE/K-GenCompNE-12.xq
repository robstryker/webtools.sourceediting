(:*******************************************************:)
(: Test: K-GenCompNE-12                                  :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:37+02:00                       :)
(: Purpose: General comparison causing numeric promotion from xs:untypedAtomic. :)
(:*******************************************************:)
not(1 != xs:untypedAtomic("1"))