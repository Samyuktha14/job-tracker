import { Link, useNavigate } from "react-router-dom";
import { useAuthState } from 'react-firebase-hooks/auth';
import { auth } from '../firebase';
import { useState } from 'react';

const Navbar = () => {
  const [user, loading] = useAuthState(auth);
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    auth.signOut();
    navigate('/login');
  };

  return (
    <nav className="bg-gray-800 text-white p-4 flex justify-between items-center">
      <Link to="/" className="font-bold text-xl">Job Tracker</Link>

      {user ? (
        <div className="flex items-center gap-6">
          
          {/* Navigation Links */}
          <div className="flex gap-4">
            <Link to="/" className="hover:text-blue-300">Home</Link>
            <Link to="/add" className="hover:text-blue-300">Add Application</Link>
            <Link to="/applications" className="hover:text-blue-300">Applications</Link>
            <Link to="/dashboard" className="hover:text-blue-300">Dashboard</Link>
          </div>

          {/* Profile Section */}
          <div className="relative">
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="flex items-center gap-2 hover:text-blue-300"
            >
              <img
                src="https://www.svgrepo.com/show/7025/user.svg"
                alt="Profile"
                className="w-6 h-6 rounded-full bg-white p-1"
              />
              <span>{user.email.split('@')[0]}</span>
            </button>

            {/* Dropdown */}
            {menuOpen && (
              <div className="absolute right-0 mt-2 w-56 bg-white text-black rounded shadow-lg z-50">
                <div className="p-4 border-b">
                  <p className="text-xs text-gray-500">Signed in as</p>
                  <p className="font-medium">{user.email}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      ) : (
        <div className="flex gap-4">
          {!user && <div className="w-full" />}
        </div>
      )}
    </nav>
  );
};

export default Navbar;
