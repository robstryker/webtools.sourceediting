(:*******************************************************:)
(: Test: K-NodeNumberFunc-16                             :)
(: Written by: Frans Englich                             :)
(: Date: 2006-10-05T18:29:40+02:00                       :)
(: Purpose: fn:number() inside a predicate.              :)
(:*******************************************************:)
deep-equal((1, 2, 3)[number()], (1, 2, 3))