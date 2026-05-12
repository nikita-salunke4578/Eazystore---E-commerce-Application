import { useEffect, useMemo, useRef, useState } from "react";

const initialMessages = [
  {
    id: 1,
    sender: "bot",
    text: "Hi there! 👋 I'm EazyBot — your assistant for questions about ordering, payments, tracking, and more. How can I help?",
  },
];

const BACKEND_CHAT_URL = "/api/v1/chat";

function getFallbackReply(message) {
  if (!message.trim()) {
    return "Please type a question so I can assist you.";
  }

  return (
    "Sorry, I’m not sure about that right now. You can also try the Contact page or email support@eazystore.com for personalized help."
  );
}

export default function Chatbot() {
  const [messages, setMessages] = useState(initialMessages);
  const [input, setInput] = useState("");
  const bottomRef = useRef(null);

  const scrollToBottom = () => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const [isLoading, setIsLoading] = useState(false);

  const handleSend = async () => {
    const trimmed = input.trim();
    if (!trimmed || isLoading) return;

    const userMessage = {
      id: Date.now(),
      sender: "user",
      text: trimmed,
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      const response = await fetch(BACKEND_CHAT_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: trimmed }),
      });

      if (!response.ok) {
        throw new Error(`Server responded with ${response.status}`);
      }

      const json = await response.json();
      const botReply = json?.reply || getFallbackReply(trimmed);

      const botMessage = {
        id: Date.now() + 1,
        sender: "bot",
        text: botReply,
      };

      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      const botMessage = {
        id: Date.now() + 1,
        sender: "bot",
        text: "Unable to reach the support assistant. Please try again in a moment.",
      };
      setMessages((prev) => [...prev, botMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleClear = () => {
    setMessages(initialMessages);
    setInput("");
  };

  const renderedMessages = useMemo(
    () =>
      messages.map((msg) => (
        <div
          key={msg.id}
          className={`flex ${
            msg.sender === "user" ? "justify-end" : "justify-start"
          }`}
        >
          <div
            className={`max-w-[85%] px-4 py-3 rounded-xl mb-3 shadow-sm whitespace-pre-wrap ${
              msg.sender === "user"
                ? "bg-primary text-white"
                : "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-gray-100"
            }`}
          >
            {msg.text}
          </div>
        </div>
      )),
    [messages]
  );

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <h1 className="text-3xl font-bold mb-6 text-center">
        EazyBot Support
      </h1>
      <p className="text-center text-sm text-gray-600 dark:text-gray-300 mb-8">
        Ask anything about ordering, checkout, shipping, or account help.
      </p>
      <div className="border border-gray-200 dark:border-gray-600 rounded-2xl shadow-sm overflow-hidden">
        <div className="h-[540px] overflow-y-auto p-5 bg-white dark:bg-gray-900">
          {renderedMessages}
          <div ref={bottomRef} />
        </div>
        <div className="border-t border-gray-200 dark:border-gray-600 p-4 bg-gray-50 dark:bg-gray-950">
          <div className="flex gap-2">
            <textarea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              rows={2}
              placeholder="Type your question here..."
              className="flex-1 resize-none rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 px-4 py-3 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary"
            />
            <button
              onClick={handleSend}
              className="rounded-lg bg-primary px-5 py-3 text-white font-semibold hover:bg-primary/90 disabled:opacity-50"
              disabled={!input.trim() || isLoading}
            >
              {isLoading ? "Sending..." : "Send"}
            </button>
          </div>
          <div className="mt-3 flex items-center justify-between text-xs text-gray-600 dark:text-gray-400">
            <span>Press Enter to send. Shift+Enter for new line.</span>
            <button
              type="button"
              onClick={handleClear}
              className="underline hover:text-primary"
            >
              Clear chat
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
