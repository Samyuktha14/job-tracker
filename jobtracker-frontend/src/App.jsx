// src/App.jsx
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import AddJobForm from './pages/AddJobForm';
import JobList from './pages/JobList';
import Register from './pages/Register';
import Login from './pages/Login';
import PrivateRoute from './components/privateRoute';
import Navbar from './components/Navbar';


function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/add" element={<PrivateRoute><AddJobForm /></PrivateRoute>} />
        <Route path="/applications" element={<PrivateRoute><JobList /></PrivateRoute>} />
        
      </Routes>
    </>
  );
}

export default App;
