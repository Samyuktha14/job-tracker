import React, { useState, useRef, useEffect } from "react";
import axiosInstance from "../api/axiosInstance";
import toast from "react-hot-toast";
import { useAuth } from "../context/AuthContext";

const TelegramStatus = () => {
  const { user, refreshUser } = useAuth();

  const [generating, setGenerating] = useState(false);
  const [waitingForLink, setWaitingForLink] = useState(false);
  const [unlinking, setUnlinking] = useState(false);
  const [countdown, setCountdown] = useState(15);

  const abortControllerRef = useRef(null);
  const timeoutRef = useRef(null);

  const linked = !!user?.telegramLinked;

  // ===============================
  // CLEANUP ON UNMOUNT (Modal Close)
  // ===============================
  useEffect(() => {
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  // ===============================
  // GENERATE LINK + ADVANCED POLLING
  // ===============================
  const handleGenerateLink = async () => {
    try {
      setGenerating(true);

      const res = await axiosInstance.post("/telegram/link-token");
      const link = res.data.telegramLink;

      if (!link) {
        toast.error("Failed to generate Telegram link.");
        return;
      }

      window.open(link, "_blank");

      setWaitingForLink(true);
      setCountdown(15);

      let attempts = 0;
      const maxAttempts = 5;

      const poll = async () => {
        attempts++;

        abortControllerRef.current = new AbortController();

        try {
          const userRes = await axiosInstance.get("/users/me", {
            signal: abortControllerRef.current.signal
          });

          if (userRes.data.telegramLinked) {
            await refreshUser();
            setWaitingForLink(false);
            toast.success("Telegram linked successfully!");
            return;
          }

          if (attempts >= maxAttempts) {
            setWaitingForLink(false);
            toast("Still not linked? Please click Start in Telegram.");
            return;
          }

          timeoutRef.current = setTimeout(poll, 3000);

        } catch (err) {
         if (err.name !== "CanceledError") {
        setWaitingForLink(false);
        toast.error("Unable to verify Telegram link. Please try again.");
          }
        }
      };

      timeoutRef.current = setTimeout(poll, 3000);

    } catch (err) {
      toast.error("Failed to generate link");
    } finally {
      setGenerating(false);
    }
  };

  // ===============================
  // COUNTDOWN TIMER
  // ===============================
  useEffect(() => {
    if (!waitingForLink) return;

    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [waitingForLink]);

  // ===============================
  // UNLINK
  // ===============================
  const handleUnlink = async () => {
    if (!window.confirm("Are you sure you want to unlink Telegram?")) return;

    try {
      setUnlinking(true);
      await axiosInstance.delete("/telegram/unlink");
      await refreshUser();
      toast.success("Telegram unlinked successfully");
    } catch (err) {
      toast.error("Failed to unlink Telegram");
    } finally {
      setUnlinking(false);
    }
  };

  // ===============================
  // LINKED UI
  // ===============================
  if (linked) {
    return (
      <div className="mb-4 p-4 bg-green-50 border border-green-300 rounded text-sm text-green-700">
        ✅ Telegram linked — reminders will be sent automatically.
        <br />
        <button
          onClick={handleUnlink}
          disabled={unlinking}
          className="mt-3 bg-red-500 hover:bg-red-600 text-white px-4 py-1 rounded disabled:opacity-50"
        >
          {unlinking ? "Unlinking..." : "Unlink Telegram"}
        </button>
      </div>
    );
  }

  // ===============================
  // NOT LINKED UI
  // ===============================
  return (
    <div className="mb-4 p-4 bg-yellow-50 border border-yellow-300 rounded text-sm text-yellow-700">
      ⚠️ Telegram not linked.
      <br />

      <button
        onClick={handleGenerateLink}
        disabled={generating || waitingForLink}
        className="mt-3 bg-blue-600 hover:bg-blue-700 text-white px-4 py-1 rounded disabled:opacity-50 flex items-center gap-2"
      >
        {generating && "Generating..."}
        {!generating && waitingForLink && (
          <>
            <span className="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
            Waiting... ({countdown}s)
          </>
        )}
        {!generating && !waitingForLink && "Link Telegram"}
      </button>
    </div>
  );
};

export default TelegramStatus;
