import StatCard from "./StatCard";

const AdminStatsGrid = ({ stats }) => {
  if (!stats) return null;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

      {/* User Metrics */}
      <StatCard title="Total Users" value={stats.totalUsers} icon="👤" />
      <StatCard title="Active Users" value={stats.activeUsers} icon="✅" />
      <StatCard title="Admin Users" value={stats.adminUsers} icon="🛡️" />
      <StatCard title="Normal Users" value={stats.normalUsers} icon="🙋" />
      <StatCard title="Deleted Users" value={stats.deletedUsers} icon="🗑️" />
      <StatCard title="Disabled Users" value={stats.disabledUsers} icon="⛔" />

      {/* Job Metrics */}
      <StatCard title="Total Jobs" value={stats.totalJobs} icon="📁" />
      <StatCard title="Applied" value={stats.applied} icon="📝" />
      <StatCard title="Interviewing" value={stats.interviewing} icon="💬" />
      <StatCard title="Selected" value={stats.selected} icon="🎉" />
      <StatCard title="Rejected" value={stats.rejected} icon="❌" />

    </div>
  );
};

export default AdminStatsGrid;
