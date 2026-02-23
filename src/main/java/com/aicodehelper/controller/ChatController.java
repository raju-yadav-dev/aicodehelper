package com.aicodehelper.controller;

import com.aicodehelper.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Controller handling chat data and placeholder AI logic.
 */
public class ChatController {
    private static final Pattern CODE_HINT_PATTERN = Pattern.compile(
            "(?s)(class\\s+\\w+|public\\s+static\\s+void\\s+main|\\{.*}|;|def\\s+\\w+|function\\s+\\w+|#include\\s*<)");
    private static final Pattern JAVA_PATTERN = Pattern.compile("(?i)(public\\s+class|System\\.out|import\\s+java\\.)");
    private static final Pattern PYTHON_PATTERN = Pattern.compile("(?i)(def\\s+\\w+\\(|print\\(|import\\s+\\w+|:\\s*$)");
    private static final Pattern JS_PATTERN = Pattern.compile("(?i)(function\\s+\\w+|const\\s+\\w+|let\\s+\\w+|=>)");
    private static final Pattern CPP_PATTERN = Pattern.compile("(?i)(#include\\s*<|std::|int\\s+main\\s*\\()");

    private final List<String> chatHistory = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public void startNewChat() {
        chatHistory.add(0, "Chat " + (chatHistory.size() + 1));
        messages.clear();
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message createUserMessage(String input) {
        return new Message(Message.Sender.USER, input, LocalDateTime.now(), false);
    }

    public Message createBotReply(String input) {
        String trimmed = input == null ? "" : input.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);

        if (containsCode(trimmed)) {
            return new Message(Message.Sender.BOT, buildCodeReviewResponse(trimmed), LocalDateTime.now(), false);
        }

        if (lower.contains("error")) {
            return new Message(Message.Sender.BOT, buildErrorResponse(trimmed, lower), LocalDateTime.now(), false);
        }

        String response = """
                ## Quick Guidance
                I can explain concepts, review snippets, and guide debugging in beginner-friendly steps.

                ### Try One of These
                - Paste code and ask: "What does this do?"
                - Paste an error and ask: "Why is this failing?"
                - Ask for a beginner roadmap: "How do I learn Java OOP?"

                ### Better Prompt Template
                - Goal: what you want the code to do
                - Current behavior: what actually happens
                - Constraints: language, deadline, or style requirement
                """;
        return new Message(Message.Sender.BOT, response, LocalDateTime.now(), false);
    }

    private boolean containsCode(String text) {
        return text != null && CODE_HINT_PATTERN.matcher(text).find();
    }

    private String buildCodeReviewResponse(String codeInput) {
        String language = detectLanguage(codeInput);
        int lineCount = codeInput.split("\\R", -1).length;
        int charCount = codeInput.length();
        String complexity = lineCount > 25 ? "moderate" : "low";

        return """
                ## Code Review Summary
                I detected a `%s` snippet with `%d` lines and `%d` characters.

                ### What Looks Good
                - You already have a concrete structure to solve a real problem.
                - The logic is decomposed into readable operations.

                ### Improvement Opportunities
                - Add clearer variable names for beginners who read this later.
                - Validate edge cases before core logic (null, empty, invalid values).
                - Keep each method focused on one job to reduce bug risk.

                ### Suggested Next Step
                - Run 3 tests: one normal input, one boundary input, one invalid input.
                - Expected complexity: `%s` for beginner maintenance.

                ### Example Refactor Pattern
                ```%s
                // 1) validate input
                // 2) compute result in a small method
                // 3) print/return output separately
                ```
                """.formatted(language, lineCount, charCount, complexity, language);
    }

    private String buildErrorResponse(String original, String lower) {
        String focusedHint = "Check stack trace and the exact failing line first.";
        if (lower.contains("nullpointer")) {
            focusedHint = "A reference is null before usage. Initialize it or guard with a null-check.";
        } else if (lower.contains("indexoutofbounds")) {
            focusedHint = "You are accessing an index outside collection limits. Validate index boundaries.";
        } else if (lower.contains("syntax")) {
            focusedHint = "A token is missing or misplaced. Re-check brackets, semicolons, and method signatures.";
        }

        return """
                ## Error Diagnosis
                I can help you fix this quickly.

                ### First Interpretation
                - %s
                - Keep only one change per run so the root cause stays visible.

                ### Fast Fix Checklist
                - Read the first error in the console, not only the last one.
                - Confirm imports, variable types, and method signatures.
                - Add temporary print/log lines right before the failing line.
                - Re-run with a minimal input that reproduces the issue.

                ### Share This for Better Help
                - Language + framework
                - Full error text
                - The 10-20 lines around the failing code

                ### Your Message Snapshot
                `%s`
                """.formatted(focusedHint, summarizeForInlineCode(original));
    }

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

    private String summarizeForInlineCode(String value) {
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 90) {
            return normalized;
        }
        return normalized.substring(0, 87) + "...";
    }
}
