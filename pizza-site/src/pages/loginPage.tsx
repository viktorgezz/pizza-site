import { useEffect } from "react";
import { LoginButton } from "../components/LoginButton";
import { login } from "../service/authService";
import { Button } from "@heroui/react";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      login();
    }, 8000); 
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-lg flex flex-col items-center space-y-6">
        <div className="w-full flex justify-end">
          <Button 
            variant="light" 
            color="default" 
            onClick={() => navigate("/")}
            size="sm"
          >
            На главную
          </Button>
        </div>
        <h1 className="text-2xl font-bold text-gray-900">Вход в аккаунт</h1>
        <LoginButton />
        <div className="w-full flex flex-col items-center space-y-4">
          <p className="text-gray-500 text-sm text-center">
            Для входа вы будете перенаправлены на защищённую страницу авторизации.
          </p>
          <div className="w-full border-t border-gray-200 my-4" />
          <div className="flex flex-col items-center space-y-2">
            <p className="text-gray-600 text-sm">Ещё нет аккаунта?</p>
            <Button 
              color="secondary"
              variant="flat"
              onClick={() => navigate("/register")}
              className="w-full"
            >
              Зарегистрироваться
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}