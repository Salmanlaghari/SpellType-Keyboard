package com.spelltype.keyboard.domain.developer

/**
 * Developer Keyboard — Special coding layout with symbols, shortcuts, and code snippets
 * Designed for programmers who code on mobile
 */
object DeveloperKeyboard {

    enum class DevMode(val displayName: String, val emoji: String) {
        SYMBOLS("Symbols", "🔣"),
        CODE_SNIPPETS("Snippets", "📋"),
        HTML("HTML", "🌐"),
        CSS("CSS", "🎨"),
        KOTLIN("Kotlin", "🟣"),
        PYTHON("Python", "🐍"),
        JAVASCRIPT("JS", "🟡"),
        SQL("SQL", "🗄️"),
        GIT("Git", "📦"),
        REGEX("Regex", "🔍")
    }

    // Common programming symbols
    val symbolsRow1 = listOf("{", "}", "[", "]", "(", ")", "<", ">", "/", "\\")
    val symbolsRow2 = listOf("=", "+", "-", "*", "%", "&", "|", "^", "~", "!")
    val symbolsRow3 = listOf(";", ":", "'", "\"", ".", ",", "?", "@", "#", "_")

    // Quick code snippets by language
    val snippets = mapOf(
        DevMode.KOTLIN to listOf(
            "fun main() {\n    \n}",
            "val list = listOf()",
            "if (condition) {\n    \n}",
            "when (value) {\n    else -> {}\n}",
            "data class Name(val id: Int)",
            "object Singleton {\n    \n}",
            "companion object {\n    \n}",
            "suspend fun fetchData(): Result<T>",
            "viewModelScope.launch {\n    \n}",
            "LaunchedEffect(Unit) {\n    \n}"
        ),
        DevMode.PYTHON to listOf(
            "def function_name():\n    pass",
            "if condition:\n    pass",
            "for item in iterable:\n    pass",
            "class ClassName:\n    def __init__(self):\n        pass",
            "try:\n    pass\nexcept Exception as e:\n    pass",
            "with open('file') as f:\n    pass",
            "lambda x: x + 1",
            "list comprehension: [x for x in range(10)]",
            "import module",
            "from module import name"
        ),
        DevMode.JAVASCRIPT to listOf(
            "const fn = () => {\n    \n};",
            "if (condition) {\n    \n}",
            "for (let i = 0; i < n; i++) {\n    \n}",
            "async function fetchData() {\n    const res = await fetch(url);\n}",
            "try {\n    \n} catch (err) {\n    \n}",
            "document.querySelector('.class')",
            "addEventListener('click', () => {});",
            "export default Component;",
            "import { name } from 'module';",
            "console.log();"
        ),
        DevMode.HTML to listOf(
            "<!DOCTYPE html>",
            "<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n</head>\n<body>\n</body>\n</html>",
            "<div class=\"\">\n    \n</div>",
            "<a href=\"\"></a>",
            "<img src=\"\" alt=\"\">",
            "<input type=\"text\" placeholder=\"\">",
            "<button onclick=\"\"></button>",
            "<ul>\n    <li></li>\n</ul>",
            "<script src=\"\"></script>",
            "<link rel=\"stylesheet\" href=\"\">"
        ),
        DevMode.CSS to listOf(
            "display: flex;",
            "display: grid;",
            "position: relative;",
            "justify-content: center;",
            "align-items: center;",
            "margin: 0 auto;",
            "padding: 16px;",
            "border-radius: 8px;",
            "box-shadow: 0 2px 4px rgba(0,0,0,0.1);",
            "transition: all 0.3s ease;",
            "@media (max-width: 768px) {}",
            ":root {\n    --primary: #000;\n}"
        ),
        DevMode.SQL to listOf(
            "SELECT * FROM table;",
            "INSERT INTO table (col) VALUES ();",
            "UPDATE table SET col = val WHERE id = 1;",
            "DELETE FROM table WHERE id = 1;",
            "CREATE TABLE name (\n    id INT PRIMARY KEY\n);",
            "ALTER TABLE name ADD col TYPE;",
            "JOIN table2 ON table1.id = table2.fk",
            "GROUP BY col HAVING COUNT(*) > 1",
            "ORDER BY col ASC",
            "LIMIT 10 OFFSET 0"
        ),
        DevMode.GIT to listOf(
            "git init",
            "git add .",
            "git commit -m \"\"",
            "git push origin main",
            "git pull origin main",
            "git checkout -b branch",
            "git merge branch",
            "git stash",
            "git log --oneline",
            "git reset --soft HEAD~1"
        ),
        DevMode.REGEX to listOf(
            "\\d+ (digits)",
            "\\w+ (word chars)",
            "\\s+ (whitespace)",
            "[a-zA-Z] (letters)",
            "[0-9] (numbers)",
            "^start (begins with)",
            "end$ (ends with)",
            "(group) (capture group)",
            "a|b (or)",
            "x{n,m} (repeat n-m times)"
        )
    )

    // Developer shortcuts
    val shortcuts = mapOf(
        "//" to "// TODO: ",
        "/*" to "/*  */",
        "->" to "→ () => {}",
        "=>" to "⇒ () => {}",
        "null" to "null",
        "undefined" to "undefined",
        "true" to "true",
        "false" to "false",
        "const" to "const ",
        "let" to "let ",
        "var" to "var ",
        "func" to "function ",
        "async" to "async ",
        "await" to "await ",
        "return" to "return ",
        "import" to "import ",
        "export" to "export ",
        "class" to "class ",
        "extends" to "extends ",
        "interface" to "interface "
    )

    /**
     * Get snippets for a specific dev mode
     */
    fun getSnippets(mode: DevMode): List<String> {
        return snippets[mode] ?: emptyList()
    }

    /**
     * Expand shortcut to full code
     */
    fun expandShortcut(input: String): String? {
        return shortcuts[input.lowercase()]
    }

    /**
     * Get all dev modes
     */
    fun getAllModes(): List<DevMode> = DevMode.values().toList()
}
