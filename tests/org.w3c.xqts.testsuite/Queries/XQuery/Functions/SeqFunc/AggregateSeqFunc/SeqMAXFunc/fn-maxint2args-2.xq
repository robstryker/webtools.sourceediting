(:*******************************************************:)
(:Test: maxint2args-2                                     :)
(:Written By: Carmelo Montanez                            :)
(:Date: Fri Dec 10 10:15:47 GMT-05:00 2004                :)
(:Purpose: Evaluates The "max" function                  :)
(: with the arguments set as follows:                    :)
(:$arg1 = xs:int(mid range)                              :)
(:$arg2 = xs:int(lower bound)                            :)
(:*******************************************************:)

fn:max((xs:int("-1873914410"),xs:int("-2147483648")))