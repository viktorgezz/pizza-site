import DefaultLayout from "@/layouts/default";
import { useEffect, useState } from "react";
import { getUser, logout } from "../service/authService";
import { fetchUserInfoFromResourceServer } from "../service/resourceUserService";
import { useNavigate } from "react-router-dom";
import { LoginButton } from "../components/LoginButton";

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null);
  const [userInfo, setUserInfo] = useState<any>(null);
  const navigate = useNavigate();

  useEffect(() => {
    getUser().then(u => {
      setUser(u);
      if (!u) navigate("/login");
    });
  }, [navigate]);

  useEffect(() => {
    if (user) {
      fetchUserInfoFromResourceServer()
        .then(setUserInfo)
        .catch(() => setUserInfo(null));
    }
  }, [user]);

  return (
    <DefaultLayout>
      <section className="flex flex-col items-center justify-center gap-6 py-12 md:py-16">
        <div className="max-w-md w-full p-8 bg-default-100 shadow-lg rounded-xl flex flex-col items-center">
          {user ? (
            <>
              <img
                src="/profile_photo.jpg"
                alt="Аватар пользователя"
                className="w-32 h-32 rounded-full mb-6 border-4 border-default-200 shadow"
              />
              <h2 className="text-2xl font-bold text-default-900 mb-2">
                {user.profile.name || user.profile.sub}
              </h2>
              {userInfo && (
                <div className="w-full text-left mt-4">
                  <div><b>ID:</b> {userInfo.id}</div>
                  <div><b>Username:</b> {userInfo.username}</div>
                  <div><b>Roles:</b> {userInfo.roles?.join(", ")}</div>
                </div>
              )}
              <button
                onClick={logout}
                className="mt-4 bg-gray-200 px-4 py-2 rounded"
              >
                Выйти
              </button>
            </>
          ) : (
            <>
              <h2 className="text-2xl font-bold text-default-900 mb-2">
                Войдите, чтобы просмотреть профиль
              </h2>
              <LoginButton />
            </>
          )}
        </div>
      </section>
    </DefaultLayout>
  );
} 