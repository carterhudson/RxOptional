[ ![Download](https://api.bintray.com/packages/carterhudson/maven/rxoptional/images/download.svg) ](https://bintray.com/carterhudson/maven/rxoptional/_latestVersion)
### An implementation of Java 8's Optional&lt;T> using RxJava2 & Java 7

I made this so I could use Optionals while developing for Android. It's just a wrapper around some Rx functionality that acts like a Java 8 Optional. Supports `toObservable()` so you don't have to break the chain to do more robust Rx operations. Check tests for examples.

Note: 1.0.0 is probably the final, stable release as I've moved on to Kotlin for the most part.

gradle:
`compile 'com.carterhudson.tools:rxoptional:1.0.0'`

## License
```
Copyright 2014 Netflix, Inc.
Copyright 2017 Carter Hudson
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0
  
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
