(:*******************************************************:)
(: Test: K2-FunctionProlog-5                             :)
(: Written by: Frans Englich                             :)
(: Date: 2006-08-04T17:13:26Z                            :)
(: Purpose: A function requiring xs:integer but is passed an xs:decimal. :)
(:*******************************************************:)
declare function local:myFunction($arg as xs:integer) 
{
        $arg
};
local:myFunction(1.0)