import { useEffect } from "react";
import { LoginButton } from "../components/LoginButton";
import { login } from "../service/authService";

export default function LoginPage() {
  useEffect(() => {
    const timer = setTimeout(() => {
      login();
    }, 2000); // 1 секунда
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-lg flex flex-col items-center">
        <h1 className="text-2xl font-bold mb-6">Вход в аккаунт</h1>
        <LoginButton />
        <p className="mt-4 text-gray-500 text-sm text-center">
          Для входа вы будете перенаправлены на защищённую страницу авторизации.
        </p>
      </div>
    </div>
  );
}