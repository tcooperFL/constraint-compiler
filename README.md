# constraint-compiler
[![Build Status](https://travis-ci.org/tcooperFL/constraint-compiler.svg?branch=master)](https://travis-ci.org/tcooperFL/constraint-compiler)
[![codecov](https://codecov.io/gh/tcooperFL/constraint-compiler/branch/master/graph/badge.svg)](https://codecov.io/gh/tcooperFL/constraint-compiler)
[![Clojars Project](https://img.shields.io/clojars/v/constraint-compiler.svg)](https://clojars.org/constraint-compiler)

A Clojure library designed to implement the [full general constraint filtering
capability](https://frontlinetechnologies.atlassian.net/wiki/spaces/AR/pages/147757737/Subscription+Constraints) defined for lion-o.

```clj
[constraint-compiler "0.0.0"]
```

## Usage

This package contains a single function that is designed for a liono-clj to perform
fast filtering on events based on the constraint portion of a subscription. In a liono
implementation, an incoming subscription would establish a bookmark in kafka and an
event stream for events of that type. This constraint would be used to filter out
and ignore all events of that type that the subscriber isn't interested in.


```clj
(constraint-compiler.core/create-pred m)

Compiles the constraint and returns a unary predicat function that
accepts a map and returns true iff it satisfies the constraint. 
```

## License

Copyright © 2018 Frontline Education

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
