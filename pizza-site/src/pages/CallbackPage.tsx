import { useEffect } from "react";
import { handleCallback } from "../service/authService";
import { useNavigate } from "react-router-dom";

export const CallbackPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    handleCallback().then(() => {
      navigate("/"); // редирект на главную после успешного логина
    });
  }, [navigate]);

  return <div>Авторизация...</div>;
};
