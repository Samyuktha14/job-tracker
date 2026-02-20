import axiosInstance from "./axiosInstance";

export const getCurrentUser = async () => {
  const res = await axiosInstance.get("/users/me");
  return res.data;
};
