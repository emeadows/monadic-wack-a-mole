Monadic Wack-A-Mole
===================

Basically the more code you write the more you find yourself with for / yields that don’t compile due to random types.
This is to give beginners ideas on how to handle this in an easy and functional way.

Hopefully at the end of this you'll have your code hammer out to get those peskey Monads

Types Of Monads
---------------
This example is quite contrived but tries to show some real world examples.
+ Disjunction - Scalaz Either (shown as \\/)
+ DecodeResult - Argonaut Parsing
+ Result - custom type
+ ConfiguredResult - custom type

Result is a monad which a Disjunction and DecodeResult can be lifted too.
ConfiguredResult is a Kleisli Function that takes the apply function for Result, some configuation.

Cat Herding
-----------
This is my 'not so' real world example.
Initially it starts with some Json which requires parsing (returning a DecodeResult).
If the DecodeResult is a success then the String id is converted to a UUID (returning a Disjunction).
The last part that is the Cat lookup changes between a basic implementation (returning an Option)
to a more complex one which takes some kind of configuration (returning a ConfiguredResult)

The order of complexity of the examples are:
+ CatHerdingForBeginners
+ CatHerdingForBeginners2
+ CatHerdingWithConf
+ CatHerdingWithConf2


      /\_/\       /\_/\       /\_/\       /\_/\
     ( o o )     ( o o )     ( o o )     ( o o )
      >   <       >   <       >   <       >   <