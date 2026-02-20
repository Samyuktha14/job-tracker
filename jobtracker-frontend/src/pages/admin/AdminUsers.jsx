import { useEffect, useState } from "react";
import {
  getAdminUsers,
  toggleUser,
  makeAdmin,
  deleteUser,
  demoteAdmin,
  restoreUser
} from "../../api/AdminApi";
import { useAuth } from "../../context/AuthContext";
import { useConfirm } from "../../context/ConfirmContext";
import ActionDropdown from "../../components/admin/ActionDropdown";
import toast from "react-hot-toast";

const AdminUsers = () => {
  const { user } = useAuth();
  const confirm = useConfirm();

  const currentUid = user?.uid;
  const currentRole = user?.role;

  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [role, setRole] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  // Debounce search
  useEffect(() => {
    const timeout = setTimeout(() => {
      setDebouncedSearch(search);
    }, 400);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    loadUsers();
  }, [page, debouncedSearch, status, role]);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await getAdminUsers(
        page,
        10,
        debouncedSearch,
        status,
        role
      );
      setUsers(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch {
      toast.error("Failed to load users");
    } finally {
      setLoading(false);
    }
  };

  const safeAction = async (action, successMessage) => {
    try {
      setActionLoading(true);
      await action();
      toast.success(successMessage);
      await loadUsers();
    } catch (err) {
      toast.error(err.response?.data?.message || "Action failed");
    } finally {
      setActionLoading(false);
    }
  };

  const confirmAndRun = async (config, action, successMessage) => {
    const ok = await confirm(config);
    if (ok) {
      await safeAction(action, successMessage);
    }
  };

  return (
    <div className="p-6 bg-slate-100 min-h-screen">
      <h1 className="text-2xl font-bold text-indigo-700 mb-6">
        👥 User Management
      </h1>

      {/* FILTERS */}
      <div className="flex flex-wrap gap-4 mb-4 items-center">
        <input
          type="text"
          placeholder="Search by email..."
          className="px-3 py-2 border rounded min-w-[200px]"
          value={search}
          onChange={(e) => {
            setPage(0);
            setSearch(e.target.value);
          }}
        />

        <select
          className="px-3 py-2 border rounded min-w-[150px]"
          value={status}
          onChange={(e) => {
            setPage(0);
            setStatus(e.target.value);
          }}
        >
          <option value="">All Status</option>
          <option value="ACTIVE">Active</option>
          <option value="DISABLED">Disabled</option>
          <option value="DELETED">Deleted</option>
        </select>

        <select
          className="px-3 py-2 border rounded min-w-[150px]"
          value={role}
          onChange={(e) => {
            setPage(0);
            setRole(e.target.value);
          }}
        >
          <option value="">All Roles</option>
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
          <option value="SUPER_ADMIN">Super Admin</option>
        </select>
      </div>

      {/* TABLE */}
      <div className="bg-white rounded-xl shadow border">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              <th className="p-3 text-left">Email</th>
              <th className="p-3 text-center">Role</th>
              <th className="p-3 text-center">Status</th>
              <th className="p-3 text-center w-[120px]">Actions</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan="4" className="text-center p-6 text-gray-500">
                  Loading users...
                </td>
              </tr>
            ) : users.length === 0 ? (
              <tr>
                <td colSpan="4" className="text-center p-8 text-gray-400">
                  No users found
                </td>
              </tr>
            ) : (
              users.map((u) => {
                const isSelf = u.uid === currentUid;
                const isSuper = currentRole === "SUPER_ADMIN";
                const isAdmin = currentRole === "ADMIN";
                const isDeleted = u.deleted;

                return (
                  <tr key={u.uid} className="border-b hover:bg-slate-50">
                    <td className="p-3">
                      {u.email}
                      {isSelf && (
                        <span className="ml-2 text-xs text-indigo-600 font-semibold">
                          (You)
                        </span>
                      )}
                    </td>

                    <td className="p-3 text-center">{u.role}</td>

                    <td className="p-3 text-center">
                      {isDeleted
                        ? "Deleted"
                        : u.active
                        ? "Active"
                        : "Disabled"}
                    </td>

                    <td className="p-3 text-center">
                      {!isSelf && (
                        <ActionDropdown
                          user={u}
                          isSuper={isSuper}
                          isAdmin={isAdmin}
                          isDeleted={isDeleted}
                          toggleUser={toggleUser}
                          makeAdmin={makeAdmin}
                          demoteAdmin={demoteAdmin}
                          deleteUser={deleteUser}
                          restoreUser={restoreUser}
                          confirmAndRun={confirmAndRun}
                        />
                      )}
                    </td>
                  </tr>
                );
              })
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

export default AdminUsers;
