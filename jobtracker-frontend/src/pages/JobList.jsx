 // src/components/JobList.jsx
import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { auth } from '../firebase';
import axiosInstance from '../api/axiosInstance'; // adjust as needed


const JobList = () => {
  const [jobs, setJobs] = useState([]);
  const [status, setStatus] = useState('');
  const [companySearch, setCompanySearch] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [editingId, setEditingId] = useState(null);

  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    companyName: '', role: '', applicationStatus: '', appliedDate: '',
    reminderDate: '', rejectionDate: '', notes: '', currentStage: '',
    currentRound: '', totalRounds: '', source: '', resumeFileName: ''
  });

  useEffect(() => {
    if (!auth.currentUser) {
      alert("You must be logged in to view job applications.");
      navigate("/login");
    }
  }, [navigate]);

  const fetchJobs = useCallback(async () => {
    try {
      const user = auth.currentUser;
      if (!user) return;

      let url = `/jobs/paged?page=${page}&size=${size}`;
      if (status)
        url = `/jobs/status?userId=${user.uid}&status=${status}&page=${page}&size=${size}`;
      else if (companySearch)
        url = `/jobs/search?userId=${user.uid}&company=${companySearch}&page=${page}&size=${size}`;

      const res = await axiosInstance.get(url);
      setJobs(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error('Fetch error:', err.message);
    }
  }, [page, status, companySearch, size]);

  useEffect(() => {
    fetchJobs();
  }, [fetchJobs]);

  const deleteJob = async (id) => {
    try {
      await axiosInstance.delete(`/jobs/${id}`);
      fetchJobs();
    } catch (err) {
      console.error('Delete error:', err.message);
    }
  };

  const editJob = (job) => {
    setEditingId(job.id);
    setFormData({
      companyName: job.companyName,
      role: job.role,
      applicationStatus: job.applicationStatus,
      appliedDate: job.appliedDate,
      reminderDate: job.reminderDate || '',
      rejectionDate: job.rejectionDate || '',
      notes: job.notes,
      currentStage: job.currentStage || '',
      currentRound: job.currentRound?.toString() || '',
      totalRounds: job.totalRounds?.toString() || '',
      source: job.source || '',
      resumeFileName: job.resumeFileName || ''
    });
  };

  const handleSave = async () => {
    try {
      const user = auth.currentUser;
      if (!user) return;

      const updatedData = {
        ...formData,
        currentRound: parseInt(formData.currentRound) || 0,
        totalRounds: parseInt(formData.totalRounds) || 0,
        userId: user.uid,
        rejectionDate:
          formData.applicationStatus === "Rejected"
            ? formData.rejectionDate || new Date().toISOString().split("T")[0]
            : null,
      };

      await axiosInstance.put(`/jobs/${editingId}`, updatedData);
      await fetchJobs();
      setEditingId(null);
      setFormData({
        companyName: '', role: '', applicationStatus: '', appliedDate: '',
        reminderDate: '', rejectionDate: '', notes: '', currentStage: '',
        currentRound: '', totalRounds: '', source: '', resumeFileName: ''
      });
    } catch (err) {
      console.error('Update error:', err.message);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "applicationStatus") {
      if (value === "Rejected") {
        const today = new Date();
        const localDate = new Date(today.getTime() - today.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
        setFormData(prev => ({ ...prev, [name]: value, rejectionDate: localDate }));
      } else {
        setFormData(prev => ({ ...prev, [name]: value, rejectionDate: '' }));
      }
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

const downloadResume = async (filename) => {
  if (!filename) {
    alert("No resume file found.");
    return;
  }

  try {
    const response = await axiosInstance.get(`/jobs/download-resume/${filename}`, {
      responseType: 'blob',
    });

    const blob = new Blob([response.data], { type: 'application/pdf' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error("Download failed:", error);
    alert("Failed to download resume.");
  }
};
  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <h2 className="text-2xl font-bold mb-4">🎯 Job Applications</h2>

      <div className="flex flex-wrap gap-4 mb-6">
        <input
          className="border border-gray-300 px-3 py-2 rounded w-full sm:w-64"
          placeholder="Search company"
          value={companySearch}
          onChange={(e) => {
            setCompanySearch(e.target.value);
            setStatus('');
            setPage(0);
          }}
        />
        <select
          className="border border-gray-300 px-3 py-2 rounded w-full sm:w-64"
          value={status}
          onChange={(e) => {
            setStatus(e.target.value);
            setCompanySearch('');
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

      <div className="overflow-x-auto">
        <table className="min-w-full border border-gray-300 text-sm">
          <thead className="bg-gray-100 text-left">
            <tr>
              {["Company", "Role", "Status", "Applied", "Reminder", "Rejection", "Notes", "Stage", "Current", "Total", "Source", "Resume", "Actions"].map((head) => (
                <th key={head} className="p-2 border">{head}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {jobs.length === 0 && (
              <tr>
                <td colSpan="13" className="text-center py-4">No jobs found.</td>
              </tr>
            )}
            {jobs.map(job => (
              editingId === job.id ? (
                <tr key={job.id} className="bg-yellow-50">
                  {["companyName", "role", "applicationStatus", "appliedDate", "reminderDate", "rejectionDate", "notes", "currentStage", "currentRound", "totalRounds", "source"].map((field, index) => (
                    <td key={index} className="p-2 border">
                      {field === "applicationStatus" ? (
                        <select name={field} value={formData[field]} onChange={handleChange} className="border rounded px-2 py-1 w-full">
                          <option value="Applied">Applied</option>
                          <option value="Interviewing">Interviewing</option>
                          <option value="Rejected">Rejected</option>
                          <option value="Selected">Selected</option>
                        </select>
                      ) : field === "notes" ? (
                        <textarea name={field} value={formData[field]} onChange={handleChange} className="border rounded px-2 py-1 w-full" />
                      ) : field === "rejectionDate" ? (
                        <input type="date" name={field} value={formData[field]} readOnly className="bg-gray-100 border rounded px-2 py-1 w-full" />
                      ) : (
                        <input
                          type={["appliedDate", "reminderDate"].includes(field) ? "date" : "text"}
                          name={field}
                          value={formData[field]}
                          onChange={handleChange}
                          className="border rounded px-2 py-1 w-full"
                        />
                      )}
                    </td>
                  ))}
                   <td className="p-2 border text-center">
                    {job.resumeFileName ? (
                      <a href={`http://localhost:8080/api/jobs/download-resume/${job.resumeFileName}`} target="_blank" rel="noopener noreferrer" className="text-blue-600 underline">📄</a>
                    ) : '-'}
                  </td>
                  <td className="p-2 border space-x-2">
                    <button onClick={handleSave} className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded">Save</button>
                    <button onClick={() => setEditingId(null)} className="bg-gray-400 hover:bg-gray-500 text-white px-3 py-1 rounded">Cancel</button>
                  </td>
                </tr>
              ) : (
                <tr key={job.id}>
                  <td className="p-2 border">{job.companyName}</td>
                  <td className="p-2 border">{job.role}</td>
                  <td className="p-2 border">{job.applicationStatus}</td>
                  <td className="p-2 border">{job.appliedDate}</td>
                  <td className="p-2 border">{job.reminderDate}</td>
                  <td className="p-2 border">{job.rejectionDate}</td>
                  <td className="p-2 border">{job.notes}</td>
                  <td className="p-2 border">{job.currentStage}</td>
                  <td className="p-2 border">{job.currentRound}</td>
                  <td className="p-2 border">{job.totalRounds}</td>
                  <td className="p-2 border">{job.source}</td>
                  <td className="p-2 border text-center">
                    {job.resumeFileName ? (
                      <button
                        onClick={() => downloadResume(job.resumeFileName)}
                        className="text-blue-600 underline"
                      >
                        📄
                      </button>
                    ) : (
                      "-"
                    )}
                  </td>
                  <td className="p-2 border space-x-2">
                    <button onClick={() => editJob(job)} className="bg-yellow-400 hover:bg-yellow-500 text-white px-2 py-1 rounded">✏️</button>
                  <button
                  onClick={() => {
                    const confirmed = window.confirm("Are you sure you want to delete this job?");
                    if (confirmed) {
                      deleteJob(job.id);
                    }
                  }}
                  className="bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded"
                >
                  🗑
                </button>

                  </td>
                </tr>
              )
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex justify-between items-center mt-6 text-sm">
        <span className="text-gray-700">Page {page + 1} of {totalPages}</span>
        <div className="space-x-2">
          <button
            onClick={() => setPage(prev => Math.max(prev - 1, 0))}
            disabled={page === 0}
            className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded disabled:opacity-50"
          >
            ⬅ Prev
          </button>
          <button
            onClick={() => setPage(prev => Math.min(prev + 1, totalPages - 1))}
            disabled={page + 1 >= totalPages}
            className="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded disabled:opacity-50"
          >
            Next ➡
          </button>
        </div>
      </div>
    </div>
  );
};

export default JobList;
