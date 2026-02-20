// src/components/PrivateRoute.jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const PrivateRoute = ({ children }) => {
  const { firebaseUser, user, loading } = useAuth();

  if (loading) return null;

  // Not logged in
  if (!firebaseUser) {
    return <Navigate to="/login" replace />;
  }

  // Email not verified
  if (!firebaseUser.emailVerified) {
    return <Navigate to="/login" replace />;
  }

  // Backend user missing
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default PrivateRoute;
