(:*******************************************************:)
(:Test: maxnni1args-3                                     :)
(:Written By: Carmelo Montanez                            :)
(:Date: Fri Dec 10 10:15:47 GMT-05:00 2004                :)
(:Purpose: Evaluates The "max" function                  :)
(: with the arguments set as follows:                    :)
(:$arg = xs:nonNegativeInteger(upper bound)              :)
(:*******************************************************:)

fn:max((xs:nonNegativeInteger("999999999999999999")))