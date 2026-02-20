import { createContext, useContext, useEffect, useState } from "react";
import { onAuthStateChanged } from "firebase/auth";
import { auth } from "../firebase";
import axiosInstance from "../api/axiosInstance";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [firebaseUser, setFirebaseUser] = useState(null);
  const [user, setUser] = useState(null); // backend user
  const [loading, setLoading] = useState(true);

  

  const refreshUser = async () => {
  try {
    const res = await axiosInstance.get("/users/me");
    setUser(res.data);
  } catch (err) {
    console.error("Failed to refresh user", err);
  }
};


  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (fbUser) => {
      setLoading(true);
      setFirebaseUser(fbUser);
  

      // Not logged in
      if (!fbUser) {
        setUser(null);
        setLoading(false);
        return;
      }

      //  Email not verified → do NOT call backend
      if (!fbUser.emailVerified) {
        setUser(null);
        setLoading(false);
        return;
      }

      try {
        //  Force refresh token before backend call
        await fbUser.getIdToken(true);

        const res = await axiosInstance.get("/users/me");
        setUser(res.data);
      } catch (err) {
        console.error("Failed to load backend user", err);
        setUser(null);
      } finally {
        setLoading(false);
      }
    });

    return () => unsubscribe();
  }, []);

  const value = {
    firebaseUser,
    user,
    role: user?.role,
    active: user?.active,
    isAdmin: user?.role === "ADMIN",
    loading,
    refreshUser 
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
