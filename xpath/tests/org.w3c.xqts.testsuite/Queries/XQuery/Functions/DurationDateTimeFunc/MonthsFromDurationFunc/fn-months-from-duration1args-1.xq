(:*******************************************************:)
(:Test: months-from-duration1args-1                       :)
(:Written By: Carmelo Montanez                            :)
(:Date: Wed Apr 13 09:47:37 GMT-05:00 2005                :)
(:Purpose: Evaluates The "months-from-duration" function :)
(: with the arguments set as follows:                    :)
(:$arg = xs:yearMonthDuration(lower bound)              :)
(:*******************************************************:)

fn:months-from-duration(xs:yearMonthDuration("P0Y0M"))