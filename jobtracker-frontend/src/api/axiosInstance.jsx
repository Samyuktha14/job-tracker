import axios from 'axios';
import { getAuth } from 'firebase/auth';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
});

axiosInstance.interceptors.request.use(async (config) => {
  const user = getAuth().currentUser;
  if (user) {
    const token = await user.getIdToken();
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
