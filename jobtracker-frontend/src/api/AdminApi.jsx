import axiosInstance from "./axiosInstance";

// ===============================
// GET USERS (Paginated + Filters)
// ===============================
export const getAdminUsers = async (
  page = 0,
  size = 10,
  search = "",
  status = "",
  role = ""
) => {
  const res = await axiosInstance.get("/admin/users", {
    params: { page, size, search, status, role }
  });
  return res.data;
};

// ===============================
// TOGGLE USER
// ===============================
export const toggleUser = async (uid) => {
  const res = await axiosInstance.put(
    `/admin/toggle-user/${uid}`
  );
  return res.data;
};

// ===============================
// MAKE ADMIN
// ===============================
export const makeAdmin = async (uid) => {
  const res = await axiosInstance.put(
    `/admin/make-admin/${uid}`
  );
  return res.data;
};

// ===============================
// DEMOTE ADMIN
// ===============================
export const demoteAdmin = async (uid) => {
  const res = await axiosInstance.put(
    `/admin/demote-admin/${uid}`
  );
  return res.data;
};

// ===============================
// RESTORE USER
// ===============================
export const restoreUser = async (uid) => {
  const res = await axiosInstance.put(
    `/admin/restore-user/${uid}`
  );
  return res.data;
};

// ===============================
// DELETE USER (Soft Delete)
// ===============================
export const deleteUser = async (uid) => {
  const res = await axiosInstance.delete(
    `/admin/delete-user/${uid}`
  );
  return res.data;
};

// ===============================
// ADMIN STATS
// ===============================
export const getAdminStats = async () => {
  const res = await axiosInstance.get(`/admin/stats`);
  return res.data;
};

// ===============================
// GET ADMIN LOGS (Paginated)
// ===============================
export const getAdminLogs = async (page = 0, size = 10) => {
  const res = await axiosInstance.get("/admin/logs", {
    params: { page, size }
  });
  return res.data;
};
