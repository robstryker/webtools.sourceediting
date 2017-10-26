(:*******************************************************:)
(:Test: op-time-less-than-9                              :)
(:Written By: Carmelo Montanez                           :)
(:Date: June 13, 2005                                    :)
(:Purpose: Evaluates The "time-less-than" function used  :)
(:together with "or" expression (lt operator).          :)
(:*******************************************************:)
 
(xs:time("13:00:00Z") lt xs:time("17:00:00Z")) or (xs:time("13:00:00Z") lt xs:time("17:00:00Z"))