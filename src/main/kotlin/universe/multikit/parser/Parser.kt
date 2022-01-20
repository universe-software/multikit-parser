package universe.multikit.parser

import kotlin.js.Promise

external interface Readable {
    public fun on(event: String, handler: (data: String) -> Unit)
}

external interface Process {
    val stdin: Readable
}

external val process: Process

fun parse(prefix: String, substitutions: Map<String, (Array<String>) -> String>) {
    var source = ""
    var output = ""

    process.stdin.on("data") {
        source += it
    }

    process.stdin.on("close") {
        var didSubstitute = true

        while(didSubstitute) {
            output = ""
            didSubstitute = false
            var i = 0
            var identifier = ""

            while(i < source.length) {
                if(source[i] == '/' && i < source.length - 1) {
                    if(source[i + 1] == '/') {
                        while(!(source[i] == '\n' || i == source.length - 1))
                            i++
                        
                        i++
                    } else if(source[i + 1] == '*') {
                        while(!((source[i] == '/' && source[i - 1] == '*') || i == source.length - 1))
                            i++
                        
                        i++
                    }
                } else if(source[i] == '"') {
                    output += source[i]
                    i++

                    while(source[i] != '"') {
                        output += source[i]
                        i++

                        if(source[i] == '\\') {
                            output += source[i]
                            i++
                        }
                    }

                    output += source[i]
                    i++
                }

                if(i < source.length) {
                    if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_.".contains(source[i]))
                        identifier += source[i]
                    else if(identifier.length > 0) {
                        if(identifier.startsWith(prefix + ".")) {
                            if(i < source.length && source[i] == '(') {
                                i++
                                val args: MutableList<String> = mutableListOf()
                                var arg = ""
                                var parensLevel = 1

                                while(parensLevel > 0) {
                                    if(source[i] == '"') {
                                        arg += source[i]
                                        i++

                                        while(source[i] != '"') {
                                            arg += source[i]

                                            if(source[i] == '\\') {
                                                i++
                                                arg += source[i]
                                            }

                                            i++
                                        }

                                        arg += source[i]
                                    } else if(parensLevel == 1 && source[i] == ',') {
                                        args.add(arg)
                                        arg = ""
                                    } else if(source[i] == '(') {
                                        parensLevel++
                                        arg += source[i]
                                    } else if(source[i] == ')') {
                                        parensLevel--

                                        if(parensLevel > 0)
                                            arg += source[i]
                                    } else
                                        arg += source[i]

                                    i++
                                }

                                val nextChar = source[i]
                                i++

                                if(arg.length > 0)
                                    args.add(arg)
                                
                                if(substitutions.containsKey(identifier)) {
                                    didSubstitute = true
                                    output += substitutions[identifier]!!(args.toTypedArray())
                                } else
                                    throw Exception("Unknown substitution: " + identifier)

                                output += nextChar
                            }
                        } else
                            output += identifier

                        output += source[i]
                        identifier = ""
                    } else
                        output += source[i]
                }

                i++
            }

            source = output
        }

        println(output.substring(IntRange(0, output.length - 3)))
    }
}