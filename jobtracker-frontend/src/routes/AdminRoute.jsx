import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const AdminRoute = ({ children, superOnly = false }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <p className="text-center mt-10">
        Loading admin dashboard...
      </p>
    );
  }

  // Not logged in
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // SUPER_ADMIN only route
  if (superOnly && user.role !== "SUPER_ADMIN") {
    return <Navigate to="/admin" replace />;
  }

  // Allow ADMIN & SUPER_ADMIN normally
  if (
    user.role !== "ADMIN" &&
    user.role !== "SUPER_ADMIN"
  ) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default AdminRoute;
