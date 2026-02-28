package com.aicodehelper.service;

import com.aicodehelper.model.Message;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * ChatService provides mock AI responses for demonstration purposes.
 * This is production-ready and can be easily extended to integrate real AI APIs
 * (OpenAI, Claude, etc.) by replacing the response generation logic.
 *
 * Architecture: Separates AI logic from UI controller for clean separation of concerns.
 */
public class ChatService {
    private static final Pattern CODE_HINT_PATTERN = Pattern.compile(
            "(?s)(class\\s+\\w+|public\\s+static\\s+void\\s+main|\\{.*}|;|def\\s+\\w+|function\\s+\\w+|#include\\s*<)");
    private static final Pattern JAVA_PATTERN = Pattern.compile("(?i)(public\\s+class|System\\.out|import\\s+java\\.)");
    private static final Pattern PYTHON_PATTERN = Pattern.compile("(?i)(def\\s+\\w+\\(|print\\(|import\\s+\\w+|:\\s*$)");
    private static final Pattern JS_PATTERN = Pattern.compile("(?i)(function\\s+\\w+|const\\s+\\w+|let\\s+\\w+|=>)");
    private static final Pattern CPP_PATTERN = Pattern.compile("(?i)(#include\\s*<|std::|int\\s+main\\s*\\()");

    /**
     * Generates a mock AI response based on user input.
     * Strategy: Detects if input contains code or errors, provides contextual guidance.
     *
     * Integration Guide for Real AI:
     * Replace this method with:
     * - OpenAI: ChatGPT API call
     * - Anthropic: Claude API call
     * - Local: Ollama/LLaMA integration
     *
     * Example:
     * {@code
     *   public Message generateResponse(String userInput) {
     *       String response = openAiClient.createCompletion(userInput);
     *       return new Message(Message.Sender.BOT, response, LocalDateTime.now(), false);
     *   }
     * }
     *
     * @param userInput The user's message
     * @return A Message object with the AI's response
     */
    public Message generateResponse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return new Message(Message.Sender.BOT,
                    "Please ask me a question or share code for feedback!",
                    LocalDateTime.now(), false);
        }

        String trimmed = userInput.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);

        // Route to appropriate response handler
        if (containsCode(trimmed)) {
            return buildCodeReviewResponse(trimmed);
        }

        if (lower.contains("error") || lower.contains("bug") || lower.contains("fail")) {
            return buildErrorResponse(trimmed, lower);
        }

        return buildGeneralGuidanceResponse();
    }

    // ========== RESPONSE BUILDERS ==========

    private Message buildCodeReviewResponse(String codeInput) {
        String language = detectLanguage(codeInput);
        int lineCount = codeInput.split("\\R", -1).length;
        int charCount = codeInput.length();
        String complexity = lineCount > 25 ? "moderate" : "low";

        String response = """
                ## Code Review Summary
                I detected a `%s` snippet with `%d` lines and `%d` characters.

                ### What Looks Good
                - You have a concrete structure that solves a real problem
                - The logic is decomposed into readable operations
                - You're thinking about practical implementation

                ### Improvement Opportunities
                - Add clearer variable names for long-term maintainability
                - Validate inputs and edge cases (null checks, empty values)
                - Keep each method focused on a single responsibility
                - Consider adding comments for non-obvious logic

                ### Best Practice Pattern
                ```%s
                // 1) Validate input parameters
                // 2) Execute core logic
                // 3) Return or display results
                // 4) Handle edge cases early
                ```

                ### Next Steps
                1. Test with normal, boundary, and invalid inputs
                2. Expected complexity for maintenance: `%s`
                3. Ask me to refactor any specific section
                """.formatted(language, lineCount, charCount, language, complexity);

        return new Message(Message.Sender.BOT, response, LocalDateTime.now(), false);
    }

    private Message buildErrorResponse(String original, String lower) {
        String focusedHint = "Check the stack trace and locate the exact failing line.";
        if (lower.contains("nullpointer")) {
            focusedHint = "A reference is null when used. Initialize it or add a null-check guard.";
        } else if (lower.contains("indexoutofbounds")) {
            focusedHint = "You're accessing an invalid index. Verify collection sizes and boundaries.";
        } else if (lower.contains("syntax")) {
            focusedHint = "A token is missing or misplaced. Check brackets, semicolons, and method signatures.";
        } else if (lower.contains("classnotfound")) {
            focusedHint = "The class file is missing or the import path is incorrect.";
        } else if (lower.contains("type")) {
            focusedHint = "Type mismatch detected. Check variable assignments and method return types.";
        }

        String summary = summarizeForInlineCode(original);

        String response = """
                ## Error Diagnosis & Solution

                ### First Interpretation
                %s

                ### Fast Debugging Checklist
                1. **Read the first error**, not just the last one—that's where the root cause is
                2. **Check the exact line number** and surrounding code (10-20 lines)
                3. **Verify variable types** and method signatures
                4. **Test with minimal input** to isolate the issue
                5. **Add temporary logging** right before the failing line

                ### Information to Share for Best Help
                - Programming language & framework
                - Complete error message (full stack trace)
                - The code section around the failing line (with imports)
                - What you expected vs. what actually happened

                ### Your Message
                ```
                %s
                ```

                ### Pro Tip
                Errors are learning opportunities! Each tells you exactly what went wrong.
                """.formatted(focusedHint, summary);

        return new Message(Message.Sender.BOT, response, LocalDateTime.now(), false);
    }

    private Message buildGeneralGuidanceResponse() {
        String response = """
                ## Welcome to Your AI Coding Assistant

                I'm here to help you learn and solve coding challenges. Here's what I do best:

                ### What I Can Help With
                - **Code Review**: Paste code and ask "What does this do?" or "How can I improve this?"
                - **Error Debugging**: Share errors and I'll help you understand and fix them
                - **Concept Explanation**: Ask about programming concepts in beginner-friendly terms
                - **Code Suggestions**: Request patterns, best practices, or refactoring ideas
                - **Learning Roadmaps**: Ask "How do I learn X?" for structured guidance

                ### Better Prompts = Better Help
                Instead of: "How do I code?"
                Try: "I want to learn Java OOP. Should I start with classes or inheritance?"

                Instead of: "This doesn't work"
                Try: "I get IndexOutOfBoundsException on line 25. Here's my code: [...code...]"

                ### Tips for Best Results
                - Share complete, runnable code examples
                - Include the full error message
                - Mention your current experience level
                - Ask follow-up questions—it helps me refine explanations

                **What would you like to work on?**
                """;

        return new Message(Message.Sender.BOT, response, LocalDateTime.now(), false);
    }

    // ========== UTILITY METHODS ==========

    /**
     * Detects the programming language from code content using regex patterns.
     * @param code The code to analyze
     * @return Language identifier (java, python, javascript, cpp, text)
     */
    private String detectLanguage(String code) {
        if (JAVA_PATTERN.matcher(code).find()) {
            return "java";
        }
        if (PYTHON_PATTERN.matcher(code).find()) {
            return "python";
        }
        if (JS_PATTERN.matcher(code).find()) {
            return "javascript";
        }
        if (CPP_PATTERN.matcher(code).find()) {
            return "cpp";
        }
        return "text";
    }

    /**
     * Checks if text contains code hints using pattern matching.
     * @param text The text to check
     * @return true if code patterns are detected
     */
    private boolean containsCode(String text) {
        return text != null && CODE_HINT_PATTERN.matcher(text).find();
    }

    /**
     * Summarizes long text for inline display, truncating with ellipsis if needed.
     * @param value The text to summarize
     * @return Summarized text (max ~90 chars)
     */
    private String summarizeForInlineCode(String value) {
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 90) {
            return normalized;
        }
        return normalized.substring(0, 87) + "...";
    }
}
