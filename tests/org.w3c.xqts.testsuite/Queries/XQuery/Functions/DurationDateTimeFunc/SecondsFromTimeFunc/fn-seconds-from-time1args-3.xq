(:*******************************************************:)
(:Test: seconds-from-time1args-3                          :)
(:Written By: Carmelo Montanez                            :)
(:Date: Wed Apr 13 09:47:38 GMT-05:00 2005                :)
(:Purpose: Evaluates The "seconds-from-time" function    :)
(: with the arguments set as follows:                    :)
(:$arg = xs:time(upper bound)                            :)
(:*******************************************************:)

fn:seconds-from-time(xs:time("23:59:59Z"))