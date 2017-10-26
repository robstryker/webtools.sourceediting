(:*******************************************************:)
(:Test: adjust-date-to-timezone1args-2                    :)
(:Written By: Carmelo Montanez                            :)
(:Date: Tue Apr 12 16:29:08 GMT-05:00 2005                :)
(:Purpose: Evaluates The "adjust-date-to-timezone" function:)
(: with the arguments set as follows:                    :)
(:$arg = xs:date(mid range)                              :)
(:*******************************************************:)

fn:adjust-date-to-timezone(xs:date("1983-11-17Z"),xs:dayTimeDuration("-PT10H"))