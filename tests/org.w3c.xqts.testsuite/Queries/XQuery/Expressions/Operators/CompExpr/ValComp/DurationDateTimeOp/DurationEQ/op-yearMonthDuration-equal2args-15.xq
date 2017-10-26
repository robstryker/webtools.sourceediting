(:*******************************************************:)
(:Test: op-yearMonthDuration-equal2args-15               :)
(:Written By: Carmelo Montanez                           :)
(:Date: June 3, 2005                                     :)
(:Purpose: Evaluates The "op:yearMonthDuration-equal" operator (le) :)
(: with the arguments set as follows:                    :)
(:$arg1 = xs:yearMonthDuration(lower bound)             :)
(:$arg2 = xs:yearMonthDuration(upper bound)             :)
(:*******************************************************:)

xs:yearMonthDuration("P0Y0M") le xs:yearMonthDuration("P2030Y12M")