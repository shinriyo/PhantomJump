package com.shinriyo.phantomjump

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiSearchHelper
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import java.util.regex.Pattern

class PhantomJumpAction : AnAction() {
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        
        if (!isSupportedFile(psiFile)) return
        
        val caretModel = editor.caretModel
        val offset = caretModel.offset
        
        // Search for symbols in comments
        val symbol = findSymbolInComment(psiFile, offset)
        if (symbol != null) {
            navigateToSymbol(project, symbol)
        }
    }
    
    private fun isSupportedFile(psiFile: PsiFile): Boolean {
        val supportedTypes = listOf("JAVA", "Kotlin", "JavaScript", "TypeScript")
        return supportedTypes.contains(psiFile.fileType.name)
    }
    
    private fun findSymbolInComment(psiFile: PsiFile, offset: Int): String? {
        val element = psiFile.findElementAt(offset) ?: return null
        
        // Get comment element
        val commentElement = PsiTreeUtil.getParentOfType(element, PsiComment::class.java)
            ?: return null
        
        val commentText = commentElement.text
        
        // Calculate relative position of cursor
        val commentStartOffset = commentElement.textRange.startOffset
        val relativeOffset = offset - commentStartOffset
        
        // Detect symbols in comment
        return extractSymbolAtPosition(commentText, relativeOffset)
    }
    
    private fun extractSymbolAtPosition(commentText: String, position: Int): String? {
        // General identifier pattern (class names, function names, variable names, etc.)
        val identifierPattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
        val matcher = identifierPattern.matcher(commentText)
        
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            
            if (position >= start && position <= end) {
                return matcher.group()
            }
        }
        
        return null
    }
    
    private fun navigateToSymbol(project: Project, symbolName: String) {
        // Display simple notification (advanced search functionality can be implemented in the future)
        NotificationGroupManager.getInstance()
            .getNotificationGroup("PhantomJump")
            .createNotification(
                "PhantomJump",
                "Searching for symbol: '$symbolName' (Feature in development)",
                NotificationType.INFORMATION
            )
            .notify(project)
    }
    
    override fun update(event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
        
        event.presentation.isEnabledAndVisible = project != null && 
                                               editor != null && 
                                               psiFile != null && 
                                               isSupportedFile(psiFile)
    }
}