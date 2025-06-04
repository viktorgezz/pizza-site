import { Button, Form, Input } from "@heroui/react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../config/api";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = (formData: FormData) => {
    const newErrors: Record<string, string> = {};
    const username = formData.get("username") as string;
    const password = formData.get("password") as string;
    const email = formData.get("email") as string;
    const phone = formData.get("phone") as string;
    const address = formData.get("address") as string;

    // Username validation
    if (!username || username.length < 3 || username.length > 50) {
      newErrors.username = "Имя пользователя должно быть от 3 до 50 символов";
    } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      newErrors.username = "Имя пользователя может содержать только буквы, цифры и знак подчеркивания";
    }

    // Password validation
    if (!password || password.length < 8 || password.length > 255) {
      newErrors.password = "Пароль должен быть от 8 до 255 символов";
    } else if (!/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(password)) {
      newErrors.password = "Пароль должен содержать минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ";
    }

    // Email validation
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = "Некорректный формат email";
    } else if (email.length > 255) {
      newErrors.email = "Email не может быть длиннее 255 символов";
    }

    // Phone validation
    if (!phone || !/^\+7\d{10}$/.test(phone)) {
      newErrors.phone = "Номер телефона должен быть в формате +7XXXXXXXXXX";
    } else if (phone.length > 50) {
      newErrors.phone = "Номер телефона не может быть длиннее 50 символов";
    }

    // Address validation
    if (!address || address.length > 255) {
      newErrors.address = "Адрес не может быть длиннее 255 символов";
    }

    return newErrors;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const validationErrors = validateForm(formData);

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/signup/customer`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: formData.get("username"),
          password: formData.get("password"),
          email: formData.get("email"),
          phone: formData.get("phone"),
          address: formData.get("address"),
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        setErrors({ submit: errorData.message || "Ошибка при регистрации" });
        return;
      }

      // Успешная регистрация
      navigate("/login");
    } catch (error) {
      setErrors({ submit: "Ошибка сервера. Попробуйте позже." });
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-lg">
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
        <div>
          <h2 className="text-center text-3xl font-bold tracking-tight text-gray-900">
            Регистрация
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Уже есть аккаунт?{" "}
            <Button
              variant="light"
              color="primary"
              onClick={() => navigate("/login")}
              className="font-medium"
            >
              Войти
            </Button>
          </p>
        </div>

        <Form
          className="mt-8 space-y-6"
          validationErrors={errors}
          onSubmit={handleSubmit}
        >
          <div className="space-y-4">
            <Input
              isRequired
              label="Имя пользователя"
              name="username"
              placeholder="Введите имя пользователя"
              variant="bordered"
              errorMessage={errors.username}
            />

            <Input
              isRequired
              type="password"
              label="Пароль"
              name="password"
              placeholder="Введите пароль"
              variant="bordered"
              errorMessage={errors.password}
            />

            <Input
              isRequired
              type="email"
              label="Email"
              name="email"
              placeholder="Введите email"
              variant="bordered"
              errorMessage={errors.email}
            />

            <Input
              isRequired
              label="Телефон"
              name="phone"
              placeholder="+7XXXXXXXXXX"
              variant="bordered"
              errorMessage={errors.phone}
            />

            <Input
              isRequired
              label="Адрес"
              name="address"
              placeholder="Введите адрес"
              variant="bordered"
              errorMessage={errors.address}
            />
          </div>

          {errors.submit && (
            <p className="text-sm text-danger text-center">{errors.submit}</p>
          )}

          <Button
            type="submit"
            color="primary"
            className="w-full"
            size="lg"
          >
            Зарегистрироваться
          </Button>
        </Form>
      </div>
    </div>
  );
} 