(:*******************************************************:)
(:Test: deep-equalsht2args-5                              :)
(:Written By: Carmelo Montanez                            :)
(:Date: Fri Dec 10 10:15:47 GMT-05:00 2004                :)
(:Purpose: Evaluates The "deep-equal" function           :)
(: with the arguments set as follows:                    :)
(:$parameter1 = xs:short(lower bound)                    :)
(:$parameter2 = xs:short(upper bound)                    :)
(:*******************************************************:)

fn:deep-equal((xs:short("-32768")),(xs:short("32767")))