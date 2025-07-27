import React from 'react';
import { Link } from 'react-router-dom';
import { useAuthState } from 'react-firebase-hooks/auth';
import { auth } from '../firebase';

const Home = () => {
  const [user, loading]= useAuthState(auth);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <p className="text-lg font-medium">Loading...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 px-6">
        {/* Hero Section */}
        <header className="bg-gradient-to-br from-blue-100 via-white to-indigo-100 py-16 px-4 text-center w-full shadow-md relative overflow-hidden">
          <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-20 animate-scroll-bg" />
          <div className="relative z-10 max-w-4xl mx-auto">
            <h1 className="text-4xl md:text-5xl font-extrabold text-blue-800 mb-6 animate-fadeUp">Welcome to Job Tracker 👋</h1>
            <p className="text-lg text-gray-700 mb-4">Your smart assistant to organize, track, and manage all your job applications — all in one place.</p>
            <p className="text-md text-gray-600 mb-8">Never miss an opportunity again. Plan, apply, follow up, and land your dream job!</p>
            <div className="space-x-4">
              <Link to="/login" className="bg-blue-600 text-white px-6 py-2 rounded-md shadow hover:bg-blue-700 transition">Log In</Link>
              <Link to="/register" className="bg-indigo-600 text-white px-6 py-2 rounded-md shadow hover:bg-indigo-700 transition">Get Started</Link>
            </div>
          </div>
        </header>

        {/* Features Section */}
        <section className="mt-12 text-center max-w-3xl">
          <h2 className="text-2xl font-semibold text-gray-800 mb-6">What You Can Do:</h2>
          <ul className="text-gray-700 space-y-3">
            <li>✅ Add and track multiple job applications</li>
            <li>✅ Update status as you move through interviews</li>
            <li>✅ View all your progress on a single dashboard</li>
            <li>✅ Stay organized and boost your job search efficiency</li>
          </ul>
        </section>

        {/* Footer */}
        <footer className="mt-20 text-sm text-gray-500">
          Built with ❤️ by Samyuktha J
        </footer>
      </div>
    );
  }

  return (
<div className="bg-gray-50 min-h-screen flex flex-col">
  {/* Hero */}
  <header className="bg-gradient-to-br from-indigo-100 via-white to-blue-100 py-16 px-4 text-center relative overflow-hidden">
    <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10" />
    <div className="relative z-10 max-w-3xl mx-auto">
      <h1 className="text-4xl md:text-5xl font-bold text-indigo-800 mb-4 animate-fadeUp">
        Hello, {user.displayName || user.email?.split('@')[0] || 'User'} 👋
      </h1>
      <p className="text-lg text-gray-700 mb-6">
        Manage all your job applications in one place. Stay organized. Stay ahead.
      </p>
      <Link
        to="/dashboard"
        className="inline-block bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-md shadow-md transition"
      >
        Go to Dashboard
      </Link>
    </div>
  </header>

  {/* Why Use Section */}
  <section className="py-12 bg-white px-6 text-center">
    <h2 className="text-2xl font-bold text-indigo-700 mb-6">Why use Job Tracker? 📌</h2>
    <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 max-w-6xl mx-auto">
      <div className="p-5 bg-gray-100 rounded-md shadow hover:shadow-md transition">
        <h3 className="text-lg font-semibold text-indigo-800 mb-2">🧠 Smart Tracking</h3>
        <p className="text-gray-700">Log applications, interviews, and offers in a few clicks.</p>
      </div>
      <div className="p-5 bg-gray-100 rounded-md shadow hover:shadow-md transition">
        <h3 className="text-lg font-semibold text-indigo-800 mb-2">📅 Stay on Schedule</h3>
        <p className="text-gray-700">Set reminders and track status with ease.</p>
      </div>
      <div className="p-5 bg-gray-100 rounded-md shadow hover:shadow-md transition">
        <h3 className="text-lg font-semibold text-indigo-800 mb-2">📈 Visual Progress</h3>
        <p className="text-gray-700">View your journey visually and stay motivated.</p>
      </div>
    </div>
  </section>

  {/* Tips and Motivation */}
  <section className="py-10 px-6 bg-gray-50 text-center">
    <div className="max-w-4xl mx-auto grid gap-6 sm:grid-cols-2">
      <div className="p-6 bg-white rounded-lg shadow hover:shadow-lg transition">
        <h4 className="text-xl font-semibold text-green-700 mb-2">💬 Daily Motivation</h4>
        <p className="text-gray-600 italic">
          "Success is not final, failure is not fatal: It is the courage to continue that counts."
        </p>
      </div>
      <div className="p-6 bg-white rounded-lg shadow hover:shadow-lg transition">
        <h4 className="text-xl font-semibold text-indigo-700 mb-2">💡 Pro Tip</h4>
        <p className="text-gray-600">
          Tailor your resume for each job. It shows effort and improves your chances!
        </p>
      </div>
    </div>
  </section>

  {/* CTA */}
  <section className="bg-white py-12 text-center">
    <h3 className="text-xl text-indigo-800 font-semibold mb-2">Start Strong. Stay Consistent.</h3>
    <p className="text-gray-600 mb-4">Tracking even 2 jobs a week puts you ahead of 80% of applicants.</p>
  </section>

  {/* Footer */}
  <footer className="bg-white py-4 text-center text-sm text-gray-500">
    © {new Date().getFullYear()} Job Tracker. Built with 💙 by Samyuktha.
  </footer>
</div>

  );
};

export default Home;
