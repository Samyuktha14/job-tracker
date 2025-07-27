import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { PlusCircle, ClipboardList, X } from 'lucide-react';
import { auth } from '../firebase';
import { useAuthState } from 'react-firebase-hooks/auth';

const Dashboard = () => {
  const navigate = useNavigate();
  const [user, loading] = useAuthState(auth);

  const defaultJobShortcuts = [
    {
      name: 'LinkedIn',
      icon: 'https://img.icons8.com/color/48/linkedin.png',
      url: 'https://www.linkedin.com/jobs',
    },
    {
      name: 'Naukri',
      icon: 'https://img.icons8.com/fluency/48/naukri.png',
      url: 'https://www.naukri.com/',
    },
    {
      name: 'Indeed',
      icon: 'https://img.icons8.com/fluency/48/indeed.png',
      url: 'https://www.indeed.com/',
    },
  ];

  const codingPlatforms = [
    {
      name: 'LeetCode',
      icon: 'https://img.icons8.com/external-tal-revivo-shadow-tal-revivo/48/external-level-up-your-coding-skills-and-quickly-land-a-job-logo-shadow-tal-revivo.png',
      url: 'https://leetcode.com/',
    },
    {
      name: 'HackerRank',
      icon: 'https://img.icons8.com/external-tal-revivo-color-tal-revivo/48/external-hackerrank-is-a-technology-company-that-focuses-on-competitive-programming-logo-color-tal-revivo.png',
      url: 'https://www.hackerrank.com/',
    },
    {
      name: 'CodeChef',
      icon: 'https://img.icons8.com/color/48/codechef.png',
      url: 'https://www.codechef.com/',
    },
    {
      name: 'GitHub',
      icon: 'https://img.icons8.com/ios-glyphs/50/github.png',
      url: 'https://github.com/',
    },
  ];

  const [shortcuts, setShortcuts] = useState([]);
  const [newShortcut, setNewShortcut] = useState({ name: '', url: '', icon: '' });

  useEffect(() => {
    if (!loading && !user) {
      navigate('/login');
    }
  }, [user, loading, navigate]);

  useEffect(() => {
    const stored = localStorage.getItem('shortcuts');
    setShortcuts(stored ? JSON.parse(stored) : defaultJobShortcuts);
  }, []);

  useEffect(() => {
    localStorage.setItem('shortcuts', JSON.stringify(shortcuts));
  }, [shortcuts]);

  const handleAddShortcut = () => {
    if (!newShortcut.name || !newShortcut.url) {
      alert('Name and URL are required!');
      return;
    }
    setShortcuts((prev) => [...prev, newShortcut]);
    setNewShortcut({ name: '', url: '', icon: '' });
  };

  const handleRemoveShortcut = (index) => {
    if (window.confirm('Remove this shortcut?')) {
      setShortcuts((prev) => prev.filter((_, i) => i !== index));
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-tr from-slate-100 via-white to-indigo-100 p-6">
      <div className="max-w-6xl mx-auto space-y-6">

        {/* Top Buttons */}
        <div className="grid sm:grid-cols-2 gap-4">
          <Link
            to="/add"
            className="flex items-center justify-center gap-2 bg-indigo-700 text-white py-4 px-6 rounded-xl shadow hover:bg-indigo-800 transition"
          >
            <PlusCircle className="w-5 h-5" />
            <span className="font-semibold">Add New Application</span>
          </Link>

          <Link
            to="/applications"
            className="flex items-center justify-center gap-2 bg-slate-800 text-white py-4 px-6 rounded-xl shadow hover:bg-slate-900 transition"
          >
            <ClipboardList className="w-5 h-5" />
            <span className="font-semibold">View All Applications</span>
          </Link>
        </div>

        {/* Main Grid */}
        <div className="grid md:grid-cols-2 gap-6">

          {/* Motivation Section */}
          <div className="bg-white rounded-2xl shadow-md p-6 border border-slate-200">
            <h2 className="text-xl font-semibold text-indigo-700 mb-3">📈 Stay Focused</h2>
            <ul className="list-disc space-y-2 text-gray-700 pl-6 text-sm">
              <li>“Success is where preparation and opportunity meet.”</li>
              <li>One step at a time. Your effort compounds daily.</li>
              <li>Dev mode on. Results loading... ⌛</li>
            </ul>
          </div>

          {/* Coding Practice Platforms */}
          <div className="bg-white rounded-2xl shadow-md p-6 border border-slate-200">
            <h2 className="text-xl font-semibold text-indigo-700 mb-4">💻 Coding Practice</h2>
            <div className="grid grid-cols-2 gap-4">
              {codingPlatforms.map((p, index) => (
                <a
                  key={index}
                  href={p.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex flex-col items-center text-center bg-slate-50 hover:bg-slate-100 p-4 rounded-lg shadow-sm transition"
                >
                  <img src={p.icon} alt={p.name} className="h-10 w-10 mb-2 object-contain" />
                  <p className="text-sm font-medium text-gray-800">{p.name}</p>
                </a>
              ))}
            </div>
          </div>
        </div>

        {/* Job Portals & Add Shortcut */}
        <div className="bg-white rounded-2xl shadow-md p-6 border border-slate-200">
          <h2 className="text-xl font-semibold text-indigo-700 mb-4">🔗 Job Search Portals</h2>

          <div className="grid grid-cols-3 sm:grid-cols-4 gap-4 mb-4">
            {shortcuts.map((s, index) => (
              <div key={index} className="relative group">
                <a
                  href={s.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex flex-col items-center text-center bg-slate-50 hover:bg-slate-100 p-4 rounded-lg shadow-sm transition"
                >
                  {s.icon && (
                    <img
                      src={s.icon}
                      alt={s.name}
                      onError={(e) => (e.target.style.display = 'none')}
                      className="h-10 w-10 mb-2 object-contain"
                    />
                  )}
                  <p className="text-sm font-medium text-gray-800">{s.name}</p>
                </a>
                <button
                  onClick={() => handleRemoveShortcut(index)}
                  className="absolute top-1 right-1 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition"
                  title="Remove"
                >
                  <X size={14} />
                </button>
              </div>
            ))}
          </div>

          {/* Add Shortcut */}
          <div className="bg-slate-50 p-4 rounded-md shadow-inner space-y-3">
            <h3 className="text-sm font-medium text-gray-800">➕ Add Shortcut</h3>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
              <input
                type="text"
                placeholder="Name"
                value={newShortcut.name}
                onChange={(e) => setNewShortcut({ ...newShortcut, name: e.target.value })}
                className="p-2 border border-gray-300 rounded text-sm"
              />
              <input
                type="url"
                placeholder="https://site.com"
                value={newShortcut.url}
                onChange={(e) => setNewShortcut({ ...newShortcut, url: e.target.value })}
                className="p-2 border border-gray-300 rounded text-sm"
              />
              <input
                type="url"
                placeholder="Icon URL (optional)"
                value={newShortcut.icon}
                onChange={(e) => setNewShortcut({ ...newShortcut, icon: e.target.value })}
                className="p-2 border border-gray-300 rounded text-sm"
              />
            </div>
            <button
              onClick={handleAddShortcut}
              className="bg-indigo-700 hover:bg-indigo-800 text-white py-2 px-4 rounded mt-2 text-sm"
            >
              Add Shortcut
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
