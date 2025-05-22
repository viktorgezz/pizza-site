import { getUser } from "./authService";

export async function fetchUserInfoFromResourceServer() {
  const user = await getUser();
  if (!user || !user.access_token) {
    throw new Error("Not authenticated");
  }
  const response = await fetch("http://localhost:8080/me", {
    headers: {
      Authorization: `Bearer ${user.access_token}`,
    },
  });
  if (!response.ok) {
    throw new Error("Failed to fetch user info");
  }
  return response.json();
} 