(:*******************************************************:)
(:Test: minnpi2args-1                                     :)
(:Written By: Carmelo Montanez                            :)
(:Date: Fri Dec 10 10:15:47 GMT-05:00 2004                :)
(:Purpose: Evaluates The "min" function                  :)
(: with the arguments set as follows:                    :)
(:$arg1 = xs:nonPositiveInteger(lower bound)             :)
(:$arg2 = xs:nonPositiveInteger(lower bound)             :)
(:*******************************************************:)

fn:min((xs:nonPositiveInteger("-999999999999999999"),xs:nonPositiveInteger("-999999999999999999")))