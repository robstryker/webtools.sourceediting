(:*******************************************************:)
(: Test: K2-TimeAddDTD-1                                 :)
(: Written by: Frans Englich                             :)
(: Date: 2006-08-04T17:13:26Z                            :)
(: Purpose: No '+' operator is available between xs:time and xs:time. :)
(:*******************************************************:)
xs:time("10:10:10") +
                  xs:time("23:10:10")