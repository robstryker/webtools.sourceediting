(:*******************************************************:)
(:Test: op-add-dayTimeDuration-to-time2args-3             :)
(:Written By: Carmelo Montanez                            :)
(:Date: Tue Apr 12 16:29:08 GMT-05:00 2005                :)
(:Purpose: Evaluates The "op:add-dayTimeDuration-to-time" operator:)
(: with the arguments set as follows:                    :)
(:$arg1 = xs:time(upper bound)                           :)
(:$arg2 = xs:dayTimeDuration(lower bound)               :)
(:*******************************************************:)

xs:time("23:59:59Z") + xs:dayTimeDuration("P0DT0H0M0S")