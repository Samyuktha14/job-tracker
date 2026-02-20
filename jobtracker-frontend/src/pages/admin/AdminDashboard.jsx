import { useEffect, useState } from "react";
import { getAdminStats } from "../../api/AdminApi";
import toast from "react-hot-toast";
import StatCard from "../../components/admin/StatCard";

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getAdminStats();
        setStats(data);
      } catch (err) {
        toast.error(
          err.response?.data?.message || "Failed to load admin stats"
        );
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);
    if (loading) {
      return (
        <div className="h-screen flex items-center justify-center">
          <div className="animate-spin h-8 w-8 border-4 border-indigo-500 border-t-transparent rounded-full"></div>
        </div>
      );
    }

  if (!stats) {
    return (
      <div className="p-6 text-center text-red-500">
        Unable to load dashboard.
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 p-6">
      {/* HEADER */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-indigo-700">
          🛠 Admin Dashboard
        </h1>
        <p className="text-sm text-gray-600">
          System overview & health metrics
        </p>
      </div>

      {/* USER METRICS */}
      <div className="mb-10">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">
          👥 User Metrics
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <StatCard title="Total Users" value={stats.totalUsers} icon="👤" />
          <StatCard title="Active Users" value={stats.activeUsers} icon="✅" />
          <StatCard title="Admin Users" value={stats.adminUsers} icon="🛡️" />
          <StatCard title="Normal Users" value={stats.normalUsers} icon="🙋" />
          <StatCard title="Deleted Users" value={stats.deletedUsers} icon="🗑️" />
          <StatCard title="Disabled Users" value={stats.disabledUsers} icon="⛔" />
        </div>
      </div>

      {/* JOB METRICS */}
      <div>
        <h2 className="text-lg font-semibold text-gray-800 mb-4">
          📊 Job Application Metrics
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
          <StatCard title="Total Jobs" value={stats.totalJobs} icon="📁" />
          <StatCard title="Applied" value={stats.applied} icon="📝" />
          <StatCard title="Interviewing" value={stats.interviewing} icon="💬" />
          <StatCard title="Selected" value={stats.selected} icon="🎉" />
          <StatCard title="Rejected" value={stats.rejected} icon="❌" />
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
