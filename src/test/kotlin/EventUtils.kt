import javax.script.ScriptEngine

class EventUtils {

    companion object {

        private var engine: ScriptEngine? = null
        private val slashRegex = "^/+".toRegex()

        /*init {
            var containsJS = false
            val manager = ScriptEngineManager()

            manager.engineFactories.forEach {
                if(it.engineName == "js") {
                    containsJS = true
                }
            }

            if(containsJS) {
                val en = try {
                    manager.getEngineByExtension("js")
                } catch (e: Exception) {
                    null
                }

                if(en == null) {
                    LSSMP.INSTANCE.logger.severe("JS Engine is null, IF statements will not work!")
                } else {
                    val factory = en.factory
                    Assert.assertNotNull("ScriptEngineManager failed to find JavaScript engine", factory)
                    engine = factory.scriptEngine
                }
            }
        }*/

        fun String.tryEvaluate(placeholders: Map<String, Any>): String {
            //if(engine == null) {
                var result = this
                placeholders.forEach {
                    result = result.replace(it.key, it.value.toString())
                }
                return result
            //}
            //return evaluate(placeholders).replace(slashRegex, "")
        }
/*
        fun String.evaluate(placeholders: Map<String, Any>): String {
            val tokens = tokenize()
            val valid = tokens.validate()

            if(valid == null) {
                var result = this
                placeholders.forEach { (key, value) ->
                    result = result.replace(key, if(value is String) "\"$value\"" else value.toString())
                }
                return result
            }

            // They are using if statements

            if(!valid) {
                var result = this
                placeholders.forEach { (key, value) ->
                    result = result.replace(key, if(value is String) "\"$value\"" else value.toString())
                }
                return result
            }

            // They are using valid if statements

            val sb = StringBuilder()
            tokens.forEach {
                when(it.type) {
                    TokenType.IF -> sb.append("if ")
                    TokenType.ELSE -> sb.append("} else ")
                    TokenType.CONDITION -> {
                        var v = it.value.toString()
                        val stringPos = mutableMapOf<Int, Int>()

                        fun updatePositions() {
                            stringPos.clear()
                            var inString = false
                            var stringStart = 0
                            var stringEnd: Int

                            for (i in v.indices) {
                                if (v[i] == '"') {
                                    if (!inString) {
                                        inString = true
                                        stringStart = i
                                    } else {
                                        inString = false
                                        stringEnd = i
                                        stringPos[stringStart] = stringEnd
                                    }
                                }
                            }
                        }

                        updatePositions()

                        placeholders.forEach { (key, value) ->
                            while(v.contains(key)) {
                                val i = v.indexOf(key)
                                var insideString = false
                                for ((k, v1) in stringPos) {
                                    if (i in k..v1) {
                                        insideString = true
                                        break
                                    }
                                }
                                v = v.replaceRange(
                                    i,
                                    i + key.length,
                                    if (value is String && !insideString) "\"$value\"" else value.toString()
                                )
                                updatePositions()
                            }
                        }
                        sb.append("($v) {")
                    }
                    TokenType.STRING -> {
                        var v = it.value.toString()
                        placeholders.forEach { (key, value) ->
                            v = v.replace(key, value.toString())
                        }
                        sb.append("\"$v\"")
                    }
                }
            }
            var js = sb.toString()

            placeholders.forEach { (key, value) ->
                js = js.replace(key, if(value is String) "\"$value\"" else value.toString())
            }

            while(true) {
                try {
                    return (engine?.eval(js) ?: "null").toString()
                } catch (e: Exception) {
                    if(e.message?.contains("Expected } but found eof") == true) {
                        val matcher = "[0-9]+:(?<column>[0-9]+)".toPattern().matcher(e.message!!)
                        if(matcher.find()) {
                            val column = matcher.group("column").toInt()
                            if(column >= this.length) js += "}"
                        }
                    } else {
                        e.printStackTrace()
                        return this
                    }
                }
            }
        }

        enum class TokenType {
            IF,
            ELSE,
            CONDITION,
            STRING
        }

        open class Token(val type: TokenType, val value: Any?) {
            override fun toString(): String {
                return "$type${if (value != null) ": $value" else ""}"
            }
        }

        object IfToken : Token(TokenType.IF, null)
        object ElseToken: Token(TokenType.ELSE, null)

        fun String.tokenize(): List<Token> {
            val buf = StringBuilder()
            var inString = false
            var conditionStart: Int
            var conditionEnd: Int
            var i = 0
            while(i < this.length) {
                val char = this[i]
                if(char == '"')
                    inString = !inString

                if(char == ' ' && inString) {
                    buf.append("\\SC32O16")
                    i++
                    continue
                }

                if(char == '(' && !inString) {
                    conditionStart = i
                    conditionEnd = -1
                    for(j in i+1 until this.length) {
                        if(this[j] == ')') {
                            conditionEnd = j
                            break
                        }
                    }
                    if(conditionEnd == -1)
                        throw IllegalArgumentException("Unmatched parenthesis")
                    buf.append(" ")
                    buf.append(this.substring(conditionStart, conditionEnd+1)
                        .replace("is less than", "<")
                        .replace("is greater than", ">")
                        .replace("is equal to", "==")
                        .replace("is not equal to", "!=")
                        .replace("less than", "<")
                        .replace("greater than", ">")
                        .replace("equal to", "==")
                        .replace("not equal to", "!=")
                        .replace("is not", "!=")
                        .replace("is", "==")
                        .replace(" ", "\\SC32O16"))
                    buf.append(" ")
                    i = conditionEnd + 1
                    continue
                }

                buf.append(char)
                i++
            }

            val tokens = buf.toString().split(" ")
            buf.clear()

            val tokenList = mutableListOf<Token>()
            for (token in tokens) {
                if(token.uppercase() == "IF")
                    tokenList.add(IfToken)
                else if(token.uppercase() == "OTHERWISE" || token.uppercase() == "ELSE")
                    tokenList.add(ElseToken)
                else if(token.matches("\\(.*\\)".toRegex()))
                    tokenList.add(Token(TokenType.CONDITION, token.substring(1, token.length - 1).replace("\\SC32O16", " ")))
                else if(token.matches("\"[^\"]*\"".toRegex()))
                    tokenList.add(Token(TokenType.STRING, token.substring(1, token.length - 1).replace("\\SC32O16", " ")))
            }
            return tokenList
        }

        fun List<Token>.validate(): Boolean? {
            if(!this.contains(IfToken))
                return null

            var valid = true
            var i = 0
            while (i < this.size && valid) {
                val token = this[i]
                if(token.type == TokenType.IF) {
                    val next = this.getOrNull(++i)
                    if(next == null || next.type != TokenType.CONDITION)
                        valid = false
                    continue
                }

                if(i == this.size - 1 && token.type != TokenType.STRING)
                    valid = false
                i++
            }
            return valid
        }*/
    }

}