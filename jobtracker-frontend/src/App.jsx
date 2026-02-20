// src/App.jsx
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import AddJobForm from './pages/AddJobForm';
import JobList from './pages/JobList';
import Register from './pages/Register';
import Login from './pages/Login';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import ForgotPassword from "./pages/ForgotPassword";

import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminRoute from "./routes/AdminRoute";
import AdminLogs from "./pages/admin/AdminLogs";


function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

         <Route path="/forgot-password" element={<ForgotPassword />} />

        {/* USER ROUTES */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/add"
          element={
            <PrivateRoute>
              <AddJobForm />
            </PrivateRoute>
          }
        />
        <Route
          path="/applications"
          element={
            <PrivateRoute>
              <JobList />
            </PrivateRoute>
          }
        />
        {/* ADMIN ROUTE */}
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminDashboard />
            </AdminRoute>
          }
        />
        <Route
          path="/admin/users"
          element={
            <AdminRoute>
              <AdminUsers />
            </AdminRoute>
          }
        />
      <Route
        path="/admin/logs"
        element={
          <AdminRoute superOnly={true}>
            <AdminLogs />
          </AdminRoute>
        }
      />
      </Routes>
    </>
  );
}
export default App;