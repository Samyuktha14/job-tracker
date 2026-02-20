import axios from "axios";
import { getAuth, signOut } from "firebase/auth";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

axiosInstance.interceptors.request.use(
  async (config) => {
    const auth = getAuth();
    const user = auth.currentUser;

    if (user) {
      const token = await user.getIdToken();
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error.response?.status;
    const errorCode = error.response?.data?.error;
    const auth = getAuth();

    if (auth.currentUser) {

      if (status === 401) {
        await signOut(auth);
        window.location.replace("/login");
      }

      if (
        status === 403 &&
        (errorCode === "ACCOUNT_DELETED" ||
         errorCode === "ACCOUNT_DISABLED")
      ) {
        alert("Account restricted.");
        await signOut(auth);
        window.location.replace("/login");
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;