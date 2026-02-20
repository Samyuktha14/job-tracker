import { Link, useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { auth } from "../firebase";
import { useAuth } from "../context/AuthContext";
import { FiSettings, FiLogOut } from "react-icons/fi";

const Navbar = () => {
  const { firebaseUser, user, loading } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleLogout = async () => {
    await auth.signOut();
    navigate("/login");
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  if (loading) return null;

  const isLoggedIn =
    firebaseUser && firebaseUser.emailVerified && user;

  const showAdminLinks =
    user?.role === "ADMIN" || user?.role === "SUPER_ADMIN";

  const getRoleBadge = (role) => {
    if (role === "SUPER_ADMIN")
      return "bg-purple-100 text-purple-700";
    if (role === "ADMIN")
      return "bg-indigo-100 text-indigo-700";
    return "bg-gray-100 text-gray-700";
  };

  return (
    <nav className="bg-gray-800 text-white px-6 py-4 flex justify-between items-center shadow">
      <Link to="/" className="font-bold text-xl tracking-wide">
        Job Tracker
      </Link>

      {isLoggedIn ? (
        <div className="flex items-center gap-6">

          {/* NAV LINKS */}
          <div className="flex gap-5 text-sm font-medium">
            <Link to="/" className="hover:text-blue-300">Home</Link>
            <Link to="/dashboard" className="hover:text-blue-300">Dashboard</Link>
            <Link to="/add" className="hover:text-blue-300">Add Application</Link>
            <Link to="/applications" className="hover:text-blue-300">Applications</Link>

            {showAdminLinks && (
              <>
                <Link to="/admin" className="hover:text-blue-300">Admin</Link>
                <Link to="/admin/users" className="hover:text-blue-300">Users</Link>
                 {user?.role === "SUPER_ADMIN" && (
                <Link to="/admin/logs" className="hover:text-blue-300">
                  Admin Logs
                </Link>
              )}
              </>
            )}
          </div>

          {/* PROFILE DROPDOWN */}
          <div className="relative" ref={dropdownRef}>
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="px-3 py-1 rounded hover:bg-gray-700 transition"
            >
              {user.displayName}
            </button>

            {menuOpen && (
              <div className="absolute right-0 mt-3 w-56 bg-white text-black rounded-xl shadow-lg border z-50">

                <div className="p-4 border-b">
                  <p className="text-sm font-medium">{user.email}</p>
                  <span
                    className={`inline-block mt-2 px-2 py-1 text-xs font-semibold rounded ${getRoleBadge(
                      user.role
                    )}`}
                  >
                    {user.role}
                  </span>
                </div>

                <button
                  onClick={() => {
                    setMenuOpen(false);
                    handleLogout();
                  }}
                  className="flex items-center gap-2 w-full text-left px-4 py-3 text-sm hover:bg-gray-100 rounded-b-xl"
                >
                  <FiLogOut size={16} />
                  Logout
                </button>


              </div>
            )}

          </div>

        </div>
      ) : (
        <div className="flex gap-4 text-sm font-medium">
          <Link to="/login" className="hover:text-blue-300">Login</Link>
          <Link to="/register" className="hover:text-blue-300">Register</Link>
        </div>
      )}
    </nav>
  );
};

export default Navbar;
