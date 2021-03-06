# MultiKit Parser
## Bloatless Cross-Platform With Code Preprocessing

MultiKit Parser is a code preprocessor library for making static preprocessor substitutions, made with Kotlin/JS for Node.js.

## Why?

Many cross-platform toolkits inject large runtime libraries to run application code, but cross-platform development shouldn't come at the cost of runtime overhead. MultiKit statically preprocesses application code and outputs code that directly uses native platform features - no runtime libraries.

# How?

This is the MultiKit parser library for use in preprocessor modules. By using this library, preprocessor tools can easily be made to substitute certain function calls with other code that directly uses native features. The same code can be passed through different preprocessor modules to produce code for different environments.

For example, with an HTTP preprocessor module for JavaScript:

```javascript
MultiKit.HTTP.request('GET', myURL).then(response =>
    console.log(MultiKit.HTTP.Response.getStatusCode(response))
)
```

might translate into the following for Node.js:

```javascript
if(!globalThis.__MultiKit)
    globalThis.__MultiKit = {}

if(!__MultiKit.HTTP)
    __MultiKit.HTTP = {}

if(!__MultiKit.HTTP.https)
    __MultiKit.HTTP.https = require('https')

new Promise((resolve, reject) => {
    try {
        __MultiKit.HTTP.https.request(myURL, {method: 'GET'}).on('response', resolve).end()
    } catch(e) {
        reject(e)
    }
}).then(response =>
    console.log(response.statusCode)
)
```

or, for browser:

```javascript
fetch(myURL, {method: 'GET'}).then(response =>
    console.log(response.status)
)
```

## Using the Parser Library

### Create a Kotlin/JS project

Follow [the official guide](https://kotlinlang.org/docs/js-project-setup.html) to make a Kotlin/JS project and target Node.js.

### Use Kotlin/JS IR Compiler

Add the following line to `gradle.properties` if it is not already set:

```
kotlin.js.compiler=ir
```

### Clone This Repository

Run `git clone --depth=1 --single-branch https://github.com/universe-software/multikit-parser.git` to put the `multikit-parser` directory in the root of your project directory. (Or use Git submodules if your project is a Git repository.)

### Include the Library in Gradle Build

In settings.gradle(.kts), include the `multikit-parser` project:

#### For KotlinScript DSL (`settings.gradle.kts`)

```kotlin
includeBuild("multikit-parser")
```

#### For Groovy DSL (`settings.gradle`)

```groovy
includeBuild 'multikit-parser'
```

### Add MultiKit Parser

Add an implementation dependency on `universe.multikit:parser:1.0.0`:

#### For KotlinScript DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("universe.multikit:parser:1.0.0")
}
```

#### For Groovy DSL (`build.gradle`)

```groovy
dependencies {
    implementation 'universe.multikit:parser:1.0.0'
}
```

### Use the Parser Library

```kotlin
import universe.multikit.parser.parse

fun main() {
    parse("YourNamespace", mapOf(
        // ...
    ))
}
```

#### `fun parse(prefix: String, substitutions: Map<String, (Array<String>) -> String>)`

##### Parameters

* `prefix`: The namespace prefix of calls the parser should try to substitute. For example, if the prefix is `MultiKit`, the parser will try to substitute calls in the form of `MultiKit....()`.

* `substitutions`: Map of preprocessor function names to functions that compute the replacement output. A substitution function will be called when the parser encounters that function name (which must start with the prefix and a dot) passing the parameters of the call as an array of strings. The returned string will replace the call in the output.

##### Result

When called, `parse` will read all input from the standard input stream and then begin preprocessing. When done, it will print the output to the standard output stream.