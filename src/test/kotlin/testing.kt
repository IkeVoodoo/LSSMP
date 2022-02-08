import org.junit.Assert
import javax.script.ScriptEngineManager

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

fun tokenize(inp: String): List<Token> {
    val buf = StringBuilder()
    var inString = false
    var conditionStart: Int
    var conditionEnd: Int
    var i = 0
    while(i < inp.length) {
        val char = inp[i]
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
            for(j in i+1 until inp.length) {
                if(inp[j] == ')') {
                    conditionEnd = j
                    break
                }
            }
            if(conditionEnd == -1)
                throw IllegalArgumentException("Unmatched parenthesis")
            buf.append(" ")
            buf.append(inp.substring(conditionStart, conditionEnd+1)
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

fun main() {
    val factory = ScriptEngineManager().getEngineByExtension("js").factory
    Assert.assertNotNull(factory)
    val engine = factory.scriptEngine
    //val input = "IF (\"2e4d673c-89d8-4955-93d0-fd3fb2124e67\" is \"b5728217-5b32-4fea-b6ad-27c4a1154f2c\") IF(false) \"a\" ELSE IF(false) \"say The player is IkeVoodoo!\" ELSE IF (\"b5728217-5b32-4fea-b6ad-27c4a1154f2c\" is \"b5728217-5b32-4fea-b6ad-27c4a1154f2c\") \"say The player is Semmieboy_YT\" ELSE \"say The player is not IkeVoodoo\""

    val input = "if (%player uuid% is \"2e4d673c-89d8-4955-93d0-fd3fb2124e67\") \"say it's ike!\""
        .replace("%player uuid%", "\"2e4d673c-89d8-4955-93d0-fd3fb2124e67\"")

    fun toString(tokens: List<Token>): String {
        val sb = StringBuilder()
        tokens.forEach {
            when(it.type) {
                TokenType.IF -> sb.append("if ")
                TokenType.ELSE -> sb.append("} else ")
                TokenType.CONDITION -> sb.append("(${it.value.toString()}) {")
                TokenType.STRING -> sb.append("\"${it.value.toString()}\"")
            }
        }
        return sb.toString()
    }

    fun run(): String {
        var v = toString(tokenize(input))
        while(true) {
            try {
                return (engine.eval(v) ?: "null").toString()
            } catch (e: Exception) {
                if(e.message?.contains("Expected } but found eof") == true) {
                    val matcher = "[0-9]+:(?<column>[0-9]+)".toPattern().matcher(e.message!!)
                    if(matcher.find()) {
                        val column = matcher.group("column").toInt()
                        if(column >= input.length) v += "}"
                    }
                } else {
                    e.printStackTrace()
                    return "Error"
                }
            }
        }
    }
    println(run())
}