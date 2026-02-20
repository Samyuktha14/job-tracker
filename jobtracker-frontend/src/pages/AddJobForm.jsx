import React, { useState, useEffect } from "react";
import { auth } from "../firebase";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance";
import TelegramStatus from "../components/TelegramStatus";

const REQUIRED = ["companyName", "role", "appliedDate", "applicationStatus"];

const AddJobForm = () => {
  const navigate = useNavigate();

  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});
  const [accountDisabled, setAccountDisabled] = useState(false);
  const [checkingStatus, setCheckingStatus] = useState(true);

  const [form, setForm] = useState({
    companyName: "",
    role: "",
    appliedDate: "",
    applicationStatus: "",
    currentStage: "",
    currentRound: "",
    totalRounds: "",
    nextActionType: "",
    nextActionAt: "",
    notes: "",
    source: "",
    applicationLink: "",
    resumeLink: ""
  });

  // ===============================
  // AUTH GUARD
  // ===============================
  useEffect(() => {
    if (!auth.currentUser) navigate("/login");
  }, [navigate]);

  // ===============================
  // CHECK USER STATUS
  // ===============================
  useEffect(() => {
    const checkStatus = async () => {
      try {
        const res = await axiosInstance.get("/users/me");
        if (res.data.active === false) setAccountDisabled(true);
      } catch (err) {
        console.error("Failed to check user status");
      } finally {
        setCheckingStatus(false);
      }
    };
    checkStatus();
  }, []);

  // ===============================
  // HELPERS
  // ===============================
  const clean = (v) => (typeof v === "string" ? v.trim() : v);

  const getTodayDate = () =>
    new Date().toISOString().split("T")[0];

  const getLocalDateTime = () => {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (["currentRound", "totalRounds"].includes(name)) {
      if (value !== "" && Number(value) < 0) return;
    }

    // If reminder type cleared → also clear datetime
    if (name === "nextActionType" && value === "") {
      setForm((prev) => ({
        ...prev,
        nextActionType: "",
        nextActionAt: ""
      }));
      return;
    }

    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // ===============================
  // VALIDATION
  // ===============================
  const validate = () => {
    const e = {};
    const now = new Date();

    REQUIRED.forEach((k) => {
      if (!clean(form[k])) e[k] = "Required";
    });

    if (form.appliedDate && form.appliedDate > getTodayDate()) {
      e.appliedDate = "Cannot select future date";
    }

    if (form.resumeLink && !form.resumeLink.startsWith("http")) {
      e.resumeLink = "Must be valid URL";
    }

    if (form.applicationLink && !form.applicationLink.startsWith("http")) {
      e.applicationLink = "Must be valid URL";
    }

    if (form.currentRound && isNaN(form.currentRound)) {
      e.currentRound = "Must be number";
    }

    if (form.totalRounds && isNaN(form.totalRounds)) {
      e.totalRounds = "Must be number";
    }

    if (form.nextActionType && !form.nextActionAt) {
      e.nextActionAt = "Reminder time required";
    }

    if (form.nextActionAt) {
      if (new Date(form.nextActionAt) < now) {
        e.nextActionAt = "Cannot set past reminder";
      }
    }

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  // ===============================
  // SUBMIT
  // ===============================
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (accountDisabled) {
      alert("🚫 Your account is disabled.");
      return;
    }

    if (!validate()) return;
    if (!auth.currentUser) return;

    setSubmitting(true);

    try {
      const combinedSource = clean(form.applicationLink)
        ? `${clean(form.source)} (${clean(form.applicationLink)})`
        : clean(form.source);

      const payload = {
        companyName: clean(form.companyName),
        role: clean(form.role),
        appliedDate: form.appliedDate,
        applicationStatus: form.applicationStatus,
        currentStage: clean(form.currentStage) || null,
        currentRound: Number(form.currentRound) || 0,
        totalRounds: Number(form.totalRounds) || 0,
        nextActionType: form.nextActionType || null,
        nextActionAt: form.nextActionAt || null,
        notes: clean(form.notes) || null,
        source: combinedSource || null,
        resumeLink: clean(form.resumeLink) || null
      };

      await axiosInstance.post("/jobs", payload);

      alert("✅ Job added successfully");
      navigate("/applications");
    } catch (err) {
  console.error(err);
  console.log("Backend error:", err.response?.data);
  alert(
    err.response?.data?.message ||
    JSON.stringify(err.response?.data) ||
    "Failed to add job"
  );
}

  };

  // ===============================
  // UI STATES
  // ===============================
  if (checkingStatus)
    return (
      <div className="text-center mt-10 text-gray-500">
        Checking account status…
      </div>
    );

  if (accountDisabled)
    return (
      <div className="max-w-xl mx-auto mt-10 p-6 bg-red-50 border rounded text-center">
        <h2 className="text-lg font-bold text-red-700">
          Account Disabled
        </h2>
        <p className="text-sm text-red-600 mt-2">
          You cannot add jobs.
        </p>
      </div>
    );

  const fieldError = (name) =>
    errors[name] && (
      <p className="text-red-500 text-xs mt-1">{errors[name]}</p>
    );

  // ===============================
  // FORM
  // ===============================
  return (
    <form
      onSubmit={handleSubmit}
      className="max-w-xl mx-auto bg-white p-6 rounded shadow space-y-4"
    >
      <h2 className="text-xl font-bold text-center">
        Add Job Application
      </h2>

      <TelegramStatus />

      {[
        ["Company Name", "companyName"],
        ["Role", "role"],
        ["Applied Date", "appliedDate", "date"],
        ["Application Status", "applicationStatus", "select"],
        ["Current Stage", "currentStage"],
        ["Current Round", "currentRound", "number"],
        ["Total Rounds", "totalRounds", "number"],
        ["Source", "source"],
        ["Job Link", "applicationLink"]
      ].map(([label, name, type = "text"]) => (
        <div key={name}>
          <label className="block mb-1 font-medium">{label}</label>

          {type === "select" ? (
            <select
              name={name}
              value={form[name]}
              onChange={handleChange}
              className="w-full p-2 border rounded"
            >
              <option value="">Select Status</option>
              <option>Applied</option>
              <option>Interviewing</option>
              <option>Rejected</option>
              <option>Selected</option>
            </select>
          ) : (
            <input
              type={type}
              name={name}
              value={form[name]}
              max={name === "appliedDate" ? getTodayDate() : undefined}
              onChange={handleChange}
              className="w-full p-2 border rounded"
            />
          )}

          {fieldError(name)}
        </div>
      ))}

      {/* Reminder */}
      <div>
        <label className="block mb-1 font-medium">
          Next Action (optional)
        </label>
        <select
          name="nextActionType"
          value={form.nextActionType}
          onChange={handleChange}
          className="w-full p-2 border rounded"
        >
          <option value="">None</option>
          <option value="APPLY">Apply</option>
          <option value="INTERVIEW">Interview</option>
          <option value="ASSIGNMENT">Assignment</option>
          <option value="FOLLOW_UP">Follow-up</option>
        </select>
      </div>

      <div>
        <label className="block mb-1 font-medium">
          Reminder Time
        </label>
      <input
        type="datetime-local"
        name="nextActionAt"
        value={form.nextActionAt}
        min={new Date().toISOString().slice(0,16)}
        onChange={handleChange}
        className="w-full p-2 border rounded"
      />

        {fieldError("nextActionAt")}
      </div>

      <div>
        <label className="block mb-1 font-medium">Notes</label>
        <input
          type="text"
          name="notes"
          value={form.notes}
          onChange={handleChange}
          className="w-full p-2 border rounded"
        />
      </div>

      <div>
        <label className="block mb-1 font-medium">Resume Link</label>
        <input
          type="url"
          name="resumeLink"
          value={form.resumeLink}
          onChange={handleChange}
          className="w-full p-2 border rounded"
        />
        {fieldError("resumeLink")}
      </div>

      <button
        type="submit"
        disabled={submitting}
        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-50"
      >
        {submitting ? "Submitting…" : "Submit"}
      </button>
    </form>
  );
};

export default AddJobForm;
