// src/components/PrivateRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthState } from 'react-firebase-hooks/auth';
import { auth } from '../firebase';

const PrivateRoute = ({ children }) => {
  const [user, loading] = useAuthState(auth);

  if (loading) return <p className="text-center mt-10">Loading...</p>;
  return user ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;
