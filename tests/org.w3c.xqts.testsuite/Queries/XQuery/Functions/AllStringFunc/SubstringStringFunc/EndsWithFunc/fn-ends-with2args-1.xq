(:*******************************************************:)
(:Test: ends-with2args-1                                  :)
(:Written By: Carmelo Montanez                            :)
(:Date: Fri Dec 10 10:15:46 GMT-05:00 2004                :)
(:Purpose: Evaluates The "ends-with" function            :)
(: with the arguments set as follows:                    :)
(:$arg1 = xs:string(lower bound)                         :)
(:$arg2 = xs:string(lower bound)                         :)
(:*******************************************************:)

fn:ends-with(xs:string("This is a characte"),xs:string("This is a characte"))