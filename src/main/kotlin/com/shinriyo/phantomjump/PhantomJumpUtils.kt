package com.shinriyo.phantomjump

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import java.util.regex.Pattern

object PhantomJumpUtils {
    
    /**
     * Detect symbols in comments with more detail
     */
    fun findSymbolsInComment(commentText: String): List<SymbolInfo> {
        val symbols = mutableListOf<SymbolInfo>()
        
        // Detect symbols with various patterns
        val patterns = listOf(
            // Class names (starting with uppercase)
            Pattern.compile("\\b([A-Z][a-zA-Z0-9_]*)\\b") to SymbolType.CLASS,
            // Function names (starting with lowercase, possibly followed by parentheses)
            Pattern.compile("\\b([a-z][a-zA-Z0-9_]*)\\s*\\(") to SymbolType.FUNCTION,
            // Variable names (starting with lowercase)
            Pattern.compile("\\b([a-z][a-zA-Z0-9_]*)\\b") to SymbolType.VARIABLE,
            // Constants (uppercase and underscores)
            Pattern.compile("\\b([A-Z][A-Z0-9_]*)\\b") to SymbolType.CONSTANT
        )
        
        patterns.forEach { (pattern, type) ->
            val matcher = pattern.matcher(commentText)
            while (matcher.find()) {
                val name = matcher.group(1)
                val start = matcher.start(1)
                val end = matcher.end(1)
                
                symbols.add(SymbolInfo(name, type, start, end))
            }
        }
        
        return symbols.distinctBy { it.name }
    }
    
    /**
     * Search for symbols in project (simplified version)
     */
    @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
    fun findSymbolInProject(project: Project, symbolName: String, symbolType: SymbolType): List<PsiElement> {
        // Currently simplified implementation. Advanced search functionality can be implemented in the future
        return emptyList()
    }
    
    /**
     * Data class to hold symbol information
     */
    data class SymbolInfo(
        val name: String,
        val type: SymbolType,
        val startOffset: Int,
        val endOffset: Int
    )
    
    enum class SymbolType {
        CLASS,
        FUNCTION,
        VARIABLE,
        CONSTANT
    }
}