(: Use Cases: STRING :)
(: uses the classpath uri resolver :)

declare function local:books() as element()*{
    let $book  := doc("xquery/string.xml")//book
    return $book
};

declare function local:main() as element()*{
    let $books as element()* := local:books()
    return <books>{$books}</books>
};

local:main()