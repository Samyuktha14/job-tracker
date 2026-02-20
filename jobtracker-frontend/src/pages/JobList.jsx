// src/components/JobList.jsx
import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { auth } from "../firebase";
import axiosInstance from "../api/axiosInstance";
import { useConfirm } from "../context/ConfirmContext";

const JobList = () => {
  const [jobs, setJobs] = useState([]);
  const [status, setStatus] = useState("");
  const [companySearch, setCompanySearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [editingId, setEditingId] = useState(null);

  const navigate = useNavigate();
  const confirm = useConfirm();

  const [formData, setFormData] = useState({
    companyName: "",
    role: "",
    applicationStatus: "",
    appliedDate: "",
    nextActionType: "",
    nextActionAt: "",
    rejectionDate: "",
    notes: "",
    currentStage: "",
    currentRound: "",
    totalRounds: "",
    source: "",
    resumeLink: ""
  });

  // ================= AUTH CHECK =================
  useEffect(() => {
    if (!auth.currentUser) navigate("/login");
  }, [navigate]);

  // ================= DEBOUNCE SEARCH =================
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(companySearch);
      setPage(0);
    }, 500);

    return () => clearTimeout(timer);
  }, [companySearch]);

  // ================= FETCH JOBS =================
  const fetchJobs = useCallback(async () => {
    try {
      let url = `/jobs/paged?page=${page}&size=${size}`;

      if (status)
        url = `/jobs/status?status=${status}&page=${page}&size=${size}`;
      else if (debouncedSearch)
        url = `/jobs/search?company=${debouncedSearch}&page=${page}&size=${size}`;

      const res = await axiosInstance.get(url);
      setJobs(res.data.content);
      setTotalPages(res.data.totalPages);

    } catch (err) {
      console.error("Fetch error:", err.response?.data || err.message);
    }
  }, [page, status, debouncedSearch, size]);

  useEffect(() => {
    fetchJobs();
  }, [fetchJobs]);

  // ================= DELETE =================
  const handleDelete = async (id) => {
    const isConfirmed = await confirm({
      title: "Delete Application?",
      message: "This will remove the job from your list.",
      danger: true
    });

    if (!isConfirmed) return;

    try {
      await axiosInstance.delete(`/jobs/${id}`);
      fetchJobs();
    } catch (err) {
      console.error("Delete failed:", err.response?.data || err.message);
    }
  };

  // ================= EDIT LOAD =================
  const editJob = (job) => {
    setEditingId(job.id);
    setFormData({
      companyName: job.companyName || "",
      role: job.role || "",
      applicationStatus: job.applicationStatus || "Applied",
      appliedDate: job.appliedDate || "",
      nextActionType: job.nextActionType || "",
      nextActionAt: job.nextActionAt
        ? job.nextActionAt.slice(0, 16)
        : "",
      rejectionDate: job.rejectionDate || "",
      notes: job.notes || "",
      currentStage: job.currentStage || "",
      currentRound: job.currentRound?.toString() || "",
      totalRounds: job.totalRounds?.toString() || "",
      source: job.source || "",
      resumeLink: job.resumeLink || ""
    });
  };

  // ================= SAVE =================
  const handleSave = async () => {
    try {
      if (!editingId) return;

      const now = new Date();

      let nextActionType = formData.nextActionType || null;
      let nextActionAt = formData.nextActionAt
        ? new Date(formData.nextActionAt)
        : null;

      if (formData.applicationStatus === "Rejected") {
        nextActionType = null;
        nextActionAt = null;
      }

      if (!nextActionType) {
        nextActionAt = null;
      }

      if (nextActionAt && nextActionAt <= now) {
        alert("Reminder time must be in future");
        return;
      }

      const updatedData = {
        companyName: formData.companyName?.trim() || "",
        role: formData.role?.trim() || "",
        applicationStatus: formData.applicationStatus,
        appliedDate: formData.appliedDate || null,
        nextActionType,
        nextActionAt: nextActionAt
          ? nextActionAt.toISOString()
          : null,
        rejectionDate: formData.rejectionDate || null,
        notes: formData.notes || null,
        currentStage: formData.currentStage || null,
        currentRound: Number(formData.currentRound) || 0,
        totalRounds: Number(formData.totalRounds) || 0,
        source: formData.source || null,
        resumeLink: formData.resumeLink || null
      };

      await axiosInstance.put(`/jobs/${editingId}`, updatedData);

      setEditingId(null);
      fetchJobs();

    } catch (error) {
      console.error("Update failed:", error.response?.data || error.message);
      alert("Update failed. Check console.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  // ================= RENDER =================
  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-4">🎯 Job Applications</h2>

      {/* Filters */}
      <div className="flex gap-4 mb-6">
        <input
          className="border px-3 py-2 rounded"
          placeholder="Search company"
          value={companySearch}
          onChange={(e) => setCompanySearch(e.target.value)}
        />

        <select
          className="border px-3 py-2 rounded"
          value={status}
          onChange={(e) => {
            setStatus(e.target.value);
            setCompanySearch("");
            setPage(0);
          }}
        >
          <option value="">Filter by status</option>
          <option value="Applied">Applied</option>
          <option value="Interviewing">Interviewing</option>
          <option value="Rejected">Rejected</option>
          <option value="Selected">Selected</option>
        </select>
      </div>

      {/* TABLE */}
      <div className="overflow-x-auto">
        <table className="min-w-full border text-sm">
          <thead className="bg-gray-100">
            <tr>
              {[
                "Company","Role","Status","Applied",
                "Next Action","Reminder Time","Reminder Sent",
                "Rejection","Notes","Stage","Current",
                "Total","Source","Resume","Actions"
              ].map(h => (
                <th key={h} className="p-2 border">{h}</th>
              ))}
            </tr>
          </thead>

          <tbody>
            {jobs.map(job =>
              editingId === job.id ? (
                <tr key={job.id} className="bg-yellow-50">
                  <td className="p-2 border">
                    <input
                      name="companyName"
                      value={formData.companyName}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>
                  <td className="p-2 border">
                    <input
                      name="role"
                      value={formData.role}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <select
                      name="applicationStatus"
                      value={formData.applicationStatus}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    >
                      <option value="Applied">Applied</option>
                      <option value="Interviewing">Interviewing</option>
                      <option value="Rejected">Rejected</option>
                      <option value="Selected">Selected</option>
                    </select>
                  </td>

                  <td className="p-2 border">
                    <input
                      type="date"
                      name="appliedDate"
                      value={formData.appliedDate}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <select
                      name="nextActionType"
                      value={formData.nextActionType}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    >
                      <option value="">None</option>
                      <option value="APPLY">Apply</option>
                      <option value="INTERVIEW">Interview</option>
                      <option value="ASSIGNMENT">Assignment</option>
                      <option value="FOLLOW_UP">Follow-up</option>
                    </select>
                  </td>

                  <td className="p-2 border">
                    <input
                      type="datetime-local"
                      name="nextActionAt"
                      value={formData.nextActionAt}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border text-center">-</td>

                  <td className="p-2 border">
                    <input
                      type="date"
                      name="rejectionDate"
                      value={formData.rejectionDate}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="notes"
                      value={formData.notes}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="currentStage"
                      value={formData.currentStage}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="currentRound"
                      value={formData.currentRound}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="totalRounds"
                      value={formData.totalRounds}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="source"
                      value={formData.source}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <input
                      name="resumeLink"
                      value={formData.resumeLink}
                      onChange={handleChange}
                      className="w-full border rounded px-2 py-1"
                    />
                  </td>

                  <td className="p-2 border">
                    <button
                      onClick={handleSave}
                      className="bg-green-500 text-white px-2 py-1 rounded mr-2"
                    >
                      Save
                    </button>
                    <button
                      onClick={() => setEditingId(null)}
                      className="bg-gray-400 text-white px-2 py-1 rounded"
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              ) : (
                <tr key={job.id}>
                  <td className="p-2 border">{job.companyName}</td>
                  <td className="p-2 border">{job.role}</td>
                  <td className="p-2 border">{job.applicationStatus}</td>
                  <td className="p-2 border">{job.appliedDate}</td>
                  <td className="p-2 border">{job.nextActionType || "-"}</td>
                  <td className="p-2 border">
                    {job.nextActionAt
                      ? new Date(job.nextActionAt).toLocaleString()
                      : "-"}
                  </td>
                  <td className="p-2 border text-center">
                    {!job.nextActionAt
                      ? "-"
                      : job.reminderSent
                        ? "✔ Sent"
                        : "Pending"}
                  </td>
                  <td className="p-2 border">{job.rejectionDate || "-"}</td>
                  <td className="p-2 border">{job.notes || "-"}</td>
                  <td className="p-2 border">{job.currentStage || "-"}</td>
                  <td className="p-2 border">{job.currentRound}</td>
                  <td className="p-2 border">{job.totalRounds}</td>
                  <td className="p-2 border">{job.source || "-"}</td>
                  <td className="p-2 border text-center">
                    {job.resumeLink ? (
                      <a
                        href={job.resumeLink}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 underline"
                      >
                        Open
                      </a>
                    ) : "-"}
                  </td>
                  <td className="p-2 border">
                    <button
                      onClick={() => editJob(job)}
                      className="bg-yellow-400 px-2 py-1 rounded mr-2"
                    >
                      ✏️
                    </button>
                    <button
                      onClick={() => handleDelete(job.id)}
                      className="bg-red-500 text-white px-2 py-1 rounded"
                    >
                      🗑
                    </button>
                  </td>
                </tr>
              )
            )}
          </tbody>
        </table>
      </div>

      {/* PAGINATION */}
      <div className="flex justify-center items-center gap-3 mt-6">
        <button
          onClick={() => setPage(p => Math.max(p - 1, 0))}
          disabled={page === 0}
          className="px-3 py-1 border rounded disabled:opacity-40"
        >
          Prev
        </button>

        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            onClick={() => setPage(i)}
            className={`px-3 py-1 border rounded ${
              page === i ? "bg-blue-500 text-white" : ""
            }`}
          >
            {i + 1}
          </button>
        ))}

        <button
          onClick={() =>
            setPage(p => Math.min(p + 1, totalPages - 1))
          }
          disabled={page === totalPages - 1}
          className="px-3 py-1 border rounded disabled:opacity-40"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default JobList;
