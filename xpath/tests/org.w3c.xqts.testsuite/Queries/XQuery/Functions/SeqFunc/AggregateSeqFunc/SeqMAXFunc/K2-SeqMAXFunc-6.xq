(:*******************************************************:)
(: Test: K2-SeqMAXFunc-6                                 :)
(: Written by: Frans Englich                             :)
(: Date: 2006-08-04T17:13:26Z                            :)
(: Purpose: Invoke fn:max() with an unsupported collation and one xs:anyURI value. :)
(:*******************************************************:)
max(xs:anyURI("str1"), "max://example.com/UNSUPPORTED_COLLATION")