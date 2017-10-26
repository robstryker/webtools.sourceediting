(: Name: copynamespace-10 :)
(: Written by: Carmelo Montanez :)
(: Description: Evaluates copy namespace declaration with value set to "preserve inherit" .:)
(: Use global variables and namespaces with prefixes.:)

declare copy-namespaces preserve,inherit;

declare variable $existingElement := <existingElement xmlns:existingNamespace="www.existingnamespace.com">{"Existing Content"}</existingElement>;
declare variable $new := <newElement xmlns:newNamespace = "www.mynamespace.com">{$existingElement}</newElement>;

(: insert-start :)
declare variable $input-context1 external;
(: insert-end :)

for $var in (in-scope-prefixes($new/existingElement))
order by $var ascending return $var