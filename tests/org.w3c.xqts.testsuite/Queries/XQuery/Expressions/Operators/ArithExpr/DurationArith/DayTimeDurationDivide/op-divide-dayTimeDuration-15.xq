(:*******************************************************:)
(:Test: op-divide-dayTimeDuration-15                     :)
(:Written By: Carmelo Montanez                           :)
(:Date: June 29, 2005                                    :)
(:Purpose: Evaluates The "divide-dayTimeDuration" function used  :)
(:together with the numeric-equal operator "le".         :)
(:*******************************************************:)
 
(xs:dayTimeDuration("P10DT10H01M") div 2.0) le xs:dayTimeDuration("P17DT10H02M")