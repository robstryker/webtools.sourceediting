(: Name: fn-idref-dtd-23 :)
(: Description: Evaluation of fn:idref with declare ordering. :)

declare ordering ordered;

(: insert-start :)
declare variable $input-context1 external;
(: insert-end :)

<results>{fn:idref("id4", $input-context1/IDS)}</results>