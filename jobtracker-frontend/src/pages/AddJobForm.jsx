// src/components/AddJobForm.jsx
import React, { useState, useEffect } from 'react';
import { auth } from '../firebase';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const AddJobForm = () => {
  const [form, setForm] = useState({
    companyName: '', role: '', appliedDate: '', applicationStatus: '',
    currentStage: '', currentRound: '', totalRounds: '', reminderDate: '',
    notes: '', source: '', applicationLink: '', resumeFile: null
  });

  const navigate = useNavigate();

  useEffect(() => {
    const user = auth.currentUser;
    if (!user) {
      alert("You must be logged in to submit a job application.");
      navigate("/login");
    }
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    setForm(prev => ({ ...prev, resumeFile: e.target.files[0] }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const user = auth.currentUser;
    if (!user) return;

    const formData = new FormData();

    // Combine source + application link
    const combinedSource = form.applicationLink
      ? `${form.source} (${form.applicationLink})`
      : form.source;

    Object.entries(form).forEach(([key, value]) => {
      if (key === 'applicationLink') return; // Don't append separately
      if (key === 'source') {
        formData.append('source', combinedSource);
      } else if (value) {
        formData.append(key, value);
      }
    });

    formData.append("userId", user.uid);

    try {
      const token = await user.getIdToken();

      await axiosInstance.post('/jobs', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`
        }
      });

      alert('Job submitted!');
      setForm({
        companyName: '', role: '', appliedDate: '', applicationStatus: '',
        currentStage: '', currentRound: '', totalRounds: '', reminderDate: '',
        notes: '', source: '', applicationLink: '', resumeFile: null
      });
    } catch (error) {
      console.error('Submit failed:', error);
      alert('Submission error. Check console.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-xl mx-auto bg-white p-6 rounded shadow space-y-4">
      <h2 className="text-xl font-bold text-center">Add Job Application</h2>

      {[
        ["Company Name", "companyName"],
        ["Role", "role"],
        ["Applied Date", "appliedDate", "date"],
        ["Application Status", "applicationStatus", "select"],
        ["Current Stage", "currentStage"],
        ["Current Round", "currentRound", "number"],
        ["Total Rounds", "totalRounds", "number"],
        ["Reminder Date", "reminderDate", "date"],
        ["Notes", "notes"],
        ["Source Name", "source"],
        ["Job Link", "applicationLink"]
      ].map(([label, name, type = "text"]) => (
        <div key={name}>
          <label className="block mb-1 font-medium">
            {label}
            {["companyName", "role", "appliedDate", "applicationStatus"].includes(name) && (
              <span className="text-red-500 ml-1">*</span>
            )}
          </label>
          {type === "select" ? (
            <select
              className="w-full p-2 border rounded"
              name={name}
              value={form[name]}
              onChange={handleChange}
              required
            >
              <option value="">Select Status</option>
              <option value="Applied">Applied</option>
              <option value="Interviewing">Interviewing</option>
              <option value="Rejected">Rejected</option>
              <option value="Selected">Selected</option>
            </select>
          ) : (
            <input
              className="w-full p-2 border rounded"
              type={type}
              name={name}
              value={form[name]}
              onChange={handleChange}
              required={["companyName", "role", "appliedDate", "applicationStatus"].includes(name)}
              placeholder={type === "number" ? `e.g., 2` : ""}
              min={type === "number" ? 0 : undefined}
            />
          )}
        </div>
      ))}

      <div>
        <label className="block mb-1 font-medium">Resume File</label>
        <input
          className="w-full p-2 border rounded"
          type="file"
          name="resumeFile"
          accept=".pdf,.doc,.docx"
          onChange={handleFileChange}
        />
      </div>

      <button type="submit" className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition">
        Submit
      </button>
    </form>
  );
};

export default AddJobForm;
