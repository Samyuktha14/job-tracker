// src/pages/admin/AdminLogs.jsx
import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";
import { getAdminLogs } from "../../api/AdminApi";
import toast from "react-hot-toast";

const AdminLogs = () => {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadLogs();
  }, [page]);

  const loadLogs = async () => {
    try {
      setLoading(true);
      const data = await getAdminLogs(page, 10);
      setLogs(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      toast.error("Failed to load admin logs");
    } finally {
      setLoading(false);
    }
  };

  // SECURE EXPORT USING AXIOS (TOKEN INCLUDED)
  const handleExport = async () => {
    try {
      const response = await axiosInstance.get(
        "/admin/logs/export",
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(
        new Blob([response.data])
      );
      
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "admin_logs.csv");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);


      toast.success("CSV exported successfully 🎉");
    } catch (err) {
      toast.error("Export failed");
    }
  };

  return (
    <div className="p-6 bg-slate-100 min-h-screen">

      {/* HEADER */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-indigo-700">
          🛡️ Admin Activity Logs
        </h1>

        <button
          onClick={handleExport}
          className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow transition"
        >
          Export CSV
        </button>
      </div>

      {/* TABLE */}
      <div className="bg-white rounded-xl shadow border overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="p-3 text-left">Time</th>
              <th className="p-3 text-left">Actor Email</th>
              <th className="p-3 text-center">Actor Role</th>
              <th className="p-3 text-center">Action</th>
              <th className="p-3 text-left">Target Email</th>
              <th className="p-3 text-center">Target Role</th>
              <th className="p-3 text-left">Details</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" className="text-center p-6 text-gray-500">
                  Loading logs...
                </td>
              </tr>
            ) : logs.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center p-8 text-gray-400">
                  No admin activity recorded.
                </td>
              </tr>
            ) : (
              logs.map((log) => (
                <tr key={log.id} className="border-b hover:bg-slate-50">
                  <td className="p-3">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>
                  <td className="p-3">{log.actorEmail}</td>
                  <td className="p-3 text-center">{log.actorRole}</td>
                  <td className="p-3 text-center font-semibold">
                    {log.action}
                  </td>
                  <td className="p-3">{log.targetEmail}</td>
                  <td className="p-3 text-center">{log.targetRole}</td>
                  <td className="p-3">{log.details}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* PAGINATION */}
      <div className="flex justify-center gap-3 mt-6">
        <button
          disabled={page === 0}
          onClick={() => setPage(page - 1)}
          className="px-3 py-1 border rounded disabled:opacity-40"
        >
          Prev
        </button>

        <span className="text-sm text-gray-600">
          Page {page + 1} of {totalPages}
        </span>

        <button
          disabled={page + 1 >= totalPages}
          onClick={() => setPage(page + 1)}
          className="px-3 py-1 border rounded disabled:opacity-40"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AdminLogs;
